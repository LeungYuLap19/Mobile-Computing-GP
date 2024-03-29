package com.mobileComputing.groupProject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.mobileComputing.groupProject.R;

public class MainCreateGroupActivity extends AppCompatActivity {

    ImageButton return_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_create_group);

        return_btn = findViewById(R.id.return_btn);

        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainCreateGroupActivity.this, MainGroupsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
