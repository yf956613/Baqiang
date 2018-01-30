package com.jiebao.baqiang.adapter;


import android.content.Context;

import com.jiebao.baqiang.R;

import java.util.List;

public class SingleChoiceAdapter extends CommonAdapter<String>{

    public SingleChoiceAdapter(Context context, List<String> datas){
        super(context, datas, R.layout.list_item_singlechoice);
    }

    @Override
    public void convert(ViewHolder holder, String str) {
        holder.setText(R.id.choice_text, str);
    }
}
