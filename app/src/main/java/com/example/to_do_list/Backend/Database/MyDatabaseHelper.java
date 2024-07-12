package com.example.to_do_list.Backend.Database;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todoapp.db";
    public static final String TABLE_NAME= "myTasks";
    public static final String COLUMN_taskId= "taskId";
    public static final String COLUMN_dueDate= "dueDate";
    public static final String COLUMN_taskName= "taskName";
    public static final String COLUMN_isCompleted= "isCompleted";
    public static final String COLUMN_taskCategory= "taskCategory";
    private static final int DATABASE_VERSION = 2;
    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+" (taskId INTEGER PRIMARY KEY AUTOINCREMENT,taskCategory TEXT DEFAULT 'Default', taskName TEXT,isCompleted INTEGER DEFAULT 0,dueDate INTEGER DEFAULT 0);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
