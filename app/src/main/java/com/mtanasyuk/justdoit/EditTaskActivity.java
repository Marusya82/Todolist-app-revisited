package com.mtanasyuk.justdoit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EditTaskActivity extends AppCompatActivity {

    EditText etEditTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        // get a handle to edit text field
        etEditTask = (EditText) findViewById(R.id.etEditTask);
        String taskToEdit = getIntent().getStringExtra("taskContent");
        etEditTask.setText(taskToEdit);
    }

    public void onSaveTask(View view) {
        // prepare data intent
        Intent data = new Intent();
        // pass relevant data back as a result
        data.putExtra("taskEdited", etEditTask.getText().toString());
        setResult(RESULT_OK, data);
        // closes this activity and returns to main activity
        this.finish();
    }
}