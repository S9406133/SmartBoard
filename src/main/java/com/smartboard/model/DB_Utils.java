package com.smartboard.model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class DB_Utils {

    public static class DatabaseConnection {
        private static final String DB_URL = "jdbc:sqlite:smartboard.db";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(DB_URL);
        }
    }

    public static void CheckTableExist(String tableName) {
        //final String TABLE_NAME = "Student";

        try (Connection con = DatabaseConnection.getConnection()) {

            DatabaseMetaData dbm = con.getMetaData();

            ResultSet tables = dbm.getTables(null, null, tableName, null);

            if (tables != null) {
                if (tables.next()) {
                    System.out.println("Table " + tableName + " exists.");
                } else {
                    System.out.println("Table " + tableName + " does not exist.");
                }
                tables.close(); // use close method to close ResultSet object
            } else {
                System.out.println("Problem with retrieving database metadata");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static ArrayList<User> SelectAllUsers() {
        final String TABLE_NAME = "USER";
        ArrayList<User> users = new ArrayList<>();

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
                }
            }
        } catch (SQLException | StringLengthException e) {
            System.out.println(e.getMessage());
        }

        return users;
    }

    public static ArrayList<Project> SelectProjectsOfUser(String username) {
        final String TABLE_NAME = "PROJECT";
        ArrayList<Project> projectList = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            String query = "SELECT * FROM " + TABLE_NAME +
                    " WHERE Username LIKE '" + username + "'";

            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    System.out.printf("PROJECT -> Id: %d | Name: %s | Default: %s | UserName: %s\n",
                            resultSet.getInt("projectId"), resultSet.getString("name"),
                            resultSet.getBoolean("isDefault"), resultSet.getString("Username"));

                    projectList.add(new Project(resultSet.getString("name")));
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

    public static ArrayList<Column> SelectColumnsOfProject(int projectID) {
        final String TABLE_NAME = "COLUMN";
        ArrayList<Column> columnList = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            String query = "SELECT * FROM " + TABLE_NAME +
                    " WHERE ProjectID = " + projectID;

            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    System.out.printf("  COLUMN -> Col Id: %d | Name: %s | Proj Id: %d\n",
                            resultSet.getInt("ColumnID"), resultSet.getString("Name"),
                            resultSet.getInt("ProjectID"));

                    columnList.add(new Column(resultSet.getString("Name")));
                    Column currColumn = columnList.get(columnList.size() - 1);
                    currColumn.setColumnID(resultSet.getInt("ColumnID"));

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

    public static ArrayList<Task> SelectTasksOfColumn(int columnID) {
        final String TABLE_NAME = "TASK";
        ArrayList<Task> taskList = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            String query = "SELECT * FROM " + TABLE_NAME +
                    " WHERE ColumnID = " + columnID;

            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    System.out.printf("    TASK -> Task Id: %d | Name: %s | Desc: %s | DueDate: %s | IsComp: %s | Col Id: %d\n",
                            resultSet.getInt("TaskID"), resultSet.getString("Name"),
                            resultSet.getString("Description"), resultSet.getString("DueDate"),
                            resultSet.getBoolean("IsCompleted"), resultSet.getInt("ColumnID"));

                    taskList.add(new Task(resultSet.getString("Name")));
                    Task currTask = taskList.get(taskList.size() - 1);
                    currTask.setTaskID(resultSet.getInt("TaskID"));
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

    public static ArrayList<ChecklistItem> SelectCLItemsOfTask(int taskID) {
        final String TABLE_NAME = "CHECKLISTITEM";
        ArrayList<ChecklistItem> cliList = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            String query = "SELECT * FROM " + TABLE_NAME +
                    " WHERE TaskID = " + taskID;

            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    System.out.printf("      CLI -> Descr: %s | Checked: %s | TaskId: %d\n",
                            resultSet.getString("Description"), resultSet.getBoolean("Checked"),
                            resultSet.getInt("TaskID"));

                    cliList.add(new ChecklistItem(resultSet.getString("Description")));
                    cliList.get(cliList.size() - 1).setChecked(resultSet.getBoolean("Checked"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return cliList;
    }

    public static void SelectAllQuery(String tableName) {
        //final String TABLE_NAME = "STUDENT";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            String query = "SELECT * FROM " + tableName;

            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    System.out.printf("Id: %d | Student Number: %s | First Name: %s | Last Name: %s\n",
                            resultSet.getInt("id"), resultSet.getString("student_number"),
                            resultSet.getString("first_name"), resultSet.getString("last_name"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public static void InsertRow(String tableName) {
        //final String TABLE_NAME = "Student";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            String query = "INSERT INTO " + tableName +
                    " VALUES (1, 's3388490', 'Peter', 'Tilmanis')";

            int result = stmt.executeUpdate(query);

            if (result == 1) {
                System.out.println("Insert into table " + tableName + " executed successfully");
                System.out.println(result + " row(s) affected");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void DeleteRow(String tableName) {
        //final String TABLE_NAME = "Student";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            String sql = "DELETE FROM " + tableName +
                    " WHERE first_name LIKE 'Tom'";

            int result = stmt.executeUpdate(sql);

            if (result == 1) {
                System.out.println("Delete from table " + tableName + " executed successfully");
                System.out.println(result + " row(s) affected");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void UpdateTable(String tableName) {
        //final String TABLE_NAME = "Student";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            String sql = "UPDATE " + tableName +
                    " SET last_name = 'Singleton'" +
                    " WHERE student_number LIKE 's3388490'";

            int result = stmt.executeUpdate(sql);

            if (result == 1) {
                System.out.println("Update table " + tableName + " executed successfully");
                System.out.println(result + " row(s) affected");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void UsePreparedStatement(String tableName) {
        //final String TABLE_NAME = "Student";

        String sql = "INSERT INTO " + tableName + " (id, student_number, first_name, last_name)" +
                " VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, 2);
            stmt.setString(2, "s3089940");
            stmt.setString(3, "Tom");
            stmt.setString(4, "Bruster");

            int result = stmt.executeUpdate();

            if (result == 1) {
                System.out.println("Insert into table " + tableName + " executed successfully");
                System.out.println(result + " row(s) affected");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
