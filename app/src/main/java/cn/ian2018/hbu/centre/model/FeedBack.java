package cn.ian2018.hbu.centre.model;

/**
 * Created by 陈帅 on 2017/8/7/007.
 */

public class FeedBack {
    private String account;
    private String msg;
    private String PhoneBrand;
    private String PhoneBrandType;
    private String AndroidVersion;
    private int anonymous; // 是否匿名
    private String name;    // 姓名
    private int groupCode;  // 组别
    private int grade;  // 年级
    private String time;    // 时间

    public int getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(int anonymous) {
        this.anonymous = anonymous;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(int groupCode) {
        this.groupCode = groupCode;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getPhoneBrand() {
        return PhoneBrand;
    }

    public void setPhoneBrand(String phoneBrand) {
        PhoneBrand = phoneBrand;
    }

    public String getPhoneBrandType() {
        return PhoneBrandType;
    }

    public void setPhoneBrandType(String phoneBrandType) {
        PhoneBrandType = phoneBrandType;
    }

    public String getAndroidVersion() {
        return AndroidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        AndroidVersion = androidVersion;
    }
}
