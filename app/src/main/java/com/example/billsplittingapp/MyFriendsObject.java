package com.example.billsplittingapp;

import com.google.firebase.database.DataSnapshot;

public class MyFriendsObject {
    public String friendName;
    public String friendStatus;
    public String friendEmail;

    public MyFriendsObject(){}

    public MyFriendsObject (String friendName, String friendEmail, String friendStatus){
        this.friendName = friendName;
        this.friendEmail = friendEmail;
        this.friendStatus = friendStatus;
    }

    public String getFriendName(){
        return friendName;
    }

    public String getFriendEmail(){
        return friendEmail;
    }

    public String getFriendStatus(){
        return friendStatus;
    }
}
