package org.enshine.myshortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.enshine.myshortlink.project.dao.entity.LinkDO;
import org.enshine.myshortlink.project.dao.mapper.LinkMapper;
import org.enshine.myshortlink.project.service.ILinkService;
import org.springframework.stereotype.Service;

@Service
public class LinkServiceImpl extends ServiceImpl<LinkMapper, LinkDO> implements ILinkService {
}
