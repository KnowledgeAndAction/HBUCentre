package cn.ian2018.hbu.centre.model;

/**
 * Created by 陈帅 on 2018/3/8/008.
 */

public class PermissionsInfo {
    private String name;
    private String account;
    private int grade;
    private int type;

    public PermissionsInfo() {
    }

    public PermissionsInfo(String name, String account, int grade, int type) {
        this.name = name;
        this.account = account;
        this.grade = grade;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
