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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.mtanasyuk.justdoit.CustomAlertDialogFragment.CustomAlertListener;

public class EditTaskActivity extends AppCompatActivity implements CustomAlertListener {

    EditText etEditTask;
    Bundle data;
    Task task;
    Spinner spinner;
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // set the toolbar as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // unpack the task
        data = getIntent().getExtras();
        task = data.getParcelable("task");

        // get a handle to the fields and set the data
        etEditTask = (EditText) findViewById(R.id.etEditTask);
        datePicker = (DatePicker) findViewById(R.id.datePicker);

        // set the spinner to custom predefined choices
        spinner = (Spinner) findViewById(R.id.priority_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priorities_array, android.R.layout.simple_list_item_activated_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (etEditTask != null & spinner != null & datePicker != null) {
            etEditTask.setText(task.taskName);
            etEditTask.setSelection(task.taskName.length());
            spinner.setSelection(task.taskPriority);
            datePicker.updateDate(task.taskYear, task.taskMonth, task.taskDay);
        }
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
                // save changes, prepare data intent
                Intent data = new Intent();
                task.taskName = etEditTask.getText().toString();
                String priority = spinner.getSelectedItem().toString();
                switch (priority) {
                    case "HIGH":
                        task.taskPriority = 0;
                        break;
                    case "MEDIUM":
                        task.taskPriority = 1;
                        break;
                    case "LOW":
                        task.taskPriority = 2;
                        break;
                }
                task.taskDay = datePicker.getDayOfMonth();
                task.taskMonth = datePicker.getMonth();
                task.taskYear = datePicker.getYear();
                // pass relevant data back as a result
                data.putExtra("mode", "edit");
                data.putExtra("task", task);
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
        // add functionality to delete a task
        Intent intent = new Intent();
        intent.putExtra("mode", "delete");
        intent.putExtra("task", task);
        setResult(RESULT_OK, intent);
        this.finish();
    }

    @Override
    public void onCancelButton() {
        // empty constructor required to implement interface
    }
}