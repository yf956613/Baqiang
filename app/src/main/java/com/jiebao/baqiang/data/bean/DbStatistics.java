package com.jiebao.baqiang.data.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

@Table(name = "dbstatistics")
public class DbStatistics {
    @Column(name = "id", isId = true, autoGen = true, property = "NOT NULL")
    int id;

    @Column(name = "表名")
    String name;

    @Column(name = "总记录数")
    int allRecords;

    @Column(name = "未上传记录数")
    int unloadRecords;

    public DbStatistics() {

    }

    public DbStatistics(String name, int allRecords, int unloadRecords) {
        this.name = name;
        this.allRecords = allRecords;
        this.unloadRecords = unloadRecords;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAllRecords() {
        return allRecords;
    }

    public void setAllRecords(int allRecords) {
        this.allRecords = allRecords;
    }

    public int getUnloadRecords() {
        return unloadRecords;
    }

    public void setUnloadRecords(int unloadRecords) {
        this.unloadRecords = unloadRecords;
    }
}
