/**
 * This class holds the static methods to interact with the related database
 */

package com.smartboard.model;

import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class DB_Utils {

    /**
     * An inner class which is used to return a connection to the database
     */
    public static class DatabaseConnection {
        private static final String DB_URL = "jdbc:sqlite:smartboard.db";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(DB_URL);
        }
    }

    /**
     * Used to determine if the specified table exists in the database
     *
     * @return True if it doesn't exist, False if it does exist
     */
    public static boolean TableNotExists(String tableName) {
        boolean returnVal = false;

        try (Connection con = DatabaseConnection.getConnection()) {
            DatabaseMetaData dbm = con.getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);

            if (tables != null) {
                returnVal = (tables.next());
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return !returnVal;
    }

    /**
     * Selects all records in the USER db and returns them as a list of User class
     */
    public static ArrayList<User> SelectAllUsers() {
        final String TABLE_NAME = "USER";
        ArrayList<User> users = new ArrayList<>();

        if (TableNotExists(TABLE_NAME)) {
            System.out.println("Table '" + TABLE_NAME + "' does not exist.");
            return users;
        }

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            String query = "SELECT * FROM " + TABLE_NAME;

            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    users.add(new User(
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("firstname"),
                            resultSet.getString("lastname"))
                    );
                    users.get(users.size() - 1).setImagePath(resultSet.getString("Imagepath"));
                }
            }
        } catch (SQLException | StringLengthException e) {
            System.out.println(e.getMessage());
        }

        return users;
    }

    /**
     * Selects all records from the PROJECT db with a foreign key of the passed
     * in username and returns them as a list of Project class
     */
    public static @NotNull ArrayList<Project> SelectProjectsOfUser(String username) {
        final String TABLE_NAME = "PROJECT";
        ArrayList<Project> projectList = new ArrayList<>();

        if (TableNotExists(TABLE_NAME)) {
            System.out.println("Table '" + TABLE_NAME + "' does not exist.");
            return projectList;
        }

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            String query = "SELECT * FROM " + TABLE_NAME +
                    " WHERE Username LIKE '" + username + "'";

            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    projectList.add(new Project(resultSet.getString("name"), resultSet.getString("Username")));
                    Project currProject = projectList.get(projectList.size() - 1);
                    currProject.setDefault(resultSet.getBoolean("isDefault"));
                    currProject.setProjectID(resultSet.getInt("projectId"));

                    for (Column column : SelectColumnsOfProject(currProject.getProjectID())) {
                        currProject.getSubItemList().add(column);
                    }
                }
            }
        } catch (SQLException | StringLengthException e) {
            System.out.println(e.getMessage());
        }

        return projectList;
    }

    /**
     * Selects all records from the COLUMN db with a foreign key of the passed
     * in projectID and returns them as a list of Column class
     */
    public static @NotNull ArrayList<Column> SelectColumnsOfProject(int projectID) {
        final String TABLE_NAME = "COLUMN";
        ArrayList<Column> columnList = new ArrayList<>();

        if (TableNotExists(TABLE_NAME)) {
            System.out.println("Table '" + TABLE_NAME + "' does not exist.");
            return columnList;
        }

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            String query = "SELECT * FROM " + TABLE_NAME +
                    " WHERE ProjectID = " + projectID;

            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {

                    // Orders the records in the list by their orderIndex value
                    int currIndex;
                    if (columnList.size() <= resultSet.getInt("OrderIndex")) {
                        columnList.add(new Column(resultSet.getString("Name"), resultSet.getInt("ProjectID")));
                        currIndex = columnList.size() - 1;
                    } else {
                        currIndex = resultSet.getInt("OrderIndex");
                        columnList.add(currIndex,
                                new Column(resultSet.getString("Name"), resultSet.getInt("ProjectID")));
                    }

                    Column currColumn = columnList.get(currIndex);
                    currColumn.setColumnID(resultSet.getInt("ColumnID"));
                    currColumn.setOrderIndex(resultSet.getInt("OrderIndex"));

                    for (Task task : SelectTasksOfColumn(currColumn.getColumnID())) {
                        currColumn.getSubItemList().add(task);
                    }
                }
            }
        } catch (SQLException | StringLengthException e) {
            System.out.println(e.getMessage());
        }

        return columnList;
    }

    /**
     * Selects all records from the TASK db with a foreign key of the passed
     * in columnID and returns them as a list of Task class
     */
    public static @NotNull ArrayList<Task> SelectTasksOfColumn(int columnID) {
        final String TABLE_NAME = "TASK";
        ArrayList<Task> taskList = new ArrayList<>();

        if (TableNotExists(TABLE_NAME)) {
            System.out.println("Table '" + TABLE_NAME + "' does not exist.");
            return taskList;
        }

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            String query = "SELECT * FROM " + TABLE_NAME +
                    " WHERE ColumnID = " + columnID;

            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {

                    // Orders the records in the list by their orderIndex value
                    int currIndex;
                    if (taskList.size() <= resultSet.getInt("OrderIndex")) {
                        taskList.add(
                                new Task(resultSet.getString("Name"), resultSet.getInt("ColumnID")));
                        currIndex = taskList.size() - 1;
                    } else {
                        currIndex = resultSet.getInt("OrderIndex");
                        taskList.add(currIndex,
                                new Task(resultSet.getString("Name"), resultSet.getInt("ColumnID")));
                    }

                    Task currTask = taskList.get(currIndex);
                    currTask.setTaskID(resultSet.getInt("TaskID"));
                    currTask.setOrderIndex(resultSet.getInt("OrderIndex"));
                    if (resultSet.getString("Description") != null) {
                        currTask.setDescription(resultSet.getString("Description"));
                    }
                    if (resultSet.getString("DueDate") != null) {
                        currTask.setDueDate(LocalDate.parse(resultSet.getString("DueDate")));
                    }
                    currTask.setCompleted(resultSet.getBoolean("IsCompleted"));

                    for (ChecklistItem clItem : SelectCLItemsOfTask(currTask.getTaskID())) {
                        currTask.getSubItemList().add(clItem);
                    }
                }
            }
        } catch (SQLException | StringLengthException e) {
            System.out.println(e.getMessage());
        }

        return taskList;
    }

    /**
     * Selects all records from the CHECKLISTITEM db with a foreign key of the passed
     * in taskID and returns them as a list of ChecklistItem class
     */
    public static @NotNull ArrayList<ChecklistItem> SelectCLItemsOfTask(int taskID) {
        final String TABLE_NAME = "CHECKLISTITEM";
        ArrayList<ChecklistItem> cliList = new ArrayList<>();

        if (TableNotExists(TABLE_NAME)) {
            System.out.println("Table '" + TABLE_NAME + "' does not exist.");
            return cliList;
        }

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            String query = "SELECT * FROM " + TABLE_NAME +
                    " WHERE TaskID = " + taskID;

            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    cliList.add(new ChecklistItem(resultSet.getString("Description")));
                    ChecklistItem currCli = cliList.get(cliList.size() - 1);
                    currCli.setChecked(resultSet.getBoolean("Checked"));
                    currCli.setItemID(resultSet.getInt("ItemID"));
                    currCli.setTaskID(resultSet.getInt("TaskID"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return cliList;
    }

    /**
     * Inserts a new record of a User into the USER table
     */
    public static void InsertNewUser(@NotNull User user) {
        final String TABLE_NAME = "USER";

        String sql = "INSERT INTO " + TABLE_NAME +
                " VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getImagePath());

            int result = stmt.executeUpdate();

            if (result < 1) {
                System.out.println("Error saving new user");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Updates the attributes of a record in the USER table
     */
    public static void UpdateUser(@NotNull User user) {
        final String TABLE_NAME = "USER";

        String sql = "UPDATE " + TABLE_NAME +
                " SET Firstname = ?, Lastname = ?, Imagepath = ?" +
                " WHERE Username LIKE ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getImagePath());
            stmt.setString(4, user.getName());

            int result = stmt.executeUpdate();

            if (result < 1) {
                System.out.println("Error saving user data");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Inserts a new record of a Project into the PROJECT table
     */
    public static void InsertNewProject(@NotNull Project project) {
        final String TABLE_NAME = "PROJECT";

        String sql = "INSERT INTO " + TABLE_NAME + "(Name, IsDefault, Username)" +
                " VALUES (?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, project.getName());
            stmt.setBoolean(2, project.isDefault());
            stmt.setString(3, project.getUsername());

            int result = stmt.executeUpdate();

            if (result < 1) {
                System.out.println("Error saving new project");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        project.setProjectID(SelectOwnID(TABLE_NAME, "ProjectID"));
    }

    /**
     * Updates the attributes of a record in the PROJECT table
     */
    public static void UpdateProject(@NotNull Project project) {
        final String TABLE_NAME = "PROJECT";

        String sql = "UPDATE " + TABLE_NAME +
                " SET Name = ?, IsDefault = ?" +
                " WHERE ProjectID = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, project.getName());
            stmt.setBoolean(2, project.isDefault());
            stmt.setInt(3, project.getProjectID());

            int result = stmt.executeUpdate();

            if (result < 1) {
                System.out.println("Error saving project data");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Inserts a new record of a Column into the COLUMN table
     */
    public static void InsertNewColumn(@NotNull Column column) {
        final String TABLE_NAME = "COLUMN";

        String sql = "INSERT INTO " + TABLE_NAME + "(Name, OrderIndex, ProjectID)" +
                " VALUES (?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, column.getName());
            stmt.setInt(2, column.getOrderIndex());
            stmt.setInt(3, column.getProjectID());

            int result = stmt.executeUpdate();

            if (result < 1) {
                System.out.println("Error saving new column");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        column.setColumnID(SelectOwnID(TABLE_NAME, "ColumnID"));
    }

    /**
     * Updates the attributes of a record in the COLUMN table
     */
    public static void UpdateColumn(@NotNull Column column) {
        final String TABLE_NAME = "COLUMN";

        String sql = "UPDATE " + TABLE_NAME +
                " SET Name = ?, OrderIndex = ?" +
                " WHERE ColumnID = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, column.getName());
            stmt.setInt(2, column.getOrderIndex());
            stmt.setInt(3, column.getColumnID());

            int result = stmt.executeUpdate();

            if (result < 1) {
                System.out.println("Error saving column data");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Inserts a new record of a Task into the TASK table
     */
    public static void InsertNewTask(@NotNull Task task) {
        final String TABLE_NAME = "TASK";

        String sql = "INSERT INTO " + TABLE_NAME + "(Name, Description, Duedate, IsCompleted, OrderIndex, ColumnID)" +
                " VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, task.getName());
            stmt.setString(2, task.getDescription());
            if (task.getDueDate() != null) {
                stmt.setString(3, task.getDueDate().toString());
            } else {
                stmt.setString(3, null);
            }
            stmt.setBoolean(4, task.isCompleted());
            stmt.setInt(5, task.getOrderIndex());
            stmt.setInt(6, task.getColumnID());

            int result = stmt.executeUpdate();

            if (result < 1) {
                System.out.println("Error saving new task");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        task.setTaskID(SelectOwnID(TABLE_NAME, "TaskID"));
    }

    /**
     * Updates the attributes of a record in the TASK table
     */
    public static void UpdateTask(@NotNull Task task) {
        final String TABLE_NAME = "TASK";

        String sql = "UPDATE " + TABLE_NAME +
                " SET Name = ?, Description = ?, Duedate = ?, IsCompleted = ?, OrderIndex = ?, ColumnID = ?" +
                " WHERE TaskID = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, task.getName());
            stmt.setString(2, task.getDescription());
            if (task.getDueDate() != null) {
                stmt.setString(3, task.getDueDate().toString());
            } else {
                stmt.setString(3, null);
            }
            stmt.setBoolean(4, task.isCompleted());
            stmt.setInt(5, task.getOrderIndex());
            stmt.setInt(6, task.getColumnID());
            stmt.setInt(7, task.getTaskID());

            int result = stmt.executeUpdate();

            if (result < 1) {
                System.out.println("Error saving task data");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Inserts a new record of a ChecklistItem into the CHECKLISTITEM table
     */
    public static void InsertNewCLItem(int taskID, @NotNull ChecklistItem cli) {
        final String TABLE_NAME = "CHECKLISTITEM";

        String sql = "INSERT INTO " + TABLE_NAME + "(Description, Checked, TaskID)" +
                " VALUES (?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, cli.getDescription());
            stmt.setBoolean(2, cli.isChecked());
            stmt.setInt(3, taskID);

            int result = stmt.executeUpdate();

            if (result < 1) {
                System.out.println("Error saving new checklist item");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        cli.setItemID(SelectOwnID(TABLE_NAME, "ItemID"));
    }

    /**
     * Deletes all records from the CHECKLISTITEM table with a foreign key of the passed in taskID
     */
    public static void DeleteAllTaskCLItems(int taskID) {
        final String TABLE_NAME = "CHECKLISTITEM";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            String sql = "DELETE FROM " + TABLE_NAME +
                    " WHERE TaskID = " + taskID;

            int result = stmt.executeUpdate(sql);

            if (result >= 1) {
                System.out.println("Delete from table " + TABLE_NAME + " executed successfully");
                System.out.println(result + " row(s) affected");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Selects and returns the ID (Primary Key) of the last record added to the specified table
     */
    public static int SelectOwnID(String tableName, String fieldname) {
        int ownID = 0;

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            String query = "SELECT MAX(" + fieldname + ") ownID" +
                    " FROM " + tableName;

            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    ownID = resultSet.getInt("ownID");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return ownID;
    }

    /**
     * Deletes the records from the specified typeName table according to the fieldName and fieldID specified.
     * ie. DELETE FROM typeName WHERE fieldName = itemID.
     * This method is called recursively to delete all sub-items of the initial specified records.
     *
     * @param typeName  The name of the type of Boarditem to be deleted (also the table name)
     * @param fieldName The name of the field that the itemID relates to (either Primary or Foreign key field)
     * @param itemID    The ID number to delete by as in the fieldName
     */
    public static void DeleteItem(@NotNull String typeName, String fieldName, int itemID) {

        switch (typeName) {
            case "Project" -> {
                for (Integer fieldID : SelectAllSubitemIDs("Column", "ProjectID", itemID)) {
                    DeleteItem("Column", "ColumnID", fieldID);
                }
            }
            case "Column" -> {
                for (Integer fieldID : SelectAllSubitemIDs("Task", "ColumnID", itemID)) {
                    DeleteItem("Task", "TaskID", fieldID);
                }
            }
            case "Task" -> {
                for (Integer fieldID : SelectAllSubitemIDs("Checklistitem", "TaskID", itemID)) {
                    DeleteItem("Checklistitem", "ItemID", fieldID);
                }
            }
        }

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            String sql = "DELETE FROM " + typeName +
                    " WHERE " + fieldName + " = " + itemID;

            int result = stmt.executeUpdate(sql);

            if (result == 1) {
                System.out.println("Delete from table " + typeName + " executed successfully");
                System.out.println(result + " row(s) affected");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Selects all the ID (Primary Key) numbers of the records form the parameters specified and returns them as a list
     */
    public static @NotNull ArrayList<Integer> SelectAllSubitemIDs(@NotNull String tableName, String fieldName, int fieldID) {
        String selectID = (tableName.equalsIgnoreCase("Checklistitem")) ? "ItemID" : tableName + "ID";
        ArrayList<Integer> itemList = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {

            String query = "SELECT " + selectID + " FROM " + tableName +
                    " WHERE " + fieldName + " = " + fieldID;

            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    System.out.println(resultSet.getInt(selectID));
                    itemList.add(resultSet.getInt(selectID));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return itemList;
    }

}
