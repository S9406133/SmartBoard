package com.smartboard.model;

public class Column extends BoardItem<Task> {

	public Column(String name) throws StringLengthException {
		super(name);
	}

	@Override
	protected void addSubItem(String subItemName) throws StringLengthException {
		this.subItems.add(new Task(subItemName));
	}
	
}
