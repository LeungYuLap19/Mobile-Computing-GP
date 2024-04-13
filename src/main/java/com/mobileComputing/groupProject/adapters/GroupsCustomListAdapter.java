    package com.mobileComputing.groupProject.adapters;

    import android.content.Context;
    import android.content.Intent;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ArrayAdapter;
    import android.widget.LinearLayout;
    import android.widget.TextView;

    import com.mobileComputing.groupProject.R;
    import com.mobileComputing.groupProject.activities.MainGroupTasksActivity;
    import com.mobileComputing.groupProject.models.Category;
    import com.mobileComputing.groupProject.models.Group;
    import com.mobileComputing.groupProject.models.Task;
    import com.mobileComputing.groupProject.services.firebase.TaskService;
    import com.mobileComputing.groupProject.services.interfaces.GetTasksCallBack;
    import com.mobileComputing.groupProject.services.interfaces.TaskCountCallback;
    import com.mobileComputing.groupProject.states.AppStates;

    import java.text.SimpleDateFormat;
    import java.util.Date;
    import java.util.List;
    import java.util.Locale;
    import java.util.Map;

    public class GroupsCustomListAdapter extends ArrayAdapter<Group> {

        AppStates appStates;
        private Context context;
        private List<Group> groupList;
        private TaskService taskService;
        Map<String, List<Task>> tasksList;
        int countToday;

        public GroupsCustomListAdapter(Context context, List<Group> groupList, AppStates appStates) {
            super(context, 0, groupList);
            this.context = context;
            this.groupList = groupList;
            this.appStates = appStates;
            this.taskService = new TaskService();
            this.countToday = 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.main_groups_listview_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.group_item = convertView.findViewById(R.id.group_item);
                viewHolder.group_name = convertView.findViewById(R.id.group_name);
                viewHolder.tasks_today = convertView.findViewById(R.id.tasks_today);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Group group = groupList.get(position);
            viewHolder.group_name.setText(group.getGroupname());
            getTaskNumberOfToday(group.getGroupid(), new TaskCountCallback() {
                @Override
                public void onSuccess(int count) {
                    viewHolder.tasks_today.setText("has " + count + " tasks today");
                }

                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                }
            });

            viewHolder.group_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    appStates.setGroup(group);
                    Intent intent = new Intent(context, MainGroupTasksActivity.class);
                    context.startActivities(new Intent[]{intent});
                }
            });

            return convertView;
        }

        private static class ViewHolder {
            LinearLayout group_item;
            TextView group_name;
            TextView tasks_today;
        }

        private void getTaskNumberOfToday(String groupId, TaskCountCallback callback) {
            taskService.getAllTasksByGroupId(groupId, new GetTasksCallBack() {
                @Override
                public void onSuccess(Map<String, List<Task>> tasksByDate) {
                    int count = 0;

                    Date today = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
                    String formattedDate = dateFormat.format(today);

                    for (String key : tasksByDate.keySet()) {
                        if (key.equals(formattedDate)) {
                            count = tasksByDate.get(key).size();
                            break;
                        }
                    }

                    callback.onSuccess(count);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.onFailure(e);
                }
            });
        }
    };
