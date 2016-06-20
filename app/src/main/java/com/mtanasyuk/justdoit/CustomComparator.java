package com.mtanasyuk.justdoit;

import java.util.Comparator;

/*
    Allows comparing tasks by its priority
 */
public class CustomComparator implements Comparator<Task> {

    @Override
    public int compare(Task task1, Task task2) {
        return task1.taskPriority.compareTo(task2.taskPriority);
    }

}
