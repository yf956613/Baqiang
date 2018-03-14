package com.jiebao.baqiang.scan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.jb.barcode.BarcodeManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.global.BeepManager;

/**
 * 扫描包装类
 */

public class ScanHelper {
    //states
    private static final int STATE_IDLE = 0;
    private static final int STATE_DECODE = 1;
    private static final int STATE_HANDSFREE = 2;
    private static final int STATE_SNAPSHOT = 4;
    private static final int STATE_VIDEO = 5;

    public static ScanHelper instance = null;
    private BarcodeManager barcodeManager;
    private long nowTime = 0;
    private long lastTime = 0;

    private BeepManager beepManager;
    private final int Handler_SHOW_RESULT = 1999;
    private final int Handler_SHOW_STAT = 2000;
    private boolean isInit = false;
    private String currentActivityClazz;
    private ScanListener currentListener;
    private final String TAG = "ScanHelper";
    private boolean isActivityFocus = true;  //是否只有actiivty有焦点才能扫描
    //private BarCodeReader bcr = null;
    private int state = STATE_IDLE;

    private String decodeDataString;
    private String decodeStatString;
    private int decodes = 0;
    private static int decCount = 0;
    private int motionEvents = 0;
    private int modechgEvents = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Handler_SHOW_RESULT:
                    if (null != msg.obj) {
                        final String barcode = msg.obj.toString().trim();
                        //String interceptCode = StringUtils.barcodeInterceptionMode(barcode);
                        parseBarcode(barcode);
                    }
                    break;
            }
        }
    };

    public synchronized void parseBarcode(final String barcode) {
//        if (!checkCanParse()) {
//            return;
//        }
//        if (isActivityFocus) {
//            if (!BaqiangApplication.mTopActivity.hasWindowFocus()) {
//                beepManager.notifySound();
//                return;
//            } else {
        beepManager.play();
//            }
//        }
        currentListener.fillCode(barcode);
    }

    private ScanHelper() {
    }

    public synchronized static ScanHelper getInstance() {
        if (instance == null) {
            instance = new ScanHelper();
        }
        return instance;
    }

    private void loadHardScanModule(Context activity) {
        BaqiangApplication.getContext().sendBroadcast(new Intent("ReleaseCom"));
        barcodeManager = BarcodeManager.getInstance();
        barcodeManager.Barcode_Open(activity, dataReceived);
    }

    public void Open_Barcode(Context activity) {
        try {
            if (!isInit) {
                if (BaqiangApplication.isSoftDecodeScan) {
                    //   softBarcodeDecoder = new SoftBarcodeDecoder();
                    //   softBarcodeDecoder.openModule(activity);
                } else {
                    loadHardScanModule(activity);
                }
                /**
                 * 监听橙色按钮按键广播
                 */
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("com.jb.action.F4key");
                intentFilter.addAction("com.jb.action.Key");
                intentFilter.addAction("ReLoadCom");
                intentFilter.addAction("ReleaseCom");
                intentFilter.addAction("com.jb.action.SCAN_SWITCH");
                BaqiangApplication.getContext().registerReceiver(f4Receiver, intentFilter);
                isInit = true;
            }
            if (beepManager == null) {
                beepManager = new BeepManager(BaqiangApplication.getContext(), true, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeHardScanModule() {
        BaqiangApplication.getContext().sendBroadcast(new Intent("ReLoadCom"));
        if (null != barcodeManager) {
            barcodeManager.Barcode_Close();
            barcodeManager.Barcode_Stop();
        }
    }

    public void Close_Barcode() {
        try {
            BaqiangApplication.getContext().unregisterReceiver(f4Receiver);
            currentListener = null;
            currentActivityClazz = null;
            // if (BaqiangApplication.isSoftDecodeScan) {
            //     closeSoftScanModule();
            // } else {
            closeHardScanModule();
            //  }
            isInit = false;
        } catch (Exception ignored) {

        }
    }

    public void setScanListener(String activityName, ScanListener scanListener) {
        setScanListener(activityName, scanListener, true);
    }

    public void setScanListener(String activityName, ScanListener scanListener, boolean
            isActivityFocus) {
        this.currentActivityClazz = activityName;
        this.currentListener = scanListener;
        this.isActivityFocus = isActivityFocus;
    }

    private BarcodeManager.Callback dataReceived = new BarcodeManager.Callback() {

        @Override
        public void Barcode_Read(byte[] buffer, String codeId, int errorCode) {
            synchronized (ScanHelper.this) {
                if (null != buffer/* && currentActivityClazz != null*/) {
                    Message msg = new Message();
                    msg.what = Handler_SHOW_RESULT;
                    msg.obj = new String(buffer);
                    mHandler.sendMessage(msg);
                    BarcodeManager.getInstance().Barcode_Stop();
                }
            }
        }
    };

    /**
     * 捕获扫描物理按键广播
     */
    private BroadcastReceiver f4Receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
//            if (!checkCanParse()) {
//                return;
//            }
            if (intent.hasExtra("F4key")) {
                if (intent.getStringExtra("F4key").equals("down")) {
                    Log.e(TAG, "key down");
                    //  if (BaqiangApplication.isSoftDecodeScan) {
                    //  softBarcodeDecoder.doDecode();
                    //  } else {
                    if (null != barcodeManager) {
                        nowTime = System.currentTimeMillis();
                        if (nowTime - lastTime > 300) {
                            lastTime = nowTime;
                            barcodeManager.Barcode_Stop();
                            if (null != barcodeManager) {
                                barcodeManager.Barcode_Start();
                            }
                        }
                    }
                    //  }
                } else if (intent.getStringExtra("F4key").equals("up")) {
                    Log.e("ScanHelper", "key up");
                }
            }
        }
    };

    //当前的Activity
    private String getCurrentActivity() {
        return BaqiangApplication.mTopActivity.getClass().getName();
    }

    //是否可以处理
    private boolean checkCanParse() {
        if (currentActivityClazz == null || currentListener == null) return false;

        String topActivity = getCurrentActivity();
        Log.e(TAG, "receive broadcast-- current activity---" + currentActivityClazz + "   top " +
                "activity  " + topActivity);
        if (!currentActivityClazz.equals(topActivity)) return false;

        return true;
    }

    public BeepManager getBeepManager() {
        return beepManager;
    }

//    public  class SoftBarcodeDecoder{
//        private int trigMode = BarCodeReader.ParamVal.LEVEL;
//        private BarCodeReader bcr = null;
//
//        public int setIdle() {
//            int prevState = state;
//            int ret = prevState;        //for states taking time to chg/end
//
//            state = STATE_IDLE;
//            switch (prevState) {
//                case STATE_HANDSFREE:
//                    resetTrigger();
//                case STATE_DECODE:
//                    if (currentListener != null) {
//                        try {
//                            currentListener.dspStat("decode stopped");
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    if (bcr != null)
//                        bcr.stopDecode();
//                    break;
//
//                case STATE_VIDEO:
//                    bcr.stopPreview();
//                    break;
//
//                case STATE_SNAPSHOT:
//                    ret = STATE_IDLE;
//                    break;
//
//                default:
//                    ret = STATE_IDLE;
//            }
//            return ret;
//        }
//
//        public void doDecode() {
//            if (setIdle() != STATE_IDLE)
//                return;
//            state = STATE_DECODE;
//            decCount = 0;
//            decodeDataString = new String("");
//            decodeStatString = new String("");
//            try {
//                bcr.startDecode(); // start decode (callback gets results)
//            } catch (Exception e) {
//                Log.v(TAG, "open excp:" + e);
//            }
//        }
//
//        void resetTrigger() {
//            doSetParam(BarCodeReader.ParamNum.PRIM_TRIG_MODE, BarCodeReader.ParamVal.LEVEL);
//            trigMode = BarCodeReader.ParamVal.LEVEL;
//        }
//
//        public int doSetParam(int num, int val) {
//            setIdle();
//            String s = "";
//            int ret = bcr.setParameter(num, val);
//            if (ret != BarCodeReader.BCR_ERROR) {
//                if (num == BarCodeReader.ParamNum.PRIM_TRIG_MODE) {
//                    trigMode = val;
//                    if (val == BarCodeReader.ParamVal.HANDSFREE) {
//                        s = "HandsFree";
//                    } else if (val == BarCodeReader.ParamVal.AUTO_AIM) {
//                        s = "AutoAim";
//                        ret = bcr.startHandsFreeDecode(BarCodeReader.ParamVal.AUTO_AIM);
//                        if (ret != BarCodeReader.BCR_SUCCESS) {
//                            currentListener.dspStat("AUtoAIm start FAILED");
//                        }
//                    } else if (val == BarCodeReader.ParamVal.LEVEL) {
//                        s = "Level";
//                    }
//                }
//            } else
//                s = " FAILED (" + ret + ")";
//
//            currentListener.dspStat("Set #" + num + " to " + val + " " + s);
//            return ret;
//        }
//
//        private boolean isHandsFree() {
//            return (trigMode == BarCodeReader.ParamVal.HANDSFREE);
//        }
//
//        private boolean isAutoAim() {
//            return (trigMode == BarCodeReader.ParamVal.AUTO_AIM);
//        }
//
//        public void stopDecode(){
//            bcr.stopDecode();
//        }

//        public void openModule(Context context) {
//            System.loadLibrary("IAL");
//            System.loadLibrary("SDL");
//            if (android.os.Build.VERSION.SDK_INT >= 19)
//                System.loadLibrary("barcodereader44"); // Android 4.4
//            else if (android.os.Build.VERSION.SDK_INT >= 18)
//                System.loadLibrary("barcodereader43"); // Android 4.3
//            else
//                System.loadLibrary("barcodereader");   // Android 2.3 - Android 4.2
//
//            try {
//                Log.v(TAG, "SDLService onStart bcr: " + bcr);
//                if (bcr == null) {
//                    if (android.os.Build.VERSION.SDK_INT >= 18) {
//                        bcr = BarCodeReader.open(1, context); // Android 4.3 and above
//                        Log.v(TAG, "SDLService onStart bcr no: " + bcr);
//                    } else
//                        bcr = BarCodeReader.open(1); // Android 2.3
//                }
//                if (bcr == null) {
//                    if (currentListener != null)
//                        currentListener.dspStat("ERROR open failed");
//                    Log.v(TAG, "sdl open failed" + " mScanListener: " + currentListener);
//                    return;
//                }
//                bcr.setDecodeCallback(ScanHelper.this);
//
//                bcr.setErrorCallback(ScanHelper.this);
//
//                bcr.setParameter(687, 4); // 4 - omnidirectional
//                bcr.setParameter(7, 1);  //ENAble CODABAR   　
//                bcr.setParameter(11, 1);   //ENAbleMSI
//                bcr.setParameter(764, 1);//reduce power
//                bcr.setParameter(765, 0);
//                bcr.setParameter(137, 0);//scan the same code
//                bcr.setParameter(716, 1);//support phone mode
//            } catch (Exception e) {
//                Log.v(TAG, "open excp:" + e + " mScanListener: " + currentListener);
//            }
//        }
//
//        public void release() {
//            if (bcr != null) {
//                setIdle();
//                bcr.stopDecode();
//                bcr.release();
//                bcr = null;
//                Log.v(TAG, "destory sdl decode");
//            }
//        }
//    }
//
//    public void onDecodeComplete(int symbology, int length, byte[] data, BarCodeReader reader) {
//        if (state == STATE_DECODE)
//            state = STATE_IDLE;
//
//        // Get the decode count
//        if (length == BarCodeReader.DECODE_STATUS_MULTI_DEC_COUNT)
//            decCount = symbology;
//
//        if (length > 0) {
//            byte[] bs = new byte[length];
//            if (softBarcodeDecoder.isHandsFree() == false && softBarcodeDecoder.isAutoAim() ==
// false){
//                softBarcodeDecoder.stopDecode();
//            }
//
//            ++decodes;
//            {
//                if (symbology == 0x99)    //type 99?
//                {
//                    symbology = data[0];
//                    int n = data[1];
//                    int s = 2;
//                    int d = 0;
//                    int len = 0;
//                    byte d99[] = new byte[data.length];
//                    for (int i = 0; i < n; ++i) {
//                        s += 2;
//                        len = data[s++];
//                        System.arraycopy(data, s, d99, d, len);
//                        s += len;
//                        d += len;
//                    }
//                    d99[d] = 0;
//                    data = d99;
//                }
//
//                System.arraycopy(data, 0, bs, 0, length);
//                decodeStatString = new String("[" + decodes + "] type: " + symbology + " len: "
// + length);
//                decodeDataString = new String(bs);
//                //Log.v(TAG,"onDecodeComplete decodeDataString: "+decodeDataString+"
// length:"+length+" bs: "+bs.length);
//                if (currentListener != null) {
//                    Message msg1 = mHandler.obtainMessage();
//                    msg1.what = Handler_SHOW_RESULT;
//                    msg1.obj = decodeDataString;
//                    mHandler.sendMessage(msg1);
//                    Message msg2 = mHandler.obtainMessage();
//                    msg2.what = Handler_SHOW_STAT;
//                    msg2.obj = decodeStatString;
//                    mHandler.sendMessage(msg2);
//                }
//            }
//        } else {
//            if (currentListener != null) {
//                try {
//                    currentListener.fillCode("");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            switch (length) {
//                case BarCodeReader.DECODE_STATUS_TIMEOUT:
//                    Log.v(TAG, "decode timed out");
//                    break;
//
//                case BarCodeReader.DECODE_STATUS_CANCELED:
//                    Log.v(TAG, "decode cancelled");
//                    break;
//
//                case BarCodeReader.DECODE_STATUS_ERROR:
//                default:
//                    Log.v(TAG, "decode failed");
//                    break;
//            }
//        }
//    }
//
//    private void closeSoftScanModule() {
//        softBarcodeDecoder.release();
//        //BaqiangApplication.getContext().sendBroadcast(new Intent("SdlScanServiceDestroy"));
//    }

//    @Override
//    public void onError(int error, BarCodeReader reader) {
//        // TODO Auto-generated method stub
//    }
//
//    public void onEvent(int event, int info, byte[] data, BarCodeReader reader) {
//        switch (event) {
//            case BarCodeReader.BCRDR_EVENT_SCAN_MODE_CHANGED:
//                ++modechgEvents;
//                if (currentListener != null) {
//                    try {
//                        currentListener.dspStat("Scan Mode Changed Event (#" + modechgEvents +
// ")");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
//
//            case BarCodeReader.BCRDR_EVENT_MOTION_DETECTED:
//                ++motionEvents;
//                if (currentListener != null) {
//                    try {
//                        currentListener.dspStat("Motion Detect Event (#" + motionEvents + ")");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
//
//            case BarCodeReader.BCRDR_EVENT_SCANNER_RESET:
//                if (currentListener != null) {
//                    try {
//                        currentListener.dspStat("Reset Event");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
//
//            default:
//                break;
//        }
//    }

    public void setActivityFocus(boolean activityFocus) {
        isActivityFocus = activityFocus;
    }
}
