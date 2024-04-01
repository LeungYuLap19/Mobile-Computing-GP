package com.mobileComputing.groupProject.models;

public class Task {

    private String taskid;
    private String groupid;
    private String title;
    private String notes;
    private String location;
    private String date;
    private String time;
    private String assignMember;
    private String priority;

    public Task (String groupid, String title, String notes, String location
    , String date, String time, String assignMember, String priority) {
        this.taskid = null;
        this.groupid = groupid;
        this.title = title;
        this.notes = notes;
        this.location = location;
        this.date = date;
        this.time = time;
        this.assignMember = assignMember;
        this.priority = priority;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getTaskid() {
        return taskid;
    }

    public String getGroupid() {
        return groupid;
    }

    public String getTitle() {
        return title;
    }

    public String getNotes() {
        return notes;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getAssignMember() {
        return assignMember;
    }

    public String getPriority() {
        return priority;
    }
}
