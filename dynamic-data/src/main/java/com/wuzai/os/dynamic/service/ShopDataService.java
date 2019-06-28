package com.wuzai.os.dynamic.service;

import com.wuzai.os.dynamic.cache.RedisCache;
import com.wuzai.os.dynamic.constants.CacheConstants;
import com.wuzai.os.dynamic.dao.shop.ShopDao;
import com.wuzai.os.dynamic.entity.Shop;
import com.wuzai.os.dynamic.utils.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 数据源服务层
 */
@Service
public class ShopDataService {

    private static final RedisCache redis = RedisCache.getInstance();
    private static ScheduledExecutorService timerService = Executors.newSingleThreadScheduledExecutor();

    @Resource
    private ShopDao shopDao;
    @PostConstruct
    public void init() {
        timerService.scheduleAtFixedRate(() -> this.loadShopDataSources(), 0, 10, TimeUnit.MINUTES);
    }

    public Map<String, Shop> loadShopDataSources() {
        List<Shop> shopList = shopDao.findShopBySql( "`status`=" + 1);

        Map<String, Shop> shopMap = shopList.stream().collect(Collectors.toMap(Shop::getDbKey, e -> e));
        Map<String, Shop> inCache = (Map<String, Shop>) redis.regionGetAll(CacheConstants.DATA_SOURCES_REGION);
        if (shopMap.equals(inCache)) {
            return shopMap;
        }
        redis.regionSetBatch(CacheConstants.DATA_SOURCES_REGION, shopMap);
        if (ObjectUtils.isNotNull(inCache)) {
            Set<String> keySet = inCache.keySet();
            keySet.removeAll(shopMap.keySet());
            redis.regiontRemoveBatch(CacheConstants.DATA_SOURCES_REGION, keySet);
        }
        return shopMap;
    }
}
