package com.mtanasyuk.justdoit;

import android.os.Parcel;
import android.os.Parcelable;


public class Task implements Parcelable {

    public String taskName;
    public String taskText;
    public Integer taskPriority;
    public Integer taskDay;
    public Integer taskMonth;
    public Integer taskYear;

    public Task() {
        this.taskName = "";
        this.taskText = "";
        this.taskPriority = 0;
        this.taskDay = 1;
        this.taskMonth = 1;
        this.taskYear = 2015;
    }

    public Task(String name, String description, int priority, int day, int month, int year) {
        this.taskName = name;
        this.taskText = description;
        this.taskPriority = priority;
        this.taskDay = day;
        this.taskMonth = month;
        this.taskYear = year;
    }

    protected Task(Parcel in) {
        String[] data = new String[3];
        in.readStringArray(data);
        this.taskName = data[0];
        this.taskText = data[1];
        this.taskPriority = Integer.parseInt(data[2]);
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.taskName,
                                            this.taskText,
                                            this.taskPriority.toString()});
    }
}
