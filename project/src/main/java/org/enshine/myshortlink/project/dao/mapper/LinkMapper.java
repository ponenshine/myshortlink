package org.enshine.myshortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.enshine.myshortlink.project.dao.entity.LinkDO;

@Mapper
public interface LinkMapper extends BaseMapper<LinkDO> {
}
