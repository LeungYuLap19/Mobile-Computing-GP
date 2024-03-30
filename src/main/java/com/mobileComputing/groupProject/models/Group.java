package com.mobileComputing.groupProject.models;

import java.util.List;

public class Group {
    private String groupname;
    private List<User> members;

    public Group(String groupname, List<User> members) {
        this.groupname = groupname;
        this.members = members;
    }

    public String getGroupname() {
        return groupname;
    }

    public List<User> getMembers() {
        return  members;
    }
}
