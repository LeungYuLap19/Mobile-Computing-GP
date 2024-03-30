package com.mobileComputing.groupProject.models;

public class User {

    private String userid;
    private String email;
    private String username;
    private boolean isAdmin;

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

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }
}
