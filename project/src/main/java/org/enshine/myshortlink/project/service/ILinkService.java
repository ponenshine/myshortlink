package org.enshine.myshortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.enshine.myshortlink.project.dao.entity.LinkDO;
import org.enshine.myshortlink.project.dto.req.LinkPageReqDTO;
import org.enshine.myshortlink.project.dto.req.LinkSaveReqDTO;
import org.enshine.myshortlink.project.dto.resp.LinkPageRespDTO;

public interface ILinkService extends IService<LinkDO> {
    void create(LinkSaveReqDTO requestParam);

    IPage<LinkPageRespDTO> pageByGid(LinkPageReqDTO requestParam);
}
