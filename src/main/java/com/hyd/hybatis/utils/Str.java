package com.hyd.hybatis.utils;

public class Str {

    public static String camel2Underline(String camel) {
        if (camel == null || camel.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camel.length(); i++) {
            char c = camel.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append("_");
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String underline2Camel(String underline) {
        if (underline == null || underline.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < underline.length(); i++) {
            char c = underline.charAt(i);
            if (c == '_') {
                i++;
                sb.append(Character.toUpperCase(underline.charAt(i)));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String capitalize(String s) {
        return s == null ? null : s.isEmpty() ? "" :
            Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static boolean isNotBlank(CharSequence s) {
        if (s == null) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            var c = s.charAt(i);
            if (!Character.isWhitespace(c)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBlank(CharSequence s) {
        return !isNotBlank(s);
    }

    public static String firstNonBlank(String... strings) {
        if (strings == null) {
            return null;
        }
        for (String s : strings) {
            if (isNotBlank(s)) {
                return s;
            }
        }
        return strings.length == 0 ? null : strings[strings.length - 1];
    }

    public static boolean isInteger(String s) {
        return s != null && s.matches("[+-]?\\d+");
    }

    public static boolean isPositiveInteger(String s) {
        return s != null && s.matches("\\d+");
    }

    /**
     * 在字符串中查询多个前缀，如果找到其中一个，则返回移除该前缀后的字符串。
     */
    public static String removeStart(String s, String... prefixes) {
        if (s == null) {
            return null;
        }
        for (String prefix : prefixes) {
            if (prefix == null) {
                continue;
            }
            if (!s.startsWith(prefix)) {
                continue;
            }
            return s.substring(prefix.length());
        }
        return s;
    }

    public static String repeat(String s, int n, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(s);
        }
        return sb.toString();
    }
}
