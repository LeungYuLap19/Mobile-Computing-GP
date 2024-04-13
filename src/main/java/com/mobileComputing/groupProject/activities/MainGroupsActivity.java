package com.mobileComputing.groupProject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.mobileComputing.groupProject.R;
import com.mobileComputing.groupProject.adapters.GroupsCustomListAdapter;
import com.mobileComputing.groupProject.models.Category;
import com.mobileComputing.groupProject.models.Group;
import com.mobileComputing.groupProject.models.User;
import com.mobileComputing.groupProject.services.firebase.GroupService;
import com.mobileComputing.groupProject.services.interfaces.GetGroupsCallBack;
import com.mobileComputing.groupProject.states.AppStates;

import java.util.ArrayList;
import java.util.List;

public class MainGroupsActivity extends AppCompatActivity{

    AppStates appStates;
    GroupService groupService;
    ImageView groups_profile_btn;
    MaterialButton groups_add_btn;
    EditText groups_search_et;
    ListView groups_lv;
    SwipeRefreshLayout swipe_refresh_layout;
    List<Group> groupList, loadedGroups;
    GroupsCustomListAdapter groupsCustomListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_groups);

        appStates = (AppStates) getApplication();
        groupService = new GroupService();
        groups_profile_btn = findViewById(R.id.groups_profile_btn);
        groups_add_btn = findViewById(R.id.groups_add_btn);
        groups_search_et = findViewById(R.id.groups_search_et);
        groups_lv = findViewById(R.id.groups_lv);
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);

        groupList = new ArrayList<>();
        loadedGroups = new ArrayList<>();

        groupsCustomListAdapter = new GroupsCustomListAdapter(this, groupList, appStates);
        groups_lv.setAdapter(groupsCustomListAdapter);
        groups_lv.setDivider(null);

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

        groups_search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString().trim();

                if (searchText.isEmpty()) {
                    fetchGroups();
                }
                else {
                    searchGroups(searchText);
                }
            }
        });

        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchGroups();
                swipe_refresh_layout.setRefreshing(false);
            }
        });

        fetchGroups();
    }

    private void fetchGroups() {
        User user = appStates.getUser();
        groupService.getGroups(user.getUserid(), new GetGroupsCallBack() {
            @Override
            public void onSuccess(List<Group> groups) {
                groupList.clear();
                groupList.addAll(groups);
                changeList(groupList);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("firebaseMsg", "failed to get groups");
            }
        });
    }

    private void searchGroups(String searchText) {
        List<Group> resultList = new ArrayList<>();

        for(Group group : groupList) {
            int n = searchText.length();
            String subStr = group.getGroupname().substring(0, n).toLowerCase();

            if (searchText.toLowerCase().equals(subStr)) {
                resultList.add(group);
            }
        }

        changeList(resultList);
    }

    private void changeList(List<Group> list) {
        groupsCustomListAdapter = new GroupsCustomListAdapter(this, list, appStates);
        groups_lv.setAdapter(groupsCustomListAdapter);
    }

}
