/**
 * This record defines the Quote record
 */

package com.smartboard.model;

public record Quote(String quote, String author) {

    @Override
    public String toString() {
        return String.format("\"%s\" - %s", quote, author);
    }

}
