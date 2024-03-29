package org.enshine.myshortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.enshine.myshortlink.admin.dao.entity.UserDO;
import org.enshine.myshortlink.admin.dto.req.UserLoginReqDTO;
import org.enshine.myshortlink.admin.dto.req.UserRegisterReqDTO;
import org.enshine.myshortlink.admin.dto.req.UserUpdateReqDTO;
import org.enshine.myshortlink.admin.dto.resp.UserLoginRespDTO;
import org.enshine.myshortlink.admin.dto.resp.UserRespDTO;

public interface IUserService extends IService<UserDO> {
    UserRespDTO getUserByUsername(String username);

    Boolean hasUsername(String username);

    void register(UserRegisterReqDTO requestParam);

    void update(UserUpdateReqDTO requestParam);

    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    Boolean checkLogin(String username, String token);

    void logout(String username, String token);
}
