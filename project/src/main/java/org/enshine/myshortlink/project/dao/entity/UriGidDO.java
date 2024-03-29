package org.enshine.myshortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("t_uri_gid")
public class UriGidDO {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String uri;
    private String gid;
}
