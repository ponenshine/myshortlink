package org.enshine.myshortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.enshine.myshortlink.admin.common.convention.exception.ClientException;
import org.enshine.myshortlink.admin.common.enums.UserErrorCodeEnum;
import org.enshine.myshortlink.admin.dao.entity.UserDO;
import org.enshine.myshortlink.admin.dao.mapper.UserMapper;
import org.enshine.myshortlink.admin.dto.resp.UserRespDTO;
import org.enshine.myshortlink.admin.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements IUserService {
    @Override
    public UserRespDTO getUserByUsername(String username) {
        UserDO userDO = lambdaQuery().eq(UserDO::getUsername, username).one();
        if (userDO == null) throw new ClientException(UserErrorCodeEnum.USER_NULL);
        return BeanUtil.toBean(userDO, UserRespDTO.class);
    }
}
