package com.mobileComputing.groupProject.services.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.mobileComputing.groupProject.models.Category;
import com.mobileComputing.groupProject.models.Group;
import com.mobileComputing.groupProject.models.User;
import com.mobileComputing.groupProject.services.interfaces.AddGroupCallBack;
import com.mobileComputing.groupProject.services.interfaces.AddTaskCallBack;
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
                            List<Map<String, Object>> categoryList = (List<Map<String, Object>>) document.get("categories");
                            List<User> members = new ArrayList<>();
                            List<Category> categories = new ArrayList<>();

                            for (Map<String, Object> memberMap : memberList) {
                                String memberid = (String) memberMap.get("userid");
                                String username = (String) memberMap.get("username");
                                boolean isAdmin = (boolean) memberMap.get("admin");

                                User user = new User(memberid, username, null);
                                user.setIsAdmin(isAdmin);
                                members.add(user);

                                if (memberid.equals(userid)) {
                                    Group group = new Group(document.getId(), groupname, members);
                                    groups.add(group);

                                }
                            }

                            if (categoryList != null) {
                                for (Map<String, Object> categoryMap : categoryList) {
                                    String categoryName = (String) categoryMap.get("categoryName");
                                    long hexCodeLong = (long) categoryMap.get("hexCode");
                                    int hexCode = (int) hexCodeLong;
                                    Category category = new Category(categoryName, hexCode);
                                    categories.add(category);
                                }
                            }

                            // Set category list for the group
                            if (!groups.isEmpty()) {
                                Group lastGroup = groups.get(groups.size() - 1);
                                lastGroup.setCategoryList(categories);
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

    public void saveGroupById(String groupId, Group updatedGroup, AddGroupCallBack callback) {
        DocumentReference groupRef = groupsCollection.document(groupId);

        // Create a map to represent the updated group data
        Map<String, Object> updatedGroupData = new HashMap<>();
        updatedGroupData.put("groupname", updatedGroup.getGroupname());

        // Create a list of member maps
        List<Map<String, Object>> memberList = new ArrayList<>();
        for (User user : updatedGroup.getMembers()) {
            String memberId = user.getUserid();
            String username = user.getUsername();

            Map<String, Object> memberMap = new HashMap<>();
            memberMap.put("userid", memberId);
            memberMap.put("username", username);
            memberMap.put("admin", user.getIsAdmin());

            memberList.add(memberMap);
        }

        // Add the member list to the updatedGroupData map
        updatedGroupData.put("members", memberList);

        groupRef.set(updatedGroupData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.onSuccess(updatedGroup.getGroupname());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }

    public void addCategoryToGroup(String groupId, Category category, AddTaskCallBack callback) {
        DocumentReference groupRef = groupsCollection.document(groupId);

        groupRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<Map<String, Object>> categoryList = (List<Map<String, Object>>) documentSnapshot.get("categories");

                if (categoryList == null) {
                    // Create a new category list if it doesn't exist in the document
                    categoryList = new ArrayList<>();
                }

                boolean exist = false;
                for (Map<String, Object> categoryMap : categoryList) {
                    long hexCodeLong = (long) categoryMap.get("hexCode");
                    int hexCode = (int) hexCodeLong;
                    if (hexCode == category.getHexCode()) {
                        categoryMap.put("categoryName", category.getCategoryName());
                        exist = true;
                        break;
                    }
                }

                if (!exist) {
                    Map<String, Object> categoryData = new HashMap<>();
                    categoryData.put("categoryName", category.getCategoryName());
                    categoryData.put("hexCode", category.getHexCode());
                    categoryList.add(categoryData);
                }

                groupRef.update("categories", categoryList)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                callback.onSuccess();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                callback.onFailure(e);
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void deleteGroupById(String groupId, AddTaskCallBack callback) {
        DocumentReference groupRef = groupsCollection.document(groupId);

        groupRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.onSuccess();
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
