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
import com.mobileComputing.groupProject.services.firebase.AuthService;
import com.mobileComputing.groupProject.services.interfaces.AuthCallBack;
import com.mobileComputing.groupProject.states.AppStates;

public class AuthRegisterActivity extends AppCompatActivity {

    AuthService firebaseAuthService;
    AppStates appStates;
    EditText register_username_et, register_email_et, register_password_et;
    Button register_signup_btn;
    TextView register_login_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_register);

        firebaseAuthService = new AuthService(AuthRegisterActivity.this);
        appStates = (AppStates) getApplication();
        register_username_et = findViewById(R.id.register_username_et);
        register_email_et = findViewById(R.id.register_email_et);
        register_password_et = findViewById(R.id.register_password_et);
        register_signup_btn = findViewById(R.id.register_signup_btn);
        register_login_link = findViewById(R.id.register_login_link);

        register_signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = register_username_et.getText().toString();
                String email = register_email_et.getText().toString();
                String password = register_password_et.getText().toString();

                if (username.equals("")) {
                    Toast.makeText(AuthRegisterActivity.this, "Enter username", Toast.LENGTH_SHORT).show();
                }
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(AuthRegisterActivity.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
                }
                else if (password.equals("")) {
                    Toast.makeText(AuthRegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                }
                else {
                    registerNewUser();
                }
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

    private void registerNewUser() {
        String username = register_username_et.getText().toString();
        String email = register_email_et.getText().toString();
        String password = register_password_et.getText().toString();

        firebaseAuthService.newUser(username, email, password, new AuthCallBack() {
            @Override
            public void onSuccess(User user) {
                Toast.makeText(AuthRegisterActivity.this, "Register success", Toast.LENGTH_SHORT).show();
                appStates.setUser(user);
                firebaseAuthService.storeValueInSharedPreferences(AuthRegisterActivity.this, user.getUserid());
                Intent intent = new Intent(AuthRegisterActivity.this, MainGroupsActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                register_username_et.setText("");
                register_email_et.setText("");
                register_password_et.setText("");
                Toast.makeText(AuthRegisterActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
