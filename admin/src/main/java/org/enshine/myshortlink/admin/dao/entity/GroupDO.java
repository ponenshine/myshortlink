package org.enshine.myshortlink.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.enshine.myshortlink.admin.common.database.BaseDO;

@Data
@TableName("t_group")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class GroupDO extends BaseDO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 创建分组用户名
     */
    private String username;

    /**
     * 分组排序
     */
    private Integer sortOrder;
}
