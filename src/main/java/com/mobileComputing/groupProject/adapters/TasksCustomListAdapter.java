package com.mobileComputing.groupProject.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.mobileComputing.groupProject.R;
import com.mobileComputing.groupProject.activities.MainCreateTaskActivity;
import com.mobileComputing.groupProject.models.Task;
import com.mobileComputing.groupProject.services.firebase.TaskService;
import com.mobileComputing.groupProject.services.interfaces.AddTaskCallBack;
import com.mobileComputing.groupProject.states.AppStates;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class TasksCustomListAdapter extends ArrayAdapter<Task> {

    TaskService taskService;
    private Context context;
    private List<Task> tasksList;
    private AppStates appStates;

    public TasksCustomListAdapter(Context context, List<Task> tasksList, AppStates appStates) {
        super(context, 0, tasksList);
        this.taskService = new TaskService();
        this.context = context;
        this.tasksList = tasksList;
        this.appStates = appStates;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.task_listview_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.task_item = convertView.findViewById(R.id.task_item);
            viewHolder.finish_btn = convertView.findViewById(R.id.finish_btn);
            viewHolder.task_name = convertView.findViewById(R.id.task_name);
            viewHolder.task_time = convertView.findViewById(R.id.task_time);
            viewHolder.member_assign = convertView.findViewById(R.id.member_assign);
            viewHolder.category_color = convertView.findViewById(R.id.category_color);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Task task = tasksList.get(position);

        String prioritySign = "";
        int priorityColor = ContextCompat.getColor(context, R.color.text_highlight);
        if (task.getPriority().equals("Low")) {
            prioritySign = "! ";
        } else if (task.getPriority().equals("Medium")) {
            prioritySign = "! ! ";
        } else if (task.getPriority().equals("High")) {
            prioritySign = "! ! ! ";
        }

        String taskTitle = task.getTitle();
        String fullTitle = prioritySign + taskTitle;

        SpannableString spannableString = new SpannableString(fullTitle);
        spannableString.setSpan(new ForegroundColorSpan(priorityColor), 0, prioritySign.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        viewHolder.task_name.setText(spannableString);
        viewHolder.task_time.setText("At " + task.getTime());
        viewHolder.member_assign.setText(task.getAssignMember());

        if (task.getDone()) {
            viewHolder.task_name.setTextColor(ContextCompat.getColor(context, R.color.text_small));
            viewHolder.task_time.setTextColor(ContextCompat.getColor(context, R.color.text_small));
            viewHolder.finish_btn.setBackgroundResource(R.drawable.layout_task_done_btn_bg);
        } else if (!task.getDone()) {
            viewHolder.finish_btn.setBackgroundResource(R.drawable.layout_task_btn_bg);
            if(overtime(task.getTime(), task.getDate())) {
                viewHolder.task_time.setTextColor(ContextCompat.getColor(context, R.color.text_highlight));
            }
            viewHolder.task_name.setTextColor(ContextCompat.getColor(context, R.color.text_regular));
        }

        viewHolder.finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Debug", String.valueOf(task.getDone()));
                setTaskdone(task.getTaskid(), !task.getDone());
                task.setDone(!task.getDone());
                notifyDataSetChanged();
            }
        });

        viewHolder.task_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appStates.setTask(task);
                Intent intent = new Intent(context, MainCreateTaskActivity.class);
                context.startActivities(new Intent[]{intent});
                ((Activity) context).finish();
            }
        });

        if (task.getAssignMember().equals("None") || task.getAssignMember().equals("All") || task.getAssignMember().equals(appStates.getUser().getUsername())) {
            Log.d("Debug", task.getAssignMember());
            viewHolder.finish_btn.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.finish_btn.setVisibility(View.INVISIBLE);
        }

        viewHolder.category_color.setBackgroundColor(task.getCategory().getHexCode());

        return convertView;
    }

    private static class ViewHolder {
        LinearLayout task_item, finish_btn;
        TextView task_name, task_time, member_assign;
        View category_color;
    }

    private boolean overtime(String time, String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        String currentDateTime = sdf.format(new Date());

        try {
            Date taskDateTime = sdf.parse(date + " " + time);
            Date currentDateTimeObj = sdf.parse(currentDateTime);

            return currentDateTimeObj.compareTo(taskDateTime) > 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void setTaskdone(String taskid, boolean taskState) {
        taskService.setTaskDone(taskid, taskState, new AddTaskCallBack() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }
}
