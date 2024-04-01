package com.mobileComputing.groupProject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mobileComputing.groupProject.R;
import com.mobileComputing.groupProject.models.Task;

import java.util.List;

public class TasksCustomListAdapter extends ArrayAdapter<Task> {

    private Context context;
    private List<Task> tasksList;

    public TasksCustomListAdapter(Context context, List<Task> tasksList) {
        super(context, 0, tasksList);
        this.context = context;
        this.tasksList = tasksList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.task_listview_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.task_item = convertView.findViewById(R.id.task_item);
            viewHolder.finish_btn = convertView.findViewById(R.id.finish_btn);
            viewHolder.finish_btn_empty = convertView.findViewById(R.id.finish_btn_empty);
            viewHolder.task_name = convertView.findViewById(R.id.task_name);
            viewHolder.task_time = convertView.findViewById(R.id.task_time);
            viewHolder.member_assign = convertView.findViewById(R.id.member_assign);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Task task = tasksList.get(position);
        if (task.getPriority().equals("Low")) {
            viewHolder.task_name.setText("\u2757" + task.getTitle());
        }
        if (task.getPriority().equals("Medium")) {
            viewHolder.task_name.setText("\u2757\u2757" + task.getTitle());
        }
        if (task.getPriority().equals("High")) {
            viewHolder.task_name.setText("\u2757\u2757\u2757" + task.getTitle());
        }
        else {
            viewHolder.task_name.setText(task.getTitle());
        }
        viewHolder.task_time.setText("At " + task.getTime());
        viewHolder.member_assign.setText(task.getAssignMember());

        return convertView;
    }

    private static class ViewHolder {
        LinearLayout task_item, finish_btn;
        View finish_btn_empty;
        TextView task_name, task_time, member_assign;
    }
}
