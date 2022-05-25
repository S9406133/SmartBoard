package com.smartboard.model;

public class Column extends BoardItem<Task> implements Reorderable {

    private int columnID;
    private int orderIndex;

    public Column(String name) throws StringLengthException {
        super(name);
    }

    @Override
    public Task addSubItem(String subItemName) throws StringLengthException {
        this.subItems.add(new Task(subItemName));
        return this.subItems.get(this.subItems.size() - 1);
    }

    @Override
    public void setName(String name) throws StringLengthException {
        super.setName(name);

        DB_Utils.UpdateColumn(this);
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
}
