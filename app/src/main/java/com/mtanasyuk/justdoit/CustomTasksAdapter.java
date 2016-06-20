package com.mtanasyuk.justdoit;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomTasksAdapter extends ArrayAdapter<Task> {
    public CustomTasksAdapter(Context context, ArrayList<Task> arrayOfTasks) {
        super(context, 0, arrayOfTasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Task task = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task_main, parent, false);
        }

        // Lookup view for data population
        TextView taskName = (TextView) convertView.findViewById(R.id.tvTaskName);
        TextView taskPrior = (TextView) convertView.findViewById(R.id.tvTaskPrior);

        // Populate the data into the template view using the data object
        Integer priority = task.taskPriority;
        taskName.setText(task.taskName);

        switch(priority) {
            case 0: taskPrior.setText("HIGH");
                taskPrior.setTextColor(Color.RED);
                break;
            case 1: taskPrior.setText("MEDIUM");
                taskPrior.setTextColor(Color.BLUE);
                break;
            case 2: taskPrior.setText("LOW");
                taskPrior.setTextColor(Color.GREEN);
                break;
        }

        // Return the completed view to render on screen
        return convertView;
    }

}
