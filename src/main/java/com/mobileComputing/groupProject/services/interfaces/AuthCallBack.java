package com.mobileComputing.groupProject.services.interfaces;

import com.mobileComputing.groupProject.models.User;

public interface AuthCallBack {
    void onSuccess(User user);
    void onFailure(Exception e);
}
