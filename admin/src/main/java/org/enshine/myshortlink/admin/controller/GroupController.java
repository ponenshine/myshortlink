package org.enshine.myshortlink.admin.controller;

import lombok.RequiredArgsConstructor;
import org.enshine.myshortlink.admin.common.convention.result.Result;
import org.enshine.myshortlink.admin.common.convention.result.Results;
import org.enshine.myshortlink.admin.dto.req.GroupSortReqDTO;
import org.enshine.myshortlink.admin.dto.req.GroupUpdateReqDTO;
import org.enshine.myshortlink.admin.dto.resp.GroupRespDTO;
import org.enshine.myshortlink.admin.service.IGroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupController {
    private final IGroupService groupService;

    /**
     * 增加短链分组
     */
    @PostMapping("/api/shortlink/v1/group")
    public Result<Void> save(@RequestParam("name") String name) {
        groupService.save(name);
        return Results.success();
    }

    /**
     * 修改短链接分组名称
     */
    @PutMapping("/api/shortlink/v1/group")
    public Result<Void> update(@RequestBody GroupUpdateReqDTO requestParam) {
        groupService.update(requestParam);
        return Results.success();
    }

    /**
     * 查询短链接分组集合
     */
    @GetMapping("/api/shortlink/v1/group")
    public Result<List<GroupRespDTO>> list() {
        return Results.success(groupService.listByUsername());
    }

    /**
     * 删除短链接分组
     */
    @DeleteMapping("/api/shortlink/v1/group")
    public Result<Void> delete(@RequestParam("gid") String gid) {
        groupService.delete(gid);
        return Results.success();
    }

    /**
     * 短链接分组排序
     */
    @PutMapping("/api/shortlink/v1/group/sort")
    public Result<Void> sort(@RequestBody List<GroupSortReqDTO> requestParam) {
        groupService.sort(requestParam);
        return Results.success();
    }
}
