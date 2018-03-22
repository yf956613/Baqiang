package com.jiebao.baqiang.activity;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.data.bean.SearchTime;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import org.xutils.x;

import java.util.Calendar;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

public class BussinessQueryActivity extends BaseActivityWithTitleAndNumber implements View
        .OnClickListener {

    private Spinner mSpinnerBussiness;
    private ArrayAdapter mArrayAdapter;
    private Button mBtnBeginDay;
    private Button mBtnBeginHour;
    private Button mBtnEndDay;
    private Button mBtnEndHour;

    private String mBussinessType = "装车发件";

    private SearchTime mShowBeginTime = null;
    private SearchTime mShowEndTime = null;
    private Button mBtnSearch;

    @Override
    public void initView() {
        setContent(R.layout.activity_bussiness_query);
        setHeaderLeftViewText("业务查询");
        x.view().inject(BussinessQueryActivity.this);
    }

    @Override
    public void initData() {
        mSpinnerBussiness = this.findViewById(R.id.spinner_bussiness);
        mArrayAdapter = ArrayAdapter.createFromResource(this, R.array.bussiness_types, R.layout
                .spinner_item);

        mSpinnerBussiness.setAdapter(mArrayAdapter);
        mSpinnerBussiness.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] bussinessType = getResources().getStringArray(R.array.bussiness_types);
                mBussinessType = bussinessType[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBtnBeginDay = this.findViewById(R.id.btn_begin_day);
        mBtnBeginDay.setOnClickListener(this);
        mBtnBeginHour = this.findViewById(R.id.btn_begin_hour);
        mBtnBeginHour.setOnClickListener(this);
        mBtnEndDay = this.findViewById(R.id.btn_end_day);
        mBtnEndDay.setOnClickListener(this);
        mBtnEndHour = this.findViewById(R.id.btn_end_hour);
        mBtnEndHour.setOnClickListener(this);
        mBtnSearch = this.findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(this);

        mShowBeginTime = new SearchTime(TextStringUtil.getCurrentTimeArray(Calendar.YEAR),
                TextStringUtil.getCurrentTimeArray(Calendar.MONTH) + 1, TextStringUtil
                .getCurrentTimeArray(Calendar.DAY_OF_MONTH), 0, 0, 0);
        mShowEndTime = new SearchTime(TextStringUtil.getCurrentTimeArray(Calendar.YEAR),
                TextStringUtil.getCurrentTimeArray(Calendar.MONTH) + 1, TextStringUtil
                .getCurrentTimeArray(Calendar.DAY_OF_MONTH), 23, 59, 59);
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
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog
                        .OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mShowBeginTime.setYear(year);
                        mShowBeginTime.setMonth(month + 1);
                        mShowBeginTime.setDay(dayOfMonth);
                        mBtnBeginDay.setText(mShowBeginTime.getDayString());
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog(BussinessQueryActivity.this,
                        DatePickerDialog.THEME_TRADITIONAL, listener, mShowBeginTime.getYear(),
                        mShowBeginTime.getMonth() - 1, mShowBeginTime.getDay());
                dialog.show();

                break;
            }

            case R.id.btn_begin_hour: {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog
                        .OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        LogUtil.trace("hourOfDay:" + hourOfDay + "; minute:" + minute);
                        mShowBeginTime.setHour(hourOfDay);
                        mShowBeginTime.setMinute(minute);
                        mBtnBeginHour.setText(mShowBeginTime.getHourString());
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(BussinessQueryActivity
                        .this, TimePickerDialog.THEME_TRADITIONAL, onTimeSetListener,
                        mShowBeginTime.getHour(), mShowBeginTime.getMinute(), true);
                timePickerDialog.show();

                break;
            }

            case R.id.btn_end_day: {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog
                        .OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mShowEndTime.setYear(year);
                        mShowEndTime.setMonth(month + 1);
                        mShowEndTime.setDay(dayOfMonth);
                        mBtnEndDay.setText(mShowEndTime.getDayString());
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog(BussinessQueryActivity.this,
                        DatePickerDialog.THEME_TRADITIONAL, listener, mShowEndTime.getYear(),
                        mShowEndTime.getMonth() - 1, mShowEndTime.getDay());
                dialog.show();
                break;
            }

            case R.id.btn_end_hour: {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog
                        .OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        LogUtil.trace("hourOfDay:" + hourOfDay + "; minute:" + minute);

                        mShowEndTime.setHour(hourOfDay);
                        mShowEndTime.setMinute(minute);
                        mBtnEndHour.setText(mShowEndTime.getHourString());
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(BussinessQueryActivity
                        .this, TimePickerDialog.THEME_TRADITIONAL, onTimeSetListener,
                        mShowEndTime.getHour(), mShowEndTime.getMinute(), true);
                timePickerDialog.show();

                break;
            }

            case R.id.btn_search: {
                Intent intent = new Intent(BussinessQueryActivity.this, SearchRecordsActivity
                        .class);
                intent.putExtra("search_type", mBussinessType);
                intent.putExtra("start_time", mShowBeginTime.getDayString() + " " +
                        mShowBeginTime.getHourString());
                intent.putExtra("end_time", mShowEndTime.getDayString() + " " + mShowEndTime
                        .getHourString());
                this.startActivity(intent);
                break;
            }
        }
    }


}
