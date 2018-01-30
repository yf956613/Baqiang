package com.jiebao.baqiang.util;

import java.util.List;

public class ListUtil {
    public static <T> boolean isEmpty(List<T> list) {
        if (list == null || list.isEmpty()) return true;
        return false;

    }
}
