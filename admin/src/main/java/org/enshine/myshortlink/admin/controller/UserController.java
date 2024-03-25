package org.enshine.myshortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.enshine.myshortlink.admin.common.convention.result.Result;
import org.enshine.myshortlink.admin.common.convention.result.Results;
import org.enshine.myshortlink.admin.dto.req.UserLoginReqDTO;
import org.enshine.myshortlink.admin.dto.req.UserRegisterReqDTO;
import org.enshine.myshortlink.admin.dto.req.UserUpdateReqDTO;
import org.enshine.myshortlink.admin.dto.resp.UserActualRespDTO;
import org.enshine.myshortlink.admin.dto.resp.UserLoginRespDTO;
import org.enshine.myshortlink.admin.dto.resp.UserRespDTO;
import org.enshine.myshortlink.admin.service.IUserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    /**
     * 根据用户名查询脱敏用户信息
     */
    @GetMapping("/api/shortlink/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable String username) {
        UserRespDTO userRespDTO = userService.getUserByUsername(username);
        return Results.success(userRespDTO);
    }

    /**
     * 根据用户名查询无脱敏用户信息
     */
    @GetMapping("/api/shortlink/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByUsername(@PathVariable String username) {
        UserRespDTO userRespDTO = userService.getUserByUsername(username);
        return Results.success(BeanUtil.toBean(userRespDTO, UserActualRespDTO.class));
    }

    /**
     * 查询用户名是否存在
     * true：存在
     */
    @GetMapping("/api/shortlink/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username) {
        return Results.success(userService.hasUsername(username));
    }

    /**
     * 用户注册
     */
    @PostMapping("/api/shortlink/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

    /**
     * 修改用户个人信息
     */
    @PutMapping("/api/shortlink/v1/user")
    public Result<Void> update(@RequestBody UserUpdateReqDTO requestParam) {
        userService.update(requestParam);
        return Results.success();
    }

    /**
     * 用户登录
     */
    @PostMapping("/api/shortlink/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {
        return Results.success(userService.login(requestParam));
    }

    /**
     * 检查用户登录态是否有效
     */
    @GetMapping("/api/shortlink/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("username") String username,
                                      @RequestParam("token") String token) {
        return Results.success(userService.checkLogin(username, token));
    }

    /**
     * 用户退出登录
     */
    @DeleteMapping("/api/shortlink/v1/user/logout")
    public Result<Void> logout(@RequestParam("username") String username,
                               @RequestParam("token") String token){
        userService.logout(username,token);
        return Results.success();
    }
}
