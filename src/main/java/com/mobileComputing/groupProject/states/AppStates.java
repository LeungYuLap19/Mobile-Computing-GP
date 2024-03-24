package com.mobileComputing.groupProject.states;

import android.app.Application;

import com.mobileComputing.groupProject.models.User;

public class AppStates extends Application {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
