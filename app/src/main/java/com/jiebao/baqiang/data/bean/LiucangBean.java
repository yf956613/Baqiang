package com.jiebao.baqiang.data.bean;

/**
 * Created by open on 2018/1/23.
 */
import com.google.gson.annotations.SerializedName;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 车辆码
 */
@Table(name ="car_code")
public class LiucangBean {
    @Column(
            name = "ID",
            isId = true,
            autoGen = true
    )
    private int id;

    @SerializedName("number")
    @Column(name="编号")
    private String number;

    @SerializedName("NUME")
    @Column(name="名称")
    private String NUME;

    @SerializedName("REMARKS")
    @Column(name="备注")
    private String REMARKS;
    public int get_id() {
        return id;
    }
    public void set_id(int id) {
        this.id = id;
    }

    public String getNUME(){
        return NUME;
    }

    public  void setNUME(String nume){
        this.NUME =nume;
    }

    public String getREMARKS(){
        return REMARKS;
    }

    public  void setREMARKS(String remarks){
        this.REMARKS =remarks;
    }
}
