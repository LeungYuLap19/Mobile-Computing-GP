package com.mobileComputing.groupProject.services.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.mobileComputing.groupProject.models.Group;
import com.mobileComputing.groupProject.models.User;
import com.mobileComputing.groupProject.services.interfaces.AddGroupCallBack;
import com.mobileComputing.groupProject.services.interfaces.GetGroupsCallBack;
import com.mobileComputing.groupProject.states.AppStates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupService {
    private FirebaseFirestore db;
    private CollectionReference groupsCollection;

    public GroupService() {
        db = FirebaseFirestore.getInstance();
        groupsCollection = db.collection("groups");
    }

    public void addGroup(String groupName, List<User> existingUsers, String adminUserId, AddGroupCallBack callback) {
        // Create a map to represent the group document
        Map<String, Object> groupData = new HashMap<>();
        groupData.put("groupname", groupName);

        // Create a list of member maps
        List<Map<String, Object>> memberList = new ArrayList<>();
        for (User user : existingUsers) {
            String memberId = user.getUserid();
            String username = user.getUsername();

            Map<String, Object> memberMap = new HashMap<>();
            memberMap.put("userid", memberId);
            memberMap.put("username", username);
            memberMap.put("admin", memberId.equals(adminUserId)); // Set admin flag based on the member ID

            memberList.add(memberMap);
        }

        // Add the member list to the groupData map
        groupData.put("members", memberList);

        // Add the group document to the "groups" collection
        groupsCollection.document().set(groupData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.onSuccess(groupName);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }

    public void getGroups(String userid, GetGroupsCallBack callBack) {
        groupsCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Group> groups = new ArrayList<>();

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String groupname = document.getString("groupname");
                            List<Map<String, Object>> memberList = (List<Map<String, Object>>) document.get("members");
                            List<User> members = new ArrayList<>();

                            for (Map<String, Object> memberMap : memberList) {
                                String memberid = (String) memberMap.get("userid");
                                String username = (String) memberMap.get("username");
                                boolean isAdmin = (boolean) memberMap.get("admin");

                                User user = new User(memberid, username, null);
                                user.setIsAdmin(isAdmin);
                                members.add(user);

                                if (memberid.equals(userid)) {
                                    Group group = new Group(groupname, members);
                                    groups.add(group);

                                }
                            }
                        }
                        callBack.onSuccess(groups);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callBack.onFailure(e);
                    }
                });
    }
}
