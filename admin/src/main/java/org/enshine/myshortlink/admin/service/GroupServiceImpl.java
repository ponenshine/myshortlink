package org.enshine.myshortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.enshine.myshortlink.admin.dao.entity.GroupDO;
import org.enshine.myshortlink.admin.dao.mapper.GroupMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements IGroupService {
}
