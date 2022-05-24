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

//    public static void createInitUser() throws StringLengthException {
//        users.add(new User("Sim", "a", "Simon", "James"));
//        users.get(0).addSubItem("Test Project");
//        users.get(0).getSubItem(0).addSubItem("To Do");
//        users.get(0).getSubItem(0).addSubItem("Doing");
//        users.get(0).getSubItem(0).addSubItem("Done");
//        users.get(0).getSubItem(0).getSubItem(0).addSubItem("First Task");
//        users.get(0).getSubItem(0).getSubItem(0).addSubItem("Second Task");
//        users.get(0).getSubItem(0).getSubItem(0).addSubItem("Third Task");
//        users.get(0).getSubItem(0).getSubItem(0).getSubItem(0).addSubItem("First item");
//        users.get(0).getSubItem(0).getSubItem(0).getSubItem(0).addSubItem("Second item");
//        users.get(0).getSubItem(0).getSubItem(0).getSubItem(0).addSubItem("Third item");
//    }

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

    public static void updateUserImagepath(String imagePath){
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

}
