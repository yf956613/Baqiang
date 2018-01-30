package com.jiebao.baqiang.data.bean;

import java.util.List;

/**
 * Created by open on 2018/1/29.
 */

/*
   留仓类型
 */

public class LiucangListInfo {

    private int liuCangCnt;
    private List<LiucangBean> liuCangInfo;

    public int getLiuCangCnt() {
        return liuCangCnt;
    }

    public List<LiucangBean> getLiuCangInfo() {
        return liuCangInfo;
    }
}
