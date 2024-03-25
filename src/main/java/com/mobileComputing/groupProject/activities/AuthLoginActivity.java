package com.mobileComputing.groupProject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mobileComputing.groupProject.R;
import com.mobileComputing.groupProject.models.User;
import com.mobileComputing.groupProject.services.firebase.Auth;
import com.mobileComputing.groupProject.services.interfaces.AuthCallBack;
import com.mobileComputing.groupProject.states.AppStates;

public class AuthLoginActivity extends AppCompatActivity {

    Auth firebaseAuth;
    AppStates appStates;
    EditText login_email_et, login_password_et;
    Button login_signin_btn;
    TextView login_register_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_login);

        firebaseAuth = new Auth();
        appStates = (AppStates) getApplication();
        login_email_et = findViewById(R.id.login_email_et);
        login_password_et = findViewById(R.id.login_password_et);
        login_signin_btn = findViewById(R.id.login_signin_btn);
        login_register_link = findViewById(R.id.login_register_link);

        login_signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAuthUser();
            }
        });

        login_register_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AuthLoginActivity.this, AuthRegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void loginAuthUser() {
        String email = login_email_et.getText().toString();
        String password = login_password_et.getText().toString();

        firebaseAuth.authUser(email, password, new AuthCallBack() {
            @Override
            public void onSuccess(User user) {
                appStates.setUser(user);
                Intent intent = new Intent(AuthLoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                login_email_et.setText("");
                login_password_et.setText("");
                Toast.makeText(AuthLoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
