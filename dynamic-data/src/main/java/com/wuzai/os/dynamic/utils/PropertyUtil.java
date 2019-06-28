package com.wuzai.os.dynamic.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.*;

/**
 * 获取properties属性文件的工具类
 * @author s.li
 *
 */
public class PropertyUtil {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/**属性文件获取工类的Map，Key=文件名，value=PropertyUtil*/
	private static Map<String, PropertyUtil> instance = Collections.synchronizedMap(new HashMap<String, PropertyUtil>());
	private String sourceUrl;
	private ResourceBundle resourceBundle;
	private static Map<String, String> convert = Collections.synchronizedMap(new HashMap<String, String>());

	protected PropertyUtil(String sourceUrl) {
		this.sourceUrl = sourceUrl;
		load();
	}

	/**
	 * 初始化读取
	 * @param sourceUrl 属性文件的名字，不包含后缀
	 * @return
	 */
	public static PropertyUtil getInstance(String sourceUrl) {
		synchronized (PropertyUtil.class) {
			PropertyUtil manager = (PropertyUtil) instance.get(sourceUrl);
			if (manager == null) {
				manager = new PropertyUtil(sourceUrl);
				instance.put(sourceUrl, manager);
			}
			return manager;
		}
	}

	/**
	 * 加载属性文件内容（key=value）
	 */
	private synchronized void load() {
		try {
			this.resourceBundle = ResourceBundle.getBundle(this.sourceUrl);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("sourceUrl = " + this.sourceUrl + " file load error!");
		}
	}

	/**
	 * 通过key获取属性文件value
	 * @param key 属性文件的
	 * @return 返回属性key对应的value
	 */
	public String getProperty(String key) {
		String value = null;
		try {
			value = DesUtil.decrypt(new String(this.resourceBundle.getString(key).getBytes("iso-8859-1"), "utf-8"));
		} catch (Exception e) {

		}
		return value;
	}

	/**
	 * 读取任意目录下的文件
	 * @author: guo shi qi
	 * @Param: [path]
	 * @create 2017/11/20 下午1:11
	 * @return java.util.Properties
	 */
	public static Properties getPropertiesPath(String path) {
		Properties propertie = new Properties();
		try {
			propertie = PropertiesLoaderUtils.loadProperties(new PathResource(path));
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		return propertie;
	}

}
