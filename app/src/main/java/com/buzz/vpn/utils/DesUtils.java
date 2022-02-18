package com.buzz.vpn.utils;

import android.util.Base64;

import com.buzz.vpn.api.network.Api;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import static com.buzz.vpn.api.network.Api.DEFAULT_DES_KEY;

public class DesUtils {

    public static final String ALGORITHM_DES = "DES";



    /**
     * DES算法，加密
     * @param data 待加密字符串
     * @param key 加密私钥，长度不能够小于8位
     * @return 加密后的字节数组，一般结合Base64编码使用
     * @throws Exception 异常
     */
    public static String encryptDES(String key, String data, String url) throws Exception {

        String noNeedEncryptUrl = "/product-app/user/loginByPhone,/product-app/user/getSmsCode";
        if (url.startsWith(Api.ins().getBaseUrl())) {
            String transCode = url.substring(Api.ins().getBaseUrl().length());
            if (noNeedEncryptUrl.contains(transCode)) {
                return data;
            }
        }

        if (StringUtils.isEmpty(key)) {
            key = DEFAULT_DES_KEY;
        }
        return encryptDES(key, data.getBytes());
    }


    /**
     * DES算法，加密
     * @param data 待加密字符串
     * @param key 加密私钥，长度不能够小于8位
     * @return 加密后的字节数组，一般结合Base64编码使用
     * @throws Exception 异常
     */
    public static String encryptDES(String key, String data) throws Exception {
        return encryptDES(key, data.getBytes());
    }
    /**
     * DES算法，加密
     * @param data  待加密字符串
     * @param key  加密私钥，长度不能够小于8位
     * @return 加密后的字节数组，一般结合Base64编码使用
     * @throws Exception 异常
     */
    private static String encryptDES(String key, byte[] data) throws Exception {
        try {
            // 生成一个可信任的随机数源
            SecureRandom sr = new SecureRandom();
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // key的长度不能够小于8位字节
            SecretKey secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);
            byte[] bytes = cipher.doFinal(data);
            return Base64.encodeToString(bytes, 0);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
    /**
     * DES算法，解密
     * @param data  待解密字符串
     * @param key 解密私钥，长度不能够小于8位
     * @return 解密后的字节数组
     * @throws Exception  异常
     */
    private static byte[] decryptDES(String key, byte[] data) throws Exception {
        try {
            // 生成一个可信任的随机数源
            SecureRandom sr = new SecureRandom();
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // key的长度不能够小于8位字节
            SecretKey secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * 解密.
     * @param key
     * @param data
     * @return
     * @throws Exception
     */
    public static String decryptDES(String key, String data) {
        byte[] datas;
        String value = null;
        try {
            if (System.getProperty("os.name") != null
                    && (System.getProperty("os.name").equalsIgnoreCase("sunos") || System
                    .getProperty("os.name").equalsIgnoreCase("linux"))) {
                datas = decryptDES(key, Base64.decode(data, 0));
            } else {
                datas = decryptDES(key, Base64.decode(data, 0));
            }
            value = new String(datas);
        } catch (Exception e) {
            value = data;
        }
        return value;
    }
}
