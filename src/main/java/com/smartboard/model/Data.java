package com.smartboard.model;

import java.util.ArrayList;

public class Data {

    public static final Quote[] QUOTES = {
            new Quote("Do or do not. There is no try.", "Yoda"),
            new Quote("You must unlearn what you have learned.", "Yoda"),
            new Quote("Named must be your fear before banish it you can.", "Yoda"),
            new Quote("Fear is the path to the dark side. Fear leads to anger. Anger leads to hate. Hate leads to suffering.", "Yoda"),
            new Quote("Who's the more foolish: the fool or the fool who follows him?", "Obi-Wan Kenobi"),
            new Quote("Never tell me the odds!", "Han Solo"),
            new Quote("May the Force be with you.", "Obi-Wan Kenobi"),
            new Quote("Never tell me the odds!", "Han Solo"),
            new Quote("Your focus determines your reality.", "Qui-Gon Jinn")
    };
    public static String defPicturePath = "fry_avatar.jpg";
    public static User currentUser = null;
    public static ArrayList<User> users = new ArrayList<>();
    public static Column currentColumn = null;
    public static Task currentTask = null;

    public static void loadUsersFromDB() {
        users = DB_Utils.SelectAllUsers();
        System.out.println(users.size());
    }

    public static void setCurrentUser(User user) {
        currentUser = user;

        for (Project project : DB_Utils.SelectProjectsOfUser(currentUser.getName())) {
            currentUser.getSubItemList().add(project);
        }
    }

    public static void logoutCurrentUser() {
        currentUser.getSubItemList().clear();
        currentUser = null;
    }

    public static void addNewUser(String username, String password, String firstName, String lastName, String imagePath)
            throws IllegalArgumentException, StringLengthException {

        if (usernameExists(username)) {
            throw new IllegalArgumentException("This Username already exists");
        }

        users.add(new User(username, password, firstName, lastName));
        currentUser = users.get(users.size() - 1);
        currentUser.setImagePath(imagePath);

        DB_Utils.InsertNewUser(currentUser);
    }

    /**
     * Method to find if the username already exists in the users list
     */
    private static boolean usernameExists(String username) {
        boolean returnVal = false;

        for (User user : users) {
            if (username.equalsIgnoreCase(user.getName())) {
                returnVal = true;
                break;
            }
        }

        return returnVal;
    }

    public static void updateUserImagepath(String imagePath) {
        currentUser.setImagePath(imagePath);
        DB_Utils.UpdateUser(currentUser);
    }

    public static void updateUserFirstname(String firstName) throws StringLengthException {
        currentUser.setFirstName(firstName);
        DB_Utils.UpdateUser(currentUser);
    }

    public static void updateUserLastname(String lastName) throws StringLengthException {
        currentUser.setLastName(lastName);
        DB_Utils.UpdateUser(currentUser);
    }

    public static Project addNewProject(String name) throws StringLengthException {
        Project newProject = currentUser.addSubItem(name);
        DB_Utils.InsertNewProject(newProject);
        return newProject;
    }

    public static void updateProjectName(Project project, String name) throws StringLengthException {
        project.setName(name);
        DB_Utils.UpdateProject(project);
    }

    public static void updateProjectDefault(Project project, boolean isDefault) {
        project.setDefault(isDefault);
        DB_Utils.UpdateProject(project);
    }

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
            task.setOrderIndex(i);
            DB_Utils.UpdateTask(task);
            i++;
        }
    }

    public static void updateTaskColumn(Column column, Task task) {
        task.setColumnID(column.getColumnID());
        DB_Utils.UpdateTask(task);
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
