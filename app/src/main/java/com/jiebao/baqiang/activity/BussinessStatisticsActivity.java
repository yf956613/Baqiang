package com.jiebao.baqiang.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.data.bean.SearchTime;
import com.jiebao.baqiang.data.db.DaojianDBHelper;
import com.jiebao.baqiang.data.db.FajianDBHelper;
import com.jiebao.baqiang.data.db.LiucangDBHelper;
import com.jiebao.baqiang.data.db.XcdjDBHelper;
import com.jiebao.baqiang.data.db.ZcFajianDBHelper;
import com.jiebao.baqiang.util.BQTimeUtil;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by yaya on 2018/3/28.
 */

public class BussinessStatisticsActivity extends
        BaseActivityWithTitleAndNumber implements View.OnClickListener {
    private static final String TAG = BussinessStatisticsActivity.class
            .getSimpleName();

    @ViewInject(R.id.btn_begin_day)
    private Button mBtnBeginDay;
    @ViewInject(R.id.btn_begin_hour)
    private Button mBtnBeginHour;
    @ViewInject(R.id.btn_end_day)
    private Button mBtnEndDay;
    @ViewInject(R.id.btn_end_hour)
    private Button mBtnEndHour;
    @ViewInject(R.id.btn_search)
    private Button mBtnSearch;
    @ViewInject(R.id.listview_content)
    private ListView mListView;

    private SearchTime mShowBeginTime = null;
    private SearchTime mShowEndTime = null;
    private ItemApdater mItemAdapter;
    private List<ItemObjects> mItemObjects;

    @Override
    public void initView() {
        LogUtil.trace();
        setContent(R.layout.activity_bussiness_statistics);
        setHeaderLeftViewText("业务统计");
        x.view().inject(BussinessStatisticsActivity.this);
    }

    @Override
    public void initData() {
        mBtnBeginDay.setOnClickListener(this);
        mBtnBeginHour.setOnClickListener(this);
        mBtnEndDay.setOnClickListener(this);
        mBtnEndHour.setOnClickListener(this);
        mBtnSearch.setOnClickListener(this);

        mShowBeginTime = new SearchTime(TextStringUtil.getCurrentTimeArray
                (Calendar.YEAR),
                TextStringUtil.getCurrentTimeArray(Calendar.MONTH) + 1,
                TextStringUtil
                        .getCurrentTimeArray(Calendar.DAY_OF_MONTH), 0, 0, 0);
        mShowEndTime = new SearchTime(TextStringUtil.getCurrentTimeArray
                (Calendar.YEAR),
                TextStringUtil.getCurrentTimeArray(Calendar.MONTH) + 1,
                TextStringUtil
                        .getCurrentTimeArray(Calendar.DAY_OF_MONTH), 23, 59,
                59);

        mItemObjects = new ArrayList<>();
        mItemAdapter = new ItemApdater(BussinessStatisticsActivity.this, R
                .layout.item_bussiness_statistic, mItemObjects);
        mListView.setAdapter(mItemAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBtnBeginDay.setText(mShowBeginTime.getDayString());
        mBtnBeginHour.setText(mShowBeginTime.getHourString());
        mBtnEndDay.setText(mShowEndTime.getDayString());
        mBtnEndHour.setText(mShowEndTime.getHourString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_begin_day: {
                DatePickerDialog.OnDateSetListener listener = new
                        DatePickerDialog
                                .OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int
                                    month, int dayOfMonth) {
                                mShowBeginTime.setYear(year);
                                mShowBeginTime.setMonth(month + 1);
                                mShowBeginTime.setDay(dayOfMonth);
                                mBtnBeginDay.setText(mShowBeginTime
                                        .getDayString());
                            }
                        };
                DatePickerDialog dialog = new DatePickerDialog
                        (BussinessStatisticsActivity.this,
                                DatePickerDialog.THEME_TRADITIONAL, listener,
                                mShowBeginTime.getYear(),
                                mShowBeginTime.getMonth() - 1, mShowBeginTime
                                .getDay());
                dialog.show();

                break;
            }

            case R.id.btn_begin_hour: {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new
                        TimePickerDialog
                                .OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int
                                    hourOfDay, int
                                                          minute) {
                                LogUtil.trace("hourOfDay:" + hourOfDay + "; " +
                                        "minute:"
                                        + minute);
                                mShowBeginTime.setHour(hourOfDay);
                                mShowBeginTime.setMinute(minute);
                                mBtnBeginHour.setText(mShowBeginTime
                                        .getHourString());
                            }
                        };
                TimePickerDialog timePickerDialog = new TimePickerDialog
                        (BussinessStatisticsActivity
                                .this, TimePickerDialog.THEME_TRADITIONAL,
                                onTimeSetListener,
                                mShowBeginTime.getHour(), mShowBeginTime
                                .getMinute(),
                                true);
                timePickerDialog.show();

                break;
            }

            case R.id.btn_end_day: {
                DatePickerDialog.OnDateSetListener listener = new
                        DatePickerDialog
                                .OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int
                                    month, int dayOfMonth) {
                                mShowEndTime.setYear(year);
                                mShowEndTime.setMonth(month + 1);
                                mShowEndTime.setDay(dayOfMonth);
                                mBtnEndDay.setText(mShowEndTime.getDayString());
                            }
                        };
                DatePickerDialog dialog = new DatePickerDialog
                        (BussinessStatisticsActivity.this,
                                DatePickerDialog.THEME_TRADITIONAL, listener,
                                mShowEndTime.getYear(),
                                mShowEndTime.getMonth() - 1, mShowEndTime
                                .getDay());
                dialog.show();
                break;
            }

            case R.id.btn_end_hour: {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new
                        TimePickerDialog
                                .OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int
                                    hourOfDay, int
                                                          minute) {
                                LogUtil.trace("hourOfDay:" + hourOfDay + "; " +
                                        "minute:"
                                        + minute);

                                mShowEndTime.setHour(hourOfDay);
                                mShowEndTime.setMinute(minute);
                                mBtnEndHour.setText(mShowEndTime
                                        .getHourString());
                            }
                        };
                TimePickerDialog timePickerDialog = new TimePickerDialog
                        (BussinessStatisticsActivity
                                .this, TimePickerDialog.THEME_TRADITIONAL,
                                onTimeSetListener,
                                mShowEndTime.getHour(), mShowEndTime
                                .getMinute(), true);
                timePickerDialog.show();

                break;
            }

            case R.id.btn_search: {
                String beginTime = mShowBeginTime.getDayString() + " " +
                        mShowBeginTime
                                .getHourString();
                String endTime = mShowEndTime.getDayString() + " " +
                        mShowEndTime.getHourString();

                LogUtil.trace("业务数据查询：" + "beginTime-->" + beginTime + "; " +
                        "endTime-->" + endTime);

                try {
                    long mBeginDate = new SimpleDateFormat("yyyyMMddHHmmss")
                            .parse(BQTimeUtil
                                    .convertSearchTime(beginTime, 1)).getTime();
                    long mEndDate = new SimpleDateFormat("yyyyMMddHHmmss")
                            .parse(BQTimeUtil
                                    .convertSearchTime(endTime, 2)).getTime();

                    if (mBeginDate > mEndDate) {
                        Toast.makeText(BussinessStatisticsActivity.this,
                                "开始时间必须早于结束时间！", Toast
                                        .LENGTH_SHORT).show();
                        return;
                    }

                    searchRecords(mBeginDate, mEndDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            }

            default:
                break;
        }
    }

    private void searchRecords(long mBeginDate, long mEndDate) {
        mItemObjects.clear();
        // 获取指定时间段内的可用数据
        mItemObjects.add(new ItemObjects("装车发件",
                ZcFajianDBHelper.findTimeLimitedUsableRecords
                        (mBeginDate, mEndDate),
                ZcFajianDBHelper.findTimeLimitedUploadRecords
                        (mBeginDate, mEndDate)));
        mItemObjects.add(new ItemObjects("卸车到件", XcdjDBHelper
                .findTimeLimitedUsableRecords(mBeginDate,
                        mEndDate), XcdjDBHelper
                .findTimeLimitedUploadRecords(mBeginDate,
                        mEndDate)));
        mItemObjects.add(new ItemObjects("到件", DaojianDBHelper
                .findTimeLimitedUsableRecords
                        (mBeginDate, mEndDate), DaojianDBHelper
                .findTimeLimitedUploadRecords(mBeginDate,
                        mEndDate)));
        mItemObjects.add(new ItemObjects("发件", FajianDBHelper
                .findTimeLimitedUsableRecords
                        (mBeginDate, mEndDate), FajianDBHelper
                .findTimeLimitedUploadRecords(mBeginDate,
                        mEndDate)));
        mItemObjects.add(new ItemObjects("留仓件", LiucangDBHelper
                .findTimeLimitedUsableRecords
                        (mBeginDate, mEndDate), LiucangDBHelper
                .findTimeLimitedUploadRecords(mBeginDate,
                        mEndDate)));

        mItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void syncViewAfterUpload(int updateType) {
        super.syncViewAfterUpload(updateType);

        try {
            long mBeginDate = new SimpleDateFormat("yyyyMMddHHmmss")
                    .parse(BQTimeUtil
                            .convertSearchTime(mShowBeginTime.getDayString()
                                    + " " +
                                    mShowBeginTime
                                            .getHourString(), 1)).getTime();
            long mEndDate = new SimpleDateFormat("yyyyMMddHHmmss")
                    .parse(BQTimeUtil
                            .convertSearchTime(mShowEndTime.getDayString() +
                                    " " +
                                    mShowEndTime.getHourString(), 2)).getTime();

            if (mBeginDate > mEndDate) {
                Toast.makeText(BussinessStatisticsActivity.this,
                        "开始时间必须早于结束时间！", Toast
                                .LENGTH_SHORT).show();
                return;
            }

            searchRecords(mBeginDate, mEndDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ItemObjects {
        private String name;
        private int allRecords;
        private int unUploadRecords;

        public ItemObjects() {
        }

        public ItemObjects(String name, int allRecords, int unUploadRecords) {
            this.name = name;
            this.allRecords = allRecords;
            this.unUploadRecords = unUploadRecords;
        }

        public String getName() {
            return name;
        }

        public int getAllRecords() {
            return allRecords;
        }

        public int getUploadRecords() {
            return unUploadRecords;
        }
    }

    class ItemApdater extends ArrayAdapter {
        private Context mCxt;
        private int layoutResource;
        private List<ItemObjects> mContents;

        public ItemApdater(@NonNull Context context, @LayoutRes int resource,
                           @NonNull List<ItemObjects> objects) {
            super(context, resource, objects);
            this.mCxt = context;
            this.layoutResource = resource;
            this.mContents = objects;
        }

        @Override
        public int getCount() {
            return mContents.size();
        }

        @Override
        public Object getItem(int position) {
            return mContents.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                convertView = LayoutInflater.from(mCxt).inflate
                        (layoutResource, parent, false);
                holder = new ViewHolder();

                holder.tvName = convertView.findViewById(R.id.tv_name);
                holder.tvAllRecords = convertView.findViewById(R.id
                        .tv_all_records);
                holder.tvUploadRecords = convertView.findViewById(R.id
                        .tv_upload_records);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ItemObjects value = mContents.get(position);
            holder.tvName.setText("" + value.getName());
            holder.tvAllRecords.setText("" + value.getAllRecords());
            holder.tvUploadRecords.setText("" + value.getUploadRecords());

            return convertView;
        }

        private class ViewHolder {
            TextView tvName;
            TextView tvAllRecords;
            TextView tvUploadRecords;
        }
    }
}