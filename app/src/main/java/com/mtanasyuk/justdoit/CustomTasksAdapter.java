package com.mtanasyuk.justdoit;

import android.content.Context;
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
        }

        // Lookup view for data population
        TextView taskName = (TextView) convertView.findViewById(R.id.tvTaskName);
        TextView taskPrior = (TextView) convertView.findViewById(R.id.tvTaskPrior);

        // Populate the data into the template view using the data object
        taskName.setText(task.taskName);
        taskPrior.setText(task.priority);

        // Return the completed view to render on screen
        return convertView;
    }
}
