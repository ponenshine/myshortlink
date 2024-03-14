package org.enshine.myshortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.enshine.myshortlink.admin.service.IUserService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;


}
