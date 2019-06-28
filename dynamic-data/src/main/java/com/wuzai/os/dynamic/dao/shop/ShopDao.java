package com.wuzai.os.dynamic.dao.shop;


import com.wuzai.os.dynamic.entity.Shop;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 店铺 DAO
 * Created by BingSen.Wang on 2017/6/25.
 */
public interface ShopDao  {


    /**
     * 根据自定义sql查询店铺信息
     * @param whereSql
     * @return
     */
    @Select("SELECT * FROM `shop` WHERE ${whereSql}")
    List<Shop> findShopBySql(@Param("whereSql")String whereSql);
}
