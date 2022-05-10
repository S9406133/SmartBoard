package com.smartboard.model;

import java.util.ArrayList;

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
    public static Column currentColumn = null;
    public static Task currentTask = null;

    public static void createInitUser() throws StringLengthException {
        users.add(new User("Simo", "a", "Simon", "James"));

        //currentUser = users.get(0); // Only for testing - no login required
    }

}
