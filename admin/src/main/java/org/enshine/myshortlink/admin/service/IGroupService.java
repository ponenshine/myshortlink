package org.enshine.myshortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.enshine.myshortlink.admin.dao.entity.GroupDO;
import org.enshine.myshortlink.admin.dto.req.GroupSortReqDTO;
import org.enshine.myshortlink.admin.dto.req.GroupUpdateReqDTO;
import org.enshine.myshortlink.admin.dto.resp.GroupRespDTO;

import java.util.List;

public interface IGroupService extends IService<GroupDO> {
    void save(String name);

    void update(GroupUpdateReqDTO requestParam);

    List<GroupRespDTO> listByUsername();

    void delete(String gid);

    void sort(List<GroupSortReqDTO> requestParam);
}
