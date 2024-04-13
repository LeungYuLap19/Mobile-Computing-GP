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

//        askNotificationPermission();

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

    // Declare the launcher at the top of your Activity/Fragment:
//    private final ActivityResultLauncher<String> requestPermissionLauncher =
//            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
//                if (isGranted) {
//                    // FCM SDK (and your app) can post notifications.
//                } else {
//                    // TODO: Inform user that that your app will not show notifications.
//                }
//            });
//
//    private void askNotificationPermission() {
//        // This is only necessary for API level >= 33 (TIRAMISU)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
//                    PackageManager.PERMISSION_GRANTED) {
//                // FCM SDK (and your app) can post notifications.
//            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
//                // TODO: display an educational UI explaining to the user the features that will be enabled
//                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
//                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
//                //       If the user selects "No thanks," allow the user to continue without notifications.
//            } else {
//                // Directly ask for the permission
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
//            }
//        }
//    }
}
