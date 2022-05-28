package com.smartboard.view;

import javafx.scene.control.Hyperlink;

public class DirectionButton extends Hyperlink {

    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String UP = "up";
    private static final String DOWN = "down";
    private final String STYLE = "-fx-text-fill: black; -fx-underline: false; -fx-font-weight: bold";
    private String direction;

    public DirectionButton() {
        this.initialize();
    }

    private void initialize() {
        this.setStyle(STYLE);
        this.setToLeft();
    }

    public void setToLeft() {
        this.setText("<");
        this.rotateProperty().set(0);
        this.direction = LEFT;
    }

    public void setToRight() {
        this.setText(">");
        this.rotateProperty().set(0);
        this.direction = RIGHT;
    }

    public void setToUp() {
        this.setText(">");
        this.rotateProperty().set(-90);
        this.direction = UP;
    }

    public void setToDown() {
        this.setText(">");
        this.rotateProperty().set(90);
        this.direction = DOWN;
    }

    public String getDirection() {
        return this.direction;
    }
}
