package cn.ian2018.hbu.centre.model;

/**
 * Created by 陈帅 on 2017/12/23/023.
 */

public class LeaseInfo {
    private String name;
    private String actualBackTime;
    private int articleID;
    private int nid;
    private String handle;
    private String time;
    private String backtime;
    private String account;
    private int status;

    public LeaseInfo() {
    }

    public LeaseInfo(String name, String actualBackTime, int articleID, int nid, String handle, String time, String backtime, String account, int status) {
        this.name = name;
        this.actualBackTime = actualBackTime;
        this.articleID = articleID;
        this.nid = nid;
        this.handle = handle;
        this.time = time;
        this.backtime = backtime;
        this.account = account;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActualBackTime() {
        return actualBackTime;
    }

    public void setActualBackTime(String actualBackTime) {
        this.actualBackTime = actualBackTime;
    }

    public int getArticleID() {
        return articleID;
    }

    public void setArticleID(int articleID) {
        this.articleID = articleID;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBacktime() {
        return backtime;
    }

    public void setBacktime(String backtime) {
        this.backtime = backtime;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
