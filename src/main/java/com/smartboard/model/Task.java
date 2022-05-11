package com.smartboard.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task extends BoardItem<ChecklistItem> {

    private String description;
    private LocalDate dueDate;
    private boolean isCompleted;

    public Task(String name) throws StringLengthException {
        super(name);

        this.description = "Provide a description";
        this.dueDate = LocalDate.now();
        this.isCompleted = false;
    }

    @Override
    public ChecklistItem addSubItem(String subItemName) {
        this.subItems.add(new ChecklistItem(subItemName));
        return this.subItems.get(this.subItems.size() - 1);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (!description.isBlank()) {
            this.description = description;
        }
    }

    public LocalDate getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(LocalDate newDate) {
        if (newDate.isAfter(LocalDate.now())) {
            this.dueDate = newDate;
        }
    }

    public void nullDueDate(){
        this.dueDate = null;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
