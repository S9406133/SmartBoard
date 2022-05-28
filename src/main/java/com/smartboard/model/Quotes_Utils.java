/**
 * This interface holds the static data and methods for the Quotes instance
 */

package com.smartboard.model;

import java.util.Random;

public interface Quotes_Utils {

    Quote[] QUOTES = {
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

    static Quote getRandomQuote() {
        Random rand = new Random();
        int randomInt = rand.nextInt(Quotes_Utils.QUOTES.length);
        return Quotes_Utils.QUOTES[randomInt];
    }
}
