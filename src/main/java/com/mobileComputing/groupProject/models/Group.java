package com.mobileComputing.groupProject.models;

import java.util.ArrayList;
import java.util.List;

public class Group {

    private String groupid;
    private String groupname;
    private List<User> members;
    private List<Category> categoryList;

    public Group(String groupid, String groupname, List<User> members) {
        this.groupid = groupid;
        this.groupname = groupname;
        this.members = members;
        this.categoryList = new ArrayList<>();
    }

    public String getGroupid() {
        return groupid;
    }

    public String getGroupname() {
        return groupname;
    }

    public List<User> getMembers() {
        return  members;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }
}
