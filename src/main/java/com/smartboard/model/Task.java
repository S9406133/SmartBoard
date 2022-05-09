package com.smartboard.model;

import java.time.LocalDateTime;

public class Task extends BoardItem<ChecklistItem> {

    private String description;
    private LocalDateTime dueDate;

    public Task(String name) throws StringLengthException {
        super(name);

        this.description = "Provide a description";
        this.dueDate = LocalDateTime.now();
    }

    @Override
    public void addSubItem(String subItemName) {
        this.subItems.add(new ChecklistItem(subItemName));
    }

    protected String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        if (!description.isBlank()) {
            this.description = description;
        }
    }

    public LocalDateTime getDueDate() {
        return this.dueDate;
    }

    protected void setDueDate(LocalDateTime newDate) {
        if (newDate.isAfter(LocalDateTime.now())) {
            this.dueDate = newDate;
        }
    }

}
