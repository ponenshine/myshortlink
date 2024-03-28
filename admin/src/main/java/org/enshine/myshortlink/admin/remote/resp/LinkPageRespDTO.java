package org.enshine.myshortlink.admin.remote.resp;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkPageRespDTO {
    /**
     * 短链接域名
     */
    private String domain;

    /**
     * 短链接uri
     */
    private String shortUri;

    /**
     * 短链接完整url
     */
    private String fullShortUrl;

    /**
     * 原始url
     */
    private String originUrl;

    /**
     * 点击量
     */
    private Integer clickNum;

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
