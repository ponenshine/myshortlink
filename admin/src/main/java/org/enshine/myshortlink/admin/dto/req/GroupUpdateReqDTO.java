package org.enshine.myshortlink.admin.dto.req;

import lombok.Data;

/**
 * 修改分组名称请求参数
 */
@Data
public class GroupUpdateReqDTO {
    private String gid;
    private String name;
}
