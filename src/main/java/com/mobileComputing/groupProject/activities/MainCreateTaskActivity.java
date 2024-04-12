package com.mobileComputing.groupProject.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.mobileComputing.groupProject.R;
import com.mobileComputing.groupProject.adapters.CategorySpinnerAdapter;
import com.mobileComputing.groupProject.models.Category;
import com.mobileComputing.groupProject.models.Group;
import com.mobileComputing.groupProject.models.Task;
import com.mobileComputing.groupProject.models.User;
import com.mobileComputing.groupProject.services.firebase.GroupService;
import com.mobileComputing.groupProject.services.firebase.TaskService;
import com.mobileComputing.groupProject.services.firebaseMessaging.MessageService;
import com.mobileComputing.groupProject.services.interfaces.AddTaskCallBack;
import com.mobileComputing.groupProject.states.AppStates;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainCreateTaskActivity  extends AppCompatActivity {

    GroupService groupService;
    TaskService taskService;
    AppStates appStates;
    Group currentGroup;
    ImageButton return_btn;
    TextView create_task_btn, select_date, select_time, task_page_title, select_category, switch_btn;
    EditText title_et, notes_et, location_et;
    Spinner members_spinner, priority_spinner, category_spinner;
    LinearLayout date_picker_bg, time_picker_bg, color_picker_bg, new_category, existing_category;
    DatePicker date_picker;
    TimePicker time_picker;
    Button delete_task_btn;

    String nameList[];
    final String priorityList[] = {"None", "Low", "Medium", "High"};
    List<Category> categoryList;
    GridLayout color_grid;
    View color_block, select_color;
    Button save_category_btn;
    EditText category_name;
    Boolean color_selected;
    Boolean switchCategory = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_create_tasks);

        groupService = new GroupService();
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
        categoryList = new ArrayList<>();
        categoryList.add(new Category("None", R.color.white));
        List<Category> existedCategories = currentGroup.getCategoryList();
        categoryList.addAll(existedCategories);

        return_btn = findViewById(R.id.return_btn);
        create_task_btn = findViewById(R.id.create_task_btn);
        select_date = findViewById(R.id.select_date);
        select_time = findViewById(R.id.select_time);
        select_category = findViewById(R.id.select_category);
        task_page_title = findViewById(R.id.task_page_title);
        title_et = findViewById(R.id.title_et);
        notes_et = findViewById(R.id.notes_et);
        location_et = findViewById(R.id.location_et);
        members_spinner = findViewById(R.id.members_spinner);
        priority_spinner = findViewById(R.id.priority_spinner);
        category_spinner = findViewById(R.id.category_spinner);
        date_picker_bg = findViewById(R.id.date_picker_bg);
        time_picker_bg = findViewById(R.id.time_picker_bg);
        color_picker_bg = findViewById(R.id.color_picker_bg);
        date_picker = findViewById(R.id.date_picker);
        time_picker = findViewById(R.id.time_picker);
        delete_task_btn = findViewById(R.id.delete_task_btn);
        color_block = findViewById(R.id.color_block);
        save_category_btn = findViewById(R.id.save_category_btn);
        select_color = findViewById(R.id.select_color);
        category_name = findViewById(R.id.category_name);
        switch_btn = findViewById(R.id.switch_btn);
        new_category = findViewById(R.id.new_category);
        existing_category = findViewById(R.id.existing_category);

        ArrayAdapter<String> members_adapter = new ArrayAdapter<>(this, R.layout.spinner_text, nameList);
        ArrayAdapter<String> priority_adapter = new ArrayAdapter<>(this, R.layout.spinner_text, priorityList);
        CategorySpinnerAdapter category_adapter = new CategorySpinnerAdapter(this, categoryList);

        category_spinner.setAdapter(category_adapter);
        members_spinner.setAdapter(members_adapter);
        priority_spinner.setAdapter(priority_adapter);

        color_selected = false;
        int[] colorIds = {
                R.color.colorPicker1, R.color.colorPicker2, R.color.colorPicker3, R.color.colorPicker4,
                R.color.colorPicker5, R.color.colorPicker6, R.color.colorPicker7, R.color.colorPicker8,
                R.color.colorPicker9, R.color.colorPicker10
        };
        color_grid = findViewById(R.id.color_grid);
        for (int i = 0; i < colorIds.length; i++) {
            final int index = i;

            View colorBlock = new View(this);
            colorBlock.setLayoutParams(new GridLayout.LayoutParams(
                    new ViewGroup.LayoutParams(dpToPx(40), dpToPx(40))));

            colorBlock.setBackgroundResource(colorIds[i]);

            colorBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    color_selected = true;
                    color_block.setBackgroundResource(colorIds[index]);
                    category_name.setText("");
                    int hexCode = ((ColorDrawable) color_block.getBackground()).getColor();
                    for (Category category : categoryList) {
                        if (category.getHexCode() == hexCode) {
                            category_name.setText(category.getCategoryName());
                            Toast.makeText(MainCreateTaskActivity.this, "Category exist\nYou can update it", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            color_grid.addView(colorBlock);
        }

        switch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switchCategory = !switchCategory;
                if (switchCategory) {
                    new_category.setVisibility(View.VISIBLE);
                    existing_category.setVisibility(View.GONE);
                    switch_btn.setText("Existing Categories >");
                }
                else {
                    new_category.setVisibility(View.GONE);
                    existing_category.setVisibility(View.VISIBLE);
                    switch_btn.setText("New Category >");
                }
            }
        });
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

        select_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_picker_bg.setVisibility(View.VISIBLE);
            }
        });
        select_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_picker_bg.setVisibility(View.VISIBLE);
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

        color_picker_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_picker_bg.setVisibility(View.GONE);
                color_block.setBackgroundResource(R.color.text_small);
                category_name.setText("");
            }
        });

        save_category_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (switchCategory) {
                    if (category_name.getText().toString().equals("") || !color_selected) {
                        Toast.makeText(MainCreateTaskActivity.this, "Enter Category Name and Select a Color", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Drawable colorBlockBackground = color_block.getBackground();
                        select_color.setBackground(colorBlockBackground);
                        select_category.setText(category_name.getText().toString());

                        color_picker_bg.setVisibility(View.GONE);
                        color_block.setBackgroundResource(R.color.text_small);
                        category_name.setText("");
                        category_spinner.setSelection(0);
                    }
                }
                else {
                    Category category = (Category) category_spinner.getSelectedItem();
                    select_color.setBackground(null);
                    select_category.setText(category.getCategoryName());
                    select_color.setBackgroundColor(category.getHexCode());

                    color_picker_bg.setVisibility(View.GONE);
                    color_block.setBackgroundResource(R.color.text_small);
                    category_name.setText("");
                    category_spinner.setSelection(0);
                }
            }
        });



        checkTaskDetail();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
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
            String categoryName = select_category.getText().toString();
            int hexCode = ((ColorDrawable) select_color.getBackground()).getColor();

            Category category = new Category(null, hexCode);
            // call function to add task to database
            Task task = new Task(currentGroup.getGroupid(), title, notes, location, date, time, assignMember, priority, category);

            if (action.equals("create")) {
                if (hexCode != ContextCompat.getColor(MainCreateTaskActivity.this, R.color.text_small)) {
                    saveCategoryToCurrentGroup(categoryName, hexCode);
                    addCategory();
                }
                createTask(task);
            }
            else {
                if (hexCode != ContextCompat.getColor(MainCreateTaskActivity.this, R.color.text_small)) {
                    saveCategoryToCurrentGroup(categoryName, hexCode);
                    addCategory();
                }
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
                if (task.getAssignMember().equals("All")) {

                    List<User> existingUsers = currentGroup.getMembers();
                    List<String> existingUserids = new ArrayList<>();

                    for (User user : existingUsers) {
                        existingUserids.add(user.getUserid());
                    }

                    existingUserids.remove(0);

                    MessageService.sendMultipleMessages("Task Notification", task.getTitle() + " assigned to you in " + currentGroup.getGroupname(), existingUserids);
                }
                else if (!task.getAssignMember().equals("None")) {
                    List<User> existingUsers = currentGroup.getMembers();
                    for (User user : existingUsers) {
                        if (user.getUsername().equals(task.getAssignMember())) {
                            MessageService.sendMessage("Task Notification", task.getTitle() + " assigned to you in " + currentGroup.getGroupname(), user.getUserid());
                            break;
                        }
                    }
                }
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
            select_color.setBackgroundColor(task.getCategory().getHexCode());
            for (Category category : categoryList) {
                if (category.getHexCode() == task.getCategory().getHexCode()) {
                    select_category.setText(category.getCategoryName());
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
                appStates.setTask(null);
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

    private void addCategory() {
        String categoryName = select_category.getText().toString();
        int hexCode = ((ColorDrawable) select_color.getBackground()).getColor();

        Category category = new Category(categoryName, hexCode);

        groupService.addCategoryToGroup(appStates.getGroup().getGroupid(), category, new AddTaskCallBack() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void saveCategoryToCurrentGroup(String categoryName, int hexCode) {
        boolean exist = false;
        for (Category category : categoryList) {
            if (category.getHexCode() == hexCode) {
                category.setCategoryName(categoryName);
                exist = true;
                break;
            }
        }

        if (!exist) {
            categoryList.add(new Category(categoryName, hexCode));
        }
        categoryList.remove(0);
        currentGroup.setCategoryList(categoryList);
    }

}
