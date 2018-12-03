package cn.ian2018.hbu.centre.model;

import java.io.Serializable;

/**
 * Created by 陈帅 on 2017/12/22/022.
 */

public class Goods implements Serializable{
    private double price;
    private String name;
    private int nid;
    private String description;
    private int quanutity;
    private int type;
    private int status;
    private String location;
    private String imageUrl;

    public Goods() {
    }

    public Goods(double price, String name, int nid, String description, int quanutity, int type, int status,String location) {
        this.price = price;
        this.name = name;
        this.nid = nid;
        this.description = description;
        this.quanutity = quanutity;
        this.type = type;
        this.status = status;
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuanutity() {
        return quanutity;
    }

    public void setQuanutity(int quanutity) {
        this.quanutity = quanutity;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
