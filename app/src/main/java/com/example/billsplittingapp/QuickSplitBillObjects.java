package com.example.billsplittingapp;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.model.value.TimestampValue;

import java.util.List;
import java.util.Map;

public class QuickSplitBillObjects {
    Timestamp createTime;
    String owner;
    List<String> splitWith;
    String status;
    String billName;
    String billId;

    public QuickSplitBillObjects() {

    }

    public QuickSplitBillObjects(Timestamp createTime, String owner, List<String> splitWith, String status, CollectionReference items, String billName, String billId) {
        this.createTime = createTime;
        this.owner = owner;
        this.splitWith = splitWith;
        this.status = status;
        this.billName = billName;
        this.billId = billId;
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

    @PropertyName("billId")
    public String getBillId() {
        return billId;
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

class QuickSplitItemsPrice {
    double price;
    String name;

    public QuickSplitItemsPrice() {

    }

    public QuickSplitItemsPrice(String name, double price) {
        this.name = name;
        this.price = price;

    }

    @PropertyName("Field")
    public String getName() {
        return name;
    }

    @PropertyName("Value")
    public double getPrice() {
        return price;
    }
}

class QuickSplitItemsPortion {
    long portion;
    String name;

    public QuickSplitItemsPortion() {

    }

    public QuickSplitItemsPortion(String name, long portion) {
        this.name = name;
        this.portion = portion;

    }

    @PropertyName("Field")
    public String getName() {
        return name;
    }

    @PropertyName("Value")
    public long getPortion() {
        return portion;
    }
}

class QuickSplitMemberStatus {
    static QuickSplitMemberStatus status;
    public static int NOTHING_SELECTED = 1;
    public static int SELECTED_ITEM = 2;
    public static int PAID = 3;

    private QuickSplitMemberStatus() {

    }

    public static QuickSplitMemberStatus getInstance() {
        if (status == null) {
            status = new QuickSplitMemberStatus();
        }
        return status;
    }

    public static String getStatusInString(int statusCode) {
        if (statusCode == NOTHING_SELECTED) {
            return "Pending";
        } else if (statusCode == SELECTED_ITEM) {
            return "Done";
        } else if (statusCode == PAID) {
            return "Paid";
        } else {
            return "Unknow state";
        }
    }

}

class QuickSplitDebtor {
    Map<String, Double> itemPortion;
    int status;
    String displayName;
    String uid;
    Double amount;

    public QuickSplitDebtor() {

    }

    @PropertyName("uid")
    public String getUid() {
        return uid;
    }

    @PropertyName("status")
    public int getStatus() {
        return status;
    }

    @PropertyName("itemPortion")
    public Map<String, Double> getItemPortion() {
        return itemPortion;
    }

    @PropertyName("displayName")
    public String getDisplayName() {
        return displayName;
    }

}

class QuickSplitInvoiceItem {
    Long totalPortion;
    Long dividePortion;
    Double price;
    String itemName;
    Double divideAmount;

    public QuickSplitInvoiceItem() {

    }

}




