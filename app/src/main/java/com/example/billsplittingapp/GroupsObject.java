package com.example.billsplittingapp;

import com.google.firebase.firestore.PropertyName;

import java.util.Map;

public class GroupsObject {
    Map<String, Double> user;
    String groupName;

    public GroupsObject() {

    }

    public GroupsObject(String groupName, Map<String, Double> user) {
        this.groupName = groupName;
        this.user = user;
    }

    @PropertyName("groupName")
    public String getGroupName() {
        return groupName;
    }

    @PropertyName("user")
    public Map<String, Double> getUser() {
        return user;
    }
}

class GroupsNewUserObject {
    String friendName;
    String friendEmail;

    public GroupsNewUserObject() {

    }

    public GroupsNewUserObject(String friendName, String friendEmail) {
        this.friendName = friendName;
        this.friendEmail = friendEmail;
    }


    @PropertyName("friendName")
    public String getFriendName() {
        return friendName;
    }

    @PropertyName("friendEmail")
    public String getFriendEmail() {
        return friendEmail;
    }


}
