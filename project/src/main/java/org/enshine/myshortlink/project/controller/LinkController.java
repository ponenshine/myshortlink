package org.enshine.myshortlink.project.controller;

import lombok.RequiredArgsConstructor;
import org.enshine.myshortlink.project.service.ILinkService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LinkController {
    private final ILinkService linkService;


}
