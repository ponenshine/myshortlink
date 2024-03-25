package org.enshine.myshortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.enshine.myshortlink.admin.common.convention.result.Result;
import org.enshine.myshortlink.admin.common.convention.result.Results;
import org.enshine.myshortlink.admin.dto.resp.UserActualRespDTO;
import org.enshine.myshortlink.admin.dto.resp.UserRespDTO;
import org.enshine.myshortlink.admin.service.IUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    /**
     * 根据用户名查询脱敏用户信息
     */
    @GetMapping("/api/shortlink/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable String username){
        UserRespDTO userRespDTO=userService.getUserByUsername(username);
        return Results.success(userRespDTO);
    }

    /**
     * 根据用户名查询无脱敏用户信息
     */
    @GetMapping("/api/shortlink/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable String username){
        UserRespDTO userRespDTO=userService.getUserByUsername(username);
        return Results.success(BeanUtil.toBean(userRespDTO, UserActualRespDTO.class));
    }
}
