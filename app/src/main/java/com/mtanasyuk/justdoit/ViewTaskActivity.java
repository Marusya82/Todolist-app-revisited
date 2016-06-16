package com.mtanasyuk.justdoit;

//import android.app.Activity;
//import android.content.Intent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;


public class ViewTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);

        // set the toolbar as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        populateTasksList();
    }

    private void populateTasksList() {
        // Construct the data source
        ArrayList<Task> arrayOfTasks = Task.getTasks();
        // Create the adapter to convert the array to views
        CustomTasksAdapter adapter = new CustomTasksAdapter(this, arrayOfTasks);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.lvTaskView);
        listView.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                // user chose 'edit' button, bring the user to edit task activity
                Intent i = new Intent(ViewTaskActivity.this, EditTaskActivity.class);
                i.putExtra("taskContent", "");
                startActivity(i);
                return true;

            case R.id.action_back:
                // go back to the previous activity
                this.finish();
                return true;

            case R.id.action_delete:
                // delete a task, dialog to check and update
                Intent intent = new Intent();
                intent.putExtra("intent", "delete");
                setResult(RESULT_OK, intent);
                this.finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
