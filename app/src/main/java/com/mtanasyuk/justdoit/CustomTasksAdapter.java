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

    private static class ViewHolder {
        TextView vhName;
        TextView vhPriority;
    }

    public CustomTasksAdapter(Context context, ArrayList<Task> arrayOfTasks) {
        super(context, 0, arrayOfTasks);
    }

    // Returns the actual view used as a row within ListView at a particular position
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Task task = getItem(position);

        ViewHolder viewHolder;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_task_main, parent, false);

            // Lookup view for data population
            viewHolder.vhName = (TextView) convertView.findViewById(R.id.tvTaskName);
            viewHolder.vhPriority = (TextView) convertView.findViewById(R.id.tvTaskPrior);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // populate data into the template view using the data object
        viewHolder.vhName.setText(task.taskName);
        Integer priority = task.taskPriority;

        switch(priority) {
            case 0: viewHolder.vhPriority.setText("HIGH");
                viewHolder.vhPriority.setTextColor(Color.RED);
                break;
            case 1: viewHolder.vhPriority.setText("MEDIUM");
                viewHolder.vhPriority.setTextColor(Color.BLUE);
                break;
            case 2: viewHolder.vhPriority.setText("LOW");
                viewHolder.vhPriority.setTextColor(Color.GREEN);
                break;
        }

        // Return the completed view to render on screen
        return convertView;
    }

}
