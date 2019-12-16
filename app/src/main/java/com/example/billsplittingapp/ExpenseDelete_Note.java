package com.example.billsplittingapp;

public class ExpenseDelete_Note {

    String bill_name;
    String Date;
    String groupId;


    public ExpenseDelete_Note(){

    }

    public ExpenseDelete_Note (String bill_name, String Date, String groupId) {
        this.bill_name = bill_name;
        this.Date = Date;
        this.groupId = groupId;
    }

    public String getBill_name() {
        return bill_name;
    }

    public String getDate() {
        return Date;
    }

    public String getGroupId() {
        return groupId;
    }
}
