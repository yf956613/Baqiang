package com.jiebao.baqiang.activity;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.adapter.SearchRecordsAdapter;
import com.jiebao.baqiang.data.arrival.CargoArrivalFileContent;
import com.jiebao.baqiang.data.arrival.UnloadArrivalFileContent;
import com.jiebao.baqiang.data.bean.CommonDbHelperToUploadFile;
import com.jiebao.baqiang.data.bean.IDbHelperToUploadFileCallback;
import com.jiebao.baqiang.data.bean.IFileContentBean;
import com.jiebao.baqiang.data.db.DaojianDBHelper;
import com.jiebao.baqiang.data.db.FajianDBHelper;
import com.jiebao.baqiang.data.db.LiucangDBHelper;
import com.jiebao.baqiang.data.db.SalesServiceDBHelper;
import com.jiebao.baqiang.data.db.StayHouseReasonDBHelper;
import com.jiebao.baqiang.data.db.XcdjDBHelper;
import com.jiebao.baqiang.data.db.ZcFajianDBHelper;
import com.jiebao.baqiang.data.dispatch.ShipmentFileContent;
import com.jiebao.baqiang.data.stay.StayHouseFileContent;
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
        final String searchType = intent.getStringExtra("search_type");
        final String beginTime = intent.getStringExtra("start_time");
        String endTime = intent.getStringExtra("end_time");
        // type:装车发件; begin:2018-3-22 00:00; end:2018-3-22 23:59 -->
        // 20180320204809
        // begin:20180322000000; end:20180322235959
        LogUtil.trace("begin:" + BQTimeUtil.convertSearchTime(beginTime, 1) +
                "; end:" + BQTimeUtil.convertSearchTime(endTime, 2));

        try {
            long mBeginDate = new SimpleDateFormat("yyyyMMddHHmmss").parse
                    (BQTimeUtil
                            .convertSearchTime(beginTime, 1)).getTime();
            long mEndDate = new SimpleDateFormat("yyyyMMddHHmmss").parse
                    (BQTimeUtil
                            .convertSearchTime(endTime, 2)).getTime();

            if (Constant.SEARCH_NAME_ZCFJ.equals(searchType)) {
                setHeaderLeftViewText("装车发件查询");
                mSearchFlag = SearchType.ZCFJ;

                mTvRecordsAll.setText("" + ZcFajianDBHelper
                        .findTimeLimitedUsableRecords
                                (mBeginDate, mEndDate));
                mTvRecordsUnload.setText("" + ZcFajianDBHelper
                        .findTimeLimitedUnloadRecords
                                (mBeginDate, mEndDate));

                mListData = (List<IFileContentBean>) (List<?>) ZcFajianDBHelper
                        .getLimitedTimeRecords(mBeginDate, mEndDate);
            } else if (Constant.SEARCH_NAME_XCDJ.equals(searchType)) {
                setHeaderLeftViewText("卸车到件查询");
                mSearchFlag = SearchType.XCDJ;

                mTvRecordsAll.setText("" + XcdjDBHelper
                        .findTimeLimitedUsableRecords(mBeginDate,
                                mEndDate));
                mTvRecordsUnload.setText("" + XcdjDBHelper
                        .findTimeLimitedUnloadRecords
                                (mBeginDate, mEndDate));

                mListData = (List<IFileContentBean>) (List<?>) XcdjDBHelper
                        .getLimitedTimeRecords
                                (mBeginDate, mEndDate);
            } else if (Constant.SEARCH_NAME_DJ.equals(searchType)) {
                setHeaderLeftViewText("到件查询");
                mSearchFlag = SearchType.DJ;

                mTvRecordsAll.setText("" + DaojianDBHelper
                        .findTimeLimitedUsableRecords
                                (mBeginDate, mEndDate));
                mTvRecordsUnload.setText("" + DaojianDBHelper
                        .findTimeLimitedUnloadRecords
                                (mBeginDate, mEndDate));

                mListData = (List<IFileContentBean>) (List<?>) DaojianDBHelper
                        .getLimitedTimeRecords(mBeginDate, mEndDate);
            } else if (Constant.SEARCH_NAME_FJ.equals(searchType)) {
                setHeaderLeftViewText("发件查询");
                mSearchFlag = SearchType.FJ;

                mTvRecordsAll.setText("" + FajianDBHelper
                        .findTimeLimitedUsableRecords
                                (mBeginDate, mEndDate));
                mTvRecordsUnload.setText("" + FajianDBHelper
                        .findTimeLimitedUnloadRecords
                                (mBeginDate, mEndDate));

                mListData = (List<IFileContentBean>) (List<?>) FajianDBHelper
                        .getLimitedTimeRecords(mBeginDate, mEndDate);
            } else if (Constant.SEARCH_NAME_LCJ.equals(searchType)) {
                setHeaderLeftViewText("留仓件查询");
                mSearchFlag = SearchType.LCJ;

                mTvRecordsAll.setText("" + LiucangDBHelper
                        .findTimeLimitedUsableRecords
                                (mBeginDate, mEndDate));
                mTvRecordsUnload.setText("" + LiucangDBHelper
                        .findTimeLimitedUnloadRecords
                                (mBeginDate, mEndDate));

                mListData = (List<IFileContentBean>) (List<?>) LiucangDBHelper
                        .getLimitedTimeRecords(mBeginDate, mEndDate);
            } else {
                // do nothing
            }

            if (null != mListData && mListData.size() != 0) {
                mSearchRecordsAdapter = new SearchRecordsAdapter
                        (SearchRecordsActivity.this, mListData);
                mListViewData.setAdapter(mSearchRecordsAdapter);
                mListViewData.setOnItemClickListener(new AdapterView
                        .OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        LogUtil.trace("position:" + position + "; id:" + id);

                        final IFileContentBean bean = mListData.get(position);
                        if (bean != null) {
                            final AlertDialog dialog = new AlertDialog.Builder
                                    (SearchRecordsActivity.this).create();
                            dialog.setView(LayoutInflater.from
                                    (SearchRecordsActivity.this)
                                    .inflate(R.layout
                                            .dialog_record_detail_info, null));
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setTitle("记录详情");
                            dialog.show();

                            Button btnCancel = dialog.findViewById(R.id
                                    .btn_cancel);
                            btnCancel.setOnClickListener(new View
                                    .OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            Button btnRedoUpload = dialog.findViewById(R.id
                                    .btn_redo_upload);
                            if ("Unload".equals(bean.getStatus())) {
                                btnRedoUpload.setText("上传");
                            } else {
                                // do nothing
                            }

                            btnRedoUpload.setOnClickListener(new View
                                    .OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    if (!NetworkUtils.isNetworkConnected
                                            (SearchRecordsActivity
                                                    .this)) {
                                        Toast.makeText(SearchRecordsActivity
                                                        .this, "网络不可用，请检查网络",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        dialog.dismiss();
                                        // 网络可用，显示Dialog
                                        showLoadinDialog();
                                    }

                                    if (Constant.SEARCH_NAME_ZCFJ.equals
                                            (searchType)) {
                                        final ZCFajianFileContent
                                                zcFajianFileContent =
                                                (ZCFajianFileContent) bean;
                                        new CommonDbHelperToUploadFile<ZCFajianFileContent>()
                                                .setCallbackListener(new IDbHelperToUploadFileCallback() {

                                                    @Override
                                                    public boolean onSuccess
                                                            (String s) {
                                                        // 刷新UI，重写执行查询操作
                                                        syncViewAfterUpload
                                                                (Constant
                                                                        .SYNC_UNLOAD_DATA_TYPE_ZCFJ);
                                                        closeLoadinDialog();
                                                        return true;
                                                    }

                                                    @Override
                                                    public boolean onError
                                                            (Throwable
                                                                     throwable, boolean b) {
                                                        return false;
                                                    }

                                                    @Override
                                                    public boolean onFinish() {
                                                        return false;
                                                    }
                                                }).uploadSingleRecord
                                                (zcFajianFileContent);
                                    } else if (Constant.SEARCH_NAME_XCDJ.equals
                                            (searchType)) {
                                        final UnloadArrivalFileContent
                                                unloadArrivalFileContent =
                                                (UnloadArrivalFileContent) bean;
                                        new CommonDbHelperToUploadFile<UnloadArrivalFileContent>
                                                ().setCallbackListener(new IDbHelperToUploadFileCallback() {

                                            @Override
                                            public boolean onSuccess(String s) {
                                                // 刷新UI，重写执行查询操作
                                                syncViewAfterUpload(Constant
                                                        .SYNC_UNLOAD_DATA_TYPE_XCDJ);
                                                closeLoadinDialog();
                                                return true;
                                            }

                                            @Override
                                            public boolean onError(Throwable
                                                                           throwable, boolean b) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onFinish() {
                                                return false;
                                            }
                                        }).uploadSingleRecord
                                                (unloadArrivalFileContent);
                                    } else if (Constant.SEARCH_NAME_DJ.equals
                                            (searchType)) {
                                        final CargoArrivalFileContent
                                                cargoArrivalFileContent =
                                                (CargoArrivalFileContent) bean;
                                        new CommonDbHelperToUploadFile<CargoArrivalFileContent>()
                                                .setCallbackListener(new IDbHelperToUploadFileCallback() {

                                                    @Override
                                                    public boolean onSuccess
                                                            (String s) {
                                                        // 刷新UI，重写执行查询操作
                                                        syncViewAfterUpload
                                                                (Constant
                                                                        .SYNC_UNLOAD_DATA_TYPE_DJ);
                                                        closeLoadinDialog();
                                                        return true;
                                                    }

                                                    @Override
                                                    public boolean onError
                                                            (Throwable
                                                                     throwable, boolean b) {
                                                        return false;
                                                    }

                                                    @Override
                                                    public boolean onFinish() {
                                                        return false;
                                                    }
                                                }).uploadSingleRecord
                                                (cargoArrivalFileContent);
                                    } else if (Constant.SEARCH_NAME_FJ.equals
                                            (searchType)) {
                                        final ShipmentFileContent
                                                shipmentFileContent =
                                                (ShipmentFileContent) bean;
                                        new CommonDbHelperToUploadFile<ShipmentFileContent>()
                                                .setCallbackListener(new IDbHelperToUploadFileCallback() {

                                                    @Override
                                                    public boolean onSuccess
                                                            (String s) {
                                                        // 刷新UI，重写执行查询操作
                                                        syncViewAfterUpload
                                                                (Constant
                                                                        .SYNC_UNLOAD_DATA_TYPE_FJ);
                                                        closeLoadinDialog();
                                                        return true;
                                                    }

                                                    @Override
                                                    public boolean onError
                                                            (Throwable
                                                                     throwable, boolean b) {
                                                        return false;
                                                    }

                                                    @Override
                                                    public boolean onFinish() {
                                                        return false;
                                                    }
                                                }).uploadSingleRecord
                                                (shipmentFileContent);
                                    } else if (Constant.SEARCH_NAME_LCJ
                                            .equals(searchType)) {
                                        final StayHouseFileContent
                                                stayHouseFileContent =
                                                (StayHouseFileContent) bean;
                                        new CommonDbHelperToUploadFile<StayHouseFileContent>()
                                                .setCallbackListener(new IDbHelperToUploadFileCallback() {

                                                    @Override
                                                    public boolean onSuccess
                                                            (String s) {
                                                        // 刷新UI，重写执行查询操作
                                                        syncViewAfterUpload
                                                                (Constant
                                                                        .SYNC_UNLOAD_DATA_TYPE_LCJ);
                                                        closeLoadinDialog();
                                                        return true;
                                                    }

                                                    @Override
                                                    public boolean onError
                                                            (Throwable
                                                                     throwable, boolean b) {
                                                        return false;
                                                    }

                                                    @Override
                                                    public boolean onFinish() {
                                                        return false;
                                                    }
                                                }).uploadSingleRecord
                                                (stayHouseFileContent);
                                    } else {
                                        // do nothing
                                    }
                                }
                            }); // end for on click listener

                            Button btnDelete = dialog.findViewById(R.id
                                    .btn_delete);
                            if ("Load".equals(bean.getStatus())) {
                                btnDelete.setVisibility(View.GONE);
                            } else {
                                // do nothing
                            }
                            btnDelete.setOnClickListener(new View
                                    .OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    if (Constant.SEARCH_NAME_ZCFJ.equals
                                            (searchType)) {
                                        if (ZcFajianDBHelper.isRecordUpload
                                                (bean.getId())) {
                                            Toast.makeText
                                                    (SearchRecordsActivity.this,
                                                            "记录已上传，不能删除", Toast
                                                                    .LENGTH_SHORT)
                                                    .show();
                                        } else {
                                            if (ZcFajianDBHelper
                                                    .deleteFindedBean
                                                            (bean.getId
                                                                    ())) {
                                                syncViewAfterUpload(Constant
                                                        .SYNC_UNLOAD_DATA_TYPE_ZCFJ);
                                            }
                                        }
                                    } else if (Constant.SEARCH_NAME_XCDJ
                                            .equals(searchType)) {
                                        if (XcdjDBHelper.isRecordUpload
                                                (bean.getId())) {
                                            Toast.makeText
                                                    (SearchRecordsActivity.this,
                                                            "记录已上传，不能删除", Toast
                                                                    .LENGTH_SHORT).show();
                                        } else {
                                            if (XcdjDBHelper.deleteFindedBean
                                                    (bean.getId())) {
                                                syncViewAfterUpload(Constant
                                                        .SYNC_UNLOAD_DATA_TYPE_XCDJ);
                                            }
                                        }
                                    } else if (Constant.SEARCH_NAME_DJ.equals
                                            (searchType)) {
                                        if (DaojianDBHelper.isRecordUpload
                                                (bean.getId())) {
                                            Toast.makeText
                                                    (SearchRecordsActivity.this,
                                                            "记录已上传，不能删除", Toast
                                                                    .LENGTH_SHORT).show();
                                        } else {
                                            if (DaojianDBHelper.deleteFindedBean
                                                    (bean.getId())) {
                                                syncViewAfterUpload(Constant
                                                        .SYNC_UNLOAD_DATA_TYPE_DJ);
                                            }
                                        }
                                    } else if (Constant.SEARCH_NAME_FJ.equals
                                            (searchType)) {
                                        if (FajianDBHelper.isRecordUpload
                                                (bean.getId())) {
                                            Toast.makeText
                                                    (SearchRecordsActivity.this,
                                                            "记录已上传，不能删除", Toast
                                                                    .LENGTH_SHORT).show();
                                        } else {
                                            if (FajianDBHelper.deleteFindedBean
                                                    (bean.getId())) {
                                                syncViewAfterUpload(Constant
                                                        .SYNC_UNLOAD_DATA_TYPE_FJ);
                                            }
                                        }
                                    } else if (Constant.SEARCH_NAME_LCJ
                                            .equals(searchType)) {
                                        if (LiucangDBHelper.isRecordUpload
                                                (bean.getId())) {
                                            Toast.makeText
                                                    (SearchRecordsActivity.this,
                                                            "记录已上传，不能删除", Toast
                                                                    .LENGTH_SHORT).show();
                                        } else {
                                            if (LiucangDBHelper.deleteFindedBean
                                                    (bean.getId())) {
                                                syncViewAfterUpload(Constant
                                                        .SYNC_UNLOAD_DATA_TYPE_LCJ);
                                            }
                                        }
                                    }

                                    dialog.dismiss();
                                }
                            }); // end for delete on click listener


                            initDialogView(dialog, searchType);
                            // 运单号
                            TextView tvShipmentNumber = dialog.findViewById(R.id
                                    .tv_shipment_number);
                            tvShipmentNumber.setText(bean.getShipmentNumber());
                            // 快件类型
                            TextView tvShipmentType = dialog.findViewById(R
                                    .id.tv_shipment_type);
                            tvShipmentType.setText(bean.getShipmentType());
                            // 上一站
                            TextView tvPreviousStation = dialog.findViewById
                                    (R.id.tv_previous_station);
                            tvPreviousStation.setText(SalesServiceDBHelper
                                    .getServerNameFromId(bean.getPreviousStation
                                            ()));
                            // 下一站
                            TextView tvNextStation = dialog.findViewById(R.id
                                    .tv_next_station);
                            tvNextStation.setText(SalesServiceDBHelper
                                    .getServerNameFromId(bean.getNextStation
                                            ()));
                            // 留仓原因
                            TextView tvStayhouseReason = dialog.findViewById
                                    (R.id.tv_stay_house_reason);
                            tvStayhouseReason.setText(StayHouseReasonDBHelper
                                    .getReasonNameFromId(bean.getStayReason()));
                            // 车辆码
                            TextView tvVehicleCode = dialog.findViewById(R.id
                                    .tv_vehicle_code);
                            tvVehicleCode.setText(bean.getVehicleID());
                            LogUtil.trace("bean:" + bean.toString());
                            // 工号
                            TextView tvOperator = dialog.findViewById(R.id
                                    .tv_operator);
                            tvOperator.setText(bean.getScanEmployeeNumber());

                            String idString = SharedUtil.getString
                                    (SearchRecordsActivity
                                            .this, Constant
                                            .PREFERENCE_KEY_SALE_SERVICE);
                            // 网点编号
                            TextView tvSaleID = dialog.findViewById(R.id
                                    .tv_sale_id);
                            tvSaleID.setText(idString);
                            // 扫描时间
                            TextView tvScanTime = dialog.findViewById(R.id
                                    .tv_scan_time);
                            tvScanTime.setText(bean.getOperateDate());
                        } else {
                            // do nothing
                        }


                    } // on item click
                });
            } else {
                // do nothing
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        mBtnUploadRedo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LogUtil.trace("mSearchFlag:" + mSearchFlag);

                // 重传当前ListView中的所有记录
                if (SearchType.ZCFJ.equals(mSearchFlag) && mListData != null
                        && mListData.size()
                        != 0) {
                    showLoadinDialog();

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
                                public boolean onError(Throwable throwable,
                                                       boolean b) {
                                    return false;
                                }

                                @Override
                                public boolean onFinish() {
                                    closeLoadinDialog();
                                    return false;
                                }
                            }).redoUploadRecords(mListData);
                } else if (SearchType.XCDJ.equals(mSearchFlag) && mListData
                        != null && mListData
                        .size() != 0) {
                    showLoadinDialog();

                    new CommonDbHelperToUploadFile<UnloadArrivalFileContent>()
                            .setCallbackListener(new IDbHelperToUploadFileCallback() {

                                @Override
                                public boolean onSuccess(String s) {
                                    // 刷新UI，重写执行查询操作
                                    syncViewAfterUpload(Constant
                                            .SYNC_UNLOAD_DATA_TYPE_XCDJ);
                                    closeLoadinDialog();
                                    return true;
                                }

                                @Override
                                public boolean onError(Throwable throwable,
                                                       boolean b) {
                                    return false;
                                }

                                @Override
                                public boolean onFinish() {
                                    closeLoadinDialog();
                                    return false;
                                }
                            }).redoUploadRecords(mListData);
                } else if (SearchType.DJ.equals(mSearchFlag) && mListData !=
                        null && mListData
                        .size() != 0) {
                    showLoadinDialog();

                    new CommonDbHelperToUploadFile<CargoArrivalFileContent>()
                            .setCallbackListener
                                    (new IDbHelperToUploadFileCallback() {

                                        @Override
                                        public boolean onSuccess(String s) {
                                            // 刷新UI，重写执行查询操作
                                            syncViewAfterUpload(Constant
                                                    .SYNC_UNLOAD_DATA_TYPE_DJ);
                                            closeLoadinDialog();
                                            return true;
                                        }

                                        @Override
                                        public boolean onError(Throwable
                                                                       throwable,
                                                               boolean b) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onFinish() {
                                            closeLoadinDialog();
                                            return false;
                                        }
                                    }).redoUploadRecords(mListData);
                } else if (SearchType.FJ.equals(mSearchFlag) && mListData !=
                        null && mListData
                        .size() != 0) {
                    showLoadinDialog();

                    new CommonDbHelperToUploadFile<ShipmentFileContent>()
                            .setCallbackListener(new IDbHelperToUploadFileCallback() {

                                @Override
                                public boolean onSuccess(String s) {
                                    // 刷新UI，重写执行查询操作
                                    syncViewAfterUpload(Constant
                                            .SYNC_UNLOAD_DATA_TYPE_FJ);
                                    closeLoadinDialog();
                                    return true;
                                }

                                @Override
                                public boolean onError(Throwable throwable,
                                                       boolean b) {
                                    return false;
                                }

                                @Override
                                public boolean onFinish() {
                                    closeLoadinDialog();
                                    return false;
                                }
                            }).redoUploadRecords(mListData);
                } else if (SearchType.LCJ.equals(mSearchFlag) && mListData !=
                        null && mListData
                        .size() != 0) {
                    showLoadinDialog();

                    new CommonDbHelperToUploadFile<StayHouseFileContent>()
                            .setCallbackListener
                                    (new IDbHelperToUploadFileCallback() {

                                        @Override
                                        public boolean onSuccess(String s) {
                                            // 刷新UI，重写执行查询操作
                                            syncViewAfterUpload(Constant
                                                    .SYNC_UNLOAD_DATA_TYPE_LCJ);
                                            closeLoadinDialog();
                                            return true;
                                        }

                                        @Override
                                        public boolean onError(Throwable
                                                                       throwable, boolean b) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onFinish() {
                                            closeLoadinDialog();
                                            return false;
                                        }
                                    }).redoUploadRecords(mListData);
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

    /**
     * 根据搜索类型，设置布局
     *
     * @param dialog
     * @param type
     */
    private void initDialogView(AlertDialog dialog, String type) {
        LinearLayout mLiShipmentId = dialog.findViewById(R.id.ll_shipment_id);
        LinearLayout mLiShipmentType = dialog.findViewById(R.id
                .ll_shipment_type);
        LinearLayout mLiPreviousStation = dialog.findViewById(R.id
                .ll_previous_station);
        LinearLayout mLiNextStation = dialog.findViewById(R.id.ll_next_station);
        LinearLayout mLiStayHouseReason = dialog.findViewById(R.id
                .ll_stay_house_reason);
        LinearLayout mLiVehicleId = dialog.findViewById(R.id.ll_vehicle_id);
        LinearLayout mLiEmployeeId = dialog.findViewById(R.id.ll_employee_id);
        LinearLayout mLiServerId = dialog.findViewById(R.id.ll_server_id);
        LinearLayout mLiScanDate = dialog.findViewById(R.id.ll_scan_date);

        if (Constant.SEARCH_NAME_ZCFJ.equals(type)) {
            mLiPreviousStation.setVisibility(View.GONE);
            mLiStayHouseReason.setVisibility(View.GONE);
        } else if (Constant.SEARCH_NAME_XCDJ.equals(type)) {
            mLiShipmentType.setVisibility(View.GONE);
            mLiNextStation.setVisibility(View.GONE);
            mLiStayHouseReason.setVisibility(View.GONE);
        } else if (Constant.SEARCH_NAME_DJ.equals(type)) {
            mLiShipmentType.setVisibility(View.GONE);
            mLiNextStation.setVisibility(View.GONE);
            mLiVehicleId.setVisibility(View.GONE);
            mLiStayHouseReason.setVisibility(View.GONE);
        } else if (Constant.SEARCH_NAME_FJ.equals(type)) {
            mLiPreviousStation.setVisibility(View.GONE);
            mLiVehicleId.setVisibility(View.GONE);
            mLiStayHouseReason.setVisibility(View.GONE);
        } else if (Constant.SEARCH_NAME_LCJ.equals(type)) {
            mLiShipmentType.setVisibility(View.GONE);
            mLiPreviousStation.setVisibility(View.GONE);
            mLiNextStation.setVisibility(View.GONE);
            mLiVehicleId.setVisibility(View.GONE);
        } else {
            // do nothing
        }
    }

}
