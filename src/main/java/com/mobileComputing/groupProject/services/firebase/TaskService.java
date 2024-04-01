package com.mobileComputing.groupProject.services.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobileComputing.groupProject.models.Task;
import com.mobileComputing.groupProject.services.interfaces.AddTaskCallBack;
import com.mobileComputing.groupProject.services.interfaces.GetTasksCallBack;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TaskService {

    private FirebaseFirestore db;
    private CollectionReference tasksCollection;

    public TaskService() {
        db = FirebaseFirestore.getInstance();
        tasksCollection = db.collection("tasks");
    }

    public void addTask(Task task, AddTaskCallBack callBack) {
        Map<String, String> taskData = new HashMap<>();
        taskData.put("groupid", task.getGroupid());
        taskData.put("title", task.getTitle());
        taskData.put("notes", task.getNotes());
        taskData.put("location", task.getLocation());
        taskData.put("date", task.getDate());
        taskData.put("time", task.getTime());
        taskData.put("assignMember", task.getAssignMember());
        taskData.put("priority", task.getPriority());

        tasksCollection.add(taskData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        callBack.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callBack.onFailure(e);
                    }
                });
    }

    public void getAllTasksByGroupId(String groupid, GetTasksCallBack callBack) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

        tasksCollection
                .whereEqualTo("groupid", groupid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, List<Task>> tasksByDate = new HashMap<>();

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String taskid = documentSnapshot.getId();
                            String groupid = documentSnapshot.getString("groupid");
                            String title = documentSnapshot.getString("title");
                            String notes = documentSnapshot.getString("notes");
                            String location = documentSnapshot.getString("location");
                            String dateString = documentSnapshot.getString("date");
                            String time = documentSnapshot.getString("time");
                            String assignMember = documentSnapshot.getString("assignMember");
                            String priority = documentSnapshot.getString("priority");

                            // Convert the dateString to Date object
                            Date date = null;
                            try {
                                date = dateFormat.parse(dateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (date != null) {
                                Calendar taskDate = Calendar.getInstance();
                                taskDate.setTime(date);
                                taskDate.set(Calendar.HOUR_OF_DAY, 0);
                                taskDate.set(Calendar.MINUTE, 0);
                                taskDate.set(Calendar.SECOND, 0);
                                taskDate.set(Calendar.MILLISECOND, 0);

                                if (!taskDate.before(today)) {
                                    Task task = new Task(groupid, title, notes, location, dateString, time, assignMember, priority);
                                    task.setTaskid(taskid);

                                    String taskDay = dateFormat.format(taskDate.getTime());

                                    List<Task> tasks = tasksByDate.get(taskDay);
                                    if (tasks == null) {
                                        tasks = new ArrayList<>();
                                        tasksByDate.put(taskDay, tasks);
                                    }

                                    tasks.add(task);
                                }
                            }
                        }

                        callBack.onSuccess(tasksByDate);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callBack.onFailure(e);
                    }
                });
    }
}
