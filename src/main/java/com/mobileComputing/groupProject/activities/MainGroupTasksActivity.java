package com.mobileComputing.groupProject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.mobileComputing.groupProject.R;
import com.mobileComputing.groupProject.adapters.TasksCustomListAdapter;
import com.mobileComputing.groupProject.models.Task;
import com.mobileComputing.groupProject.services.firebase.TaskService;
import com.mobileComputing.groupProject.services.interfaces.GetTasksCallBack;
import com.mobileComputing.groupProject.states.AppStates;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainGroupTasksActivity extends AppCompatActivity {

    TaskService taskService;
    AppStates appStates;
    ImageButton return_btn;
    Button all_task_btn, my_task_btn;
    MaterialButton tasks_add_btn;
    ImageView groups_profile_btn;
    LinearLayout task_date_list;
    ListView task_list;
    TextView group_name;

    Map<String, List<Task>> tasksList;
    String taskdates[];
    String currentTask;
    TasksCustomListAdapter tasksCustomListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_group_tasks);

        taskService = new TaskService();
        appStates = (AppStates) getApplication();
        tasksList = new HashMap<>();

        return_btn = findViewById(R.id.return_btn);
        all_task_btn = findViewById(R.id.all_task_btn);
        my_task_btn = findViewById(R.id.my_task_btn);
        tasks_add_btn = findViewById(R.id.tasks_add_btn);
        groups_profile_btn = findViewById(R.id.groups_profile_btn);
        task_date_list = findViewById(R.id.task_date_list);
        task_list = findViewById(R.id.task_list);
        group_name = findViewById(R.id.group_name);
        group_name.setText(appStates.getGroup().getGroupname());

        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainGroupTasksActivity.this, MainGroupsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        all_task_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all_task_btn.setBackgroundResource(R.drawable.layout_login_btn_bg);
                my_task_btn.setBackgroundResource(R.drawable.layout_register_btn_bg);
                all_task_btn.setTextColor(ContextCompat.getColor(MainGroupTasksActivity.this, R.color.background));
                my_task_btn.setTextColor(ContextCompat.getColor(MainGroupTasksActivity.this, R.color.text_regular));
            }
        });

        my_task_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all_task_btn.setBackgroundResource(R.drawable.layout_register_btn_bg);
                my_task_btn.setBackgroundResource(R.drawable.layout_login_btn_bg);
                all_task_btn.setTextColor(ContextCompat.getColor(MainGroupTasksActivity.this, R.color.text_regular));
                my_task_btn.setTextColor(ContextCompat.getColor(MainGroupTasksActivity.this, R.color.background));
            }
        });

        tasks_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainGroupTasksActivity.this, MainCreateTaskActivity.class);
                startActivity(intent);
                finish();
            }
        });

        getAllTasks();
    }

    private void getAllTasks() {
        taskService.getAllTasksByGroupId(appStates.getGroup().getGroupid(), new GetTasksCallBack() {
            @Override
            public void onSuccess(Map<String, List<Task>> tasksByDate) {
                tasksList = tasksByDate;
                taskdates = sortDate();
                setDateList();
                if (taskdates.length > 0) {
                    currentTask = taskdates[0];
                    tasksCustomListAdapter = new TasksCustomListAdapter(MainGroupTasksActivity.this, tasksList.get(currentTask));
                    task_list.setAdapter(tasksCustomListAdapter);
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void setDateList() {
        boolean isFirstDate = true;
        for (String date : taskdates) {
            android.widget.Button button = new android.widget.Button(this);

            String formattedDate = date.substring(0, date.lastIndexOf(" ")) + date.substring(date.lastIndexOf(" ") + 5);

            // set button params
            if (isFirstDate) {
                button.setText(formattedDate);
                button.setTextColor(ContextCompat.getColor(MainGroupTasksActivity.this, R.color.background));
                button.setBackgroundResource(R.drawable.layout_login_btn_bg);
                isFirstDate = false;
            }
            else {
                button.setText(formattedDate);
                button.setBackgroundResource(R.drawable.layout_register_btn_bg);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    dpToPx(80),
                    dpToPx(90)
            );
            layoutParams.setMargins(0, 0, dpToPx(5), 0);
            button.setLayoutParams(layoutParams);

            // Set the OnClickListener for the button
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentTask = date;
                    tasksCustomListAdapter = new TasksCustomListAdapter(MainGroupTasksActivity.this, tasksList.get(currentTask));
                    task_list.setAdapter(tasksCustomListAdapter);
                    // Set the background of the clicked button
                    button.setBackgroundResource(R.drawable.layout_login_btn_bg);
                    button.setTextColor(ContextCompat.getColor(MainGroupTasksActivity.this, R.color.background));

                    // Reset the background and text color of all other buttons
                    for (int i = 0; i < task_date_list.getChildCount(); i++) {
                        View child = task_date_list.getChildAt(i);
                        if (child instanceof Button && child != button) {
                            Button otherButton = (Button) child;
                            otherButton.setBackgroundResource(R.drawable.layout_register_btn_bg);
                            otherButton.setTextColor(ContextCompat.getColor(MainGroupTasksActivity.this, R.color.text_regular));
                        }
                    }
                }
            });

            task_date_list.addView(button);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private String[] sortDate() {
        String taskdates[] = new String[tasksList.size()];
        int i = 0;
        for (String key : tasksList.keySet()) {
            taskdates[i] = key;
            i++;
        }

        // Create a custom comparator to compare the date strings
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        Comparator<String> dateComparator = new Comparator<String>() {
            @Override
            public int compare(String date1, String date2) {
                try {
                    return dateFormat.parse(date1).compareTo(dateFormat.parse(date2));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        };

        // Sort the taskdates array using the custom comparator
        Arrays.sort(taskdates, dateComparator);

        return taskdates;
    }
}
