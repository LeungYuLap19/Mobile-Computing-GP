package com.mobileComputing.groupProject.models;

public class User {

    private String userid;
    private String email;
    private String username;

    public User (String userid, String username, String email) {
        this.userid = userid;
        this.email = email;
        this.username = username;
    }

    public String getUserid() { return userid; }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return  username;
    }
}
