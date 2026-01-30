package com.example.to_do_list.Backend;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.to_do_list.Backend.Database.MyDatabaseHelper;
import com.example.to_do_list.Backend.Models.Task;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ToDoDatabaseManager {
    private final MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    public ToDoDatabaseManager(Context context) {
        dbHelper = new MyDatabaseHelper(context);
    }
    public void open() {
        db = dbHelper.getWritableDatabase();
    }
    public void close() {
        dbHelper.close();
    }
    // Create (Insert) a new user
    public List<Task> fetchAllTaskOf(String taskCategory){
            List<Task> tasks = new ArrayList<>();
            Cursor cursor = db.query(MyDatabaseHelper.TABLE_NAME, null, "taskCategory = ?", new String[]{taskCategory}, null, null, "taskId");
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_taskId));
                String taskName = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_taskName));
                long dueDate = cursor.getLong(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_dueDate));
                int isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_isCompleted));
                String attachment = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_attachment));
                tasks.add(new Task(id,isCompleted,dueDate,taskName,taskCategory,attachment));
            }
            cursor.close();
            return tasks;
    }

    public void addOrUpdateAttachment(String taskId,String filepath){
        ContentValues values = new ContentValues();
        values.put("attachment", filepath);
        String selection = "taskId = ?";
        String[] selectionArgs = { String.valueOf(taskId) };
        db.update(MyDatabaseHelper.TABLE_NAME, values, selection, selectionArgs);
    }
    public ArrayList<String> fetchAllTaskNameOf(String taskCategory,boolean isNotCompletedOnly){
        ArrayList<String> tasksName = new ArrayList<String>();
        Cursor cursor;
        if(isNotCompletedOnly){
            cursor = db.query(MyDatabaseHelper.TABLE_NAME, null, "taskCategory = ? AND isCompleted = ? ", new String[]{taskCategory,"0"}, null, null, "taskId");
        }
        else{
         cursor = db.query(MyDatabaseHelper.TABLE_NAME, null, "taskCategory = ?", new String[]{taskCategory}, null, null, "taskId");
        }
        while (cursor.moveToNext()) {
            String taskName = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_taskName));
            tasksName.add(taskName);
        }
        cursor.close();
        return tasksName;
    }
    public ArrayList<Integer> getAllTaskIds(boolean isNotCompleted,String taskCategory) {
        ArrayList<Integer> allTaskIds= new ArrayList<Integer>();
        Cursor cursor;
        if (isNotCompleted) {
            cursor = db.query(
                    MyDatabaseHelper.TABLE_NAME,
                    new String[]{MyDatabaseHelper.COLUMN_taskId}, // Specify the columns you need
                    MyDatabaseHelper.COLUMN_isCompleted + " = ?" +"AND "+MyDatabaseHelper.COLUMN_taskCategory+" = ? ",
                    new String[]{"0",taskCategory}, // Selection arguments
                    null,
                    null,
                    null
            );
        } else {
            cursor = db.query(
                    MyDatabaseHelper.TABLE_NAME,
                    new String[]{MyDatabaseHelper.COLUMN_taskId},
                    MyDatabaseHelper.COLUMN_taskCategory+" = ? ",
                    new String[]{taskCategory},
                    null,
                    null,
                    null
            );
        }

        while (cursor.moveToNext()) {
            int taskId = cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_taskId));
            allTaskIds.add(taskId);
        }
        cursor.close();
        return allTaskIds;
    }
    public List<Task> fetchAllTask(){
            List<Task> tasks = new ArrayList<>();
            Cursor cursor = db.query(MyDatabaseHelper.TABLE_NAME, null, null, null, null, null, "taskId");
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_taskId));
                String taskName = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_taskName));
                long dueDate = cursor.getLong(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_dueDate));
                int isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_isCompleted));
                String taskCategory = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_taskCategory));
                String attachment = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_attachment));
                tasks.add(new Task(id,isCompleted,dueDate,taskName,taskCategory,attachment));
            }
            cursor.close();
            return tasks;
    }
    public Task getTaskDetails(int myTaskId){
        Task task = new Task(0,0,0,"","",null);
        Cursor cursor = db.query(MyDatabaseHelper.TABLE_NAME, null, "taskId = ? ", new String[]{String.valueOf(myTaskId)}, null, null, "taskId");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_taskId));
            String taskName = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_taskName));
            long dueDate = cursor.getLong(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_dueDate));
            int isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_isCompleted));
            String taskCategory = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_taskCategory));
            String attachment = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_attachment));
            task = new Task(id,isCompleted,dueDate,taskName,taskCategory,attachment);
        }
        cursor.close();
        return task;
    }
    private ContentValues getContentValues(Integer taskId,String taskName, int isCompleted,String taskCategory,String attachmentFilePath,Long dueDate){
        ContentValues values = getContentValues(taskName,isCompleted,taskCategory,attachmentFilePath,dueDate);
        if(taskId != null){
            values.put(MyDatabaseHelper.COLUMN_taskId, taskId);
        }
        return values;
    }

    private ContentValues getContentValues(String taskName, int isCompleted,String taskCategory,String attachmentFilePath,Long dueDate){
        ContentValues values = new ContentValues();
        if(taskName != null){
            values.put(MyDatabaseHelper.COLUMN_taskName, taskName);
        }
        if(isCompleted == 1 || isCompleted == 0){
            values.put(MyDatabaseHelper.COLUMN_isCompleted, isCompleted);
        }
        if(taskCategory != null){
            values.put(MyDatabaseHelper.COLUMN_taskCategory,taskCategory);
        }
        else{
            values.put(MyDatabaseHelper.COLUMN_taskCategory, "default");
        }
        if(attachmentFilePath != null){
            values.put( MyDatabaseHelper.COLUMN_attachment, attachmentFilePath);
        }
        if(dueDate != null){
            values.put( MyDatabaseHelper.COLUMN_dueDate, dueDate);
        }
        return values;
    }
    // Add A Task
    public void addTask(String taskName, int isCompleted,String taskCategory) {
        ContentValues values = getContentValues(taskName,isCompleted,taskCategory,null,null);
        long result = db.insert(MyDatabaseHelper.TABLE_NAME, null, values);
        Log.d("insertUser", "insertUser Result :  "+result);
    }
    public void addTask(String taskName, int isCompleted,String taskCategory,String attachmentFilePath) {
        ContentValues values = getContentValues(taskName,isCompleted,taskCategory,attachmentFilePath,null);
        long result = db.insert(MyDatabaseHelper.TABLE_NAME, null, values);
        Log.d("insertUser", "insertUser Result :  "+result);
    }

    public void addTask(String taskName, int isCompleted,long dueDate,String taskCategory,String attachmentFilePath) {
        ContentValues values = getContentValues(taskName,isCompleted,taskCategory,attachmentFilePath,dueDate);
        long result = db.insert(MyDatabaseHelper.TABLE_NAME, null, values);
        Log.d("insertUser", "insertUser Result :  "+result);
    }
    // Update a Task details
    public int updateTask(int taskId,String taskName,int isCompleted,long dueDate,String taskCategory) {
        ContentValues values = getContentValues(taskId,taskName,isCompleted,taskCategory,null,dueDate);
        String selection = "taskId = ?";
        String[] selectionArgs = { String.valueOf(taskId) };
        return db.update(MyDatabaseHelper.TABLE_NAME, values, selection, selectionArgs);
    }
    public int updateTask(int taskId,String taskName,int isCompleted,long dueDate,String taskCategory,String attachmentFilePath) {
        ContentValues values = getContentValues(taskId,taskName,isCompleted,taskCategory,attachmentFilePath,dueDate);
        String selection = "taskId = ?";
        String[] selectionArgs = { String.valueOf(taskId) };
        return db.update(MyDatabaseHelper.TABLE_NAME, values, selection, selectionArgs);
    }


    public int setIsCompleted(int taskId,int isCompleted) {
        ContentValues values = new ContentValues();
        values.put("isCompleted", isCompleted);
        String selection = "taskId = ?";
        String[] selectionArgs = { String.valueOf(taskId) };
        return db.update(MyDatabaseHelper.TABLE_NAME, values, selection, selectionArgs);
    }
    // Delete a Task
    public int deleteTask(int taskId) {
        String selection = "taskId = ?";
        String[] selectionArgs = { String.valueOf(taskId)};
        return db.delete(MyDatabaseHelper.TABLE_NAME, selection, selectionArgs);
    }
    public int deleteAllTask() {
        return db.delete(MyDatabaseHelper.TABLE_NAME, null, null);
    }

    public ArrayList<String> getAllTaskNamesOf(boolean isNotCompleted,String taskCategory) {
        ArrayList<String> allTaskNames = new ArrayList<>();
        Cursor cursor;

        if (isNotCompleted) {
            cursor = db.query(
                    MyDatabaseHelper.TABLE_NAME,
                    new String[]{MyDatabaseHelper.COLUMN_taskName}, // Specify the columns you need
                    MyDatabaseHelper.COLUMN_isCompleted + " = ?"+" AND "+MyDatabaseHelper.COLUMN_taskCategory+" = ? ",
                    new String[]{"0",taskCategory}, // Selection arguments
                    null,
                    null,
                    null
            );
        } else {
            cursor = db.query(
                    MyDatabaseHelper.TABLE_NAME,
                    new String[]{MyDatabaseHelper.COLUMN_taskName}, // Specify the columns you need
                    MyDatabaseHelper.COLUMN_taskCategory+" = ? ",
                    new String[]{taskCategory},
                    null,
                    null,
                    null
            );
        }

        while (cursor.moveToNext()) {
            String taskName = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_taskName));
            allTaskNames.add(taskName);
        }
        Log.d("myTask", "getAllTaskNamesOf: "+allTaskNames);
        cursor.close();
        return allTaskNames;
    }

    public ArrayList<String> getAllTaskCategory(){
        ArrayList<String> allTaskCategory=  new ArrayList<String>();
        Cursor cursor = db.query(MyDatabaseHelper.TABLE_NAME, new String[]{MyDatabaseHelper.COLUMN_taskCategory},null, null,MyDatabaseHelper.COLUMN_taskCategory, null, null);
        while (cursor.moveToNext()) {
            String taskCategory = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.COLUMN_taskCategory));
            allTaskCategory.add(taskCategory);
        }
        cursor.close();
        return allTaskCategory;
    }

    public void defaultTaskForExample(){
        addTask("Work Task",0,"Work");
        addTask("Default",0,"Default");
        addTask("Personal Task",0,"Personal");
    }
}
