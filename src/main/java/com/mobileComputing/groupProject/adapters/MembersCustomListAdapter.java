package com.mobileComputing.groupProject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileComputing.groupProject.R;
import com.mobileComputing.groupProject.models.User;

import java.util.List;

public class MembersCustomListAdapter extends ArrayAdapter<User> {
    private Context context;
    private List<User> userList;
    private boolean isSearchResult;
    private List<User> existingUsers, searchResultUsers;

    public MembersCustomListAdapter(Context context, List<User> userList, List<User> existingUsers, List<User> searchResultUsers, boolean isSearchResult) {
        super(context, 0, userList);
        this.context = context;
        this.userList = userList;
        this.existingUsers = existingUsers;
        this.searchResultUsers = searchResultUsers;
        this.isSearchResult = isSearchResult;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.group_members_or_search_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.usernameTextView = convertView.findViewById(R.id.username_et);
            viewHolder.adminButton = convertView.findViewById(R.id.admin_btn);
            viewHolder.removeButton = convertView.findViewById(R.id.remove_btn);
            viewHolder.addButton = convertView.findViewById(R.id.add_btn);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        User user = userList.get(position);
        viewHolder.usernameTextView.setText(user.getUsername());

        // Set the visibility and click listeners for buttons based on the conditions
        if (isSearchResult) {
            viewHolder.adminButton.setVisibility(View.GONE);
            viewHolder.removeButton.setVisibility(View.GONE);
            viewHolder.addButton.setVisibility(View.VISIBLE);
        } else {
            if (position == 0) {
                viewHolder.adminButton.setVisibility(View.VISIBLE);
                viewHolder.removeButton.setVisibility(View.GONE);
            } else {
                viewHolder.adminButton.setVisibility(View.GONE);
                viewHolder.removeButton.setVisibility(View.VISIBLE);
            }
            viewHolder.addButton.setVisibility(View.GONE);
        }

        // Set click listeners for buttons
        viewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle remove button click
                existingUsers.remove(user);
                notifyDataSetChanged();
                Toast.makeText(context, user.getUsername() + " removed", Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle add button click
                existingUsers.add(user);
                searchResultUsers.remove(user);
                notifyDataSetChanged();
                Toast.makeText(context, user.getUsername() + " added", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView usernameTextView;
        Button adminButton;
        Button removeButton;
        Button addButton;
    }
}