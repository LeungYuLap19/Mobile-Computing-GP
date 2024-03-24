package com.mobileComputing.groupProject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mobileComputing.groupProject.R;

public class AuthRegisterActivity extends AppCompatActivity {

    EditText register_username_et, register_email_et, register_password_et;
    Button register_signup_btn;
    TextView register_login_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_register);

        register_username_et = findViewById(R.id.register_username_et);
        register_email_et = findViewById(R.id.register_email_et);
        register_password_et = findViewById(R.id.register_password_et);
        register_signup_btn = findViewById(R.id.register_signup_btn);
        register_login_link = findViewById(R.id.register_login_link);

        register_signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AuthRegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        register_login_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AuthRegisterActivity.this, AuthLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
