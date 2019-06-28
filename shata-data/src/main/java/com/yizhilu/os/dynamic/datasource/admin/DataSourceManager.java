package com.yizhilu.os.dynamic.datasource.admin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yizhilu.os.dynamic.cache.RedisCache;
import com.yizhilu.os.dynamic.constants.CacheConstants;
import com.yizhilu.os.dynamic.datasource.DynamicDataSource;
import com.yizhilu.os.dynamic.entity.Shop;
import com.yizhilu.os.dynamic.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yzl on 2017/4/8.
 */
public class DataSourceManager {

    private static Logger logger = LoggerFactory.getLogger(DataSourceManager.class);

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    private static RedisCache redis = RedisCache.getInstance();

    /**
     * 数据源Key
     */
    private static final ThreadLocal<Object> DATASOURCE_KEY = new ThreadLocal();

    /**
     * 多数据源装载容器
     */
    private static final Map<Object, Object> DATASOURCE = new ConcurrentHashMap<>();

    /**
     * 数据源模版
     */
    private static DataSource dataSourceModel;

    /**
     * 数据源修改地址、用户名及密码的方法
     */
    private static Map<String, Method> dataSourceMethod;

    /**
     * 动态数据源对象
     */
    private static DynamicDataSource dynamicDataSource;


    /**
     * 保存数据源模版
     *
     * @param dataSource
     * @throws Exception
     */
    private static void setDataSourceModel(DataSource dataSource) throws Exception {
        dataSourceModel = dataSource;
        Class<? extends DataSource> clazz = dataSource.getClass();
        Method setUrl, setUser, setPass;
        try {
            setUrl = clazz.getMethod("setUrl", String.class);
        } catch (NoSuchMethodException e) {
            setUrl = clazz.getMethod("setJdbcUrl", String.class);
        }
        try {
            setUser = clazz.getMethod("setUser", String.class);
        } catch (NoSuchMethodException e) {
            setUser = clazz.getMethod("setUsername", String.class);
        }
        setPass = clazz.getMethod("setPassword", String.class);
        dataSourceMethod = new HashMap<>();
        dataSourceMethod.put("setUrl", setUrl);
        dataSourceMethod.put("setUser", setUser);
        dataSourceMethod.put("setPass", setPass);
    }

    /**
     * 为当前线程添加数据源Key
     *
     * @param key
     */
    public static boolean setCurrentLookupKey(Object key) {
        Shop shop = getShopByDataSourceKey(key);
        updateDB(shop, key);
        if (ObjectUtils.isNull(shop)) {
            return false;
        }
        DATASOURCE_KEY.set(key);
        return true;
    }

    /**
     * 获取当前线程的数据源Key
     *
     * @return
     */
    public static Object getCurrentLookupKey() {
        return DATASOURCE_KEY.get();
    }

    /**
     * 清理线程局部变量
     */
    public static void clean() {
        DATASOURCE_KEY.remove();
    }

    /**
     * 初始化动态数据源对象
     */
    public static Map<Object, Object> initDB(DynamicDataSource dataSource) {
        try {
            dynamicDataSource = dataSource;
            setDataSourceModel(dataSource.getDataSourceModel());
            start();
        } catch (Exception e) {
            logger.error("初始化动态数据源失败", e);
            e.printStackTrace();
            System.exit(1);
        }
        return DATASOURCE;
    }

    /**
     * 启动加载程序
     */
    public static void start() {
        logger.info("获取多站点配置信息开始");
        Map<String, Shop> shopMap = getDataSources();
        load(shopMap);
    }

    /**
     * 加载 多站点配置信息
     *
     * @return
     */
    public static synchronized boolean load(Map<String, Shop> shopMap) {
        shopMap.values().forEach(e -> add(e));
        return true;
    }

    /***
     * 动态 配置 数据库参数 和 加载系统资源
     */
    public static synchronized boolean add(Shop shop) {
        //加 try catch 的原因是 不能因为某个 站点 的错误 配置信息, 影响到 其他的站点
        try {
            DATASOURCE.put(shop.getDbKey(), init(shop));
            return true;
        } catch (Exception e) {
            logger.error("配置 数据库参数 错误信息:", e);
        }
        logger.error("动态配置数据库参数错误 add()");
        return false;
    }

    /**
     * 配置数据库参数 和 加载系统资源
     */
    private static DataSource init(Shop shop) throws Exception {
        DataSource dataSource = cloneDataSource(dataSourceModel);
        dataSourceMethod.get("setUrl").invoke(dataSource, shop.getJdbcURL());
        dataSourceMethod.get("setUser").invoke(dataSource, shop.getUser());
        dataSourceMethod.get("setPass").invoke(dataSource, shop.getPass());
        return dataSource;
    }

    /**
     * 克隆数据源
     *
     * @param dataSource
     * @return
     */
    private static DataSource cloneDataSource(DataSource dataSource) {
        DataSource result = null;
        try {
            result = dataSource.getClass().newInstance();
            /**
             * 由于不同的数据源对象存在不同的不可复制属性，
             * 启动时报错的话，将相应的属性名添加到参数中即可
             */
            BeanUtils.copyProperties(dataSource, result, "logWriter", "loginTimeout", "maxIdle", "initializationFailFast", "jdbcConnectionTest");
        } catch (Exception e) {
            logger.error("克隆数据源错误", e);
        }
        return result;
    }

    /**
     * 动态修改数据源（开机、关机）
     *
     * @param shop
     */
    public static void updateDB(Shop shop, Object dbKey) {
        boolean flag = false;
        if (ObjectUtils.isNull(shop)) {
            flag = ObjectUtils.isNotNull(DATASOURCE.remove(dbKey));
        } else if (!DATASOURCE.containsKey(shop.getDbKey())) {
            logger.error("数据源更新开始---DATASOURCE not containsKey--" + shop.getDbKey());
            flag = add(shop);
            logger.error("数据源更新结束---flag" + flag);
        }
        logger.error("动态修改数据源---shop:" + ObjectUtils.isNotNull(shop) + ",flag:" + flag + ",dbKey:"+DATASOURCE.containsKey(dbKey));
        if (flag) {
            dynamicDataSource.afterPropertiesSet();
        }
    }

    /**
     * 通过dbKey获取shop（验证dbKey）
     *
     * @param dbKey 数据源key
     * @return Shop 店铺对象
     */
    private static Shop getShopByDataSourceKey(Object dbKey) {
        return (Shop) redis.regiontGet(CacheConstants.DATA_SOURCES_REGION, dbKey.toString());
    }

    /**
     * 获取数据源（项目启动）
     *
     * @return Map<String   ,       Shop>
     */
    private static Map<String, Shop> getDataSources() {
        return (Map<String, Shop>) redis.regionGetAll(CacheConstants.DATA_SOURCES_REGION);
    }
}
