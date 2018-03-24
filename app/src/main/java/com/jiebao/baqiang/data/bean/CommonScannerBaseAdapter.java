package com.jiebao.baqiang.data.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.data.arrival.UnloadArrivalFileContent;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianFileContent;

import java.util.List;

/**
 * Created by Administrator on 2018/3/24 0024.
 */

public class CommonScannerBaseAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<CommonScannerListViewBean> mData;

    public CommonScannerBaseAdapter(Context cxt, List<CommonScannerListViewBean> mData) {
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
            convertView = mInflater.inflate(R.layout.item_fajian, parent, false);
            holder = new ViewHolder();

            holder.mTvID = convertView.findViewById(R.id.tv_id);
            holder.mTvScannerData = convertView.findViewById(R.id.tv_scanner_data);
            holder.mTvStatus = convertView.findViewById(R.id.tv_status);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        IFileContentBean bean = mData.get(position).getScannerBean();
        if (bean != null) {
            // 获取CommonScannerListViewBean 序号
            holder.mTvID.setText("" + mData.get(position).getId());

            if (bean instanceof ZCFajianFileContent) {
                ZCFajianFileContent value = (ZCFajianFileContent) bean;
                holder.mTvScannerData.setText(value.getShipmentNumber());

                if ("Unload".equals(value.getmStatus())) {
                    holder.mTvStatus.setText("未上传");
                } else {
                    holder.mTvStatus.setText("已上传");
                }
            } else if(bean instanceof UnloadArrivalFileContent){
                UnloadArrivalFileContent value = (UnloadArrivalFileContent) bean;
                holder.mTvScannerData.setText(value.getShipmentNumber());

                if ("Unload".equals(value.getStatus())) {
                    holder.mTvStatus.setText("未上传");
                } else {
                    holder.mTvStatus.setText("已上传");
                }
            }else {
                // 其他类型
            }
        }

        return convertView;
    }

    private class ViewHolder {
        TextView mTvID;
        TextView mTvScannerData;
        TextView mTvStatus;
    }
}
