package cn.ian2018.hbu.centre.model;

import java.io.Serializable;

/**
 * Created by 陈帅 on 2018/3/8/008.
 */

public class RepairInfo implements Serializable {
    private int id;
    private String name;
    private String account; // 报修人
    private String handler; // 处理人
    private String time;
    private String repairDes;
    private String accountName;
    private String handlerName;

    public RepairInfo() {
    }

    public RepairInfo(String name, String account, String handler, String time, String repairDes) {
        this.name = name;
        this.account = account;
        this.handler = handler;
        this.time = time;
        this.repairDes = repairDes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
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

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRepairDes() {
        return repairDes;
    }

    public void setRepairDes(String repairDes) {
        this.repairDes = repairDes;
    }
}
