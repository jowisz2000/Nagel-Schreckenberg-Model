package com.implementation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import static com.implementation.Variables.nodesInRow;

public class Car {
    private int x;
    private int y;
    private Direction direction;
    private int velocity;
    private boolean isMoving;

    Car(int x, int y, int velocity){
        this.x=x;
        this.y=y;
        this.velocity=velocity;
        this.isMoving = false;
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
        Random random = new Random();
        try {
            this.direction = convertedDirections.get(random.nextInt(1000000) % convertedDirections.size());
        }
        catch(ArithmeticException | NullPointerException e){
            this.direction = null;
        }
    }

    public int getVelocity() {
        return velocity;
    }

    public void incrementVelocity(int maxVelocity){
        if(this.velocity < maxVelocity) this.velocity++;
    }

    public void decrementVelocity(){
        if(this.velocity > 0) this.velocity--;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }
}
