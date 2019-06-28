package com.yizhilu.os.dynamic.utils;

import java.io.IOException;

/**对象序列化工具类*/
public class SerializationUtils {

	private static CacheSerializer g_ser;

	public static void main(String[] args) throws IOException {
		String str="mony";
		byte[] bufstr =  serialize(str);
		System.out.println(deserialize(bufstr).toString());
	}

	static {
		String ser = "fst";
		//
		if (ser == null || "".equals(ser.trim()))
			g_ser = new JavaCacheSerializer();
		else {
			if (ser.equals("java")) {
				g_ser = new JavaCacheSerializer();
			} else if (ser.equals("fst")) {
				g_ser = new FSTCacheSerializer();
			} else {
				try {
					g_ser = (CacheSerializer) Class.forName(ser).newInstance();
				} catch (Exception e) {
					throw new RuntimeException("Cannot initialize SerializerClass named [" + ser + ']', e);
				}
			}
		}
	}

	/**
	 * 对外通用的序列化方法
	 * @param obj 要序列化的数据对象
	 * @return 返回序列化后的 byte[]
	 * @throws IOException
	 */
	public static byte[] serialize(Object obj) throws IOException {
		return g_ser.serialize(obj);
	}

	/**
	 * 对外通用的反序列化方法
	 * @param bytes 初始序列化过的byte[]
	 * @return 返回反序列化后的数据对象
	 * @throws IOException
	 */
	public static Object deserialize(byte[] bytes) throws IOException {
		return g_ser.deserialize(bytes);
	}

}
