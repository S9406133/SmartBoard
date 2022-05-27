package com.smartboard.model;

public class Column_Utils {

    public static Column currentColumn = null;

    public static void addNewColumn(Project project, String name) throws StringLengthException {
        Column newColumn = project.addSubItem(name);
        newColumn.setOrderIndex(project.getSubItemIndex(newColumn));
        DB_Utils.InsertNewColumn(newColumn);
    }

    public static void updateColumnName(Column column, String name) throws StringLengthException {
        column.setName(name);
        DB_Utils.UpdateColumn(column);
    }

    public static void updateColumnIndexes(Project project) {
        int i = 0;
        for (Column column : project.getSubItemList()) {
            column.setOrderIndex(i);
            DB_Utils.UpdateColumn(column);
            i++;
        }
    }

    public static void deleteColumn(Project project, String deleteClass, int itemID) {
        DB_Utils.DeleteItem(deleteClass, deleteClass + "ID", itemID);
        updateColumnIndexes(project);
    }

}
