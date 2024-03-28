package org.enshine.myshortlink.admin.remote;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.enshine.myshortlink.admin.common.convention.result.Result;
import org.enshine.myshortlink.admin.remote.req.LinkSaveReqDTO;
import org.enshine.myshortlink.admin.remote.resp.LinkPageRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "shortlink-project", url = "${aggregation.remote-url:}")
public interface ILinkRemoteService {
    /**
     * 新增短链接
     */
    @PostMapping("/api/shortlink/v1/link")
    Result<Void> create(@RequestBody LinkSaveReqDTO requestParam);

    /**
     * 分页查询短链接集合
     * 一定要用Page接收 不能用 IPage 它不能被feign反序列化
     * 当method为get时 传参一定要用RequestParam注解
     */
    @GetMapping("/api/shortlink/v1/link")
    Result<Page<LinkPageRespDTO>> page(@RequestParam("gid") String gid,
                                       @RequestParam("current") Long current,
                                       @RequestParam("size") Long size);
}
