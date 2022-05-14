package com.smartboard.model;

import java.util.ArrayList;

public abstract class BoardItem<T> {

    protected String name;
    protected ArrayList<T> subItems;

    public BoardItem(String name) throws StringLengthException {
        setName(name);
        this.subItems = new ArrayList<>();
    }

    public abstract T addSubItem(String subItemName) throws StringLengthException;

    public boolean removeSubItem(BoardItem<?> item) {
        return this.subItems.remove(item);
    }

    public T getSubItem(int index) throws IndexOutOfBoundsException {
        return this.subItems.get(index);
    }

    public int getSubItemIndex(T item) {
        return this.subItems.indexOf(item);
    }

    public ArrayList<T> getSubItemList() {
        return this.subItems;
    }

    public int getListSize() {
        return this.subItems.size();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) throws StringLengthException {
        final int NAME_LENGTH_MIN = 2;

        if (name.length() > NAME_LENGTH_MIN) {
            this.name = name;
        } else {
            throw new StringLengthException("Invalid name length - Name not changed");
        }
    }

}
