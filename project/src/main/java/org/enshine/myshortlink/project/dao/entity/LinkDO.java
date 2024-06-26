package org.enshine.myshortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.enshine.myshortlink.project.common.database.BaseDO;

import java.util.Date;

@Data
@TableName("t_link")
@Accessors(chain = true)
public class LinkDO extends BaseDO {
    private static final long serialVersionUID = 1L;

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
     * 分组标识
     */
    private String gid;

    /**
     * 点击量
     */
    private Integer clickNum;

    /**
     * 启用标识：0:启用 1:未启用
     */
    private Integer enableStatus;

    /**
     * 创建类型：0: 控制台创建 1: 接口创建
     */
    private Integer createType;

    /**
     * 有效期类型：0: 永久有效 1: 自定义
     */
    private Integer validDateType;

    /**
     * 有效期至
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
