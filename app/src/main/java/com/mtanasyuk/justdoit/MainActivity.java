package com.mtanasyuk.justdoit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
    EditText etNewTask;

    // set REQUEST_CODE to any value, used to determine the result type later
    private final int REQUEST_CODE = 20;

    // store position of a click
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get a handle to ListView
        lvTasks = (ListView) findViewById(R.id.lvTasks);

        // read tasks from a file
        readTasks();

        tasksAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);
        lvTasks.setAdapter(tasksAdapter);

        // get a handle to EditView
        etNewTask = (EditText) findViewById(R.id.etNewTask);

        // set up list view listener to remove items
        setupListViewListener();
    }

    private void readTasks() {
        File filesDir = getFilesDir();
        File justdoitFile = new File(filesDir, "justdoit.txt");
        try {
            tasks = new ArrayList<String>(FileUtils.readLines(justdoitFile));
        } catch (IOException e) {
            tasks = new ArrayList<String>();
        }
    }

    private void writeTasks() {
        File filesDir = getFilesDir();
        File justdoitFile = new File(filesDir, "justdoit.txt");
        try {
            FileUtils.writeLines(justdoitFile,tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupListViewListener() {
        lvTasks.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter, View task, int pos, long id) {
                        tasks.remove(pos);
                        tasksAdapter.notifyDataSetChanged();
                        writeTasks();
                        return true;
                    }
                }
        );

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

//    private void launchEditActivity() {
//
//    }

    // time to handle the result of the sub-activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            String taskEdited = data.getExtras().getString("taskEdited");
            // update the view and the tasks file
            tasks.set(position, taskEdited);
            tasksAdapter.notifyDataSetChanged();
            writeTasks();
        }
    }

    public void onAddTask(View view) {
        String newTask = etNewTask.getText().toString();
        tasksAdapter.add(newTask);
        etNewTask.setText("");
        writeTasks();
    }
}
