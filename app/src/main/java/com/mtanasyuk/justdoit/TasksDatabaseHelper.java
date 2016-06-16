package com.mtanasyuk.justdoit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class TasksDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "";
    private static TasksDatabaseHelper sInstance;

    // Database Info
    private static final String DATABASE_NAME = "tasksDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_TASKS = "tasks";

    // Tasks Table Columns
    private static final String KEY_TASK_ID = "id";
    private static final String KEY_TASK_NAME = "name";
    //private static final int KEY_TASK_DATE = 0;
    private static final String KEY_TASK_TEXT = "text";
    private static final String KEY_TASK_PRIORITY = "priority";

    public static synchronized TasksDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
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
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS +
                "(" +
                KEY_TASK_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_TASK_NAME + " NAME," +
                //KEY_TASK_DATE + " DATE,"  +
                KEY_TASK_TEXT + " TEXT," +
                KEY_TASK_PRIORITY + "PRIORITY" +
                ")";

        db.execSQL(CREATE_TASKS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
            onCreate(db);
        }
    }

    // Insert or update a task in the database
    // Since SQLite doesn't support "upsert" we need to fall back on an attempt to UPDATE (in case the
    // task already exists) optionally followed by an INSERT (in case the task does not already exist).
    public long addOrUpdateTask(Task task) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long taskId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TASK_NAME, task.taskName);
            values.put(KEY_TASK_TEXT, task.taskText);
            values.put(KEY_TASK_PRIORITY, task.priority);

            // First try to update the task in case the task already exists in the database
            // This assumes taskNames are unique
            int rows = db.update(TABLE_TASKS, values, KEY_TASK_NAME + "= ?", new String[]{task.taskName});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the task we just updated
                String taskSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        KEY_TASK_ID, TABLE_TASKS, KEY_TASK_NAME);
                Cursor cursor = db.rawQuery(taskSelectQuery, new String[]{String.valueOf(task.taskName)});
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
            } else {
                // task with this taskName did not already exist, so insert new task
                taskId = db.insertOrThrow(TABLE_TASKS, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update task");
        } finally {
            db.endTransaction();
        }
        return taskId;
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();

        // SELECT * FROM TASKS
        String TASKS_SELECT_QUERY = String.format("SELECT * FROM %s", TABLE_TASKS);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TASKS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Task newTask = new Task();
                    newTask.taskName = cursor.getString(cursor.getColumnIndex(KEY_TASK_NAME));
                    newTask.taskText = cursor.getString(cursor.getColumnIndex(KEY_TASK_TEXT));
                    newTask.priority = cursor.getString(cursor.getColumnIndex(KEY_TASK_PRIORITY));
                    tasks.add(newTask);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get tasks from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return tasks;
    }

    // Update the task's name
    public int updateTaskName(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TASK_NAME, task.taskName);

        // find the task


        // Updating
        return db.update(TABLE_TASKS, values, KEY_TASK_NAME + " = ?",
                new String[] { String.valueOf(task.taskName) });
    }

    // Delete all tasks in the database
    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_TASKS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all tasks");
        } finally {
            db.endTransaction();
        }
    }
}