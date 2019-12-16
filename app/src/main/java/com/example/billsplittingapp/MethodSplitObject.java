package com.example.billsplittingapp;


import java.io.Serializable;

public class MethodSplitObject {
    String uid;
    Double amount;

    public MethodSplitObject() {
    }

    public MethodSplitObject(String u, Double a) {
        this.uid = u;
        this.amount = a;
    }
}
