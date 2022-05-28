/**
 * Interface to hold the controller utility methods
 */

package com.smartboard.controller;

import com.smartboard.model.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public interface Controller_Utils {

    /**
     * Moves a task to a new column by adding it to the new column and removing it from the old
     */
    static void moveTaskToNewColumn(Project project, @NotNull Column newColumn, @NotNull Column currentColumn, Task task) {
        newColumn.getSubItemList().add(task);
        currentColumn.removeSubItem(task);

        Task_Utils.updateTaskColumn(project, newColumn, task);
    }

    /**
     * Moves a column with the project's list
     */
    static void moveColumn(@NotNull Project project, Column column, String direction) {
        int colIndex = project.getSubItemIndex(column);

        moveItem(project.getSubItemList(), colIndex, direction);
        Column_Utils.updateColumnIndexes(project);
    }

    /**
     * Moves a task within a columns list
     */
    static void moveTask(Task task, String direction) {
        int taskIndex = Column_Utils.currentColumn.getSubItemIndex(task);

        moveItem(Column_Utils.currentColumn.getSubItemList(), taskIndex, direction);
        Task_Utils.updateTaskIndexes(Column_Utils.currentColumn);
    }

    /**
     * Method to move a generic BoardItem within the list
     */
    static void moveItem(@NotNull ArrayList<?> itemList, int itemIndex, @NotNull String direction) {
        int minIndex = 0;
        int maxIndex = itemList.size() - 1;

        switch (direction.toLowerCase()) {
            case "left", "up" -> {
                if (itemIndex > minIndex) {
                    Collections.swap(itemList, itemIndex, itemIndex - 1);
                }
            }
            case "right", "down" -> {
                if (itemIndex < maxIndex) {
                    Collections.swap(itemList, itemIndex, itemIndex + 1);
                }
            }
        }
    }

    /**
     * Returns the colour associated with the task status
     */
    static String getStatusColor(@NotNull Task task) {
        final String NOT_SET = "azure";
        final String APPROACHING = "khaki";
        final String OVERDUE = "red";
        final String COMPLETED = "lightgreen";
        final String COMPLETED_LATE = "lightpink";
        String retColor = NOT_SET;

        switch (task.getStatus()) {
            case DATE_NOT_SET -> retColor = NOT_SET;
            case APPROACHING -> retColor = APPROACHING;
            case COMPLETED_ON_TIME -> retColor = COMPLETED;
            case OVERDUE -> retColor = OVERDUE;
            case COMPLETED_LATE -> retColor = COMPLETED_LATE;
        }

        return retColor;
    }

}
