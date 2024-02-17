package com.hyd.hybatis.utils;

import java.util.List;

public class Lst {

    public static <T> List<T> concat(List<T>... lists) {
        List<T> result = new java.util.ArrayList<>();
        for (List<T> list : lists) {
            result.addAll(list);
        }
        return result;
    }
}
