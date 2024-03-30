package com.mobileComputing.groupProject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mobileComputing.groupProject.R;
import com.mobileComputing.groupProject.adapters.MembersCustomListAdapter;
import com.mobileComputing.groupProject.models.User;
import com.mobileComputing.groupProject.services.firebase.GroupService;
import com.mobileComputing.groupProject.services.firebase.UserService;
import com.mobileComputing.groupProject.services.interfaces.AddGroupCallBack;
import com.mobileComputing.groupProject.services.interfaces.UserSearchCallback;
import com.mobileComputing.groupProject.states.AppStates;

import java.util.ArrayList;
import java.util.List;

public class MainCreateGroupActivity extends AppCompatActivity {

    GroupService groupService;
    UserService userService;
    AppStates appStates;
    List<User> existingUsers, searchResultUsers;
    ImageButton return_btn;
    TextView create_group_btn;
    EditText group_name_et, username_search_et;
    ListView members_lv, username_search_lv;
    MembersCustomListAdapter membersAdapter, searchResultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_create_group);

        groupService = new GroupService();
        userService = new UserService();
        appStates = (AppStates) getApplication();
        existingUsers = new ArrayList<>();
        searchResultUsers = new ArrayList<>();
        existingUsers.add(appStates.getUser());

        return_btn = findViewById(R.id.return_btn);
        create_group_btn = findViewById(R.id.create_group_btn);
        group_name_et = findViewById(R.id.group_name_et);
        username_search_et = findViewById(R.id.username_search_et);
        members_lv = findViewById(R.id.members_lv);
        username_search_lv = findViewById(R.id.username_search_lv);

        membersAdapter = new MembersCustomListAdapter(this, existingUsers, existingUsers, searchResultUsers, false);
        searchResultAdapter = new MembersCustomListAdapter(this, searchResultUsers, existingUsers, searchResultUsers, true);
        members_lv.setAdapter(membersAdapter);
        username_search_lv.setAdapter(searchResultAdapter);
        members_lv.setDivider(null);
        username_search_lv.setDivider(null);

        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainCreateGroupActivity.this, MainGroupsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        create_group_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroup();
            }
        });

        username_search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString().trim();

                if (searchText.isEmpty()) {
                    username_search_lv.setVisibility(View.GONE);
                    members_lv.setVisibility(View.VISIBLE);
                } else {
                    username_search_lv.setVisibility(View.VISIBLE);
                    members_lv.setVisibility(View.GONE);
                    searchUsers(searchText);
                }

            }
        });
    }

    private void searchUsers(String searchText) {
        userService.searchUsersByKeyword(searchText, new UserSearchCallback() {
            @Override
            public void onUserSearchSuccess(List<User> userList) {
                // Filter out existing users
                List<User> filteredList = userService.filterExistingUsers(existingUsers, userList);

                // Update the search result users list and refresh the UI
                searchResultUsers.clear();
                searchResultUsers.addAll(filteredList);
                // Refresh the search result ListView or adapter here
                searchResultAdapter.notifyDataSetChanged();
            }

            @Override
            public void onUserSearchFailure(Exception e) {
                // Handle the failure case
                Log.d("firebaseMsg", "username search failed");
            }
        });
    }

    private void createGroup() {
        String groupName = group_name_et.getText().toString();

        // Add the group to the Firestore collection using the GroupService
        groupService.addGroup(groupName, existingUsers, appStates.getUser().getUserid(), new AddGroupCallBack() {
            @Override
            public void onSuccess(String groupName) {
                Toast.makeText(MainCreateGroupActivity.this, groupName + " created", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainCreateGroupActivity.this, MainGroupsActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainCreateGroupActivity.this, groupName + " failed to create", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
