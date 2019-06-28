package com.yizhilu.os.dynamic.cache;


import com.yizhilu.os.dynamic.datasource.admin.DataSourceManager;
import com.yizhilu.os.dynamic.utils.redis.RedisManager;

/**
 * 动态数据源环境下的redis工具
 * 自动在key上拼接数据源key，区分对应不同数据源的缓存
 * Created by BingSen.Wang on 2017/8/7.
 */
public class RedisCache extends RedisManager {

    private RedisCache() {
        super.init();
    }

    public static RedisCache redisCache = new RedisCache();

    public static RedisCache getInstance() {
        return redisCache;
    }

    @Override
    public void set(String key, Object value, int seconds) {
        super.set(DataSourceManager.getCurrentLookupKey() + "_" + key, value, seconds);
    }

    /**
     * 如果key包含{}，就会使用第一个{}内部的字符串作为hash key，这样就可以保证拥有同样{}内部字符串的key就会拥有相同slot。
     * 适用于集群下批量操作keys
     *
     * @param key
     * @param value
     * @param seconds
     */
    public void slotSet(String key, Object value, int seconds) {
        super.set(DataSourceManager.getCurrentLookupKey() + "_" + key , value, seconds);
    }

    public Object slotGet(String key) {
        return super.get(DataSourceManager.getCurrentLookupKey() + "_" + key + "");
    }

    @Override
    public Object get(String key) {
        return super.get(DataSourceManager.getCurrentLookupKey() + "_" + key);
    }

    @Override
    public void regionSet(String key, Object value) {
        super.regionSet(DataSourceManager.getCurrentLookupKey() + "_" + key, value);
    }

    @Override
    public Object regiontGet(String key) {
        return super.regiontGet(DataSourceManager.getCurrentLookupKey() + "_" + key);
    }

    @Override
    public void remove(String key) {
        super.remove(DataSourceManager.getCurrentLookupKey() + "_" + key);
    }

    @Override
    public void removeBatch(String pattern) {
        super.removeBatch(DataSourceManager.getCurrentLookupKey() + "_" + pattern);
    }

    public void slotRemoveBatch(String pattern) {
        super.removeBatch( DataSourceManager.getCurrentLookupKey() + "_" +pattern  + "*");
    }

    @Override
    public void regiontRemove(String key) {
        super.regiontRemove(DataSourceManager.getCurrentLookupKey() + "_" + key);
    }
}
