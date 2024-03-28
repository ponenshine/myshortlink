package org.enshine.myshortlink.admin.remote.req;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class LinkSaveReqDTO {
    /**
     * 短链接域名
     */
    private String domain;

    /**
     * 原始url
     */
    private String originUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 启用标识：0:启用 1:未启用
     */
    private Integer enableStatus;

    /**
     * 有效期类型：0: 永久有效 1: 自定义
     */
    private Integer validDateType;

    /**
     * 有效期至
     */
    private Date validDate;

    /**
     * 描述
     */
    @TableField("`describe`")
    private String describe;

    /**
     * 网站图标
     */
    private String favicon;
}
