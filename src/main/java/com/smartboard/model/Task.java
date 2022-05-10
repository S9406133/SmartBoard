package com.smartboard.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task extends BoardItem<ChecklistItem> {

    private String description;
    private LocalDate dueDate;

    public Task(String name) throws StringLengthException {
        super(name);

        this.description = "Provide a description";
        this.dueDate = LocalDate.now();
    }

    @Override
    public void addSubItem(String subItemName) {
        this.subItems.add(new ChecklistItem(subItemName));
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

}
