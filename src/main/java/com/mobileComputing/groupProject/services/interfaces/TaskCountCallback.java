package com.mobileComputing.groupProject.services.interfaces;

public interface TaskCountCallback {
    void onSuccess(int count);
    void onFailure(Exception e);
}
