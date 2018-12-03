package cn.ian2018.hbu.centre.model;

import java.io.Serializable;

/**
 * Created by 陈帅 on 2018/4/9/009.
 */

public class InterestGroup implements Serializable{
    private int nid;
    private String interestGroup;
    private String des;

    public InterestGroup() {
    }

    public InterestGroup(int nid, String interestGroup, String des) {
        this.nid = nid;
        this.interestGroup = interestGroup;
        this.des = des;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public String getInterestGroup() {
        return interestGroup;
    }

    public void setInterestGroup(String interestGroup) {
        this.interestGroup = interestGroup;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
