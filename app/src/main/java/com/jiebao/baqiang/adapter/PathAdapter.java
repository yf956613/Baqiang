package com.jiebao.baqiang.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jiebao.baqiang.R;

import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PathAdapter extends BaseAdapter {
	private NumberFormat numberFormat;
	private DateFormat dateFormat;
	private Context context;
	private List<File> objects = new ArrayList<File>();
	private OnFcListItemClickListener itemClickListener = new DummyOnFcListItemClickListener();
	private SparseBooleanArray checkedPositions = new SparseBooleanArray();

	public PathAdapter(Context context, OnFcListItemClickListener itemClickListener) {
		this.context = context;
		numberFormat = NumberFormat.getInstance();
		dateFormat = android.text.format.DateFormat.getMediumDateFormat(context);

		if (null != itemClickListener) {
			this.itemClickListener = itemClickListener;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;

		if (null == convertView) {
			convertView = View.inflate(context, R.layout.fc_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.fc_item_name);
			viewHolder.details1 = (TextView) convertView.findViewById(R.id.fc_details1);
			viewHolder.details2 = (TextView) convertView.findViewById(R.id.fc_details2);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		File file = objects.get(position);

		viewHolder.name.setText(file.getName());

		Date modifiedDate = new Date(file.lastModified());
		StringBuilder details2Text = new StringBuilder().append(dateFormat.format(modifiedDate));
		viewHolder.details2.setText(details2Text.toString());

		convertView.setOnClickListener(new OnFcListItemViewClickListener(position));

		return convertView;
	}

	public void setObjects(List<File> objects) {
		this.objects = objects;
		checkedPositions.clear();
		notifyDataSetChanged();
	}

	public void clear() {
		objects.clear();
		checkedPositions.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public Object getItem(int position) {
		return objects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public SparseBooleanArray getCheckedPositions() {
		return checkedPositions;
	}

	public void checkPosition(int position, boolean isChecked) {
		if (isChecked) {
			checkedPositions.put(position, isChecked);
		} else {
			checkedPositions.delete(position);
		}
	}

	public void clearCheckedPositions() {
		checkedPositions.clear();
	}

	private static class ViewHolder {
		public TextView name;
		public TextView details1;
		public TextView details2;
		public CheckBox checkBox;
	}

	private class OnFcListItemViewClickListener implements View.OnClickListener {

		private int position;

		public OnFcListItemViewClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			PathAdapter.this.itemClickListener.onItemClick(position, objects.get(position));
		}
	}

	public interface OnFcListItemClickListener {
		public void onItemClick(int position, File file);
	}

	private static class DummyOnFcListItemClickListener implements OnFcListItemClickListener {

		@Override
		public void onItemClick(int position, File file) {

		}
	}

}
