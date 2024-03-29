package com.mobileComputing.groupProject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.mobileComputing.groupProject.R;
import com.mobileComputing.groupProject.states.AppStates;

public class MainGroupsActivity extends AppCompatActivity{

    ImageView groups_profile_btn;
    MaterialButton groups_add_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_groups);

        groups_profile_btn = findViewById(R.id.groups_profile_btn);
        groups_add_btn = findViewById(R.id.groups_add_btn);

        groups_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainGroupsActivity.this, MainUserProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        groups_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainGroupsActivity.this, MainCreateGroupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
