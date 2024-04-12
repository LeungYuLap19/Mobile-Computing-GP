package com.mobileComputing.groupProject.services.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.Token;
import com.mobileComputing.groupProject.models.User;
import com.mobileComputing.groupProject.services.firebaseMessaging.TokenManageService;
import com.mobileComputing.groupProject.services.interfaces.AuthCallBack;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private FirebaseFirestore db;
    private Context context;

    public AuthService(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    public void newUser(String username, String email, String password, AuthCallBack authCallBack) {
        // Check if email or username already exists
        checkExistingUser(username, email, new ExistingUserCallback() {
            @Override
            public void onExistingUser(boolean exists) {
                if (exists) {
                    Toast.makeText(context, "Email or Username already in use", Toast.LENGTH_SHORT).show();
                    Log.d("firebaseMsg", "Email or Username already exist, failed to add");
                } else {
                    // Add new user
                    Map<String, String> user = new HashMap<>();
                    user.put("username", username);
                    user.put("email", email);
                    user.put("password", hashPassword(password));

                    db.collection("users")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    String docID = documentReference.getId();
                                    Log.d("firebaseMsg", "DocumentSnapshot added with ID: " + docID);
                                    User newUser = new User(docID, username, email);
                                    authCallBack.onSuccess(newUser);
                                    TokenManageService.storeToken(docID);
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
                                    String docId = documentSnapshot.getId();
                                    String username = (String) userMap.get("username");
                                    String userEmail = (String) userMap.get("email");

                                    // Verify the password
                                    String hashedPassword = (String) userMap.get("password");
                                    if (BCrypt.checkpw(password, hashedPassword)) {
                                        // Password matches
                                        User user = new User(docId, username, userEmail);
                                        Log.d("firebaseMsg", "Found user");
                                        authCallBack.onSuccess(user);
                                        TokenManageService.storeToken(docId);
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

    private void checkExistingUser(String username, String email, ExistingUserCallback callback) {
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            boolean usernameExists = querySnapshot != null && !querySnapshot.isEmpty();

                            db.collection("users")
                                    .whereEqualTo("email", email)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                boolean emailExists = querySnapshot != null && !querySnapshot.isEmpty();

                                                // Check if either username or email exists
                                                boolean exists = usernameExists || emailExists;
                                                callback.onExistingUser(exists);
                                            } else {
                                                callback.onExistingUser(false);
                                            }
                                        }
                                    });
                        } else {
                            callback.onExistingUser(false);
                        }
                    }
                });
    }

    public void storeValueInSharedPreferences(Context context, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("KEY_NAME", value);
        editor.apply();
    }

    public String retrieveValueFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString("KEY_NAME", null);
        if (value != null) {
            return value;
        } else {
            // Value not found
            return null;
        }
    }

    private interface ExistingUserCallback {
        void onExistingUser(boolean exists);
    }

}
