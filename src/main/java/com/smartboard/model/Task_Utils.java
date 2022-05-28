/**
 * This class holds the static utility methods used to
 * update a Task instance and related Database record
 */

package com.smartboard.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Task_Utils {

    public static Task currentTask = null;

    /**
     * Adds a Task to the Database assigns the taskID to each of
     * its ChecklistItems, then refreshes their db records
     */
    public static void addNewTask(Task task) {
        DB_Utils.InsertNewTask(task);
        for (ChecklistItem item : task.getSubItemList()) {
            item.setTaskID(task.getTaskID());
        }
        refreshTaskCLItems(task.getTaskID(), task.getSubItemList());
    }

    /**
     * Updates the OrderIndex of all Task instances of a Column and their database records
     */
    public static void updateTaskIndexes(@NotNull Column column) {
        int i = 0;
        for (Task task : column.getSubItemList()) {
            task.setOrderIndex(i);
            DB_Utils.UpdateTask(task);
            i++;
        }
    }

    /**
     * Updates the columnID of a Task and the database record
     */
    public static void updateTaskColumn(@NotNull Project project, @NotNull Column newColumn, @NotNull Task task) {
        task.setColumnID(newColumn.getColumnID());

        for (Column column : project.getSubItemList()) {
            updateTaskIndexes(column);
        }
    }

    /**
     * Deletes the database record of a Task and updates the
     * OrderIndexes of all Tasks of the related Column.
     */
    public static void deleteTask(Column column, String deleteClass, int itemID) {
        DB_Utils.DeleteItem(deleteClass, deleteClass + "ID", itemID);
        updateTaskIndexes(column);
    }

    /**
     * Deletes all ChecklistItems of a Task from the db then adds a list of new ones
     */
    public static void refreshTaskCLItems(int taskID, @NotNull ArrayList<ChecklistItem> newList) {
        DB_Utils.DeleteAllTaskCLItems(taskID);
        for (ChecklistItem item : newList) {
            DB_Utils.InsertNewCLItem(taskID, item);
        }
    }

    /**
     * Deletes all ChecklistItems from a Task and their database records
     */
    public static void deleteTaskCLItems(@NotNull Task task) {
        currentTask.getSubItemList().clear();
        DB_Utils.DeleteAllTaskCLItems(task.getTaskID());
    }

}
