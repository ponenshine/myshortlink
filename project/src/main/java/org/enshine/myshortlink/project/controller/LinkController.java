package org.enshine.myshortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.enshine.myshortlink.project.common.convention.result.Result;
import org.enshine.myshortlink.project.common.convention.result.Results;
import org.enshine.myshortlink.project.dto.req.LinkPageReqDTO;
import org.enshine.myshortlink.project.dto.req.LinkSaveReqDTO;
import org.enshine.myshortlink.project.dto.resp.LinkPageRespDTO;
import org.enshine.myshortlink.project.service.ILinkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LinkController {
    private final ILinkService linkService;

    /**
     * 新增短链接
     */
    @PostMapping("/api/shortlink/v1/link")
    public Result<Void> create(@RequestBody LinkSaveReqDTO requestParam){
        linkService.create(requestParam);
        return Results.success();
    }

    /**
     * 分页查询短链接集合
     */
    @GetMapping("/api/shortlink/v1/link")
    public Result<IPage<LinkPageRespDTO>> page(LinkPageReqDTO requestParam){
        return Results.success(linkService.pageByGid(requestParam));
    }

    // TODO 短链接信息修改


}
