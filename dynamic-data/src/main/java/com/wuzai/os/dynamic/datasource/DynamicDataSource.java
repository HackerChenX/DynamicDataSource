package com.wuzai.os.dynamic.datasource;

import com.wuzai.os.dynamic.datasource.admin.DataSourceManager;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;

/**
 * 动态数据源对象，spring配置文件中数据源使用本类
 * Created by BingSen.Wang on 2017/4/7.
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    /**
     * 数据源模版
     */
    private DataSource dataSourceModel;

    /**
     * 构造方法
     *
     * @param defaultDataSource
     */
    public DynamicDataSource(DataSource defaultDataSource) {
        this.setDataSourceModel(defaultDataSource);
        this.initDataSource();
    }

    /**
     * 初始化动态数据源
     */
    private void initDataSource() {
        this.setTargetDataSources(DataSourceManager.initDB(this));
        this.setDefaultTargetDataSource(dataSourceModel);
        this.setLenientFallback(false);
    }

    /**
     * 实现父类抽象方法
     * 获取数据源Key
     *
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceManager.getCurrentLookupKey();
    }

    /**
     * 获取数据源模版
     *
     * @return
     */
    public DataSource getDataSourceModel() {
        return dataSourceModel;
    }

    /**
     * 保存一个数据源模版
     *
     * @param defaultDataSource
     */
    private void setDataSourceModel(DataSource defaultDataSource) {
        try {
            Class<? extends DataSource> clazz = defaultDataSource.getClass();
            this.dataSourceModel = clazz.newInstance();
            BeanUtils.copyProperties(defaultDataSource, this.dataSourceModel);
        } catch (Exception e) {
            logger.error("拷贝数据源模版错误", e);
        }
    }
}
