package com.smartboard.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import org.jetbrains.annotations.NotNull;

public interface Closable {

    @FXML
    void handleCloseButtonAction(@NotNull Event event);
}
