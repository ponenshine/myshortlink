package org.enshine.myshortlink.admin.dto.req;

import lombok.Data;

/**
 * 分组排序修改入参
 */
@Data
public class GroupSortReqDTO {
    private String gid;
    private Integer sortOrder;
}
