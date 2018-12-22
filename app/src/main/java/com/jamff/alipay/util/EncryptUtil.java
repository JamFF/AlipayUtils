package com.jamff.alipay.util;

import android.text.TextUtils;

import com.jamff.alipay.Constant;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * description: 加密
 * author: JamFF
 * time: 2018/12/22 00:01
 */
public class EncryptUtil {

    public static String SHA1(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] buf = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }

            return new String(buf);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();

            return "";
        }
    }

    /**
     * 用于获取一个String的32位MD5值
     *
     * @param upperCase true大写，false小写
     */
    public static String getMD5(String str, boolean upperCase) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(str.getBytes());
            StringBuilder sb = new StringBuilder(40);
            for (byte x : bs) {
                if ((x & 0xff) >> 4 == 0) {
                    sb.append("0").append(Integer.toHexString(x & 0xff));
                } else {
                    sb.append(Integer.toHexString(x & 0xff));
                }
            }
            if (upperCase) {
                return sb.toString().toUpperCase(Locale.US);
            } else {
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getSign(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return "";
        }
        String md5 = getMD5(jsonString + Constant.MD5_KRY, true);
        return jsonString.substring(0, jsonString.length() - 1) + ",\"sign\":\"" + md5 + "\"}";
    }
}
