package com.smartboard.model;

import java.sql.*;
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
                while(resultSet.next()) {
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

    public static void SelectAllQuery(String tableName) {
        //final String TABLE_NAME = "STUDENT";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            String query = "SELECT * FROM " + tableName;

            try (ResultSet resultSet = stmt.executeQuery(query)) {
                while(resultSet.next()) {
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
