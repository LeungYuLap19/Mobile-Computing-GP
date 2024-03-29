package com.mobileComputing.groupProject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mobileComputing.groupProject.R;

import java.util.List;

public class GroupsCustomListAdapter extends ArrayAdapter<String> {

    public GroupsCustomListAdapter(@NonNull Context context, List<String> items) {
        super(context, 0, items);
    }

//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        View itemView = convertView;
//        if (itemView == null) {
//            itemView = LayoutInflater.from(getContext()).inflate(R.layout.main_groups_listview_item, parent, false);
//        }
//
//
//    }
}
