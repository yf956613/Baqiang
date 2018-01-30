package com.jiebao.baqiang.adapter;


import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

public abstract class TableHeaderAdapter {

    private final Context context;
    private int[] columnsWidths;

    public TableHeaderAdapter(final Context context, int[] columnsWidths) {
       this.context = context;
        this.columnsWidths = columnsWidths;
    }

    public Context getContext() {
        return context;
    }
    public Resources getResources() {
        return getContext().getResources();
    }

    public abstract View getHeaderView(int columnIndex, ViewGroup parentView);

    public int getColumnWidth(int column){
        return columnsWidths[column];
    }

    public int getColumnCount(){
        return columnsWidths.length;
    }
}
