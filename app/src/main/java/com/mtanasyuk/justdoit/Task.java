package com.mtanasyuk.justdoit;

import java.util.ArrayList;

public class Task {

    public String taskName;
    //public Date dueDate;
    public String taskText;
    public String priority;

    public Task() {
        this.taskName = "";
        this.taskText = "";
        this.priority = "";
    }

    public Task(String name, String description, String priority) {
        this.taskName = name;
        //this.dueDate = date;
        this.taskText = description;
        this.priority = priority;
    }

    public static ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Task("task1", "very important", "high"));
        tasks.add(new Task("task2", "not very important", "med"));
        tasks.add(new Task("task3", "not important", "low"));
        return tasks;
    }

}
