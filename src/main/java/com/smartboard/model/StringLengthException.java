/**
 * This exception class is used to validate the length of user inputted strings
 */

package com.smartboard.model;

public class StringLengthException extends Exception {

    public StringLengthException() {
        super();
    }

    public StringLengthException(String message) {
        super(message);
    }

}
