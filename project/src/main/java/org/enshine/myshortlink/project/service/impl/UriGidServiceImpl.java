package org.enshine.myshortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.enshine.myshortlink.project.dao.entity.UriGidDO;
import org.enshine.myshortlink.project.dao.mapper.UriGidMapper;
import org.enshine.myshortlink.project.service.IUriGidService;
import org.springframework.stereotype.Service;

@Service
public class UriGidServiceImpl extends ServiceImpl<UriGidMapper, UriGidDO> implements IUriGidService {
}
