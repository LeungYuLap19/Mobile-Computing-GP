package com.mobileComputing.groupProject.services.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobileComputing.groupProject.models.User;
import com.mobileComputing.groupProject.services.interfaces.AuthCallBack;
import com.mobileComputing.groupProject.services.interfaces.UserSearchCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UserService {

    private FirebaseFirestore db;
    private CollectionReference usersCollection;

    public UserService() {
        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");
    }

    public void getUserById(String userId, AuthCallBack callBack) {
        usersCollection
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();

                            if (documentSnapshot.exists()) {
                                String username = documentSnapshot.getString("username");
                                String email = documentSnapshot.getString("email");
                                User user = new User(userId, username, email);

                                callBack.onSuccess(user);
                            }
                            else {
                                Log.d("firebaseMsg", "User not found");
                                callBack.onSuccess(null);
                            }
                        }
                        else {
                            callBack.onFailure(new Exception("Invalid email or password."));
                        }
                    }
                });
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
