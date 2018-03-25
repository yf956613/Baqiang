package com.jiebao.baqiang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.data.arrival.CargoArrivalFileContent;
import com.jiebao.baqiang.data.arrival.UnloadArrivalFileContent;
import com.jiebao.baqiang.data.dispatch.ShipmentFileContent;
import com.jiebao.baqiang.data.stay.StayHouseFileContent;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianFileContent;
import com.jiebao.baqiang.util.LogUtil;

import java.util.List;

/**
 * 搜索界面，与ListView对应的数据适配器Adapter
 * <p>
 * 1. 保存的数据可抽象为类型T，接收诸如：装车发件、卸车到件等数据类型;
 * 2. 上述数据类型保存记录所有内容（包括Table的Record的主键id），方便搜索；
 */

public class SearchRecordsAdapter<T> extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<T> mData;

    public SearchRecordsAdapter(Context cxt, List<T> mData) {
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

            holder.mTvStatus = convertView.findViewById(R.id.tv_status);
            holder.mTvScannerData = convertView.findViewById(R.id.tv_scanner_data);
            holder.mTvTime = convertView.findViewById(R.id.tv_day);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (null != mData && mData.size() != 0) {
            if (mData.get(0) instanceof ZCFajianFileContent) {
                LogUtil.trace("装车");

                // 装车发件
                ZCFajianFileContent bean = (ZCFajianFileContent) mData.get(position);
                if (bean != null) {
                    if ("Unload".equals(bean.getStatus())) {
                        holder.mTvStatus.setText("未上传");
                    } else {
                        holder.mTvStatus.setText("已上传");
                    }

                    holder.mTvScannerData.setText(bean.getShipmentNumber());
                    holder.mTvTime.setText("" + bean.getOperateDate());
                } else {
                    // do nothing
                }
            } else if (mData.get(0) instanceof UnloadArrivalFileContent) {
                // 卸车到件
                UnloadArrivalFileContent bean = (UnloadArrivalFileContent) mData.get(position);
                if (bean != null) {
                    if ("Unload".equals(bean.getStatus())) {
                        holder.mTvStatus.setText("未上传");
                    } else {
                        holder.mTvStatus.setText("已上传");
                    }

                    holder.mTvScannerData.setText(bean.getShipmentNumber());
                    holder.mTvTime.setText("" + bean.getOperateDate());
                } else {
                    // do nothing
                }
            } else if (mData.get(0) instanceof CargoArrivalFileContent) {
                // 到件
                CargoArrivalFileContent bean = (CargoArrivalFileContent) mData.get(position);
                if (bean != null) {
                    if ("Unload".equals(bean.getStatus())) {
                        holder.mTvStatus.setText("未上传");
                    } else {
                        holder.mTvStatus.setText("已上传");
                    }

                    holder.mTvScannerData.setText(bean.getShipmentNumber());
                    holder.mTvTime.setText("" + bean.getOperateDate());
                } else {
                    // do nothing
                }
            } else if (mData.get(0) instanceof ShipmentFileContent) {
                // 发件
                ShipmentFileContent bean = (ShipmentFileContent) mData.get(position);
                if (bean != null) {
                    if ("Unload".equals(bean.getStatus())) {
                        holder.mTvStatus.setText("未上传");
                    } else {
                        holder.mTvStatus.setText("已上传");
                    }

                    holder.mTvScannerData.setText(bean.getShipmentNumber());
                    holder.mTvTime.setText("" + bean.getOperateDate());
                } else {
                    // do nothing
                }
            } else if (mData.get(0) instanceof StayHouseFileContent) {
                // 留仓件
                StayHouseFileContent bean = (StayHouseFileContent) mData.get(position);
                if (bean != null) {
                    if ("Unload".equals(bean.getStatus())) {
                        holder.mTvStatus.setText("未上传");
                    } else {
                        holder.mTvStatus.setText("已上传");
                    }

                    holder.mTvScannerData.setText(bean.getShipmentNumber());
                    holder.mTvTime.setText("" + bean.getOperateDate());
                } else {
                    // do nothing
                }
            } else {
                // do nothing
            }
        } else {
            // do nothing
        }

        return convertView;
    }

    private class ViewHolder {
        TextView mTvStatus;
        TextView mTvScannerData;
        TextView mTvTime;
    }
}
