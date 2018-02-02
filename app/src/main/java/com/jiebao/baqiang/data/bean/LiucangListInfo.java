package com.jiebao.baqiang.data.bean;

import java.util.List;

/**
 * Created by open on 2018/1/29.
 */

/*
   留仓类型
 */

public class LiucangListInfo {

    private int storehousesCnt;
    private List<LiucangBean> storehouseInfo;

    public int getLiuCangCnt() {
        return storehousesCnt;
    }

    public List<LiucangBean> getLiuCangInfo() {
        return storehouseInfo;
    }
}
