package com.example.billsplittingapp;

import com.google.firebase.database.DataSnapshot;

public class MyFriendsObject {
    public String friendName;
    public String friendStatus;
    public String friendEmail;
    public String friendUid;

    public MyFriendsObject(){}

    public MyFriendsObject (String friendUid, String friendName, String friendEmail, String friendStatus){
        this.friendUid = friendUid;
        this.friendName = friendName;
        this.friendEmail = friendEmail;
        this.friendStatus = friendStatus;
    }

    public String getFriendUid() {return friendUid;}

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
