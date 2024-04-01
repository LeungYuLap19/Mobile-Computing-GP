package com.mobileComputing.groupProject.models;

import java.util.List;

public class Group {

    private String groupid;
    private String groupname;
    private List<User> members;

    public Group(String groupid, String groupname, List<User> members) {
        this.groupid = groupid;
        this.groupname = groupname;
        this.members = members;
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
}
