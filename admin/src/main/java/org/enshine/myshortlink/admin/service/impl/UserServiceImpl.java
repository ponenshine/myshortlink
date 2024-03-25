package org.enshine.myshortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.enshine.myshortlink.admin.common.constant.RedisCacheConstant;
import org.enshine.myshortlink.admin.common.convention.exception.ClientException;
import org.enshine.myshortlink.admin.dao.entity.UserDO;
import org.enshine.myshortlink.admin.dao.mapper.UserMapper;
import org.enshine.myshortlink.admin.dto.req.UserRegisterReqDTO;
import org.enshine.myshortlink.admin.dto.resp.UserRespDTO;
import org.enshine.myshortlink.admin.service.IUserService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import static org.enshine.myshortlink.admin.common.enums.UserErrorCodeEnum.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements IUserService {
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;

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
        RLock lock = redissonClient.getLock(RedisCacheConstant.LOCK_USER_REGISTER_KEY +requestParam.getUsername());
        try{
            if(lock.tryLock()){
                boolean save = save(BeanUtil.toBean(requestParam, UserDO.class));
                if (!save) throw new ClientException(USER_SAVE_ERROR);
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
                return;
            }
            throw new ClientException(USER_NAME_EXIST);
        }finally {
            lock.unlock();
        }
    }
}
