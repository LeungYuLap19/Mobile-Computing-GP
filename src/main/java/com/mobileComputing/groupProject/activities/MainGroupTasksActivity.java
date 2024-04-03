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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
    SwipeRefreshLayout swipe_refresh_layout;

    // all task list
    Map<String, List<Task>> tasksList;
    String taskdates[];
    String currentTask;
    TasksCustomListAdapter tasksCustomListAdapter;
    List<Task> currentTaskList;

    // my task list
    Map<String, List<Task>> myTasksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_group_tasks);

        taskService = new TaskService();
        appStates = (AppStates) getApplication();
        tasksList = new HashMap<>();
        myTasksList = new HashMap<>();

        return_btn = findViewById(R.id.return_btn);
        all_task_btn = findViewById(R.id.all_task_btn);
        my_task_btn = findViewById(R.id.my_task_btn);
        tasks_add_btn = findViewById(R.id.tasks_add_btn);
        groups_profile_btn = findViewById(R.id.groups_profile_btn);
        task_date_list = findViewById(R.id.task_date_list);
        task_list = findViewById(R.id.task_list);
        task_list.setDivider(null);
        group_name = findViewById(R.id.group_name);
        group_name.setText(appStates.getGroup().getGroupname());
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);

        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appStates.setGroup(null);
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

                updateView(tasksList);
            }
        });

        my_task_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all_task_btn.setBackgroundResource(R.drawable.layout_register_btn_bg);
                my_task_btn.setBackgroundResource(R.drawable.layout_login_btn_bg);
                all_task_btn.setTextColor(ContextCompat.getColor(MainGroupTasksActivity.this, R.color.text_regular));
                my_task_btn.setTextColor(ContextCompat.getColor(MainGroupTasksActivity.this, R.color.background));

                getMyList();
                updateView(myTasksList);
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

        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllTasks();
                all_task_btn.performClick();
                swipe_refresh_layout.setRefreshing(false);
            }
        });

        groups_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainGroupTasksActivity.this, MainCreateGroupActivity.class);
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
                updateView(tasksList);
            }

            @Override
            public void onFailure(Exception e) {}
        });
    }

    private void updateView(Map<String, List<Task>> list) {
        task_date_list.removeAllViews(); // Clear any existing buttons
        task_list.setAdapter(null); // Clear the task list

        taskdates = sortDate(list);

        setDateList();
        if (taskdates.length > 0) {
            currentTask = taskdates[0];
            currentTaskList = list.get(currentTask);
            sortTaskList();
            tasksCustomListAdapter = new TasksCustomListAdapter(MainGroupTasksActivity.this, currentTaskList, appStates);
            task_list.setAdapter(tasksCustomListAdapter);
        }
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
                    tasksCustomListAdapter = new TasksCustomListAdapter(MainGroupTasksActivity.this, tasksList.get(currentTask), appStates);
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

    private String[] sortDate(Map<String, List<Task>> list) {
        String taskdates[] = new String[list.size()];
        int i = 0;
        for (String key : list.keySet()) {
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

    private void sortTaskList() {
        Collections.sort(currentTaskList, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                // Assuming Task.getTime() returns a string in the format "HH:mm"
                String time1 = task1.getTime();
                String time2 = task2.getTime();
                return time1.compareTo(time2);
            }
        });
    }

    private void getMyList() {
        String currentUsername = appStates.getUser().getUsername();
        HashSet<String> keySet = new HashSet<>();

        for (String key : tasksList.keySet()) {
            List<Task> tasks = tasksList.get(key);
            List<Task> emptyTasks = new ArrayList<>();
            for (Task task : tasks) {
                if (task.getAssignMember().equals(currentUsername) || task.getAssignMember().equals("All")) {
                    emptyTasks.add(task);
                    keySet.add(key);
                }
            }
            if (!keySet.isEmpty()) {
                keySet.remove(key);
                myTasksList.put(key, emptyTasks);
            }
        }
    }
}
