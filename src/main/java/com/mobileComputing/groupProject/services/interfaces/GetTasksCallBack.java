package com.mobileComputing.groupProject.services.interfaces;

import com.mobileComputing.groupProject.models.Task;

import java.util.List;
import java.util.Map;

public interface GetTasksCallBack {
    void onSuccess(Map<String, List<Task>> tasksByDate);
    void onFailure(Exception e);
}
