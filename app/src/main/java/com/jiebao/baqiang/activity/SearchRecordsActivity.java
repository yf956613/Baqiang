package com.jiebao.baqiang.activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.adapter.SearchRecordsAdapter;
import com.jiebao.baqiang.data.bean.CommonDbHelperToUploadFile;
import com.jiebao.baqiang.data.bean.IDbHelperToUploadFileCallback;
import com.jiebao.baqiang.data.bean.IFileContentBean;
import com.jiebao.baqiang.data.db.ZcFajianDBHelper;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianFileContent;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.util.BQTimeUtil;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.NetworkUtils;
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

            try {
                long mBeginDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(BQTimeUtil
                        .convertSearchTime(beginTime, 1)).getTime();
                long mEndDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(BQTimeUtil
                        .convertSearchTime(endTime, 2)).getTime();

                mTvRecordsAll.setText("" + ZcFajianDBHelper.findTimeLimitedUsableRecords
                        (mBeginDate, mEndDate));
                mTvRecordsUnload.setText("" + ZcFajianDBHelper.findTimeLimitedUnloadRecords
                        (mBeginDate, mEndDate));

                mListData = (List<IFileContentBean>) (List<?>) ZcFajianDBHelper
                        .getLimitedTimeRecords(mBeginDate, mEndDate);
                if (null != mListData && mListData.size() != 0) {
                    mSearchRecordsAdapter = new SearchRecordsAdapter(SearchRecordsActivity
                            .this, mListData);
                    mListViewData.setAdapter(mSearchRecordsAdapter);
                    mListViewData.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position,
                                                long id) {
                            LogUtil.trace("position:" + position + "; id:" + id);

                            final ZCFajianFileContent bean = (ZCFajianFileContent) mListData.get
                                    (position);
                            if (bean != null) {
                                final AlertDialog dialog = new AlertDialog.Builder
                                        (SearchRecordsActivity.this).create();
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
                                        if (!NetworkUtils.isNetworkConnected(SearchRecordsActivity
                                                .this)) {
                                            Toast.makeText(SearchRecordsActivity.this,
                                                    "网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
                                            return;
                                        } else {
                                            dialog.dismiss();
                                            showLoadinDialog();
                                        }

                                        new CommonDbHelperToUploadFile<ZCFajianFileContent>()
                                                .setCallbackListener(new IDbHelperToUploadFileCallback() {

                                            @Override
                                            public boolean onSuccess(String s) {
                                                // 刷新UI，重写执行查询操作
                                                syncViewAfterUpload(Constant
                                                        .SYNC_UNLOAD_DATA_TYPE_ZCFJ);
                                                closeLoadinDialog();
                                                return true;
                                            }

                                            @Override
                                            public boolean onError(Throwable throwable, boolean b) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onFinish() {
                                                return false;
                                            }
                                        }).uploadSingleRecord(bean);
                                    }
                                });

                                Button btnDelete = dialog.findViewById(R.id.btn_delete);
                                if ("Load".equals(bean.getmStatus())) {
                                    btnDelete.setVisibility(View.GONE);
                                } else {
                                    // do nothing
                                }
                                btnDelete.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        if (ZcFajianDBHelper.deleteFindedBean(bean
                                                .getShipmentNumber())) {
                                            syncViewAfterUpload(Constant
                                                    .SYNC_UNLOAD_DATA_TYPE_ZCFJ);
                                        }
                                        dialog.dismiss();
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
                    // 重传当前ListView中的所有记录
                    showLoadinDialog();
                    new CommonDbHelperToUploadFile<ZCFajianFileContent>().setCallbackListener(new IDbHelperToUploadFileCallback() {

                        @Override
                        public boolean onSuccess(String s) {
                            // 刷新UI，重写执行查询操作
                            syncViewAfterUpload(Constant.SYNC_UNLOAD_DATA_TYPE_ZCFJ);
                            closeLoadinDialog();
                            return true;
                        }

                        @Override
                        public boolean onError(Throwable throwable, boolean b) {
                            return false;
                        }

                        @Override
                        public boolean onFinish() {
                            return false;
                        }
                    }).redoUploadRecords(mListData);
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
    public void syncViewAfterUpload(int updateType) {
        super.syncViewAfterUpload(updateType);

        initData();
        if (mSearchRecordsAdapter != null) {
            mSearchRecordsAdapter.notifyDataSetChanged();
        }
    }

}
