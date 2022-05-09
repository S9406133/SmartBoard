package com.smartboard.model;

public class ChecklistItem {

    private String description;
    private boolean checked;

    public ChecklistItem(String description) {
        this.description = description;
        this.checked = false;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
