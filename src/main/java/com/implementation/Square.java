package com.implementation;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.HashSet;

/** represents single square that is drawn on interface */
public class Square{
    private final Rectangle rectangle;
    private HashSet<Direction> possibleDirections;

    public Square(Rectangle rectangle, Pane pane) {
        this.rectangle = rectangle;
        this.rectangle.setFill(Color.GREEN);
        pane.getChildren().add(rectangle);
        this.possibleDirections = new HashSet<>();
    }

    public HashSet<Direction> getPossibleDirections() {
        return possibleDirections;
    }

    public void setPossibleDirections(HashSet<Direction> possibleDirections) {
        this.possibleDirections = possibleDirections;
    }

    public void addDirection(Direction direction){
        possibleDirections.add(direction);
    }

    public void resetDirection() { possibleDirections.clear(); }

    public Color getColor() {
        return (Color) rectangle.getFill();
    }

    public void setColor(Color color) {
        rectangle.setFill(color);
    }
}
