package com.wuzai.os.dynamic.utils;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Properties;

/**
 * DES加密解密
 *
 * @author guo shi qi
 * @version 1.0.0
 * @create 2017/11/18 下午2:40
 */
public class DesUtil {

    private final static String DES = "DES";
    private final static String ENCODE = "GBK";
    private final static String defaultKey = "u@M2KLK#1x8CGLtj11cKw@uG1*m4E*eg";

    public static void main(String[] args) throws Exception {
        String targetUrl = args[0];
        String source = targetUrl.split(".properties")[0]+"-source.properties";
        Properties propertie = PropertyUtil.getPropertiesPath(source);
        Properties encrypeProp = new Properties();
        for (Object o : propertie.keySet()) {

            System.out.println(String.valueOf(o) +"--------"+String.valueOf(propertie.get(o)));

            String encrypt = DesUtil.encrypt(String.valueOf(propertie.get(o)));
            encrypeProp.put(String.valueOf(o),encrypt.replaceAll("\\s",""));
        }
        OutputStream fos = new FileOutputStream(targetUrl);
        encrypeProp.store(fos, "Update properties");
    }

    /**
     * 使用 默认key 加密
     * @return String
     * @author guo shi qi
     * @date 2015-3-17 下午02:46:43
     */
    public static String encrypt(String data){
        return encrypt(data,defaultKey);
    }

    /**
     * 使用 默认key 解密
     * @return String
     * @author guo shi qi
     * @date 2015-3-17 下午02:49:52
     */
    public static String decrypt(String data){
        return decrypt(data,defaultKey);
    }

    /**
     * Description 根据键值进行加密
     * @author guo shi qi
     * @param data
     * @param key
     *            加密键byte数组
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String key){
        try{
            data = StringUtils.replaceTN(data);
            byte[] bt = encrypt(data.getBytes(ENCODE), key.getBytes(ENCODE));
            String strs = new BASE64Encoder().encode(bt).replace("=",".");
            return strs;
        }catch (Exception e){
            System.out.println("DES加密出现异常:"+e);
        }
        return null;
    }

    /**
     * Description 根据键值进行解密
     * @author guo shi qi
     * @param data
     * @param key
     *            加密键byte数组
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static String decrypt(String data, String key){
        try{
            if (data == null) {
                return null;
            }
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] buf = decoder.decodeBuffer(data.trim().replace(".","="));
            byte[] bt = decrypt(buf, key.trim().getBytes(ENCODE));
            return new String(bt, ENCODE);
        }catch (Exception e){
            System.out.println("DES解密出现异常:"+e);
        }
        return null;
    }

    /**
     * Description 根据键值进行加密
     * @author guo shi qi
     * @param data
     * @param key
     *            加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        return cipher.doFinal(data);
    }

    /**
     * Description 根据键值进行解密
     * @author guo shi qi
     * @param data
     * @param key
     *            加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        return cipher.doFinal(data);
    }

}

