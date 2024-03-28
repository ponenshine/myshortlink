package org.enshine.myshortlink.admin.remote.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

@Data
public class LinkPageReqDTO extends Page {
    /**
     * 分组标识
     */
    private String gid;
}
