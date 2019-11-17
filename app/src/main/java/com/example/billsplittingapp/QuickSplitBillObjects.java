package com.example.billsplittingapp;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.model.value.TimestampValue;

import java.util.List;

public class QuickSplitBillObjects {
    Timestamp createTime;
    String owner;
    List<String> splitWith;
    String status;
    String billName;

    public QuickSplitBillObjects() {

    }

    public QuickSplitBillObjects(Timestamp createTime, String owner, List<String> splitWith, String status, CollectionReference items, String billName) {
        this.createTime = createTime;
        this.owner = owner;
        this.splitWith = splitWith;
        this.status = status;
        this.billName = billName;
    }

    @PropertyName("createTime")
    public Timestamp getCreateTime() {
        return createTime;
    }

    @PropertyName("owner")
    public String getOwner() {
        return owner;
    }

    @PropertyName("splitWith")
    public List<String> getSplitWith() {
        return splitWith;
    }

    @PropertyName("status")
    public String getStatus() {
        return status;
    }


    @PropertyName("billName")
    public String getBillName() {
        return billName;
    }

    public void setCreateTime(Timestamp time) {
        this.createTime = time;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setSplitWith(List<String> splitWith) {
        this.splitWith = splitWith;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public void setBillName(String billName) {
        this.billName = billName;
    }
}

class items {
    double price;
    String name;

    public items() {

    }

    public items(String name, double price) {
        this.name = name;
        this.price = price;

    }
}

