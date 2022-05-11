package com.smartboard.model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Task extends BoardItem<ChecklistItem> {

    private String description;
    private LocalDate dueDate;
    private boolean isCompleted;

    public Task(String name) throws StringLengthException {
        super(name);

        this.description = "Provide a description...";
        this.isCompleted = false;

        this.subItems.add(new ChecklistItem("A list item"));
        this.getSubItem(0).setChecked(true);
        this.subItems.add(new ChecklistItem("B list item"));
        this.subItems.add(new ChecklistItem("C list item"));
    }

    @Override
    public ChecklistItem addSubItem(String subItemName) {
        this.subItems.add(new ChecklistItem(subItemName));
        return this.subItems.get(this.subItems.size() - 1);
    }

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

    public void setDueDate(LocalDate newDate) {
        if (newDate.isAfter(LocalDate.now())) {
            this.dueDate = newDate;
        }
    }

    public void nullDueDate() {
        this.dueDate = null;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
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
}
