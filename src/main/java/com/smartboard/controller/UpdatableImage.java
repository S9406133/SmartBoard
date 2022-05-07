package com.smartboard.controller;

import javafx.event.Event;
import org.jetbrains.annotations.NotNull;

public interface UpdatableImage {

    void onImageClicked(@NotNull Event event);
}
