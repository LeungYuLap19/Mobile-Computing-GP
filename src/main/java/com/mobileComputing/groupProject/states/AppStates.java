package com.mobileComputing.groupProject.states;

import android.app.Application;

import com.mobileComputing.groupProject.models.Group;
import com.mobileComputing.groupProject.models.User;

public class AppStates extends Application {
    private User user;
    private Group group;
    public User getUser() {
        return user;
    }
    public Group getGroup() { return group; }

    public void setUser(User user) {
        this.user = user;
    }
    public void setGroup(Group group) { this.group = group; }
}
