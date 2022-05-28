/**
 * This class holds the static utility methods used to
 * update a Column instance and related Database record
 */

package com.smartboard.model;

import org.jetbrains.annotations.NotNull;

public class Column_Utils {

    public static Column currentColumn = null;

    /**
     * Creates and adds a new Column to a Project and creates a database record of the Column
     *
     * @throws StringLengthException If name is less an 2 or greater than 30 characters
     */
    public static void addNewColumn(@NotNull Project project, String name) throws StringLengthException {
        Column newColumn = project.addSubItem(name);
        newColumn.setOrderIndex(project.getSubItemIndex(newColumn));
        DB_Utils.InsertNewColumn(newColumn);
    }

    /**
     * Updates the name of the Column instance and the database record
     *
     * @throws StringLengthException If name is less an 2 or greater than 30 characters
     */
    public static void updateColumnName(@NotNull Column column, String name) throws StringLengthException {
        column.setName(name);
        DB_Utils.UpdateColumn(column);
    }

    /**
     * Updates the OrderIndex of all Column instances of a project and their database records
     */
    public static void updateColumnIndexes(@NotNull Project project) {
        int i = 0;
        for (Column column : project.getSubItemList()) {
            column.setOrderIndex(i);
            DB_Utils.UpdateColumn(column);
            i++;
        }
    }

    /**
     * Deletes the database record of a Column and updates the
     * OrderIndexes of all Columns of the related Project.
     */
    public static void deleteColumn(Project project, int itemID) {
        String deleteClass = "Column";
        DB_Utils.DeleteItem(deleteClass, deleteClass + "ID", itemID);
        updateColumnIndexes(project);
    }

}
