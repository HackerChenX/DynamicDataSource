package com.wuzai.os.dynamic.utils.redis;

import com.wuzai.os.dynamic.utils.ObjectUtils;
import com.wuzai.os.dynamic.utils.PropertyUtil;
import com.wuzai.os.dynamic.utils.SerializationUtils;
import com.wuzai.os.dynamic.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class RedisManager {
    private Logger logger = LoggerFactory.getLogger(RedisManager.class);
    /**
     * 读取配置文件
     */
    private static PropertyUtil propertyUtil = PropertyUtil.getInstance("config");

    private static String REDIS_REGION = "yzl_os_redis_px_";

    private String redisSwitch = "OFF";

    private String redisClusterSwitch = "OFF";

    private static JedisPool jedisPool = null;

    private static JedisCluster jedisCluster = null;

    /**
     * 初始化方法
     */
    public void init() {
        logger.debug("-------------------------init redis----------------------");
        try {
            redisSwitch = propertyUtil.getProperty("redis.switch");
        } catch (Exception e) {
            logger.debug("-----------------redis redis.switch is null,redis init fail,redis not open-------");
            return;
        }
        logger.debug("-------------------------redisSwitch:" + redisSwitch);
        //redis 开关配置不是NO则不进行初始化
        if (StringUtils.isEmpty(redisSwitch) || !redisSwitch.trim().equals("NO")) {
            logger.debug("-------------------------redis not init----------------------");
            return;
        }
        //集群标识符 NO开启 OFF关闭
        redisClusterSwitch = propertyUtil.getProperty("redis.cluster.switch");
        String password = propertyUtil.getProperty("redis.password");
        //timeout for jedis try to connect to redis server, not expire time! In milliseconds
        int timeout = Integer.parseInt(propertyUtil.getProperty("redis.timeout"));
        if (redisClusterSwitch.trim().equals("OFF")) {//非集群
            String host = propertyUtil.getProperty("redis.host");
            int port = Integer.parseInt(propertyUtil.getProperty("redis.port"));
            if (jedisPool == null) {
                if (password != null && !"".equals(password)) {
                    jedisPool = new JedisPool(new JedisPoolConfig(), host, port, timeout, password);
                    logger.debug("-------------------------init redis success----------------------");
                } else if (timeout != 0) {
                    jedisPool = new JedisPool(new JedisPoolConfig(), host, port, timeout);
                    logger.debug("-------------------------init redis success----------------------");
                } else {
                    jedisPool = new JedisPool(new JedisPoolConfig(), host, port);
                    logger.debug("-------------------------init redis success----------------------");
                }
            }
        } else if (redisClusterSwitch.trim().equals("NO")) {//集群
            String address = propertyUtil.getProperty("redis.address");
            if (StringUtils.isEmpty(address)) {
                logger.debug("-----------------redis redis.address is null,redis init fail,redis not open-------");
                return;
            }
            Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
            String[] hostAndPort = address.split(":");
            jedisClusterNodes.add(new HostAndPort(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
            jedisCluster = new JedisCluster(jedisClusterNodes, timeout, timeout, 5, password, new JedisPoolConfig());
        }
    }

    /**
     * 添加缓存
     *
     * @param key     缓存中的Key
     * @param value   要缓存的数据对象
     * @param seconds 过时间（单位/秒）
     */
    public void set(String key, Object value, int seconds) {
        this._set(null, key, value, seconds);
    }

    /**
     * 通过Key获取缓存中的Value
     *
     * @param key 缓存中存在的Key
     * @return 返回缓存中的数据对象
     */
    public Object get(String key) {
        return this._get(null, key);
    }

    /**
     * 通过Key删除缓存记录
     *
     * @param key 缓存存在的Key
     */
    public void remove(String key) {
        this._remove(null, key);
    }

    /**
     * 把数据缓存到指定的区域中
     *
     * @param key   缓存中的Key
     * @param value 要缓存的数据对象
     */
    public void regionSet(String key, Object value) {
        this._set(REDIS_REGION, key, value, 0);
    }

    /**
     * 获取指定区域中的Key的Value
     *
     * @param key 缓存中存在的Key
     * @return 返回指定区域中的Key的Value
     */
    public Object regiontGet(String key) {
        return this._get(REDIS_REGION, key);
    }

    /**
     * 删除指定区域中的Key的缓存
     *
     * @param key 缓存存在的Key
     */
    public void regiontRemove(String key) {
        this._remove(REDIS_REGION, key);
    }

    /**
     * 把数据缓存到指定的区域中
     *
     * @param region 指定区域关键字
     * @param key    缓存中的Key
     * @param value  要缓存的数据对象
     */
    public void regionSet(String region, String key, Object value) {
        this._set(region, key, value, 0);
    }

    /**
     * 获取指定区域中的Key的Value
     *
     * @param region 指定区域关键字
     * @param key    缓存中存在的Key
     * @return 返回指定区域中的Key的Value
     */
    public Object regiontGet(String region, String key) {
        return this._get(region, key);
    }

    /**
     * 删除指定区域中的Key的缓存
     *
     * @param region 指定区域关键字
     * @param key    缓存存在的Key
     */
    public void regiontRemove(String region, String key) {
        this._remove(region, key);
    }

    /**
     * 批量删除指定区域中的Key的缓存
     *
     * @param region 指定区域关键字
     * @param keys   缓存存在的Key集合
     */
    public void regiontRemoveBatch(String region, Collection<String> keys) {
        this._remove(region, keys);
    }

    /**
     * 批量添加缓存到指定区域
     *
     * @param region 指定区域关键字
     * @param map    要缓存的数据对象集合
     */
    public void regionSetBatch(String region, Map<String, ? extends Object> map) {
        try {
            Jedis cache = null;
            if (ObjectUtils.isNotNull(jedisPool)) {
                cache = jedisPool.getResource();
            }
            Throwable localThrowable2 = null;
            try {
                Map<byte[], byte[]> data = new HashMap<>();
                map.keySet().forEach(e -> {
                    try {
                        data.put(e.getBytes(), SerializationUtils.serialize(map.get(e)));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                });
                if (ObjectUtils.isNotNull(cache)) {
                    cache.hmset(region.getBytes(), data);
                } else if (ObjectUtils.isNotNull(jedisCluster)) {
                    jedisCluster.hmset(region.getBytes(), data);
                }
            } catch (Throwable localThrowable1) {
                localThrowable2 = localThrowable1;
                throw localThrowable1;
            } finally {
                if (ObjectUtils.isNotNull(cache)) {
                    finallyJedis(cache, localThrowable2);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取指定区域中的全部缓存
     *
     * @param region 指定区域关键字
     * @return Map<String   ,       Object> 区域中的数据对象集合
     */
    public Map<String, ? extends Object> regionGetAll(String region) {
        Map<String, ? extends Object> result = null;
        if (region == null) {
            return null;
        }
        try {
            Jedis cache = null;
            if (ObjectUtils.isNotNull(jedisPool)) {
                cache = jedisPool.getResource();
            }
            Throwable localThrowable2 = null;
            try {
                Map<byte[], byte[]> map = null;
                if (ObjectUtils.isNotNull(cache)) {
                    map = cache.hgetAll(region.getBytes());
                } else if (ObjectUtils.isNotNull(jedisCluster)) {
                    map = jedisCluster.hgetAll(region.getBytes());
                }
                if (ObjectUtils.isNotNull(map)) {
                    result = map.entrySet().stream().collect(Collectors.toMap(e -> new String(e.getKey()), e -> {
                        try {
                            return SerializationUtils.deserialize(e.getValue());
                        } catch (IOException e1) {
                            throw new RuntimeException(e1);
                        }
                    }));
                }
            } catch (Throwable localThrowable1) {
                localThrowable2 = localThrowable1;
                throw localThrowable1;
            } finally {
                if (ObjectUtils.isNotNull(cache)) {
                    finallyJedis(cache, localThrowable2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this._remove(null, region);
            return null;
        }
        return result;
    }

    /**
     * 批量删除缓存
     *
     * @param pattern 要删除的key前缀加 *
     */
    public void removeBatch(String pattern) {
        try {
            Jedis cache = null;
            if (ObjectUtils.isNotNull(jedisPool)) {
                cache = jedisPool.getResource();
            }
            Throwable localThrowable2 = null;

            HashSet<byte[]> keys = new HashSet<>();
            try {
                Set<byte[]> keySet = null;
                if (ObjectUtils.isNotNull(cache)) {
                    keySet = cache.keys(pattern.getBytes());
                    keys.addAll(keySet);
                } else if (ObjectUtils.isNotNull(jedisCluster)) {
                    keySet = this.jedisClusterKeys(pattern.getBytes());
                }
                if (keySet.size() > 0) {
                    if (ObjectUtils.isNotNull(cache)) {
                        cache.del(keySet.toArray(new byte[keySet.size()][]));
                    } else if (ObjectUtils.isNotNull(jedisCluster)) {
                        jedisCluster.del(keySet.toArray(new byte[keySet.size()][]));
                    }
                }
            } catch (Throwable localThrowable1) {
                localThrowable2 = localThrowable1;
                throw localThrowable1;
            } finally {
                if (ObjectUtils.isNotNull(cache)) {
                    finallyJedis(cache, localThrowable2);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加有序集合
     *
     * @param key
     * @param score
     * @param member
     */
    public void zadd(String key, double score, String member) {
        try {
            Jedis cache = null;
            if (ObjectUtils.isNotNull(jedisPool)) {
                cache = jedisPool.getResource();
            }
            Throwable localThrowable2 = null;
            try {
                if (ObjectUtils.isNotNull(cache)) {
                    cache.zadd(key, score, member);
                } else if (ObjectUtils.isNotNull(jedisCluster)) {
                    jedisCluster.zadd(key, score, member);
                }
            } catch (Throwable localThrowable1) {
                localThrowable2 = localThrowable1;
                throw localThrowable1;
            } finally {
                if (ObjectUtils.isNotNull(cache)) {
                    finallyJedis(cache, localThrowable2);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回有序集合 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        Jedis cache = null;
        if (ObjectUtils.isNotNull(jedisPool)) {
            cache = jedisPool.getResource();
        }
        Throwable localThrowable2 = null;
        Set<String> range = null;
        try {
            if (ObjectUtils.isNotNull(cache)) {
                range = cache.zrangeByScore(key, min, max, offset, count);
            } else if (ObjectUtils.isNotNull(jedisCluster)) {
                range = jedisCluster.zrangeByScore(key, min, max, offset, count);
            }
        } catch (Throwable localThrowable1) {
            localThrowable2 = localThrowable1;
            throw localThrowable1;
        } finally {
            if (ObjectUtils.isNotNull(cache)) {
                finallyJedis(cache, localThrowable2);
            }
        }
        return range;
    }

    /**
     * 移除有序集合中的一个或多个成员
     * @param key
     * @param var
     * @return
     */
    public Long zrem(String key, String... var){
        Jedis cache = null;
        if (ObjectUtils.isNotNull(jedisPool)) {
            cache = jedisPool.getResource();
        }
        Throwable localThrowable2 = null;
        Long result = null;
        try {
            if (ObjectUtils.isNotNull(cache)) {
                result = cache.zrem(key,var);
            } else if (ObjectUtils.isNotNull(jedisCluster)) {
                result = jedisCluster.zrem(key, var);
            }
        } catch (Throwable localThrowable1) {
            localThrowable2 = localThrowable1;
            throw localThrowable1;
        } finally {
            if (ObjectUtils.isNotNull(cache)) {
                finallyJedis(cache, localThrowable2);
            }
        }
        return result;
    }

    /**
     * 指定键的剩余过期时间 （单位：毫秒）
     * @param key
     * @return
     */
    public Long pttl(String key) {
        try {
            Jedis cache = null;
            if (ObjectUtils.isNotNull(jedisPool)) {
                cache = jedisPool.getResource();
            }
            Throwable localThrowable2 = null;
            try {
                if (ObjectUtils.isNotNull(cache)) {
                    return cache.pttl(key);
                } else if (ObjectUtils.isNotNull(jedisCluster)) {
                    return jedisCluster.pttl(key);
                }
            } catch (Throwable localThrowable1) {
                localThrowable2 = localThrowable1;
                throw localThrowable1;
            } finally {
                if (ObjectUtils.isNotNull(cache)) {
                    finallyJedis(cache, localThrowable2);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0L;
    }


    //===============================================================

    /**
     * 添加缓存
     *
     * @param region  指定区域关键字
     * @param key     缓存标识Key
     * @param value   要缓存的数据对象
     * @param seconds (当region为null时，seconds要大于0，region不为空时seconds不起作用)
     */
    private void _set(String region, String key, Object value, int seconds) throws RuntimeException {
        try {
            Jedis cache = null;
            if (ObjectUtils.isNotNull(jedisPool)) {
                cache = jedisPool.getResource();
            }
            Throwable localThrowable2 = null;
            try {
                if (region == null || region.trim().length() <= 0) {
                    if (ObjectUtils.isNotNull(cache)) {
                        cache.setex(key.getBytes(), seconds, SerializationUtils.serialize(value));
                    } else if (ObjectUtils.isNotNull(jedisCluster)) {
                        jedisCluster.setex(key.getBytes(), seconds, SerializationUtils.serialize(value));
                    }
                } else {
                    if (ObjectUtils.isNotNull(cache)) {
                        cache.hset(region.getBytes(), key.getBytes(), SerializationUtils.serialize(value));
                    } else if (ObjectUtils.isNotNull(jedisCluster)) {
                        jedisCluster.hset(region.getBytes(), key.getBytes(), SerializationUtils.serialize(value));
                    }
                }
            } catch (Throwable localThrowable1) {
                localThrowable2 = localThrowable1;
                throw localThrowable1;
            } finally {
                if (ObjectUtils.isNotNull(cache)) {
                    finallyJedis(cache, localThrowable2);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取缓存方法
     *
     * @param region 指定区域关键字
     * @param key    缓存中存在的Key
     * @return 返回缓存中的数据对象
     */
    private Object _get(String region, String key) throws RuntimeException {
        Object obj = null;
        if (key == null) {
            return null;
        }
        try {
            Jedis cache = null;
            if (ObjectUtils.isNotNull(jedisPool)) {
                cache = jedisPool.getResource();
            }
            Throwable localThrowable2 = null;
            try {
                byte[] b = null;
                if (region == null || region.trim().length() <= 0) {
                    if (ObjectUtils.isNotNull(cache)) {
                        b = cache.get(key.getBytes());
                    } else if (ObjectUtils.isNotNull(jedisCluster)) {
                        b = jedisCluster.get(key.getBytes());
                    }
                    obj = SerializationUtils.deserialize(b);
                } else {
                    if (ObjectUtils.isNotNull(cache)) {
                        b = cache.hget(region.getBytes(), key.getBytes());
                    } else if (ObjectUtils.isNotNull(jedisCluster)) {
                        b = jedisCluster.hget(region.getBytes(), key.getBytes());
                    }
                    obj = SerializationUtils.deserialize(b);
                }
            } catch (Throwable localThrowable1) {
                localThrowable2 = localThrowable1;
                throw localThrowable1;
            } finally {
                if (ObjectUtils.isNotNull(cache)) {
                    finallyJedis(cache, localThrowable2);
                }
            }
        } catch (Exception e) {
            this._remove(region, key);
            return null;
        }
        return obj;
    }

    /**
     * 删除缓存
     *
     * @param region 指定区域的关键字
     * @param key    缓存中存在的Key
     * @throws RuntimeException
     */
    private void _remove(String region, Object key) throws RuntimeException {
        if (key == null) {
            return;
        }
        if (key instanceof Collection) {
            @SuppressWarnings("unchecked")
            Collection<Object> keys = (Collection<Object>) key;
            try {
                Jedis cache = null;
                if (ObjectUtils.isNotNull(jedisPool)) {
                    cache = jedisPool.getResource();
                }
                Throwable localThrowable3 = null;
                try {
                    byte[][] okeys = keys.stream().map(e -> {
                        try {
                            return SerializationUtils.serialize(e);
                        } catch (IOException e1) {
                            throw new RuntimeException(e1);
                        }
                    }).toArray(byte[][]::new);
                    if (region == null) {
                        if (ObjectUtils.isNotNull(cache)) {
                            cache.del(okeys);
                        } else if (ObjectUtils.isNotNull(jedisCluster)) {
                            jedisCluster.del(okeys);
                        }
                    } else {
                        if (ObjectUtils.isNotNull(cache)) {
                            cache.hdel(region.getBytes(), okeys);
                        } else if (ObjectUtils.isNotNull(jedisCluster)) {
                            jedisCluster.hdel(region.getBytes(), okeys);
                        }
                    }
                } catch (Throwable localThrowable1) {
                    localThrowable3 = localThrowable1;
                    throw localThrowable1;
                } finally {
                    if (ObjectUtils.isNotNull(cache)) {
                        finallyJedis(cache, localThrowable3);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Jedis cache = null;
                if (ObjectUtils.isNotNull(jedisPool)) {
                    cache = jedisPool.getResource();
                }
                Throwable e = null;
                try {
                    if (region == null) {
                        if (ObjectUtils.isNotNull(cache)) {
                            cache.del(key.toString().getBytes());
                        } else if (ObjectUtils.isNotNull(jedisCluster)) {
                            jedisCluster.del(key.toString().getBytes());
                        }
                    } else {
                        if (ObjectUtils.isNotNull(cache)) {
                            cache.hdel(region.getBytes(), key.toString().getBytes());
                        } else if (ObjectUtils.isNotNull(jedisCluster)) {
                            jedisCluster.hdel(region.getBytes(), key.toString().getBytes());
                        }
                    }
                } catch (Throwable localThrowable4) {
                    e = localThrowable4;
                    throw localThrowable4;
                } finally {
                    if (ObjectUtils.isNotNull(cache)) {
                        finallyJedis(cache, e);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void finallyJedis(Jedis cache, Throwable localThrowable3) {
        if (cache != null) {
            if (localThrowable3 != null) {
                try {
                    cache.close();
                } catch (Throwable x2) {
                    localThrowable3.addSuppressed(x2);
                }
            } else {
                cache.close();
            }
        }
    }

    private HashSet<byte[]> jedisClusterKeys(byte[] pattern) {
        HashSet<byte[]> keys = new HashSet<>();
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
        for (String k : clusterNodes.keySet()) {
            JedisPool jp = clusterNodes.get(k);
            Jedis connection = jp.getResource();
            try {
                keys.addAll(connection.keys(pattern));
            } catch (Exception e) {
                logger.error("Getting keys error: {}", e);
            } finally {
                connection.close();
            }
        }
        return keys;
    }


}
