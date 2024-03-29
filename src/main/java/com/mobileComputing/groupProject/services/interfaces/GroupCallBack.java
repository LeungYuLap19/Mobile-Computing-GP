package com.mobileComputing.groupProject.services.interfaces;

public interface GroupCallBack {
    void onSuccess(String groupName);
    void onFailure(Exception e);
}
