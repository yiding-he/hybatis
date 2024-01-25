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
                sb.append("_");
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

    public static boolean isNotBlank(String s) {
        if (s == null || s.isEmpty()) {
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

    public static boolean isBlank(String s) {
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
}
