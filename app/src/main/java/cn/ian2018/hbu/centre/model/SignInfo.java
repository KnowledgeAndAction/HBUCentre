package cn.ian2018.hbu.centre.model;

/**
 * 活动签到记录
 */

public class SignInfo {
    private String name;
    private String inTime;
    private String outTime;
    private int groupCode;
    private int backTo;

    public int getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(int groupCode) {
        this.groupCode = groupCode;
    }

    public int getBackTo() {
        return backTo;
    }

    public void setBackTo(int backTo) {
        this.backTo = backTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }
}
