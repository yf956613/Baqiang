package com.arthur.appupdate;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * Created by Administrator on 2018/3/22 0022.
 */

@Table(name = "Testdata")
public class ScannerDate {
    @Column(name = "id", isId = true, autoGen = true, property = "NOT NULL")
    private int id;

    @Column(name = "date")
    private Date time;

    public ScannerDate() {

    }

    public ScannerDate(Date time) {
        this.time = time;
    }
}
