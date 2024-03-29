package org.enshine.myshortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.enshine.myshortlink.project.common.constant.RedisCacheConstant;
import org.enshine.myshortlink.project.common.constant.StringConstant;
import org.enshine.myshortlink.project.common.convention.exception.ClientException;
import org.enshine.myshortlink.project.common.convention.exception.ServiceException;
import org.enshine.myshortlink.project.dao.entity.LinkDO;
import org.enshine.myshortlink.project.dao.entity.UriGidDO;
import org.enshine.myshortlink.project.dao.mapper.LinkMapper;
import org.enshine.myshortlink.project.dto.req.LinkPageReqDTO;
import org.enshine.myshortlink.project.dto.req.LinkSaveReqDTO;
import org.enshine.myshortlink.project.dto.resp.LinkPageRespDTO;
import org.enshine.myshortlink.project.service.ILinkService;
import org.enshine.myshortlink.project.service.IUriGidService;
import org.enshine.myshortlink.project.util.HashUtil;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.enshine.myshortlink.project.common.enums.LinkErrorCodeEnum.LINK_CREATE_FAIL;
import static org.enshine.myshortlink.project.common.enums.LinkErrorCodeEnum.LINK_EXIST;

@Service
@Slf4j
@RequiredArgsConstructor
public class LinkServiceImpl extends ServiceImpl<LinkMapper, LinkDO> implements ILinkService {
    private final RBloomFilter<String> saveLinkCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final IUriGidService uriGidService;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(LinkSaveReqDTO requestParam) {
        String uri = generateUri(requestParam.getOriginUrl());
        LinkDO linkDO = BeanUtil.toBean(requestParam, LinkDO.class);
        linkDO.setShortUri(uri).setCreateType(0).setFullShortUrl(requestParam.getDomain() + "/" + uri);
        RLock lock = redissonClient.getLock(RedisCacheConstant.KEY_LINK_CREATE + uri);
        try {
            if (lock.tryLock()) {
                boolean save;
                try {
                    save = save(linkDO);
                } catch (Exception e) {
                    throw new ServiceException(LINK_EXIST);
                }
                if (!save) {
                    throw new ServiceException(LINK_CREATE_FAIL);
                }
                uriGidService.save(new UriGidDO().setUri(uri).setGid(linkDO.getGid()));
                saveLinkCachePenetrationBloomFilter.add(uri);
                setUriCache(uri, linkDO);
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

    @Override
    @SneakyThrows
    public void redirect(String uri, HttpServletRequest request, HttpServletResponse response) {
        // t_link表 通过uri查询originUrl
        // 但t_link表的分片键为gid 通过uri查询会走全表扫描 查询效率极低
        // 故创建映射表存储相关信息
        // 映射表有两种选择
        // 1.uri和originUrl 2.uri和gid
        // 由于uri是全局唯一的，对于方法1，确实可以做到一定程度的查询效率提升，但是这样做不利于表的维护
        // 如果在t_link表中修改了originUrl的信息，那么还需要去映射表中再修改一次信息，降低了修改效率
        // 而采用方法2，先查询对应的gid，再从t_link表中根据gid查询originUrl，这种做法只需修改一次表
        // 提升了修改效率。而且如果后续要根据uri再t_link表的查询其它信息，采用方法2是不错的选择
        // 总之，方法2可扩展性更好，而且它的查询效率也不低
// v1
//        UriGidDO uriGidDO = uriGidService.getOne(Wrappers.lambdaQuery(UriGidDO.class).eq(UriGidDO::getUri, uri));
//        LinkDO linkDO = getOne(Wrappers.lambdaQuery(LinkDO.class)
//                .eq(LinkDO::getGid, uriGidDO.getGid()).eq(LinkDO::getShortUri, uri));
//        if(linkDO.getValidDateType()==0||linkDO.getValidDateType()==1&&linkDO.getValidDate().after(new Date())){
//            // 长期有效或存在有效期并且有效期未过
//            response.sendRedirect(linkDO.getOriginUrl());
//            return;
//        }
        // 跳转 not found 页面


        // 但是如果请求量巨大 频繁的查询使MySQL压力剧增 严重的还会导致数据库阻塞 对性能消耗巨大
        // 故使用redis存储uri-originUrl key-value缓存 降低数据库压力
        // 注意删除短链接时也要对应删除redis的key
// v2
//        String originUrl = stringRedisTemplate.opsForValue().get(RedisCacheConstant.KEY_URI + uri);
//        if (!StringUtil.isBlank(originUrl)) {
//            response.sendRedirect(originUrl);
//            return;
//        }
//        UriGidDO uriGidDO = uriGidService.getOne(Wrappers.lambdaQuery(UriGidDO.class).eq(UriGidDO::getUri, uri));
//        LinkDO linkDO = getOne(Wrappers.lambdaQuery(LinkDO.class)
//                .eq(LinkDO::getGid, uriGidDO.getGid()).eq(LinkDO::getShortUri, uri));
//        if(linkDO.getValidDateType()==0||linkDO.getValidDateType()==1&&linkDO.getValidDate().after(new Date())){
//            // 长期有效或存在有效期并且有效期未过
//            setUriCache(uri,linkDO);
//            response.sendRedirect(linkDO.getOriginUrl());
//            return;
//        }
//        // 跳转 not found 页面

        // 采用双检加锁优化
//        String originUrl = stringRedisTemplate.opsForValue().get(RedisCacheConstant.KEY_URI + uri);
//        if (!StringUtil.isBlank(originUrl)) {
//            response.sendRedirect(originUrl);
//            return;
//        }
//        RLock lock = redissonClient.getLock(RedisCacheConstant.LOCK_URI + uri);
//        lock.lock(RedisCacheConstant.LOCK_WAIT_SECONDS, TimeUnit.SECONDS);
//        try{
//            originUrl = stringRedisTemplate.opsForValue().get(RedisCacheConstant.KEY_URI + uri);
//            if (!StringUtil.isBlank(originUrl)) {
//                response.sendRedirect(originUrl);
//                return;
//            }
//            UriGidDO uriGidDO = uriGidService.getOne(Wrappers.lambdaQuery(UriGidDO.class).eq(UriGidDO::getUri, uri));
//            LinkDO linkDO = getOne(Wrappers.lambdaQuery(LinkDO.class)
//                    .eq(LinkDO::getGid, uriGidDO.getGid()).eq(LinkDO::getShortUri, uri));
//            if(linkDO.getValidDateType()==0||linkDO.getValidDateType()==1&&linkDO.getValidDate().after(new Date())){
//                // 长期有效或存在有效期并且有效期未过
//                setUriCache(uri,linkDO);
//                response.sendRedirect(linkDO.getOriginUrl());
//                return;
//            }
//            // 跳转 not found 页面
//
//        }catch (Exception e){
//            // 跳转错误页面 提示刷新
//        }finally {
//            lock.unlock();
//        }


        // 现在已经解决了大量查询已存在的uri对应的originUrl的问题
        // 但是如果有大量恶意请求不存在的uri这个问题还要解决
        // 可以将数据库中已存在的uri加入布隆过滤器
        // 确定存在的就进行下一步的查询逻辑判断是否真的存在（布隆过滤器误判）
        // 如果在布隆过滤器中判断不存在，那么该uri就一定不存在
        // 但是又出现了一个问题：如果大量短链接被删除或者过期(大量短链接失效)，那该如何同步到布隆过滤器
        // 布隆过滤器的同步可以在凌晨执行定时任务 或者再使用一个布隆过滤器记录被删除或过期的uri来辅助判断
        // 这里采用记录空记录的key值办法 假设布隆过滤器误判了已失效的记录 则先从DB中查询是否真的有该记录
        // 如果没有 则加入空记录缓存
        // 但对于大量的不同uri的请求 只能采取限流的方式来降低服务器压力 redis也爱莫能助

        // 1.查询缓存
        String originUrl = stringRedisTemplate.opsForValue().get(RedisCacheConstant.KEY_URI + uri);
        if (StringUtil.isNotBlank(originUrl)) {
            response.sendRedirect(originUrl);
            return;
        }
        // 2.查询布隆过滤器
        boolean hasUri = saveLinkCachePenetrationBloomFilter.contains(uri);
        if (!hasUri) {
            response.sendRedirect(StringConstant.PAGE_NOT_FOUND);
            return;
        }
        // 3.查询空记录key
        String nullUriValue = stringRedisTemplate.opsForValue().get(RedisCacheConstant.KEY_NULL_URI + uri);
        if (StringUtil.isNotBlank(nullUriValue)) {
            response.sendRedirect(StringConstant.PAGE_NOT_FOUND);
            return;
        }
        // 4.查询数据库 双检加锁机制优化
        RLock lock = redissonClient.getLock(RedisCacheConstant.LOCK_URI + uri);
        lock.lock(RedisCacheConstant.LOCK_WAIT_SECONDS, TimeUnit.SECONDS);
        try {
            // 4.1 再次检查缓存查看是否存在key
            // 查询是否uri存在
            originUrl = stringRedisTemplate.opsForValue().get(RedisCacheConstant.KEY_URI + uri);
            if (StringUtil.isNotBlank(originUrl)) {
                response.sendRedirect(originUrl);
                return;
            }
            // 查询空记录uri是否存在
            nullUriValue = stringRedisTemplate.opsForValue().get(RedisCacheConstant.KEY_NULL_URI + uri);
            if (StringUtil.isNotBlank(nullUriValue)) {
                response.sendRedirect(StringConstant.PAGE_NOT_FOUND);
                return;
            }

            // 4.2 查询DB看是否存在对应的uri
            // 获取gid
            UriGidDO uriGidDO = uriGidService.getOne(Wrappers.lambdaQuery(UriGidDO.class).eq(UriGidDO::getUri, uri));
            if (uriGidDO == null) {
                // 不存在的uri 加入空记录缓存
                stringRedisTemplate.opsForValue().set(RedisCacheConstant.KEY_NULL_URI + uri, "-");
                response.sendRedirect(StringConstant.PAGE_NOT_FOUND);
                return;
            }
            // 通过gid和uri查询originUrl
            LinkDO linkDO = getOne(
                    Wrappers.lambdaQuery(LinkDO.class)
                            .eq(LinkDO::getGid, uriGidDO.getGid())
                            .eq(LinkDO::getShortUri, uri)
                            .eq(LinkDO::getEnableStatus, 0)
                            .eq(LinkDO::getDelFlag, 0)
            );
            if (linkDO == null) {
                // 不存在的uri 加入空记录缓存
                stringRedisTemplate.opsForValue().set(RedisCacheConstant.KEY_NULL_URI + uri, "-");
                response.sendRedirect(StringConstant.PAGE_NOT_FOUND);
                return;
            }
            if (linkDO.getValidDateType() == 0 || linkDO.getValidDateType() == 1 && linkDO.getValidDate().after(new Date())) {
                // uri长期有效或未过期
                setUriCache(uri, linkDO);
                response.sendRedirect(originUrl);
                // 存在一种情况：uri对应的短链接之前被设置为未启用，但是这段时间内有人访问了对应的短链接，那么空记录uri会被保存
                // 如果后来该短链接又被启用 那该启用是无效的 因为空记录uri已经把它过滤掉了 不会访问到originUrl页面
                // 所以在启用短链接的时候一定要把空记录uri移除
                // 或者说从回收站撤销的短链接也要及时清楚空记录uri
                return;
            }
            // 不存在的uri 加入空记录缓存
            stringRedisTemplate.opsForValue().set(RedisCacheConstant.KEY_NULL_URI + uri, "-");
            response.sendRedirect(StringConstant.PAGE_NOT_FOUND);
        } catch (Exception e) {
            log.error(e.getMessage());
            response.sendRedirect(StringConstant.PAGE_ERROR);
        } finally {
            lock.unlock();
        }
    }

    private void setUriCache(String uri, LinkDO linkDO) {
        stringRedisTemplate.opsForValue().set(RedisCacheConstant.KEY_URI + uri, linkDO.getOriginUrl());
        // 设置过期时间
        if (linkDO.getValidDateType() == 1) {
            stringRedisTemplate.expire(
                    RedisCacheConstant.KEY_URI + uri,
                    DateUtil.between(new Date(), linkDO.getValidDate(), DateUnit.SECOND),
                    TimeUnit.SECONDS
            );
        } else {
            // 默认一个月过期
            stringRedisTemplate.expire(
                    RedisCacheConstant.KEY_URI + uri,
                    30,
                    TimeUnit.DAYS
            );
        }
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
