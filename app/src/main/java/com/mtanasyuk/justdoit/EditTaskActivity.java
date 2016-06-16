package com.mtanasyuk.justdoit;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.mtanasyuk.justdoit.CustomAlertDialogFragment.CustomAlertListener;

public class EditTaskActivity extends AppCompatActivity implements CustomAlertListener {

    EditText etEditTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // set the toolbar as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // get a handle to edit text field
        etEditTask = (EditText) findViewById(R.id.etEditTask);
        String taskToEdit = getIntent().getStringExtra("taskContent");
        etEditTask.setText(taskToEdit);
        etEditTask.setSelection(taskToEdit.length());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_back:
                // go back to the previous activity
                this.finish();
                return true;

            case R.id.action_save:
                // save changes, update files
                // prepare data intent
                Intent data = new Intent();
                // pass relevant data back as a result
                data.putExtra("intent", "edit");
                data.putExtra("taskEdited", etEditTask.getText().toString());
                setResult(RESULT_OK, data);
                // closes this activity and returns to main activity
                this.finish();
                return true;

            case R.id.action_delete:
                // create a dialog to check and update
                onShowAlertDialog(this.getCurrentFocus());
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public void onShowAlertDialog(View view) {
        FragmentManager fm = getSupportFragmentManager();
        CustomAlertDialogFragment alertDialog = CustomAlertDialogFragment.newInstance("Removing the task");
        alertDialog.show(fm, "fragment_alert");
    }

    @Override
    public void onOKButton() {
        // add functionality for delete a task
        Intent intent = new Intent();
        intent.putExtra("intent", "delete");
        setResult(RESULT_OK, intent);
        this.finish();
    }

    @Override
    public void onCancelButton() {
        // add functionality for delete a task
    }
}