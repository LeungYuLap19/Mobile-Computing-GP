package com.mobileComputing.groupProject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mobileComputing.groupProject.R;
import com.mobileComputing.groupProject.models.User;
import com.mobileComputing.groupProject.services.apis.AuthAPIs;
import com.mobileComputing.groupProject.states.AppStates;

import org.json.JSONException;

import java.io.IOException;

public class AuthLoginActivity extends AppCompatActivity {

    AuthAPIs authAPIs;
    AppStates appStates;
    EditText login_email_et, login_password_et;
    Button login_signin_btn;
    TextView login_register_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_login);

        authAPIs = new AuthAPIs();
        appStates = (AppStates) getApplication();
        login_email_et = findViewById(R.id.login_email_et);
        login_password_et = findViewById(R.id.login_password_et);
        login_signin_btn = findViewById(R.id.login_signin_btn);
        login_register_link = findViewById(R.id.login_register_link);

        login_signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    User user = loginAuthUser();
                    appStates.setUser(user);

                    Intent intent = new Intent(AuthLoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    private User loginAuthUser() throws JSONException, IOException {
        String email = login_email_et.getText().toString();
        String password = login_password_et.getText().toString();
        User user = authAPIs.authUser(email, password);
        return user;
    }
}
