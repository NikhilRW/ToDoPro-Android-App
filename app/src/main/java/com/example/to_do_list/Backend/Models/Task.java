package com.example.to_do_list.Backend.Models;

public class Task {
    int id,isCompleted;
    long  dueDate;
    String name,taskCategory,attachment;
    public Task(int id, int isCompleted, long dueDate, String name, String taskCategory,
                String attachment) {
        this.id = id;
        this.isCompleted = isCompleted;
        this.dueDate = dueDate;
        this.name = name;
        this.taskCategory = taskCategory;
        this.attachment = attachment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(int isCompleted) {
        this.isCompleted = isCompleted;
    }
    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public long getDueDate() {
        return dueDate;
    }

    public String getAttachment(){
        return attachment;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaskCategory() {
        return taskCategory;
    }

    public void setTaskCategory(String taskCategory) {
        this.taskCategory = taskCategory;
    }

}
