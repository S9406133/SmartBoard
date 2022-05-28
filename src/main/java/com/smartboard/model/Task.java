/**
 * This is the class which defines the Task held in the list of a Column
 */

package com.smartboard.model;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;

public class Task extends BoardItem<ChecklistItem> implements Reorderable {

    private int taskID;
    private int columnID;
    private int orderIndex;
    private String description;
    private LocalDate dueDate;
    private boolean isCompleted;
    private Status status;

    public Task(String name, int columnID) throws StringLengthException {
        super(name);

        this.columnID = columnID;
        this.description = "Provide a description...";
        this.isCompleted = false;
        this.status = Status.DATE_NOT_SET;
    }

    @Override
    public ChecklistItem addSubItem(String subItemName) {
        this.subItems.add(new ChecklistItem(subItemName));
        ChecklistItem newCli = this.subItems.get(this.subItems.size() - 1);
        newCli.setTaskID(this.getTaskID());
        return newCli;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getTaskID() {
        return this.taskID;
    }

    public void setColumnID(int columnID) {
        this.columnID = columnID;
    }

    public int getColumnID() {
        return this.columnID;
    }

    @Override
    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Override
    public int getOrderIndex() {
        return this.orderIndex;
    }

    /**
     * Replaces all ChecklistItems in the subItems list with newList
     */
    public void replaceEntireChecklist(ArrayList<ChecklistItem> newList) {
        this.subItems = new ArrayList<>(newList);
        if (this.taskID > 0) {
            Task_Utils.refreshTaskCLItems(this.taskID, newList);
        }
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
        this.dueDate = newDate;
        setStatus();
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

        if (completed) {
            for (ChecklistItem item : this.subItems) {
                item.setChecked(true);
            }
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

    /**
     * Sets Task Status
     * ** No due date set ->
     * and not completed = DATE_NOT_SET
     * and is completed = COMPLETED_ON_TIME,
     * ** Due date is set and not completed ->
     * and is before or on the due date = APPROACHING
     * and is after the due date = OVERDUE,
     * ** Due date is set and is completed ->
     * and is before or on the due date = COMPLETED_ON_TIME
     * and is after the due date = COMPLETED_LATE,
     */
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
