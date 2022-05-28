/**
 * This class holds the static utility methods used to
 * update a User instance and related Database record
 */

package com.smartboard.model;

import java.util.ArrayList;

public class User_Utils {

    public static String defPicturePath = "fry_avatar.jpg";
    public static User currentUser = null;
    public static ArrayList<User> users = new ArrayList<>();

    /**
     * Loads all Users from the db and adds them to the users list
     */
    public static void loadUsersFromDB() {
        users = DB_Utils.SelectAllUsers();
    }

    /**
     * Sets the current User and loads all their Projects from the db into their sublist
     */
    public static void setCurrentUser(User user) {
        currentUser = user;

        for (Project project : DB_Utils.SelectProjectsOfUser(currentUser.getName())) {
            currentUser.getSubItemList().add(project);
        }
    }

    /**
     * Clears the data from the current Users list and sets it to null
     */
    public static void logoutCurrentUser() {
        currentUser.getSubItemList().clear();
        currentUser = null;
    }

    /**
     * Creates and adds a new User to the Users list and creates a database record of the User
     *
     * @throws StringLengthException If name is less an 2 or greater than 30 characters
     * @throws IllegalArgumentException If username already exists in the users list
     */
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
     * Finds if the username already exists in the Users list
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

    /**
     * Updates the imagePath of the Column instance and the database record
     */
    public static void updateUserImagepath(String imagePath) {
        currentUser.setImagePath(imagePath);
        DB_Utils.UpdateUser(currentUser);
    }

    /**
     * Updates the first name of the User instance and the database record
     *
     * @throws StringLengthException If name is less an 2 or greater than 30 characters
     */
    public static void updateUserFirstname(String firstName) throws StringLengthException {
        currentUser.setFirstName(firstName);
        DB_Utils.UpdateUser(currentUser);
    }

    /**
     * Updates the last name of the User instance and the database record
     *
     * @throws StringLengthException If name is less an 2 or greater than 30 characters
     */
    public static void updateUserLastname(String lastName) throws StringLengthException {
        currentUser.setLastName(lastName);
        DB_Utils.UpdateUser(currentUser);
    }

}
