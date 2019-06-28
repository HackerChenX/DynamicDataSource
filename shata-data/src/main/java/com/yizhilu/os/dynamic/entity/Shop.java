package com.yizhilu.os.dynamic.entity;


/**
 * 店铺实体类
 * Created by BingSen.Wang on 2017/6/25.
 */
public class Shop {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 店铺名
     */
    private String shopName;
    /**
     * 数据源Key
     */
    private String dbKey;
    /**
     * 数据库连接地址
     */
    private String jdbcURL;
    /**
     * 数据库用户名
     */
    private String user;
    /**
     * 数据库密码
     */
    private String pass;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getDbKey() {
        return dbKey;
    }

    public void setDbKey(String dbKey) {
        this.dbKey = dbKey;
    }

    public String getJdbcURL() {
        return jdbcURL;
    }

    public void setJdbcURL(String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
