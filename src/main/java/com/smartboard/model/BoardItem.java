package com.smartboard.model;

import java.util.ArrayList;
import java.util.List;

public abstract class BoardItem<T> {

    protected String name;
    protected ArrayList<T> subItems;

    public BoardItem(String name) throws StringLengthException {

        if (name.length() > 2) {
            this.name = name;
        } else {
            throw new StringLengthException("Invalid Name Length - Item not created");
        }

        this.subItems = new ArrayList<>();
    }

    public abstract T addSubItem(String subItemName) throws StringLengthException;

    public boolean removeSubItem(T item) { return this.subItems.remove(item); }

    public T getSubItem(int index) throws IndexOutOfBoundsException { return this.subItems.get(index); }

    public int getSubItemIndexByObject(T item) {
        int index = -1;
        int i = 0;

        for (T subItem : this.subItems) {
            if (subItem == item) {
                index = i;
                break;
            }
            i++;
        }

        return index;
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

}
