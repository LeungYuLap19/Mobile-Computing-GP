package com.mobileComputing.groupProject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mobileComputing.groupProject.R;
import com.mobileComputing.groupProject.models.Group;
import com.mobileComputing.groupProject.models.Task;
import com.mobileComputing.groupProject.models.User;
import com.mobileComputing.groupProject.services.firebase.TaskService;
import com.mobileComputing.groupProject.services.interfaces.AddTaskCallBack;
import com.mobileComputing.groupProject.states.AppStates;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainCreateTaskActivity  extends AppCompatActivity {

    TaskService taskService;
    AppStates appStates;
    Group currentGroup;
    ImageButton return_btn;
    TextView create_task_btn, select_date, select_time, task_page_title;
    EditText title_et, notes_et, location_et;
    Spinner members_spinner, priority_spinner;
    LinearLayout date_picker_bg, time_picker_bg;
    DatePicker date_picker;
    TimePicker time_picker;
    Button delete_task_btn;

    String nameList[];
    final String priorityList[] = {"None", "Low", "Medium", "High"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_create_tasks);

        taskService = new TaskService();
        appStates = (AppStates) getApplication();
        currentGroup = appStates.getGroup();
        List<User> members = currentGroup.getMembers();
        nameList = new String[members.size() + 2];
        nameList[0] = "None";
        nameList[1] = "All";
        for (int i = 0; i < members.size(); i++) {
            nameList[i + 2] = members.get(i).getUsername();
        }

        return_btn = findViewById(R.id.return_btn);
        create_task_btn = findViewById(R.id.create_task_btn);
        select_date = findViewById(R.id.select_date);
        select_time = findViewById(R.id.select_time);
        task_page_title = findViewById(R.id.task_page_title);
        title_et = findViewById(R.id.title_et);
        notes_et = findViewById(R.id.notes_et);
        location_et = findViewById(R.id.location_et);
        members_spinner = findViewById(R.id.members_spinner);
        priority_spinner = findViewById(R.id.priority_spinner);
        date_picker_bg = findViewById(R.id.date_picker_bg);
        time_picker_bg = findViewById(R.id.time_picker_bg);
        date_picker = findViewById(R.id.date_picker);
        time_picker = findViewById(R.id.time_picker);
        delete_task_btn = findViewById(R.id.delete_task_btn);

        ArrayAdapter<String> members_adapter = new ArrayAdapter<>(this, R.layout.spinner_text, nameList);
        ArrayAdapter<String> priority_adapter = new ArrayAdapter<>(this, R.layout.spinner_text, priorityList);
        members_spinner.setAdapter(members_adapter);
        priority_spinner.setAdapter(priority_adapter);

        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appStates.setTask(null);
                Intent intent = new Intent(MainCreateTaskActivity.this, MainGroupTasksActivity.class);
                startActivity(intent);
                finish();
            }
        });

        create_task_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskActions("create");
            }
        });

        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_picker_bg.setVisibility(View.VISIBLE);
            }
        });

        select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_picker_bg.setVisibility(View.VISIBLE);
            }
        });

        date_picker_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date_picker_bg.setVisibility(View.GONE);

                int year = date_picker.getYear();
                int month = date_picker.getMonth();
                int day = date_picker.getDayOfMonth();

                String date = DateFormat(year, month, day);

                select_date.setText(date);
            }
        });

        time_picker_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_picker_bg.setVisibility(View.GONE);

                int hour = time_picker.getHour();
                int minute = time_picker.getMinute();

                String sHour = String.valueOf(hour);
                String sMinute = String.valueOf(minute);

                if (sHour.length() == 1) {
                    sHour = "0" + sHour;
                }
                if (sMinute.length() == 1) {
                    sMinute = "0" + sMinute;
                }

                String time = sHour + ":" + sMinute;

                select_time.setText(time);
            }
        });

        checkTaskDetail();
    }

    private void taskActions(String action) {
        String title = title_et.getText().toString();
        String notes = notes_et.getText().toString();
        String location = location_et.getText().toString();

        String date = select_date.getText().toString();
        String time = select_time.getText().toString();

        String assignMember = members_spinner.getSelectedItem().toString();
        String priority = priority_spinner.getSelectedItem().toString();

        if (checkValidTask(title, date, time)) {
            // call function to add task to database
            Task task = new Task(currentGroup.getGroupid(), title, notes, location, date, time, assignMember, priority);

            if (action.equals("create")) {
                createTask(task);
            }
            else {
                task.setTaskid(appStates.getTask().getTaskid());
                modifyTask(task);
            }
        }
    }

    private String DateFormat(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private boolean checkValidTask(String title, String date, String time) {
        if (title.equals("")) {
            Toast.makeText(this, "Enter Task Title", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (date.equals("None")) {
            Toast.makeText(this, "Pick a date", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (time.equals("None")) {
            Toast.makeText(this, "Pick a time", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void createTask(Task task) {
        taskService.addTask(task, new AddTaskCallBack() {
            @Override
            public void onSuccess() {
                appStates.setTask(null);
                Toast.makeText(MainCreateTaskActivity.this, "Task Created", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainCreateTaskActivity.this, MainGroupTasksActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void modifyTask(Task task) {
        taskService.saveTaskByTaskId(task.getTaskid(), task, new AddTaskCallBack() {
            @Override
            public void onSuccess() {
                appStates.setTask(null);
                Toast.makeText(MainCreateTaskActivity.this, "Task Saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainCreateTaskActivity.this, MainGroupTasksActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("Debug", "failed to save task");
            }
        });
    }

    private void checkTaskDetail() {
        Task task = appStates.getTask();

        if (task != null) {
            delete_task_btn.setVisibility(View.VISIBLE);
            delete_task_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteTask();
                }
            });
            task_page_title.setText(task.getTitle());
            create_task_btn.setText("Save");
            create_task_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    taskActions("save");
                }
            });
            title_et.setText(task.getTitle());
            notes_et.setText(task.getNotes());
            location_et.setText(task.getLocation());
            select_date.setText(task.getDate());
            select_time.setText(task.getTime());
            for (int i = 0; i < nameList.length; i++) {
                if (nameList[i].equals(task.getAssignMember())) {
                    members_spinner.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < priorityList.length; i++) {
                if (priorityList[i].equals(task.getPriority())) {
                    priority_spinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void deleteTask() {
        String taskid = appStates.getTask().getTaskid();
        String taskname = appStates.getTask().getTitle();

        taskService.deleteTaskById(taskid, new AddTaskCallBack() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainCreateTaskActivity.this, taskname + "deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainCreateTaskActivity.this, MainGroupTasksActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

}
