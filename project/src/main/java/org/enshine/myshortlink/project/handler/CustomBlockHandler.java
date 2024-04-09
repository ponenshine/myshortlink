package org.enshine.myshortlink.project.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.enshine.myshortlink.project.common.convention.result.Result;
import org.enshine.myshortlink.project.common.convention.result.Results;
import org.enshine.myshortlink.project.dto.req.LinkSaveReqDTO;

public class CustomBlockHandler {
    public static Result<Void> createShortLinkBlockHandlerMethod(LinkSaveReqDTO requestParam, BlockException exception) {
        return Results.failure("B100000","当前访问网站人数过多，请稍后再试...");
    }
}
