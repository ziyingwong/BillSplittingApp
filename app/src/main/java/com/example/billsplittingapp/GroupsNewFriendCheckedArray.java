package com.example.billsplittingapp;

import java.util.ArrayList;

class GroupsNewFriendCheckedArray {
    private static final GroupsNewFriendCheckedArray ourInstance = new GroupsNewFriendCheckedArray();
    ArrayList<GroupsNewUserObject> arrayList = new ArrayList<>();
    ArrayList<GroupsNewUserObject> tempList = new ArrayList<>();
    ArrayList<GroupsNewUserObject> deleteList = new ArrayList<>();

    static GroupsNewFriendCheckedArray getInstance() {
        return ourInstance;
    }

    private GroupsNewFriendCheckedArray() {
    }
}
