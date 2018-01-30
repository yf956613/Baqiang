package com.jiebao.baqiang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiebao.baqiang.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileListAdapter extends BaseAdapter {
    private int clickPosition = -1;

    private final List<File> data = new ArrayList<File>();
    private final LayoutInflater inflater;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public FileListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public void setData(File[] files) {
        data.clear();
        if (files == null)
            return;
        for (int i = (files.length - 1); i >= 0; i--) {
            data.add(files[i]);
        }

        Collections.sort(data, new Comparator<File>() {

            @Override
            public int compare(File lhs, File rhs) {
                boolean lhsDir = lhs.isDirectory();
                boolean rhsDir = rhs.isDirectory();
                if (lhsDir ^ rhsDir) {
                    return lhsDir ? -1 : 1;
                } else {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
            }
        });

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public File getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.view_list_item, parent, false);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.size = (TextView) convertView.findViewById(R.id.size);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        File file = getItem(position);
        boolean enabled = isEnabled(position);

        holder.title.setText(file.getName());
        holder.date.setText(dateFormat.format(new Date(file.lastModified())));
        holder.size.setText(file.isDirectory() ? null : humanReadableByteCount(file.length(),
                true));
        if (file.isDirectory())
            holder.icon.setBackgroundResource(R.drawable.ic_doc_directory_light);
        else
            holder.icon.setBackgroundResource(R.drawable.ic_doc_text_light);
        holder.title.setEnabled(enabled);
        holder.date.setEnabled(enabled);
        holder.size.setEnabled(enabled);
        holder.icon.setAlpha(enabled ? 1.0f : 0.5f);
        if (position == clickPosition) {
            convertView.setBackgroundResource(
                    R.color.item_select_color);
        } else {
            convertView.setBackgroundResource(android.R.color.transparent);
        }
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    static class ViewHolder {
        TextView title;
        TextView date;
        TextView size;
        ImageView icon;
    }

    public void setCLickItem(int clickPosition) {
        this.clickPosition = clickPosition;
    }

    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format(Locale.US, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}