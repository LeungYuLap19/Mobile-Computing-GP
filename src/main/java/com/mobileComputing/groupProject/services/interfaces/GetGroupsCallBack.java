package com.mobileComputing.groupProject.services.interfaces;

import com.mobileComputing.groupProject.models.Group;

import java.util.List;

public interface GetGroupsCallBack {
    void onSuccess(List<Group> groups);
    void onFailure(Exception e);
}
