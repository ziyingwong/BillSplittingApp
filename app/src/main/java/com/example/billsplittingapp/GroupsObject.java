package com.example.billsplittingapp;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

import java.util.List;
import java.util.Map;

public class GroupsObject {
    Map<String, Double> user;
    String groupName;
    String groupId;

    public GroupsObject() {

    }

    public GroupsObject(String groupName, Map<String, Double> user, String groupId) {
        this.groupName = groupName;
        this.user = user;
        this.groupId = groupId;
    }

    @PropertyName("groupName")
    public String getGroupName() {
        return groupName;
    }

    @PropertyName("user")
    public Map<String, Double> getUser() {
        return user;
    }

    @PropertyName("groupId")
    public String getGroupId() {
        return groupId;
    }
}

class GroupsNewUserObject {
    String friendName;
    String friendEmail;
    String friendUID;

    public GroupsNewUserObject() {

    }

    public GroupsNewUserObject(String friendName, String friendEmail, String friendUID) {
        this.friendName = friendName;
        this.friendEmail = friendEmail;
        this.friendUID = friendUID;
    }

    @PropertyName("friendName")
    public String getFriendName() {
        return friendName;
    }

    @PropertyName("friendEmail")
    public String getFriendEmail() {
        return friendEmail;
    }

    @PropertyName("friendUID")
    public String getFriendUID() {
        return friendUID;
    }

}

class GroupsPaymentObject {
    String billId;
    String billName;
    Timestamp createTime;
    String payer;
    Double price;
    Map<String, Double> splitAmount;
    List<String> splitUser;

    public GroupsPaymentObject() {

    }

    public GroupsPaymentObject(String billId, String billName, Timestamp createTime, String payer, Double price, Map<String, Double> splitAmount, List<String> splitUser) {
        this.billId = billId;
        this.billName = billName;
        this.createTime = createTime;
        this.payer = payer;
        this.price = price;
        this.splitAmount = splitAmount;
        this.splitUser = splitUser;
    }

    @PropertyName("billId")
    public String getBillId() {
        return billId;
    }

    @PropertyName("billName")
    public String getBillName() {
        return billName;
    }

    @PropertyName("createTime")
    public Timestamp getCreateTime() {
        return createTime;
    }

    @PropertyName("payer")
    public String getPayer() {
        return payer;
    }

    @PropertyName("price")
    public Double getPrice() {
        return price;
    }

    @PropertyName("splitAmount")
    public Map<String, Double> getSplitAmount() {
        return splitAmount;
    }

    @PropertyName("splitUser")
    public List<String> getSplitUser() {
        return splitUser;
    }
}

class GroupsAmountUserObject {
    String uid;
    Object amount;

    public GroupsAmountUserObject() {

    }

    public GroupsAmountUserObject(String u, Object a) {
        this.uid = u;
        this.amount = a;
    }

}

