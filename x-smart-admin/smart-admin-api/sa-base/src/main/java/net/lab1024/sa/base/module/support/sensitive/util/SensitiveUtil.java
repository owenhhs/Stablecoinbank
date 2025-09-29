package net.lab1024.sa.base.module.support.sensitive.util;

import java.util.Objects;

public class SensitiveUtil {

    public static String username(String value) {
        if (Objects.isNull(value) || value.length() < 2) {
            return value;
        } else if (value.length() == 2) {
            return "*" + value.charAt(value.length() - 1);
        } else {

            return value.charAt(0) + repeat("*", value.length() - 2) + value.charAt(value.length() - 1);
        }
    }

    public static String phone(String value) {
        return value.substring(0, 3) + repeat("*", 4) + value.substring(7, 11);
    }

    public static String custom(String value, int prefix, int suffix, String replaceChar) {
        return value.substring(0, prefix) + repeat(replaceChar, value.length() - suffix - prefix) + value.substring(value.length() - suffix);
    }


    /**
     * 输出指定个数的字符
     *
     * @param c      特定字符
     * @param length 输出字符个数
     * @return
     */
    private static String repeat(String c, int length) {
        StringBuilder rep = new StringBuilder();
        for (int i = 0; i < length; i++) {
            rep.append(c);
        }
        return rep.toString();
    }


    public static void main(String[] args) {
        System.out.println(Integer.class instanceof Object);
    }
}
