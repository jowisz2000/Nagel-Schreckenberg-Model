package com.example.implementation;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Square {
    private Rectangle rectangle;

    public Square(Rectangle rectangle) {
        this.rectangle = rectangle;
        this.rectangle.setFill(Color.BLUE);
        Application.group.getChildren().add(rectangle);
    }

    public Color getColor() {
        return (Color) rectangle.getFill();
    }

    public void setColor(Color color) {
        rectangle.setFill(color);
    }
}
