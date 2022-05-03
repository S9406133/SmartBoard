package com.smartboard.model;

import java.util.ArrayList;
import java.util.List;

public abstract class BoardItem<T> {

	protected String name;
	protected List<T> subItems;

	public BoardItem(String name) throws StringLengthException {

		if (name.length() > 2) {
			this.name = name;
		} else {
			throw new StringLengthException("Invalid Name Length - Item not created");
		}

		this.subItems = new ArrayList<>();
	}

	public abstract void addSubItem(String subItemName) throws StringLengthException;

	public boolean removeSubItem(BoardItem item) {
		return this.subItems.remove(item);
	}

	public T getSubItem(int index) throws IndexOutOfBoundsException {
		return this.subItems.get(index);
	}

	public List<T> getSubItemList() {
		return this.subItems;
	}

	public int getListSize() {
		return this.subItems.size();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) throws StringLengthException {
		if (name.length() > 2) {
			this.name = name;
		} else {
			throw new StringLengthException("Invalid name length - Name not changed");
		}
	}

	@Override
	public String toString() {
		StringBuilder returnVal = new StringBuilder(String.format("%s: %s\n{", this.getClass().getSimpleName(), this.name));

		for (T value : this.subItems) {
			returnVal.append("    ").append(value).append("\n");
		}

		returnVal.append("}");

		return returnVal.toString();
	}

}
