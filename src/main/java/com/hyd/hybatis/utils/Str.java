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
        return s == null ? null : s.length() == 0 ? "" :
            Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
