package com.mobileComputing.groupProject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mobileComputing.groupProject.R;
import com.mobileComputing.groupProject.models.User;
import com.mobileComputing.groupProject.services.firebase.AuthService;
import com.mobileComputing.groupProject.services.firebase.UserService;
import com.mobileComputing.groupProject.services.interfaces.AuthCallBack;
import com.mobileComputing.groupProject.states.AppStates;

public class SplashActivity extends AppCompatActivity {

    AuthService authService;
    UserService userService;
    AppStates appStates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        authService = new AuthService(SplashActivity.this);
        userService = new UserService();
        appStates = (AppStates) getApplication();

        String userid = authService.retrieveValueFromSharedPreferences(SplashActivity.this);
        if (userid == null) {
            loadToLogin();
        }
        else {
            loginWithId(userid);
        }

    }

    private void loadToHomePage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainGroupsActivity.class));
                finish();
            }
        }, 3000);
    }

    private void loadToLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, AuthWelcomeActivity.class));
                finish();
            }
        }, 3000);
    }

    private void loginWithId(String userid) {
        userService.getUserById(userid, new AuthCallBack() {
            @Override
            public void onSuccess(User user) {
                appStates.setUser(user);
                loadToHomePage();
            }

            @Override
            public void onFailure(Exception e) {
                loadToLogin();
            }
        });
    }
}