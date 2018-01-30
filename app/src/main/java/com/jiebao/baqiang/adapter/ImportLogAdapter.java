package com.jiebao.baqiang.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jiebao.baqiang.R;

import java.util.List;

public class ImportLogAdapter extends ArrayAdapter<String> {

    public ImportLogAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = View.inflate(getContext(),R.layout.list_text_item,null);
        TextView sdl_text = (TextView)convertView.findViewById(R.id.choice_text);
        sdl_text.setText(getItem(position));
        return convertView;
    }
}
