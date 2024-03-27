package org.enshine.myshortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.enshine.myshortlink.admin.common.constant.RedisCacheConstant;
import org.enshine.myshortlink.admin.common.convention.exception.ClientException;
import org.enshine.myshortlink.admin.common.convention.exception.ServiceException;
import org.enshine.myshortlink.admin.dao.entity.UserDO;
import org.enshine.myshortlink.admin.dao.mapper.UserMapper;
import org.enshine.myshortlink.admin.dto.req.UserLoginReqDTO;
import org.enshine.myshortlink.admin.dto.req.UserRegisterReqDTO;
import org.enshine.myshortlink.admin.dto.req.UserUpdateReqDTO;
import org.enshine.myshortlink.admin.dto.resp.UserLoginRespDTO;
import org.enshine.myshortlink.admin.dto.resp.UserRespDTO;
import org.enshine.myshortlink.admin.service.IUserService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static org.enshine.myshortlink.admin.common.enums.UserErrorCodeEnum.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements IUserService {
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        UserDO userDO = lambdaQuery().eq(UserDO::getUsername, username).one();
        if (userDO == null) throw new ClientException(USER_NULL);
        return BeanUtil.toBean(userDO, UserRespDTO.class);
    }

    @Override
    public Boolean hasUsername(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        if (hasUsername(requestParam.getUsername())) {
            throw new ClientException(USER_NAME_EXIST);
        }
        RLock lock = redissonClient.getLock(RedisCacheConstant.LOCK_USER_REGISTER_KEY + requestParam.getUsername());
        try {
            // 加锁 防止他人使用同一个用户名刷接口
            if (lock.tryLock()) {
                boolean save=true;
                try{
                    save = save(BeanUtil.toBean(requestParam, UserDO.class));
                }catch (Exception e){
                    throw new ServiceException(e.getMessage());
                }
                if (!save) throw new ClientException(USER_SAVE_ERROR);
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
                return;
            }
            throw new ClientException(USER_NAME_EXIST);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {
        // TODO 验证当前用户名是否为登录用户
        // TODO 验证当前用户是否与用户名匹配

        boolean update = update(BeanUtil.toBean(requestParam, UserDO.class),
                Wrappers.lambdaUpdate(UserDO.class).eq(UserDO::getUsername, requestParam.getUsername()));
        if (!update) {
            throw new ClientException(USER_UPDATE_ERROR);
        }
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        if (!hasUsername(requestParam.getUsername())) {
            throw new ClientException(USER_NAME_NOT_EXIST);
        }
        LambdaQueryWrapper<UserDO> wrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getPassword, requestParam.getPassword());
        UserDO userDO = getOne(wrapper);
        if (userDO == null) {
            throw new ClientException(USER_PWD_ERROR);
        }
        if (userDO.getDelFlag() == 1) {
            throw new ClientException(USER_LOGOFF);
        }
        Boolean hasLogin = stringRedisTemplate.hasKey(RedisCacheConstant.KEY_USER_LOGIN + requestParam.getUsername());
        if (Boolean.TRUE.equals(hasLogin)) {
            throw new ClientException(USER_HAS_LOGIN);
        }
        String data = "ai346346eouw3456645beh4563v24314fiug23542wew243eafawfff43vw4r4eafeafw65efe";
        String token = SecureUtil.md5(data);
        stringRedisTemplate.opsForHash().put(RedisCacheConstant.KEY_USER_LOGIN + requestParam.getUsername(),
                token, JSONUtil.toJsonStr(userDO));
        stringRedisTemplate.expire(RedisCacheConstant.KEY_USER_LOGIN + requestParam.getUsername(),
                30, TimeUnit.MINUTES);
        return new UserLoginRespDTO().setToken(token);
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        return stringRedisTemplate.opsForHash().hasKey(RedisCacheConstant.KEY_USER_LOGIN + username, token);
    }

    @Override
    public void logout(String username, String token) {
        if (!checkLogin(username, token)) {
            throw new ClientException(USER_NOT_LOGIN);
        }
        stringRedisTemplate.opsForHash().delete(username, token);
    }
}
