package com.jiebao.baqiang.adapter;


import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiebao.baqiang.R;

import java.util.List;

public class ScanTableHeaderAdapter extends TableHeaderAdapter {

    private Context context;
    private List<String> params;
    private float formTitleSize;

    public ScanTableHeaderAdapter(Context context, List<String> params, int[] columnWidths) {
        super(context,columnWidths);
        this.params = params;
        this.context = context;
        this.formTitleSize = context.getResources().getDimensionPixelSize(R.dimen.form_title_font_size);
        if(columnWidths == null ||  columnWidths.length == 0)
            return;
        if(params == null || params.size() == 0)
        	return;

    }

    @Override
    public View getHeaderView(int columnIndex, ViewGroup parentView) {

        TextView item = new TextView(context);
        String title = params.get(columnIndex);

        item.setText(title);
        item.setTextSize(TypedValue.COMPLEX_UNIT_PX,formTitleSize);
        item.setGravity(Gravity.CENTER);
        item.setTextColor(Color.rgb(173,181,255));
        return item;
    }

}
