package com.mobileComputing.groupProject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.mobileComputing.groupProject.R;
import com.mobileComputing.groupProject.models.User;
import com.mobileComputing.groupProject.services.firebase.AuthService;
import com.mobileComputing.groupProject.states.AppStates;

public class MainUserProfileActivity extends AppCompatActivity {

    AuthService authService;
    AppStates appStates;
    ImageButton return_btn;
    EditText username_et, email_et;
    Button logout_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_user_profile);

        authService = new AuthService(MainUserProfileActivity.this);
        appStates = (AppStates) getApplication();
        return_btn = findViewById(R.id.return_btn);
        username_et = findViewById(R.id.username_et);
        email_et = findViewById(R.id.email_et);
        logout_btn = findViewById(R.id.logout_btn);

        User user = appStates.getUser();
        username_et.setText(user.getUsername());
        email_et.setText(user.getEmail());
        username_et.setEnabled(false);
        email_et.setEnabled(false);

        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainUserProfileActivity.this, MainGroupsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authService.storeValueInSharedPreferences(MainUserProfileActivity.this, null);
                appStates.setUser(new User(null, null, null));
                Intent intent = new Intent(MainUserProfileActivity.this, AuthWelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
