package com.smartboard.model;

public class Project extends BoardItem<Column> {

    private int projectID;
    private final String username;
    private boolean isDefault;

    public Project(String name, String username) throws StringLengthException {
        super(name);

        this.username = username;
        this.isDefault = false;
    }

    @Override
    public Column addSubItem(String subItemName) throws StringLengthException {
        this.subItems.add(new Column(subItemName, this.getProjectID()));
        return this.subItems.get(this.subItems.size() - 1);
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public int getProjectID() {
        return projectID;
    }

    public String getUsername() { return username; }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

}
