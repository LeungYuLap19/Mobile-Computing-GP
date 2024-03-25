package com.mobileComputing.groupProject.services.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobileComputing.groupProject.models.User;
import com.mobileComputing.groupProject.services.interfaces.AuthCallBack;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class Auth {

    private FirebaseFirestore db;

    public Auth() {
        db = FirebaseFirestore.getInstance();
    }

    public void newUser(String username, String email, String password, AuthCallBack authCallBack) {
        Map<String, String> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("password", hashPassword(password));

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("firebaseMsg", "DocumentSnapshot added with ID: " + documentReference.getId());
                        User newUser = new User(username, email);
                        authCallBack.onSuccess(newUser);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("firebaseMsg", "Error adding document", e);
                        authCallBack.onFailure(e);
                    }
                });
    }

    public void authUser(String email, String password, AuthCallBack authCallBack) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();

                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                    Map<String, Object> userMap = documentSnapshot.getData();
                                    String username = (String) userMap.get("username");
                                    String userEmail = (String) userMap.get("email");

                                    // Verify the password
                                    String hashedPassword = (String) userMap.get("password");
                                    if (BCrypt.checkpw(password, hashedPassword)) {
                                        // Password matches
                                        User user = new User(username, userEmail);
                                        Log.d("firebaseMsg", "Found user");
                                        authCallBack.onSuccess(user);
                                        return;
                                    }
                                }
                            }
                            authCallBack.onFailure(new Exception("Invalid email or password."));
                        } else {
                            Log.d("firebaseMsg", "Error retrieving user", task.getException());
                            authCallBack.onFailure(task.getException());
                        }
                    }
                });
    }

    private String hashPassword(String password) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }

}
