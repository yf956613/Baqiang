package com.jiebao.baqiang.activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.adapter.SearchRecordsAdapter;
import com.jiebao.baqiang.data.bean.IFileContentBean;
import com.jiebao.baqiang.data.db.ZcFajianDBHelper;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianFileContent;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCfajianUploadFile;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.util.BQTimeUtil;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

public class SearchRecordsActivity extends BaseActivityWithTitleAndNumber {

    @ViewInject(R.id.tv_records_all)
    private TextView mTvRecordsAll;
    @ViewInject(R.id.tv_records_unload)
    private TextView mTvRecordsUnload;
    @ViewInject(R.id.listview_data)
    private ListView mListViewData;
    @ViewInject(R.id.btn_upload_redo)
    private Button mBtnUploadRedo;

    private SearchType mSearchFlag = SearchType.ZCFJ;
    private SearchRecordsAdapter mSearchRecordsAdapter = null;
    private List<IFileContentBean> mListData = null;

    public enum SearchType {
        ZCFJ, XCDJ, DJ, FJ, LCJ
    }

    @Override
    public void initView() {
        setContent(R.layout.activity_search_records);
        x.view().inject(SearchRecordsActivity.this);
    }

    @Override
    public void initData() {
        Intent intent = this.getIntent();
        // 开始时间的秒数，默认是：00；结束时间的秒数，默认是：59
        String searchType = intent.getStringExtra("search_type");
        String beginTime = intent.getStringExtra("start_time");
        String endTime = intent.getStringExtra("end_time");
        // type:装车发件; begin:2018-3-22 00:00; end:2018-3-22 23:59 --> 20180320204809
        // begin:20180322000000; end:20180322235959
        LogUtil.trace("begin:" + BQTimeUtil.convertSearchTime(beginTime, 1) + "; end:" +
                BQTimeUtil.convertSearchTime(endTime, 2));

        if (Constant.SEARCH_NAME_ZCFJ.equals(searchType)) {
            setHeaderLeftViewText("装车发件查询");
            mSearchFlag = SearchType.ZCFJ;

            mTvRecordsAll.setText("" + ZcFajianDBHelper.findUsableRecords());
            mTvRecordsUnload.setText("" + ZcFajianDBHelper.findUnloadRecords());

            try {
                mListData = (List<IFileContentBean>) (List<?>) ZcFajianDBHelper
                        .getLimitedTimeRecords(new SimpleDateFormat("yyyyMMddHHmmss").parse
                                (BQTimeUtil.convertSearchTime(beginTime, 1)).getTime(), new
                                SimpleDateFormat("yyyyMMddHHmmss").parse(BQTimeUtil
                                .convertSearchTime(endTime, 2)).getTime());

                if (null != mListData && mListData.size() != 0) {
                    mSearchRecordsAdapter = new SearchRecordsAdapter(SearchRecordsActivity.this,
                            mListData);
                    mListViewData.setAdapter(mSearchRecordsAdapter);
                    mListViewData.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position,
                                                long id) {
                            LogUtil.trace("position:" + position + "; id:" + id);

                            final ZCFajianFileContent bean = (ZCFajianFileContent) mListData.get
                                    (position);

                            if (bean != null) {
                                // 点击事件：弹出该记录的详细信息
                                final AlertDialog dialog = new AlertDialog.Builder
                                        (SearchRecordsActivity
                                        .this).create();
                                dialog.setView(LayoutInflater.from(SearchRecordsActivity.this)
                                        .inflate(R.layout.alert_dialog_search_detail, null));
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.show();

                                Button btnCancel = dialog.findViewById(R.id.btn_cancel);
                                btnCancel.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                                Button btnRedoUpload = dialog.findViewById(R.id.btn_redo_upload);
                                if ("Unload".equals(bean.getmStatus())) {
                                    btnRedoUpload.setText("上传");
                                } else {
                                    // do nothing
                                }
                                btnRedoUpload.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        ZCfajianUploadFile.singleRecordUpload(bean);
                                        dialog.dismiss();

                                        // TODO 刷新UI，重写执行查询操作
                                        syncViewAfterUpload();
                                    }
                                });

                                Button btnDelete = dialog.findViewById(R.id.btn_delete);
                                if ("Load".equals(bean.getmStatus())) {
                                    // 已上传，不能执行删除操作
                                    btnDelete.setVisibility(View.GONE);
                                } else {
                                    // do nothing
                                }
                                btnDelete.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        ZcFajianDBHelper.deleteFindedBean(bean.getShipmentNumber());
                                        dialog.dismiss();

                                        // TODO 刷新UI，重写执行查询操作
                                        syncViewAfterUpload();
                                    }
                                });

                                TextView tvShipmentNumber = dialog.findViewById(R.id
                                        .tv_shipment_number);
                                tvShipmentNumber.setText(bean.getShipmentNumber());

                                TextView tvOperator = dialog.findViewById(R.id.tv_operator);
                                tvOperator.setText(bean.getScanEmployeeNumber());

                                String idString = SharedUtil.getString(SearchRecordsActivity
                                        .this, Constant.PREFERENCE_KEY_SALE_SERVICE);
                                LogUtil.trace("idString:" + idString);

                                TextView tvOperatorID = dialog.findViewById(R.id.tv_operator_id);
                                tvOperatorID.setText(bean.getScanEmployeeNumber().replace
                                        (idString, ""));
                                TextView tvSaleID = dialog.findViewById(R.id.tv_sale_id);
                                tvSaleID.setText(idString);

                                TextView tvScanTime = dialog.findViewById(R.id.tv_scan_time);
                                tvScanTime.setText(bean.getOperateDate());
                            }
                        }
                    });
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (Constant.SEARCH_NAME_XCDJ.equals(searchType)) {
            setHeaderLeftViewText("卸车到件查询");
            mSearchFlag = SearchType.XCDJ;
        } else if (Constant.SEARCH_NAME_DJ.equals(searchType)) {
            setHeaderLeftViewText("到件查询");
            mSearchFlag = SearchType.DJ;
        } else if (Constant.SEARCH_NAME_FJ.equals(searchType)) {
            setHeaderLeftViewText("发件查询");
            mSearchFlag = SearchType.FJ;
        } else if (Constant.SEARCH_NAME_LCJ.equals(searchType)) {
            setHeaderLeftViewText("留仓件查询");
            mSearchFlag = SearchType.LCJ;
        } else {
            // do nothing
        }

        mBtnUploadRedo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUtil.trace("mSearchFlag:" + mSearchFlag);

                if (SearchType.ZCFJ.equals(mSearchFlag)) {
                    ZCfajianUploadFile.redoUploadRecords(mListData);

                    syncViewAfterUpload();
                } else if (SearchType.XCDJ.equals(mSearchFlag)) {
                    setHeaderLeftViewText("卸车到件查询");
                    mSearchFlag = SearchType.XCDJ;
                } else if (SearchType.DJ.equals(mSearchFlag)) {
                    setHeaderLeftViewText("到件查询");
                    mSearchFlag = SearchType.DJ;
                } else if (SearchType.FJ.equals(mSearchFlag)) {
                    setHeaderLeftViewText("发件查询");
                    mSearchFlag = SearchType.FJ;
                } else if (SearchType.LCJ.equals(mSearchFlag)) {
                    setHeaderLeftViewText("留仓件查询");
                    mSearchFlag = SearchType.LCJ;
                } else {
                    // do nothing
                }
            }
        });
    }

    @Override
    public void syncViewAfterUpload() {
        super.syncViewAfterUpload();

        initData();
        mSearchRecordsAdapter.notifyDataSetChanged();
    }

}
