package com.smartboard.model;

public class Project extends BoardItem<Column> {

    private int projectID;
    private boolean isDefault;

    public Project(String name) throws StringLengthException {
        super(name);

        this.isDefault = false;
    }

    @Override
    public Column addSubItem(String subItemName) throws StringLengthException {
        this.subItems.add(new Column(subItemName));
        Column newColumn = this.subItems.get(this.subItems.size() - 1);
        DB_Utils.InsertNewColumn(this, newColumn);
        return newColumn;
    }

    @Override
    public void setName(String name) throws StringLengthException {
        super.setName(name);

        DB_Utils.UpdateProject(this);
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public int getProjectID() {
        return projectID;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;

        DB_Utils.UpdateProject(this);
    }

}
