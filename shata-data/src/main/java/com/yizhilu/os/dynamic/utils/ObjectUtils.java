package com.yizhilu.os.dynamic.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 对象工具类
 * @author s.li
 * @create 2016-12-15-9:51
 */
public class ObjectUtils {
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    /**
     * 判断对象是否非空
     *
     * @param o
     * @return
     */
    public static boolean isNull(Object o) {
        return null == o;
    }

    /**
     * 判断集合是否非空
     *
     * @param list
     * @return
     */
    public static boolean isNull(List<?> list) {

        return null == list || list.size() == 0;
    }

    /**
     * 判断集合是否非空
     *
     * @param set
     * @return
     */
    public static boolean isNull(Set<?> set) {
        return null == set || set.size() == 0;
    }

    /**
     * 判断集合是否为空
     *
     * @param map
     * @return
     */
    public static boolean isNull(Map<?, ?> map) {
        return null == map || map.size() == 0;
    }

    /**
     * 判断BigDecimal是否为空
     * @param bg
     * @return
     */
    public static boolean isNull(BigDecimal bg) {
        return null == bg || BigDecimal.ZERO.compareTo(bg) == 0;
    }

    /**
     * 判断Long是否为空
     *
     * @param lg
     * @return
     */
    public static boolean isNull(Long lg) {
        return null == lg || lg == 0;
    }

    /**
     * 判断Integer是否为空
     *
     * @param it
     * @return
     */
    public static boolean isNull(Integer it) {
        return null == it || it == 0;
    }

    /**
     * 判断Double是否为空
     * @param db
     * @return
     */
    public static boolean isNull(Double db) {
        return null == db || db.equals(0.0);
    }

    public static boolean isNull(File file) {
        return null == file || !file.exists();
    }

    /**
     * 判断数组是否为空
     *
     * @param strs
     * @return
     */
    public static boolean isNull(Object[] strs) {
        return null == strs || strs.length == 0;
    }

    /**
     * 获取数字 空返回0
     *
     * @param number
     * @return
     */
    public static Number getNumber(Number number) {

        return ObjectUtils.isNull(number) ? 0L : number;
    }

    /**
     * 数字格式化
     *
     * @param number
     * @param pattern
     *            (转化格式，默认#.##，其它的自己上网查)
     * @return
     */
    public static String numberFormat(Number number, String... pattern) {
        if (isNull(pattern)) {
            return FORMAT.format(number);
        }

        return FORMAT.format(pattern[0]);
    }

    private static Format FORMAT = new DecimalFormat("#.##");

    /**
     * 克隆
     *
     * @param o
     * @return
     */
    public static Object clone(Object o) {
        if (null == o) {
            return null;
        }

        // 将对象序列化后写在流里,因为写在流里面的对象是一份拷贝,
        // 原对象仍然在JVM里
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            return ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != bos) {
                    bos.close();
                }
                if (null != oos) {
                    oos.close();
                }
                if (null != ois) {
                    ois.close();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 判断对象是否非空
     *
     * @param o
     * @return
     */
    public static boolean isNotNull(Object o) {
        return !isNull(o);
    }

    /**
     * 判断double是否非空
     * @param db
     * @return
     */
    public static boolean isNotNull(Double db){
        return !isNull(db);
    }

    /**
     * 判断BigDecimal是否非空
     * @param bg
     * @return
     */
    public static boolean isNotNull(BigDecimal bg){
        return !isNull(bg);
    }

    /**
     * 判断集合是否非空
     *
     * @param list
     * @return
     */
    public static boolean isNotNull(List<?> list) {

        return !isNull(list);
    }

    /**
     * 判断集合是否非空
     *
     * @param set
     * @return
     */
    public static boolean isNotNull(Set<?> set) {

        return !isNull(set);
    }

    /**
     * 判断集合是否为空
     *
     * @param map
     * @return
     */
    public static boolean isNotNull(Map<?, ?> map) {
        return !isNull(map);
    }

    /**
     * 判断Long是否为空
     *
     * @param lg
     * @return
     */
    public static boolean isNotNull(Long lg) {
        return !isNull(lg);
    }

    /**
     * 判断Integer是否为空
     *
     * @param it
     * @return
     */
    public static boolean isNotNull(Integer it) {
        return !isNull(it);
    }

    public static boolean isNotNull(File file) {
        return !isNull(file);
    }

    /**
     * 判断数组是否为空
     *
     * @param strs
     * @return
     */
    public static boolean isNotNull(Object[] strs) {
        return !isNull(strs);
    }

    /**
     * 判断一个值是否在穷举内
     * @param source
     * @param values
     * @return
     */
    public static<T> boolean in(T source, T...values) {
        if (isNull(values)) {
            return false;
        }
        for (T t : values) {
            if (source.equals(t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断一个值是否不在穷举内
     * @param source
     * @param values
     * @return
     */
    public static <T> boolean notIn(T source, T...values) {
        return !in(source, values);
    }


    /**
     * 把Obj换成Map
     * @param obj
     * @return Map<String,String>
     */
    public static Map<String,String> objToMap(Object obj){
        if(ObjectUtils.isNotNull(obj)){
            String stringMap = gson.toJson(obj);
            return gson.fromJson(stringMap,new TypeToken<Map<String,String>>(){}.getType());
        }
        return null;
    }
}
