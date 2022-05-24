package com.smartboard.model;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;

public class Task extends BoardItem<ChecklistItem> {

    private int taskID;
    private String description;
    private LocalDate dueDate;
    private boolean isCompleted;
    private Status status;

    public Task(String name) throws StringLengthException {
        super(name);

        this.description = "Provide a description...";
        this.isCompleted = false;
        this.status = Status.DATE_NOT_SET;
    }

    @Override
    public ChecklistItem addSubItem(String subItemName) {
        this.subItems.add(new ChecklistItem(subItemName));
        return this.subItems.get(this.subItems.size() - 1);
    }

    public void setTaskID(int taskID) { this.taskID = taskID; }

    public int getTaskID() { return this.taskID; }

    public void replaceEntireChecklist(ArrayList<ChecklistItem> newList) {
        this.subItems = new ArrayList<>(newList);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(@NotNull LocalDate newDate) {
        if (!newDate.isBefore(LocalDate.now())) {
            this.dueDate = newDate;
            setStatus();
        }
    }

    public void nullDueDate() {
        this.dueDate = null;
        setStatus();
    }

    public boolean isCompleted() {
        return this.isCompleted;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;

        for (ChecklistItem item : this.subItems) {
            item.setChecked(true);
        }

        setStatus();
    }

    public int getNumChecklistCompleted() {
        int returnVal = 0;

        for (ChecklistItem item : this.subItems) {
            if (item.isChecked()) {
                returnVal++;
            }
        }

        return returnVal;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus() {
        LocalDate today = LocalDate.now();

        if (this.isCompleted) {
            if (this.dueDate == null) {
                this.status = Status.COMPLETED_ON_TIME;
            } else {
                if (today.isAfter(this.dueDate)) {
                    this.status = Status.COMPLETED_LATE;
                } else {
                    this.status = Status.COMPLETED_ON_TIME;
                }
            }
        } else {
            if (this.dueDate == null) {
                this.status = Status.DATE_NOT_SET;
            } else {
                if (today.isAfter(this.dueDate)) {
                    this.status = Status.OVERDUE;
                } else {
                    this.status = Status.APPROACHING;
                }
            }
        }
    }
}
