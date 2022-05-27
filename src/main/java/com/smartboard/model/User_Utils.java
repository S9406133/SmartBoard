package com.smartboard.model;

import java.util.ArrayList;

public class User_Utils {

    public static String defPicturePath = "fry_avatar.jpg";
    public static User currentUser = null;
    public static ArrayList<User> users = new ArrayList<>();

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

}
