package com.mobileComputing.groupProject.states;

import android.app.Application;

import com.mobileComputing.groupProject.models.Group;
import com.mobileComputing.groupProject.models.Task;
import com.mobileComputing.groupProject.models.User;

public class AppStates extends Application {
    private User user;
    private Group group;
    private Task task;
    public User getUser() {
        return user;
    }
    public Group getGroup() { return group; }
    public Task getTask() { return task; }

    public void setUser(User user) {
        this.user = user;
    }
    public void setGroup(Group group) { this.group = group; }

    public void setTask(Task task) {
        this.task = task;
    }
}
