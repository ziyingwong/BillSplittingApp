package com.example.billsplittingapp;


import java.io.Serializable;

public class MethodSplitObject implements Serializable {
    String uid;
    Double amount;

    public MethodSplitObject() {
    }

    public MethodSplitObject(String u, Double a) {
        this.uid = u;
        this.amount = a;
    }
}
