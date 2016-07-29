package com.frame.sdk.util;

import android.util.Base64;

import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 安全相关的类，如加密解密、生成消息摘要信息
 */
public class SecurityUtil {
    /**
     * 算法名称类
     */
    public static class AtalgorithmName {
        public static final String MD2 = "MD2";
        public static final String MD5 = "MD5";
        public static final String SHA = "SHA";
        public static final String SHA_256 = "SHA-256";
        public static final String SHA_384 = "SHA-384";
        public static final String SHA_512 = "SHA-512";
        public static final String DES = "DES";
    }

    /**
     * 获取指定算法的消息摘要
     *
     * @param algorithm 算法名称,可以参考SecurityUtil.AtalgorithmName类定义的各种算法名称
     * @return
     */
    public static byte[] getDigest(byte[] data, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            return md.digest(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取指定算法的消息摘要
     *
     * @param algorithm 算法名称,可以参考SecurityUtil.AtalgorithmName类定义的各种算法名称
     * @return
     */
    public static String getDigest(String str, String algorithm) {
        byte[] digest = getDigest(str.getBytes(), algorithm);
        return DataTypeConverUtil.bytesToHex(digest);
    }

    private static Key toSecretKey(byte[] key) {
        try {
            DESKeySpec dks = new DESKeySpec(key);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(AtalgorithmName.DES);
            SecretKey sk = skf.generateSecret(dks);
            return sk;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成加密用的密钥
     *
     * @param key
     * @return
     */
    private static Key getKey(String key) {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(AtalgorithmName.DES);
            kg.init(new SecureRandom(key.getBytes()));
            return kg.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密
     *
     * @param data 原始待加密数据
     * @param key  加密的密码
     * @return
     */
    public static byte[] encrypt(byte[] data, String key) {
        try {
            Key k = toSecretKey(getKey(key).getEncoded());
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, k);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密
     */
    public static byte[] encrypt(byte[] data, String key, String iv) {
        try {
            Key k = toSecretKey(getKey(key).getEncoded());
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec ivParameter = new IvParameterSpec(iv.getBytes("UTF-8"));
            cipher.init(Cipher.ENCRYPT_MODE, k, ivParameter);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密
     *
     * @param data 原始待加密数据
     * @param key  加密的密码
     * @return
     */
    public static String encrypt(String data, String key) {
        try {
            return Base64.encodeToString(encrypt(data.getBytes(HTTP.UTF_8), key), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param data 原始待加密数据
     * @param key  加密的密码
     * @return 出错返回null
     */
    public static byte[] decrypt(byte[] data, String key, String iv) {
        Key k = toSecretKey(getKey(key).getEncoded());
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec ivParameter = new IvParameterSpec(iv.getBytes("UTF-8"));
            cipher.init(Cipher.DECRYPT_MODE, k, ivParameter);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     */
    public static byte[] decrypt(byte[] data, String key) {
        Key k = toSecretKey(getKey(key).getEncoded());
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, k);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param data 原始待加密数据
     * @param key  加密的密码
     * @return 出错返回null
     */
    public static String decrypt(String data, String key) {
        byte[] bytes = decrypt(Base64.decode(data, Base64.DEFAULT), key);
        try {
            return new String(bytes, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*
       DES加密
     */
    public static byte[] desEncrypt(byte[] data, String keyStr, String ivStr) {
        byte[] res = null;
        try {
            DESKeySpec keySpec = new DESKeySpec(keyStr.getBytes("UTF-8"));// 设置密钥参数
            IvParameterSpec iv = new IvParameterSpec(ivStr.getBytes("UTF-8"));// 设置向量
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂
            SecretKey key = keyFactory.generateSecret(keySpec);// 得到密钥对象
            Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");// 得到加密对象Cipher
            enCipher.init(Cipher.ENCRYPT_MODE, key, iv);// 设置工作模式为加密模式，给出密钥和向量
            res = enCipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /*
      DES解密
     */
    public static byte[] desDecrypt(byte[] data, String keyStr, String ivStr) {
        byte[] retByte = null;
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(keyStr.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(ivStr.getBytes("UTF-8"));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            retByte = cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retByte;
    }
}
