package com.mobileComputing.groupProject.services.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.mobileComputing.groupProject.services.interfaces.GroupCallBack;

import java.util.ArrayList;
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

    public void addGroup(String groupName, List<String> memberUserIds, String adminUserId, GroupCallBack callback) {
        // Create a map to represent the group document
        Map<String, Object> groupData = new HashMap<>();
        groupData.put("groupname", groupName);

        // Create a list of member maps
        List<Map<String, Object>> memberList = new ArrayList<>();
        for (String memberId : memberUserIds) {
            Map<String, Object> memberMap = new HashMap<>();
            memberMap.put("userid", memberId);
            memberMap.put("admin", memberId.equals(adminUserId)); // Set admin flag based on the member ID
            memberList.add(memberMap);
        }
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
}
