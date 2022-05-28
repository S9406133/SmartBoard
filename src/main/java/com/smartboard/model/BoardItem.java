/**
 * This is the super class for Model classes which define
 * the user and the items held by the user and shown on the Smart Board
 */

package com.smartboard.model;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public abstract class BoardItem<T> {

    protected String name;
    protected ArrayList<T> subItems;
    final int MIN_NAME_LENGTH = 2;
    final int MAX_NAME_LENGTH = 30;

    public BoardItem(@NotNull String name) throws StringLengthException {

        if ((name.length() >= MIN_NAME_LENGTH) && (name.length() <= MAX_NAME_LENGTH)) {
            this.name = name;
        } else {
            throw new StringLengthException("Invalid name length - Name not changed");
        }

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

    public void setName(@NotNull String name) throws StringLengthException {

        if ((name.length() >= MIN_NAME_LENGTH) && (name.length() <= MAX_NAME_LENGTH)) {
            this.name = name;
        } else {
            throw new StringLengthException("Invalid name length - Name not changed");
        }
    }

}
