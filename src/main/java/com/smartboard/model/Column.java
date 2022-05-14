package com.smartboard.model;

public class Column extends BoardItem<Task> {

    public Column(String name) throws StringLengthException {
        super(name);
    }

    @Override
    public Task addSubItem(String subItemName) throws StringLengthException {
        this.subItems.add(new Task(subItemName));
        return this.subItems.get(this.subItems.size() - 1);
    }

}
