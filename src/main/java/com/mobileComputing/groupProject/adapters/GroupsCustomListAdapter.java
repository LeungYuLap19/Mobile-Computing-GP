package com.mobileComputing.groupProject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileComputing.groupProject.R;
import com.mobileComputing.groupProject.models.Group;

import java.util.List;

public class GroupsCustomListAdapter extends ArrayAdapter<Group> {

    private Context context;
    private List<Group> groupList;

    public GroupsCustomListAdapter(Context context, List<Group> groupList) {
        super(context, 0, groupList);
        this.context = context;
        this.groupList = groupList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.main_groups_listview_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.group_item = convertView.findViewById(R.id.group_item);
            viewHolder.group_name = convertView.findViewById(R.id.group_name);
            // viewHolder.tasks_today = ... when finished tasks add later

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Group group = groupList.get(position);
        viewHolder.group_name.setText(group.getGroupname());

        // continue after group pages finish
//        viewHolder.group_item.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        return convertView;
    }

    private static class ViewHolder {
        LinearLayout group_item;
        TextView group_name;
        // TextView tasks_today;
    }

}
