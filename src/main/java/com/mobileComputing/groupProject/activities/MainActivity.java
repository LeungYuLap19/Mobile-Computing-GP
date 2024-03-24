package com.mobileComputing.groupProject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.mobileComputing.groupProject.R;
import com.mobileComputing.groupProject.models.User;
import com.mobileComputing.groupProject.states.AppStates;

public class MainActivity extends AppCompatActivity {

    TextView test_display_user;
    AppStates appStates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // test login
        appStates = (AppStates) getApplication();
        User user = appStates.getUser();
        test_display_user = findViewById(R.id.test_display_user);
        test_display_user.setText(user.getEmail() + " " + user.getUsername());
    }
}