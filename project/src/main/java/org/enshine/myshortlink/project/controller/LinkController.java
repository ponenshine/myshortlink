package org.enshine.myshortlink.project.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.enshine.myshortlink.project.common.convention.result.Result;
import org.enshine.myshortlink.project.common.convention.result.Results;
import org.enshine.myshortlink.project.dto.req.LinkPageReqDTO;
import org.enshine.myshortlink.project.dto.req.LinkSaveReqDTO;
import org.enshine.myshortlink.project.dto.resp.LinkPageRespDTO;
import org.enshine.myshortlink.project.handler.CustomBlockHandler;
import org.enshine.myshortlink.project.service.ILinkService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LinkController {
    private final ILinkService linkService;

    /**
     * 新增短链接
     * 请求参数的originUrl一定要带上Http(s)://前缀 否则重定向会出现问题
     */
    @SentinelResource(
            value = "create_short-link",
            blockHandler = "createShortLinkBlockHandlerMethod",
            blockHandlerClass = CustomBlockHandler.class
    )
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

    /**
     * 短链接跳转
     */
    @GetMapping("/{uri}")
    @SneakyThrows
    public Result<Void> redirect(@PathVariable String uri, HttpServletRequest request, HttpServletResponse response){
        linkService.redirect(uri,request,response);
        return Results.success();
    }

}
