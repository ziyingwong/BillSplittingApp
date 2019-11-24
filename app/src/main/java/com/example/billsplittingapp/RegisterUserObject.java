package com.example.billsplittingapp;

public class RegisterUserObject {

    public String userName;
    public String userEmail;
    public String userUID;

    public RegisterUserObject(){

    }

    public RegisterUserObject (String userUID, String userEmail, String userName){
        this.userUID = userUID;
        this.userEmail = userEmail;
        this.userName = userName;
    }


    public String getUserName(){
        return userName;
    }

    public String getUserEmail(){
        return userEmail;
    }

    public String getUserUID(){ return userUID; }
}
