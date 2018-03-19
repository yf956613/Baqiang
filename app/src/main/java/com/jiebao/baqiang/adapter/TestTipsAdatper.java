package com.jiebao.baqiang.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class TestTipsAdatper extends BaseAdapter implements Filterable {
    private List<String> list = new ArrayList<String>();
    private Context context;
    private MyFilter filter = null;
    private FilterListener listener = null;

    public TestTipsAdatper(Context context, List<String> list, FilterListener filterListener) {
        this.list = list;
        this.context = context;
        this.listener = filterListener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_tips, null);
            holder = new ViewHolder();
            holder.tv_ss = (TextView) convertView.findViewById(R.id.text1);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.tv_ss.setText(list.get(position));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new MyFilter(list);
        }
        return filter;
    }

    class MyFilter extends Filter {
        private List<String> original = new ArrayList<String>();

        public MyFilter(List<String> list) {
            this.original = list;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            LogUtil.trace();

            FilterResults results = new FilterResults();

            if (TextUtils.isEmpty(constraint)) {
                results.values = original;
                results.count = original.size();
            } else {
                List<String> mList = new ArrayList<String>();
                for (String s : original) {
                    if (s.trim().toLowerCase().contains(constraint.toString().trim().toLowerCase
                            ())) {
                        mList.add(s);
                    }
                }
                results.values = mList;
                results.count = mList.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list = (List<String>) results.values;

            if (listener != null) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

            if (results != null) {
                listener.getFilterData(list);
            }
        }

    }

    class ViewHolder {
        TextView tv_ss;
    }
}
