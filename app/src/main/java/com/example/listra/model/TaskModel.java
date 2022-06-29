package com.example.listra.model;

public class TaskModel extends TaskId {
    private String Due;
    private String Task;
    private int Status, time;

    private TaskModel(){} //firebase
    //get data
    private TaskModel(String Due, String Task, int Status, int time){
        this.Due = Due;
        this.Task = Task;
        this.Status = Status;
        this.time = time;
    }

    public String getDue() {
        return Due;
    }
    public void setDue(String due) {
        this.Due = Due;
    }

    public String getTask() {
        return Task;
    }
    public void setTask(String task) {
        this.Task = Task;
    }

    public int getStatus() {
        return Status;
    }
    public void setStatus(int status) {
        this.Status = Status;
    }

    public int getTime() {
        return time;
    }
    public void setTime(int time) {
        this.time = time;
    }
}
