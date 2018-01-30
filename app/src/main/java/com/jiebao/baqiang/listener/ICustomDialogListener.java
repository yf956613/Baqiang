package com.jiebao.baqiang.listener;

public interface ICustomDialogListener {
    void onDialogPositiveClick(int requestCode, String inputArg, Object object);
    void onDialogNegativeClick(int requestCode);
}
