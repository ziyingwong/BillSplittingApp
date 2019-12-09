package com.example.billsplittingapp;

class GroupsGetNewGroupName {
    private static final GroupsGetNewGroupName ourInstance = new GroupsGetNewGroupName();
    String groupName = "";

    static GroupsGetNewGroupName getInstance() {
        return ourInstance;
    }

    private GroupsGetNewGroupName() {
    }
}
