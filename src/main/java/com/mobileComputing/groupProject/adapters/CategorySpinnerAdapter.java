package com.mobileComputing.groupProject.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.mobileComputing.groupProject.R;
import com.mobileComputing.groupProject.models.Category;

import java.util.List;

public class CategorySpinnerAdapter extends ArrayAdapter<Category> {

    private Context context;
    private List<Category> CategoryList;

    public CategorySpinnerAdapter(Context context, List<Category> CategoryList) {
        super(context, 0, CategoryList);
        this.context = context;
        this.CategoryList = CategoryList;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.category_spinner_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.category_name = convertView.findViewById(R.id.category_name);
            viewHolder.color_block = convertView.findViewById(R.id.color_block);
            viewHolder.spinner_item = convertView.findViewById(R.id.spinner_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Category category = CategoryList.get(position);
        String categoryName = category.getCategoryName();
        int color = category.getHexCode();
        viewHolder.category_name.setText(categoryName);
        viewHolder.color_block.setBackgroundColor(color);
        return convertView;
    }

    private static class ViewHolder {
        TextView category_name;
        View color_block;
        LinearLayout spinner_item;
    }
}
