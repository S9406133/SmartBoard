package com.smartboard.model;

public class Column extends BoardItem<Task> {

    private int columnID;

    public Column(String name) throws StringLengthException {
        super(name);
    }

    @Override
    public Task addSubItem(String subItemName) throws StringLengthException {
        this.subItems.add(new Task(subItemName));
        return this.subItems.get(this.subItems.size() - 1);
    }

    public void setColumnID(int columnID) { this.columnID = columnID; }

    public int getColumnID() { return this.columnID; }
}
