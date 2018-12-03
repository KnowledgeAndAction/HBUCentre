package cn.ian2018.hbu.centre.model;

/**
 * Created by 陈帅 on 2018/4/4/004.
 */

public class OnDutyTime {
    private String name;
    private long allTimes;
    private String allTime;
    private int groupCode;
    private int gradeCode;
    private String studentNum;

    public OnDutyTime() {
    }

    public OnDutyTime(String name, long allTimes, String allTime, int groupCode, int gradeCode, String studentNum) {
        this.name = name;
        this.allTimes = allTimes;
        this.allTime = allTime;
        this.groupCode = groupCode;
        this.gradeCode = gradeCode;
        this.studentNum = studentNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAllTimes() {
        return allTimes;
    }

    public void setAllTimes(long allTimes) {
        this.allTimes = allTimes;
    }

    public String getAllTime() {
        return allTime;
    }

    public void setAllTime(String allTime) {
        this.allTime = allTime;
    }

    public int getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(int groupCode) {
        this.groupCode = groupCode;
    }

    public int getGradeCode() {
        return gradeCode;
    }

    public void setGradeCode(int gradeCode) {
        this.gradeCode = gradeCode;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }
}
