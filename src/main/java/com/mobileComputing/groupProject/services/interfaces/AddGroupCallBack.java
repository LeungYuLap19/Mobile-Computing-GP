package com.mobileComputing.groupProject.services.interfaces;

public interface AddGroupCallBack {
    void onSuccess(String groupName);
    void onFailure(Exception e);
}
