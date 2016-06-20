package com.mtanasyuk.justdoit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class TasksDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DATABASE DEBUG";
    private static TasksDatabaseHelper sInstance;

    // Database Info
    private static final String DATABASE_NAME = "tasksDatabase";
    private static final int DATABASE_VERSION = 2;

    // Table Name
    private static final String TABLE_TASKS = "tasks";

    // Tasks Table Columns
    private static final String KEY_TASK_ID = "id";
    private static final String KEY_TASK_NAME = "name";
    private static final String KEY_TASK_TEXT = "text";
    private static final String KEY_TASK_PRIORITY = "priority";

    public static synchronized TasksDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you don't accidentally leak an Activity's context.
        if (sInstance == null) {
            sInstance = new TasksDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private TasksDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS +
                "(" +
                KEY_TASK_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_TASK_NAME + " TEXT," +
                KEY_TASK_TEXT + " TEXT," +
                KEY_TASK_PRIORITY + " INTEGER" +
                ")";

        db.execSQL(CREATE_TASKS_TABLE);
    }

    /**
     * Called when the database needs to be upgraded.
     * This method will only be called if a database already exists on disk with the same DATABASE_NAME,
     * but the DATABASE_VERSION is different than the version of the database that exists on disk.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
            onCreate(db);
        }
    }

    // Insert a task into the database
    public void addTask(Task task) {

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TASK_NAME, task.taskName);
            values.put(KEY_TASK_TEXT, task.taskText);
            values.put(String.valueOf(KEY_TASK_PRIORITY), task.taskPriority);

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_TASKS, null, values);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add task to database");

        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<Task> getAllTasks() {

        ArrayList<Task> tasks = new ArrayList<>();

        // SELECT * FROM TASKS
        String TASKS_SELECT_QUERY = String.format("SELECT * FROM %s", TABLE_TASKS);


        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TASKS_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Task newTask = new Task();
                    newTask.taskName = cursor.getString(cursor.getColumnIndex(KEY_TASK_NAME));
                    newTask.taskText = cursor.getString(cursor.getColumnIndex(KEY_TASK_TEXT));
                    newTask.taskPriority = cursor.getInt(cursor.getColumnIndex(KEY_TASK_PRIORITY));
                    tasks.add(newTask);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get tasks from database");

        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }
        return tasks;
    }

    // Delete all tasks in the database
    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_TASKS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all tasks");
        } finally {
            db.endTransaction();
        }
    }

    public long findTask(Task task) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long taskId = -1;

        db.beginTransaction();
        // Get the primary key of the task
        String taskSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ? AND %s = ? AND %s = ?",
                    KEY_TASK_ID, TABLE_TASKS, KEY_TASK_NAME, KEY_TASK_TEXT, KEY_TASK_PRIORITY);
        Cursor cursor = db.rawQuery(taskSelectQuery, new String[]{  String.valueOf(task.taskName),
                                                                    String.valueOf(task.taskText),
                                                                    String.valueOf(task.taskPriority)});
        try {
            if (cursor.moveToFirst()) {
                taskId = cursor.getInt(0);
                db.setTransactionSuccessful();
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        db.endTransaction();
        return taskId;
    }

    public void updateTask(Long taskInitId, Task taskData) {

        // find task by id and update it
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TASK_NAME, taskData.taskName);
            values.put(KEY_TASK_TEXT, taskData.taskText);
            values.put(KEY_TASK_PRIORITY, taskData.taskPriority);
            db.update(TABLE_TASKS, values, KEY_TASK_ID + "=" + taskInitId, null);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to update the task");
        }

        db.endTransaction();
    }

    public void deleteTask(Long taskInitId) {

        // find task by id and delete it
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            db.delete(TABLE_TASKS, KEY_TASK_ID + "=" + taskInitId, null);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete the task");
        }

        db.endTransaction();
    }
}