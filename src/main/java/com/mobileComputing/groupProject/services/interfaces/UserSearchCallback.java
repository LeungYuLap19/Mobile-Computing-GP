package com.mobileComputing.groupProject.services.interfaces;

import com.mobileComputing.groupProject.models.User;

import java.util.List;

public interface UserSearchCallback {
    void onUserSearchSuccess(List<User> userList);
    void onUserSearchFailure(Exception e);
}