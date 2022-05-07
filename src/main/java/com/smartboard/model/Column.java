package com.smartboard.model;

public class Column extends BoardItem<Task> {

    public Column(String name) throws StringLengthException {
        super(name);

        addSubItem("Test task 1");
        addSubItem("Test task 2");
        addSubItem("Test task 3");
    }

    @Override
    public void addSubItem(String subItemName) throws StringLengthException {
        this.subItems.add(new Task(subItemName));
    }

}
