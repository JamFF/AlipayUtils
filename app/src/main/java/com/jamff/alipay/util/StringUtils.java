package com.jamff.alipay.util;

/**
 * description:
 * author: JamFF
 * time: 2018/12/31 15:06
 */
public class StringUtils {

    /**
     * 字符串转 unicode
     *
     * @param full 是否需要部位（补0）
     */
    public static String string2Unicode(String string, boolean full) {

        StringBuffer unicode = new StringBuffer();

        int length = string.length();

        for (int i = 0; i < length; i++) {
            // 取出每一个字符
            char c = string.charAt(i);

            // 转换为unicode
            String key = Integer.toHexString(c);

            // 拼接头部
            unicode.append("\\u");

            if (full) {
                switch (key.length()) {
                    case 1:
                        unicode.append("000").append(key);
                        break;
                    case 2:
                        unicode.append("00").append(key);
                        break;
                    case 3:
                        unicode.append("0").append(key);
                        break;
                    case 4:
                        unicode.append(key);
                        break;
                    default:
                        unicode.append("0000");
                        break;
                }
            } else {
                unicode.append(key);
            }
        }

        return unicode.toString();
    }

    /**
     * unicode 转字符串
     */
    public static String unicode2String(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }
}
