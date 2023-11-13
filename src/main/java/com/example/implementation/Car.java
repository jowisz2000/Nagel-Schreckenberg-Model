package com.example.implementation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static com.example.implementation.Variables.maxVelocity;
import static com.example.implementation.Variables.nodesInRow;

public class Car {
    private int x;
    private int y;
    private Direction direction;
    private int velocity;
    Car(int x, int y, int velocity){
        this.x=x;
        this.y=y;
        this.velocity=velocity;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(ArrayList<Square> listOfSquares, int currentX, int currentY) {
        HashSet<Direction> possibleDirections = listOfSquares.get(currentX * nodesInRow + currentY).getPossibleDirections();
        List<Direction> convertedDirections = new ArrayList<>(possibleDirections);
        convertedDirections.forEach(System.out::println);
        Random random = new Random();
        try {
            this.direction = convertedDirections.get(random.nextInt() % convertedDirections.size());
        }
        catch(ArithmeticException | NullPointerException e){
            this.direction = null;
        }
    }

    public int getVelocity() {
        return velocity;
    }

    public void incrementVelocity(){
        if(this.velocity< maxVelocity) this.velocity++;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public void changeCoordinates(Direction roadDirection){

        switch(roadDirection){
            case DOWN -> this.y++;
            case UP -> this.y--;
            case LEFT -> this.x--;
            case RIGHT -> this.x++;
        }

    }
}
