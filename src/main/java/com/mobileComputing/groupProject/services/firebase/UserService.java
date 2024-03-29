package com.mobileComputing.groupProject.services.firebase;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobileComputing.groupProject.models.User;
import com.mobileComputing.groupProject.services.interfaces.UserSearchCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserService {

    private FirebaseFirestore db;
    private CollectionReference usersCollection;

    public UserService() {
        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");
    }

    public void searchUsersByKeyword(String userInput, UserSearchCallback callback) {
        Query query = usersCollection
                .orderBy("username")
                .startAt(userInput)
                .endAt(userInput + "\uf8ff");
        //  "\uf8ff" is appended to the userInput string to create a value that is
        //  greater than the userInput and can be used as the upper bound for the range query

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    List<User> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        String userid = documentSnapshot.getId();
                        String username = documentSnapshot.getString("username");
                        String email = documentSnapshot.getString("email");
                        User user = new User(userid, username, email);
                        userList.add(user);
                    }
                    Log.d("firebaseMsg", "found user");
                    callback.onUserSearchSuccess(userList);
                } else {
                    Log.d("firebaseMsg", "empty");
                    callback.onUserSearchSuccess(Collections.emptyList());
                }
            } else {
                callback.onUserSearchFailure(task.getException());
            }
        });
    }

    public List<User> filterExistingUsers(List<User> existingUsers, List<User> searchResultUsers) {
        List<User> filteredList = new ArrayList<>();

        for (User searchUser : searchResultUsers) {
            boolean exists = false;
            for (User existingUser : existingUsers) {
                if (searchUser.getUserid().equals(existingUser.getUserid())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                filteredList.add(searchUser);
            }
        }

        return filteredList;
    }
}
