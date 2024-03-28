package org.enshine.myshortlink.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.enshine.myshortlink.admin.common.convention.result.Result;
import org.enshine.myshortlink.admin.remote.ILinkRemoteService;
import org.enshine.myshortlink.admin.remote.req.LinkPageReqDTO;
import org.enshine.myshortlink.admin.remote.req.LinkSaveReqDTO;
import org.enshine.myshortlink.admin.remote.resp.LinkPageRespDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LinkController {
    private final ILinkRemoteService linkRemoteService;

    /**
     * 新增短链接
     */
    @PostMapping("/api/shortlink/admin/v1/link")
    public Result<Void> create(@RequestBody LinkSaveReqDTO requestParam) {
        return linkRemoteService.create(requestParam);
    }

    /**
     * 分页查询短链接集合
     */
    @GetMapping("/api/shortlink/admin/v1/link")
    public Result<Page<LinkPageRespDTO>> page(LinkPageReqDTO requestParam) {
        return linkRemoteService.page(requestParam.getGid(), requestParam.getCurrent(), requestParam.getSize());
    }
}
