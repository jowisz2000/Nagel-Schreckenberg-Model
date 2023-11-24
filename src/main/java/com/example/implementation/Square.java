package com.example.implementation;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.HashSet;

/** represents single square that is drawn on interface */
public class Square {
    private final Rectangle rectangle;
    private HashSet<Direction> possibleDirections;

    public HashSet<Direction> getPossibleDirections() {
        return possibleDirections;
    }

    public void addDirection(Direction direction){
        possibleDirections.add(direction);
    }

    public void setPossibleDirections(HashSet<Direction> possibleDirections) {
        this.possibleDirections = possibleDirections;
    }

    public Square(Rectangle rectangle) {
        this.rectangle = rectangle;
        this.rectangle.setFill(Color.GREEN);
        Application.group.getChildren().add(rectangle);
        this.possibleDirections = new HashSet<>();
    }

    public Color getColor() {
        return (Color) rectangle.getFill();
    }

    public void setColor(Color color) {
        rectangle.setFill(color);
    }
}
