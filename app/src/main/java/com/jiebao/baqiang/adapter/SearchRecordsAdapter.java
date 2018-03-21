package com.jiebao.baqiang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jiebao.baqiang.R;

import java.util.List;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

public class SearchRecordsAdapter<T> extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<T> mData;

    public ScannerBaseAdatper(Context cxt, List<T> mData) {
        mInflater = LayoutInflater.from(cxt);
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_search_records, parent, false);
            holder = new ViewHolder();

            holder.mTvID = convertView.findViewById(R.id.tv_id);
            holder.mTvScannerData = convertView.findViewById(R.id.tv_scanner_data);
            holder.mTvStatus = convertView.findViewById(R.id.tv_status);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        T bean = mData.get(position);
        // TODO 此处序号为int类型数值，填充时需要转为String
        holder.mTvID.setText("" + bean.getId());
        holder.mTvScannerData.setText(bean.getScannerData());
        holder.mTvStatus.setText(bean.getStatus());

        return convertView;
    }

    private class ViewHolder {
        TextView mTvID;
        TextView mTvScannerData;
        TextView mTvStatus;
    }
}
