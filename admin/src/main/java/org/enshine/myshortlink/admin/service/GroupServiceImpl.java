package org.enshine.myshortlink.admin.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.enshine.myshortlink.admin.common.biz.user.UserContext;
import org.enshine.myshortlink.admin.common.convention.exception.ClientException;
import org.enshine.myshortlink.admin.dao.entity.GroupDO;
import org.enshine.myshortlink.admin.dao.mapper.GroupMapper;
import org.enshine.myshortlink.admin.dto.req.GroupSortReqDTO;
import org.enshine.myshortlink.admin.dto.req.GroupUpdateReqDTO;
import org.enshine.myshortlink.admin.dto.resp.GroupRespDTO;
import org.enshine.myshortlink.admin.util.RandomGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.enshine.myshortlink.admin.common.enums.GroupErrorCodeEnum.*;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements IGroupService {
    @Override
    public void save(String name) {
        Long gCount = baseMapper.selectCount(Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername()));
        if (gCount == 10) {
            throw new ClientException(GROUP_COUNT_LIMITED);
        }
        String gid;
        while (true) {
            gid = RandomGenerator.generateRandom();
            GroupDO one = getOne(Wrappers.lambdaQuery(GroupDO.class).eq(GroupDO::getGid, gid));
            if (one == null) break;
        }
        boolean save = save(new GroupDO().setGid(gid).setName(name).setUsername(UserContext.getUsername()));
        if (!save) {
            throw new ClientException(GROUP_SAVE_FAIL);
        }
    }

    @Override
    public void update(GroupUpdateReqDTO requestParam) {
        boolean update = update(Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid, requestParam.getGid())
                .set(GroupDO::getName, requestParam.getName()));
        if (!update) {
            throw new ClientException(GROUP_UPDATE_FAIL);
        }
    }

    @Override
    public List<GroupRespDTO> listByUsername() {
        List<GroupDO> groupDOList = baseMapper.selectList(Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername()));
        return BeanUtil.copyToList(groupDOList, GroupRespDTO.class);
    }

    @Override
    public void delete(String gid) {
        boolean update = update(Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .set(GroupDO::getDelFlag, 1));
        if (!update) {
            throw new ClientException(GROUP_DELETE_FAIL);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sort(List<GroupSortReqDTO> requestParam) {
        for (GroupSortReqDTO groupSortReqDTO : requestParam) {
            boolean update = update(Wrappers.lambdaUpdate(GroupDO.class)
                    .eq(GroupDO::getGid, groupSortReqDTO.getGid())
                    .set(GroupDO::getSortOrder, groupSortReqDTO.getSortOrder()));
            if (!update) {
                throw new ClientException(GROUP_UPDATE_FAIL);
            }
        }
    }
}
