package com.smartboard.model;

public class Column extends BoardItem<Task> implements Reorderable {

    private int columnID;
    private int orderIndex;
    private final int projectID;

    public Column(String name, int projectID) throws StringLengthException {
        super(name);
        this.projectID = projectID;
    }

    @Override
    public Task addSubItem(String subItemName) throws StringLengthException {
        this.subItems.add(new Task(subItemName, this.getColumnID()));
        return this.subItems.get(this.subItems.size() - 1);
    }

    public void setColumnID(int columnID) {
        this.columnID = columnID;
    }

    public int getColumnID() {
        return this.columnID;
    }

    public int getProjectID() {
        return projectID;
    }

    @Override
    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Override
    public int getOrderIndex() {
        return this.orderIndex;
    }
}
