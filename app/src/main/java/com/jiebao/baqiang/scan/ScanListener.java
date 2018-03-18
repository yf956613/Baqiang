package com.jiebao.baqiang.scan;

/**
 * ScanListener
 */

public interface ScanListener {
    void fillCode(String barcode);
    void dspStat(String content);

    void timeout(long time);
}
