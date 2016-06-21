package com.mtanasyuk.justdoit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    // create list of tasks that is going to store results of database query
    ArrayList<Task> tasks;

    // custom adapter allows displaying the contents of an ArrayList within a ListView with other fields
    CustomTasksAdapter tasksAdapter;

    ListView lvTasks;

    // set REQUEST_CODE to any value, used to determine the result type later
    private final int REQUEST_CODE = 20;

    // store position of a click
    int position;

    // boolean to separate create and save actions
    boolean create;

    // database instance
    TasksDatabaseHelper helper;

    Task taskInit;
    Long taskInitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set the toolbar as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // get db instance and construct the data source
        helper = TasksDatabaseHelper.getInstance(this);
//        helper.deleteAll();;
        tasks = helper.getAllTasks();

        // sort all tasks based on its priority
        Collections.sort(tasks, new CustomComparator());

        // Create a custom adapter to convert the array to views
        tasksAdapter = new CustomTasksAdapter(this, tasks);
        lvTasks = (ListView) findViewById(R.id.lvTasks);
        if (lvTasks != null) {
            lvTasks.setAdapter(tasksAdapter);
        }

        // set up list view listener to remove items
        setupListViewListener();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                // user chose 'add' button, create new task out of empty string, update file
                Intent i = new Intent(MainActivity.this, EditTaskActivity.class);
                i.putExtra("task", new Task());
                // set create to true since we are creating a new task
                create = true;
                startActivityForResult(i, REQUEST_CODE);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    // bring up a clicked task to edit
    private void setupListViewListener() {
        lvTasks.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View task, int pos, long id) {
                        Intent i = new Intent(MainActivity.this, EditTaskActivity.class);
                        position = pos;
                        taskInit = tasks.get(position);
                        taskInitId = helper.findTask(taskInit);
                        i.putExtra("task", taskInit);
                        startActivityForResult(i, REQUEST_CODE);
                    }
                }
        );
    }

    // time to handle the result of the sub-activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // check if returned result is edit or delete request
            String mode = data.getExtras().getString("mode");
            if (mode != null && mode.equals("edit")) {
                // extract edited task value from result extras
                Bundle bundle = data.getExtras();
                Task taskData = bundle.getParcelable("task");
                // update the view and the tasks file
                if (create) {
                    helper.addTask(taskData);
                    tasks.add(taskData);
                    create = false;
                } else {
                    helper.updateTask(taskInitId, taskData);
                    tasks.set(position, taskData);
                }
                Collections.sort(tasks, new CustomComparator());
            } else if (mode != null && mode.equals("delete")) {
                if (create) {
                    create = false;
                } else {
                    tasks.remove(position);
                    helper.deleteTask(taskInitId);
                }
            }
            tasksAdapter.notifyDataSetChanged();
        }
    }
}