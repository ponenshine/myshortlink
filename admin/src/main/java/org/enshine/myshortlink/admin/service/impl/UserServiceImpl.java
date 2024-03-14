package org.enshine.myshortlink.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.enshine.myshortlink.admin.dao.entity.UserDO;
import org.enshine.myshortlink.admin.dao.mapper.UserMapper;
import org.enshine.myshortlink.admin.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements IUserService {
}
