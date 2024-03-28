package org.enshine.myshortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.enshine.myshortlink.project.common.constant.RedisCacheConstant;
import org.enshine.myshortlink.project.common.convention.exception.ClientException;
import org.enshine.myshortlink.project.common.convention.exception.ServiceException;
import org.enshine.myshortlink.project.dao.entity.LinkDO;
import org.enshine.myshortlink.project.dao.mapper.LinkMapper;
import org.enshine.myshortlink.project.dto.req.LinkPageReqDTO;
import org.enshine.myshortlink.project.dto.req.LinkSaveReqDTO;
import org.enshine.myshortlink.project.dto.resp.LinkPageRespDTO;
import org.enshine.myshortlink.project.service.ILinkService;
import org.enshine.myshortlink.project.util.HashUtil;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import static org.enshine.myshortlink.project.common.enums.LinkErrorCodeEnum.LINK_CREATE_FAIL;
import static org.enshine.myshortlink.project.common.enums.LinkErrorCodeEnum.LINK_EXIST;

@Service
@RequiredArgsConstructor
public class LinkServiceImpl extends ServiceImpl<LinkMapper, LinkDO> implements ILinkService {
    private final RBloomFilter<String> saveLinkCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;

    @Override
    public void create(LinkSaveReqDTO requestParam) {
        String uri = generateUri(requestParam.getOriginUrl());
        LinkDO linkDO = BeanUtil.toBean(requestParam, LinkDO.class);
        linkDO.setShortUri(uri).setCreateType(0).setFullShortUrl(requestParam.getDomain() + "/" + uri);
        RLock lock = redissonClient.getLock(RedisCacheConstant.KEY_LINK_CREATE + uri);
        try {
            if (lock.tryLock()) {
                boolean save = true;
                try {
                    save = save(linkDO);
                } catch (Exception e) {
                    throw new ServiceException(LINK_EXIST);
                }
                if (!save) {
                    throw new ServiceException(LINK_CREATE_FAIL);
                }
                saveLinkCachePenetrationBloomFilter.add(uri);
            } else {
                throw new ClientException(LINK_EXIST);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IPage<LinkPageRespDTO> pageByGid(LinkPageReqDTO requestParam) {
        Page<LinkDO> resultPage = page(requestParam, Wrappers.lambdaQuery(LinkDO.class)
                .eq(LinkDO::getGid, requestParam.getGid())
                .eq(LinkDO::getDelFlag, 0)
                .orderByDesc(LinkDO::getCreateTime));
        return resultPage.convert(each -> BeanUtil.toBean(each, LinkPageRespDTO.class));
    }

    private String generateUri(String originUrl) {
        int retryCount = 0;
        while (retryCount < 10) {
            String uri = HashUtil.hashToBase62(originUrl);
            // 判断uri是否已存在
            if (!saveLinkCachePenetrationBloomFilter.contains(uri)) {
                // 不存在
                return uri;
            }
            // 加时间戳 降低生成uri的冲突概率
            originUrl += System.currentTimeMillis();
            retryCount++;
        }
        throw new ServiceException(LINK_CREATE_FAIL);
    }
}
