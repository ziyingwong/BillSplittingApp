package com.example.billsplittingapp;

import com.google.firebase.database.DataSnapshot;

public class MyFriendsObject {
    public String friendName;
    public String friendStatus;

    public MyFriendsObject(){

    }

    public MyFriendsObject (String friendName, String friendStatus){
        this.friendName = friendName;
        this.friendStatus = friendStatus;
    }

    public String getFriendName(){
        return friendName;
    }

    public String getFriendStatus(){
        return friendStatus;
    }
}
