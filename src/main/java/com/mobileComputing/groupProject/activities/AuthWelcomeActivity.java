package com.mobileComputing.groupProject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.mobileComputing.groupProject.R;

public class AuthWelcomeActivity extends AppCompatActivity {

    Button welcome_signin_btn, welcome_signup_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_welcome);

        welcome_signin_btn = findViewById(R.id.welcome_signin_btn);
        welcome_signup_btn = findViewById(R.id.welcome_signup_btn);

        welcome_signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AuthWelcomeActivity.this, AuthLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        welcome_signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AuthWelcomeActivity.this, AuthRegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
