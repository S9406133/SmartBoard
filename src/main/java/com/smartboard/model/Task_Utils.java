package com.smartboard.model;

import java.util.ArrayList;

public class Task_Utils {

    public static Task currentTask = null;

    public static void addNewTask(Task task){
        DB_Utils.InsertNewTask(task);
        for (ChecklistItem item : task.getSubItemList()){
            item.setTaskID(task.getTaskID());
        }
        refreshTaskCLItems(task.getTaskID(), task.getSubItemList());
    }

    public static void updateTaskIndexes(Column column) {
        int i = 0;
        for (Task task : column.getSubItemList()) {
            System.out.println("loop "+task.getTaskID()+task.getName());
            task.setOrderIndex(i);
            DB_Utils.UpdateTask(task);
            i++;
        }
    }

    public static void updateTaskColumn(Project project, Column newColumn, Task task) {
        System.out.println("New col: "+newColumn.getColumnID() + " " + newColumn.getName());
        task.setColumnID(newColumn.getColumnID());

        for (Column column : project.getSubItemList()){
            updateTaskIndexes(column);
        }
    }

    public static void deleteTask(Column column, String deleteClass, int itemID) {
        DB_Utils.DeleteItem(deleteClass, deleteClass + "ID", itemID);
        updateTaskIndexes(column);
    }

    public static void refreshTaskCLItems(int taskID, ArrayList<ChecklistItem> newList) {
        DB_Utils.DeleteAllTaskCLItems(taskID);
        for (ChecklistItem item : newList) {
            DB_Utils.InsertNewCLItem(taskID, item);
        }
    }

    public static void deleteTaskCLItems(Task task){
        currentTask.getSubItemList().clear();
        DB_Utils.DeleteAllTaskCLItems(task.getTaskID());
    }

}
