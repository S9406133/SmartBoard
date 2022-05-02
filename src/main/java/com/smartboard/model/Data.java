package com.smartboard.model;

import java.util.ArrayList;
import java.util.Random;

public class Data {

    public static final Quote[] QUOTES = {
            new Quote("Do or do not. There is no try.", "Yoda"),
            new Quote("You must unlearn what you have learned.", "Yoda"),
            new Quote("Named must be your fear before banish it you can.", "Yoda"),
            new Quote("Fear is the path to the dark side. Fear leads to anger. Anger leads to hate. Hate leads to suffering.", "Yoda"),
            new Quote("Who's the more foolish: the fool or the fool who follows him?", "Obi-Wan Kenobi")
    };
    public static String defPicturePath = "fry_avatar.jpg";
    public static User currentUser = null;
    public static ArrayList<User> users = new ArrayList<>();

    public static void createInitUser() throws StringLengthException {
        users.add(new User("Simo", "a", "Simon", "James"));
    }

    public static Quote getRandomQuote() {
        Random rand = new Random();
        int randomInt = rand.nextInt(QUOTES.length);
        return QUOTES[randomInt];
    }

    public static void login(String username, String password) throws IllegalArgumentException {

        for (User user : users) {
            if (user.getName().equalsIgnoreCase(username)) {
                if (user.validateLogin(username, password)) {
                    currentUser = user;
                    break;
                } else {
                    throw new IllegalArgumentException("Password is incorrect");
                }
            }
        }

        if (currentUser == null) {
            throw new IllegalArgumentException("No such Username");
        }
    }

    public static void createUser(String username, String password, String firstName, String lastName)
            throws IllegalArgumentException, StringLengthException {

        if (usernameExists(username)) {
            throw new IllegalArgumentException("This Username already exists");
        }

        users.add(new User(username, password, firstName, lastName));
        currentUser = users.get(users.size() - 1);
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
}
