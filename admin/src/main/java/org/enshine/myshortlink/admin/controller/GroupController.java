package org.enshine.myshortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.enshine.myshortlink.admin.service.IGroupService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GroupController {
    private final IGroupService groupService;

}
