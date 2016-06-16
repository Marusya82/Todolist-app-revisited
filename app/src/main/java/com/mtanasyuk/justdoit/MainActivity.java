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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // create list of tasks
    ArrayList<String> tasks;

    // an adapter allows displaying the contents of an ArrayList within a ListView
    ArrayAdapter<String> tasksAdapter;

    ListView lvTasks;
//    EditText etNewTask;

    // set REQUEST_CODE to any value, used to determine the result type later
    private final int REQUEST_CODE = 20;

    // store position of a click
    int position;

    // boolean to separate create and save actions
    boolean create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set the toolbar as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // get a handle to ListView
        lvTasks = (ListView) findViewById(R.id.lvTasks);

        // read tasks from a file
        readTasks();

        tasksAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);
        lvTasks.setAdapter(tasksAdapter);

        // set up list view listener to remove items
        setupListViewListener();

        // get database instance
        TasksDatabaseHelper helper = TasksDatabaseHelper.getInstance(this);
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
                i.putExtra("taskContent", "");
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

    private void readTasks() {
        File filesDir = getFilesDir();
        File justdoitFile = new File(filesDir, "justdoit.txt");
        try {
            tasks = new ArrayList<>(FileUtils.readLines(justdoitFile));
        } catch (IOException e) {
            tasks = new ArrayList<>();
        }
    }

    private void writeTasks() {
        File filesDir = getFilesDir();
        File justdoitFile = new File(filesDir, "justdoit.txt");
        try {
            FileUtils.writeLines(justdoitFile, tasks);
        } catch (IOException e) {
            e.printStackTrace();
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
                        String taskContent = tasks.get(position);
                        i.putExtra("taskContent", taskContent);
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
            String intent = data.getExtras().getString("intent");
            if (intent != null && intent.equals("edit")) {
                // extract edited task value from result extras
                String taskEdited = data.getExtras().getString("taskEdited");
                // update the view and the tasks file
                if (create) {
                    tasks.add(taskEdited);
                    create = false;
                } else {
                    tasks.set(position, taskEdited);
                }
                tasksAdapter.notifyDataSetChanged();
                writeTasks();
            } else if (intent != null && intent.equals("delete")) {
                if (create) {
                    create = false;
                } else {
                    tasks.remove(position);
                    tasksAdapter.notifyDataSetChanged();
                    writeTasks();
                }
            }
        }
    }
}