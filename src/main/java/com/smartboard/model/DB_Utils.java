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

    public static ArrayList<Project> SelectProjectsOfUser(String username) {
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
                    System.out.printf("PROJECT -> Id: %d | Name: %s | Default: %s | UserName: %s\n",
                            resultSet.getInt("projectId"), resultSet.getString("name"),
                            resultSet.getBoolean("isDefault"), resultSet.getString("Username"));

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

    public static ArrayList<Column> SelectColumnsOfProject(int projectID) {
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
                    System.out.printf("  COLUMN -> Col Id: %d | Name: %s | Proj Id: %d\n",
                            resultSet.getInt("ColumnID"), resultSet.getString("Name"),
                            resultSet.getInt("ProjectID"));

                    columnList.add(new Column(resultSet.getString("Name"), resultSet.getInt("ProjectID")));
                    Column currColumn = columnList.get(columnList.size() - 1);
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

    public static ArrayList<Task> SelectTasksOfColumn(int columnID) {
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
                    System.out.printf("    TASK -> Task Id: %d | Name: %s | Desc: %s | DueDate: %s | IsComp: %s | Col Id: %d\n",
                            resultSet.getInt("TaskID"), resultSet.getString("Name"),
                            resultSet.getString("Description"), resultSet.getString("DueDate"),
                            resultSet.getBoolean("IsCompleted"), resultSet.getInt("ColumnID"));

                    taskList.add(new Task(resultSet.getString("Name"), resultSet.getInt("ColumnID")));
                    Task currTask = taskList.get(taskList.size() - 1);
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

    public static ArrayList<ChecklistItem> SelectCLItemsOfTask(int taskID) {
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
                    System.out.printf("      CLI -> Descr: %s | Checked: %s | TaskId: %d\n",
                            resultSet.getString("Description"), resultSet.getBoolean("Checked"),
                            resultSet.getInt("TaskID"));

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

    public static void InsertNewUser(User user) {
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

    public static void UpdateUser(User user) {
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

    public static void InsertNewProject(Project project) {
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

    public static void UpdateProject(Project project) {
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

    public static void InsertNewColumn(Column column) {
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

    public static void UpdateColumn(Column column) {
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

    public static void InsertNewTask(Task task) {
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

    public static void UpdateTask(Task task) {
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

    public static void InsertNewCLItem(int taskID, ChecklistItem cli) {
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

    public static int SelectOwnID(String tableName, String fieldname) {
        int ownID = 0;

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            String query = "SELECT MAX(" + fieldname + ") ownID" +
                    " FROM " + tableName;

            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    System.out.printf("Own Id: %d \n", resultSet.getInt("ownID"));
                    ownID = resultSet.getInt("ownID");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return ownID;
    }

    public static void DeleteItem(String itemType, int itemID) {
        final String TABLE_NAME = itemType;
        final String FIELD_NAME = itemType + "ID";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            String sql = "DELETE FROM " + TABLE_NAME +
                    " WHERE " + FIELD_NAME + " = " + itemID;
            System.out.println(sql);

            int result = stmt.executeUpdate(sql);

            if (result == 1) {
                System.out.println("Delete from table " + TABLE_NAME + " executed successfully");
                System.out.println(result + " row(s) affected");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

//    public static void SelectAllQuery(String tableName) {
//        //final String TABLE_NAME = "STUDENT";
//
//        try (Connection con = DatabaseConnection.getConnection();
//             Statement stmt = con.createStatement()) {
//            String query = "SELECT * FROM " + tableName;
//
//            try (ResultSet resultSet = stmt.executeQuery(query)) {
//                while (resultSet.next()) {
//                    System.out.printf("Id: %d | Student Number: %s | First Name: %s | Last Name: %s\n",
//                            resultSet.getInt("id"), resultSet.getString("student_number"),
//                            resultSet.getString("first_name"), resultSet.getString("last_name"));
//                }
//            }
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//
//    }

//    public static void InsertRow(String tableName) {
//        //final String TABLE_NAME = "Student";
//
//        try (Connection con = DatabaseConnection.getConnection();
//             Statement stmt = con.createStatement()) {
//            String query = "INSERT INTO " + tableName +
//                    " VALUES (1, 's3388490', 'Peter', 'Tilmanis')";
//
//            int result = stmt.executeUpdate(query);
//
//            if (result == 1) {
//                System.out.println("Insert into table " + tableName + " executed successfully");
//                System.out.println(result + " row(s) affected");
//            }
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    public static void DeleteRow(String tableName) {
//        //final String TABLE_NAME = "Student";
//
//        try (Connection con = DatabaseConnection.getConnection();
//             Statement stmt = con.createStatement()) {
//            String sql = "DELETE FROM " + tableName +
//                    " WHERE first_name LIKE 'Tom'";
//
//            int result = stmt.executeUpdate(sql);
//
//            if (result == 1) {
//                System.out.println("Delete from table " + tableName + " executed successfully");
//                System.out.println(result + " row(s) affected");
//            }
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    public static void UpdateTable(String tableName) {
//        //final String TABLE_NAME = "Student";
//
//        try (Connection con = DatabaseConnection.getConnection();
//             Statement stmt = con.createStatement()) {
//            String sql = "UPDATE " + tableName +
//                    " SET last_name = 'Singleton'" +
//                    " WHERE student_number LIKE 's3388490'";
//
//            int result = stmt.executeUpdate(sql);
//
//            if (result == 1) {
//                System.out.println("Update table " + tableName + " executed successfully");
//                System.out.println(result + " row(s) affected");
//            }
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    public static void UsePreparedStatement(String tableName) {
//        //final String TABLE_NAME = "Student";
//
//        String sql = "INSERT INTO " + tableName + " (id, student_number, first_name, last_name)" +
//                " VALUES (?, ?, ?, ?)";
//
//        try (Connection con = DatabaseConnection.getConnection();
//             PreparedStatement stmt = con.prepareStatement(sql)) {
//            stmt.setInt(1, 2);
//            stmt.setString(2, "s3089940");
//            stmt.setString(3, "Tom");
//            stmt.setString(4, "Bruster");
//
//            int result = stmt.executeUpdate();
//
//            if (result == 1) {
//                System.out.println("Insert into table " + tableName + " executed successfully");
//                System.out.println(result + " row(s) affected");
//            }
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//    }
}
