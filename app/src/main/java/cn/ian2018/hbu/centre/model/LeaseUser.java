package cn.ian2018.hbu.centre.model;

/**
 * Created by 陈帅 on 2018/3/8/008.
 */

public class LeaseUser {
    private String name;
    private int grade;
    private int groupCode;
    private String phone;

    public LeaseUser() {
    }

    public LeaseUser(String name, int grade, int groupCode, String phone) {
        this.name = name;
        this.grade = grade;
        this.groupCode = groupCode;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(int groupCode) {
        this.groupCode = groupCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
