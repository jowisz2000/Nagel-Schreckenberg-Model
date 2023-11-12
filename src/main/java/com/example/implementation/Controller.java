package com.example.implementation;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.implementation.Application.group;
import static com.example.implementation.Application.scene;
import static com.example.implementation.Direction.*;
import static com.example.implementation.Variables.*;

public class Controller {

    private static final ArrayList<Paint> colorsOfRoads = new ArrayList<>(Arrays.asList(Color.GREEN, Color.RED));
    /**
     * Event handler that checks if user clicked on square and then colours selected square
     * @param stage on which squares are placed
     * */

    static void onRoadSquareClick(Stage stage, ArrayList<Square> listOfSquares) {
        Robot robot = new Robot();
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
//            created coordinates that don't depend if application is on full screen or not
            double xWithoutMargin = robot.getMouseX() - leftMargin - stage.getX();
            double yWithoutMargin = robot.getMouseY() - upperMargin - 24 - stage.getY();


            double column = xWithoutMargin / (sizeOfSquare*(1+interval));
            double row = yWithoutMargin / (sizeOfSquare*(1+interval));

            try {
                if (((Rectangle) Application.group.getChildren().get(nodesInRow * (int) row + (int) column)).getFill() != Color.BLUE) {
                    listOfSquares.get((int) row * nodesInRow + (int) column).getPossibleDirections().forEach(System.out::println);
                    if(listOfSquares.get((int) row * nodesInRow + (int) column).getPossibleDirections().isEmpty()){
                        System.out.println("Dead end");
                    }
                    return;
                }
            }
            catch(IndexOutOfBoundsException ignored){}

//            it doesn't allow to colour squares when user clicks over or under squares
            if(row <0 || column <0 || row>nodesInColumn || column>nodesInRow){
                return;
            }

//            it doesn't allow to choose road on start point
            if((int)row==5 && (int)column == 0){
                return;
            }

            if(numberOfNeighbours((int)row, (int)column) == 0){
                Alert noNeighboursAlert = new Alert(Alert.AlertType.ERROR);
                noNeighboursAlert.setContentText("This cell has no neighbours! Please select one with neighbours.");
                noNeighboursAlert.show();
                return;
            }

            if(disableChoosing((int)row, (int)column)){
                Alert noNeighboursAlert = new Alert(Alert.AlertType.ERROR);
                noNeighboursAlert.setContentText("It is impossible to put road in this square");
                noNeighboursAlert.show();
                return;
            }

//            if user clicked the square then the square is coloured
            if(row % 1 < 1/(1+interval) && column % 1 < 1/(1+interval)){
                ((Rectangle) Application.group.getChildren().get(nodesInRow*(int)row+(int)column)).setFill(Color.GREEN);
                setDirection(listOfSquares, (int)row, (int)column);
            }
        });
    }

    public static void setDirection(ArrayList<Square> listOfSquares, int row, int column) {
        int numberOfNeighbours = numberOfNeighbours(row, column);

        if (numberOfNeighbours == 2) {
            if(checkLeftNeighbour(row, column) && checkRightNeighbour(row, column)){
                if(disableChoosing(row-1, column) && disableChoosing(row+1,column)){
                    ButtonType left = new ButtonType("left", ButtonBar.ButtonData.OK_DONE);
                    ButtonType right = new ButtonType("right", ButtonBar.ButtonData.OK_DONE);
                    Alert alert = new Alert(Alert.AlertType.WARNING,  "Choose direction", left, right);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.orElse(left) == left) {
                        listOfSquares.get(row * nodesInRow + column + 1).addDirection(LEFT);
                        listOfSquares.get(row * nodesInRow + column).addDirection(LEFT);
                    }
                    else{
                        listOfSquares.get(row * nodesInRow + column - 1).addDirection(RIGHT);
                        listOfSquares.get(row * nodesInRow + column).addDirection(RIGHT);
                    }
                }
                else if(disableChoosing(row-1, column)){
                    try {
                        listOfSquares.get((row + 1) * nodesInRow + column).addDirection(Direction.DOWN);
                    } catch(IndexOutOfBoundsException e){
                        listOfSquares.get(row * nodesInRow + column - 1).addDirection(RIGHT);
                        listOfSquares.get(row * nodesInRow + column + 1).addDirection(LEFT);
                    }
                }
                else if(disableChoosing(row+1, column)){
                    try {
                        listOfSquares.get((row - 1) * nodesInRow + column).addDirection(Direction.DOWN);
                    } catch(IndexOutOfBoundsException e){
                        listOfSquares.get(row * nodesInRow + column - 1).addDirection(RIGHT);
                        listOfSquares.get(row * nodesInRow + column + 1).addDirection(LEFT);
                    }
                }
            }
            else if(disableChoosing(row, column-1) && disableChoosing(row,column+1)){
                ButtonType up = new ButtonType("up", ButtonBar.ButtonData.OK_DONE);
                ButtonType down = new ButtonType("down", ButtonBar.ButtonData.OK_DONE);
                Alert alert = new Alert(Alert.AlertType.WARNING,  "Choose direction", up, down);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.orElse(up) == up) {
                    listOfSquares.get((row+1) * nodesInRow + column).addDirection(UP);
                    listOfSquares.get(row * nodesInRow + column).addDirection(UP);
                }
                else{
                    listOfSquares.get((row-1) * nodesInRow + column).addDirection(Direction.DOWN);
                    listOfSquares.get(row * nodesInRow + column).addDirection(Direction.DOWN);
                }
            }
            else{
                standardDirectionConfiguration(row, column, listOfSquares);
            }
        }
        else{
            standardDirectionConfiguration(row, column, listOfSquares);
        }
    }

    private static void standardDirectionConfiguration(int row, int column, ArrayList<Square> listOfSquares){
        if (checkLeftNeighbour(row, column)) {
            listOfSquares.get(row * nodesInRow + column - 1).addDirection(RIGHT);
        }
        if (checkRightNeighbour(row, column)) {
            listOfSquares.get(row * nodesInRow + column + 1).addDirection(LEFT);
        }
        if (checkUpperNeighbour(row, column)) {
            listOfSquares.get((row-1) * nodesInRow + column).addDirection(Direction.DOWN);
        }
        if (checkLowerNeighbour(row, column)) {
            listOfSquares.get((row+1) * nodesInRow + column).addDirection(UP);
        }
    }

    private static boolean checkLeftNeighbour(int row, int column){
        try {
            Paint colourOfSquare = ((Rectangle) Application.group.getChildren().get(row * nodesInRow + column - 1)).getFill();
            if (column-1>=0 && Controller.colorsOfRoads.contains(colourOfSquare)) {
                return true;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            return false;
        }

        return false;
    }

    private static boolean checkRightNeighbour(int row, int column){
        try {
            Paint colourOfSquare = ((Rectangle) Application.group.getChildren().get(row * nodesInRow + column + 1)).getFill();
            if (column+1<nodesInRow && Controller.colorsOfRoads.contains(colourOfSquare)) {
                return true;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            return false;
        }

        return false;
    }

    private static boolean checkLowerNeighbour(int row, int column){
        try {
            Paint colourOfSquare = ((Rectangle) Application.group.getChildren().get((row+1) * nodesInRow + column)).getFill();
            if (row+1>=0 && Controller.colorsOfRoads.contains(colourOfSquare)) {
                return true;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            return false;
        }
        return false;
    }

    private static boolean checkUpperNeighbour(int row, int column){
        try {
            Paint colourOfSquare = ((Rectangle) Application.group.getChildren().get((row-1) * nodesInRow + column)).getFill();
            if (row-1< nodesInColumn && Controller.colorsOfRoads.contains(colourOfSquare)) {
                return true;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            return false;
        }
        return false;
    }

    /** checks number of square placed on selected row and column */
    private static int numberOfNeighbours(int row, int column){
        int numberOfNeighbours = 0;
//        System.out.println("Coordinates:"+row+", "+column);
        if(checkLeftNeighbour(row, column)){
            numberOfNeighbours++;
        }
        if(checkRightNeighbour(row, column)){
            numberOfNeighbours++;
        }
        if(checkLowerNeighbour(row, column)){
            numberOfNeighbours++;
        }
        if(checkUpperNeighbour(row, column)){
            numberOfNeighbours++;
        }
//        System.out.println("Number of neighbours: "+numberOfNeighbours);
        return numberOfNeighbours;
    }

    /** method that disables choosing square if it has 3 neighbours in the corner */
    private static boolean disableChoosing(int row, int column){

//        bottom right column
        try {
            if (((Rectangle) Application.group.getChildren().get((row + 1) * nodesInRow + column)).getFill() != Color.BLUE
                    && ((Rectangle) Application.group.getChildren().get((row + 1) * nodesInRow + column+1)).getFill() != Color.BLUE
                    && ((Rectangle) Application.group.getChildren().get((row) * nodesInRow + column+1)).getFill() != Color.BLUE) {
                return true;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            return false;
        }

//        top left column
        try {
            if (((Rectangle) Application.group.getChildren().get((row+1) * nodesInRow + column)).getFill() != Color.BLUE
                    && ((Rectangle) Application.group.getChildren().get((row) * nodesInRow + column-1)).getFill() != Color.BLUE
                    && ((Rectangle) Application.group.getChildren().get((row + 1) * nodesInRow + column-1)).getFill() != Color.BLUE) {
                return true;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            return false;
        }

//        top left corner
        try {
            if (((Rectangle) Application.group.getChildren().get((row) * nodesInRow + column-1)).getFill() != Color.BLUE
                    && ((Rectangle) Application.group.getChildren().get((row-1) * nodesInRow + column)).getFill() != Color.BLUE
                    && ((Rectangle) Application.group.getChildren().get((row - 1) * nodesInRow + column-1)).getFill() != Color.BLUE) {
                return true;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            return false;
        }

        //        right down corner
        try {
            if (((Rectangle) Application.group.getChildren().get((row-1) * nodesInRow + column)).getFill() != Color.BLUE
                    && ((Rectangle) Application.group.getChildren().get(row * nodesInRow + column+1)).getFill() != Color.BLUE
                    && ((Rectangle) Application.group.getChildren().get((row - 1) * nodesInRow + column + 1)).getFill() != Color.BLUE) {
                return true;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            return false;
        }

        return false;
    }

    /** method that handles events from slider that sets probability of braking
     * @param probability keeps current probability that is on slider
     * @param probabilityOfStopSlider slider that enables to set probability */
    static void handleProbability(AtomicReference<Double> probability, Slider probabilityOfStopSlider){
        Label currentProbabilityText = new Label("Current probability: " + probability);
        currentProbabilityText.setTranslateX(150);
        currentProbabilityText.setTranslateY(80);

//        event handler that passes current value to probability variable
        probabilityOfStopSlider.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    DecimalFormat df = new DecimalFormat("#.##");
                    currentProbabilityText.setText("Current probability: " + df.format(newValue));
                    probability.set((Double) newValue);
                });
        Application.group.getChildren().add(currentProbabilityText);
    }

    /** updates probability variable when submit is clicked
     * @param probability probability to update */
    static void onSubmitClick(AtomicReference<Double> probability, TextField numberOfCars, ArrayList<Square>listOfSquares, Button submitButton){
        EventHandler<ActionEvent> event = e -> {
            if(!numberOfCars.getText().matches("\\d+")){
                Alert noNeighboursAlert = new Alert(Alert.AlertType.ERROR);
                noNeighboursAlert.setContentText("It must be a natural number!");
                noNeighboursAlert.show();
            }
            setEndPoints(listOfSquares);
            try {
                carMovement(Integer.parseInt(numberOfCars.getText()), listOfSquares, probability);
            }
            catch (InterruptedException | NumberFormatException ignored) {}

        };
        submitButton.setOnAction(event);
    }

    private static void carMovement(int numberOfCars, ArrayList<Square> listOfSquares, AtomicReference<Double> probability) throws InterruptedException {
        ArrayList<Car> carList = new ArrayList<>();
        for (int i = 0; i < numberOfCars; i++) {
            Car newCar = new Car(5, 0, 0);
            carList.add(newCar);
            ((Rectangle) group.getChildren().get(newCar.getX() * nodesInRow + newCar.getY())).setFill(Color.ORANGE);
        }


            for (Iterator<Car> car = carList.iterator(); car.hasNext();) {
                Timeline timeline = new Timeline();
                timeline.setCycleCount(10);

                AtomicReference<Boolean> reachedDeadEnd = new AtomicReference<>(false);

                Car currentCar = car.next();

                KeyFrame frame = new KeyFrame(Duration.ZERO, event -> {
                    currentCar.incrementVelocity();

                    Random random = new Random();
                    double generatedProbability = random.nextDouble();
                    System.out.println("Generated: "+generatedProbability);
                    if(generatedProbability < probability.get()){
                        currentCar.setVelocity(0);
                        System.out.println("Suddenly stopped");
                        return;
                    }
                    int currentVelocity = currentCar.getVelocity();
                    System.out.println("Velocity: " + currentVelocity);
                });
                timeline.getKeyFrames().add(frame);

                KeyFrame secondKeyframe = new KeyFrame(Duration.seconds(1.5),
                        e -> iterateInTimeFrame(listOfSquares, car, currentCar, timeline, reachedDeadEnd));
                timeline.getKeyFrames().add(secondKeyframe);

                KeyFrame stopAnimationKeyFrame = new KeyFrame(Duration.seconds(1.75), event -> {
                    if (currentCar == null) {
                        System.out.println("Stop");
                        timeline.stop();
                        timeline.getKeyFrames().clear();
                    }
                });
                timeline.getKeyFrames().add(stopAnimationKeyFrame);

                KeyFrame thirdKeyframe = new KeyFrame(Duration.seconds(2), event ->{
                        if(!reachedDeadEnd.get()){
                        ((Rectangle) group.getChildren().get(currentCar.getX() * nodesInRow + currentCar.getY())).setFill(Color.ORANGE);
                    }
                });
                timeline.getKeyFrames().add(thirdKeyframe);

                timeline.play();

            }
    }


    private static void iterateInTimeFrame(ArrayList<Square> listOfSquares, Iterator<Car> car, Car currentCar, Timeline timeline, AtomicReference<Boolean> reachedDeadEnd){
        int currentX = currentCar.getX();
        int currentY = currentCar.getY();
        int checkedBoxes = 0;

        while (checkedBoxes < currentCar.getVelocity()) {

            System.out.println("We're inside loop");
            currentCar.setDirection(listOfSquares, currentX, currentY);
            Direction roadDirection = currentCar.getDirection();

            switch (roadDirection) {
                case DOWN -> currentX++;
                case UP -> currentX--;
                case LEFT -> currentY--;
                case RIGHT -> currentY++;
            }

            if (listOfSquares.get(currentX * nodesInRow + currentY).getColor() == Color.ORANGE) {
                System.out.println("Invoke going back");
                switch (roadDirection) {
                    case DOWN -> currentX--;
                    case UP -> currentX++;
                    case LEFT -> currentY++;
                    case RIGHT -> currentY--;
                }
                break;
            } else if (listOfSquares.get(currentX * nodesInRow + currentY).getColor() == Color.PINK) {
                try {
                    ((Rectangle) group.getChildren().get(currentCar.getX() * nodesInRow + currentCar.getY())).setFill(Color.GREEN);
                    currentCar.setX(currentX);
                    currentCar.setY(currentY);
                    reachedDeadEnd.set(true);
                    car.remove();
                    assert timeline != null;
                    timeline.stop();
                    return;
                }
                catch(IllegalStateException e){
                    if (timeline != null) {
                        timeline.stop();
                        timeline.getKeyFrames().clear();
                        return;
                    }
                }
            }

            ((Rectangle) group.getChildren().get(currentCar.getX() * nodesInRow + currentCar.getY())).setFill(Color.GREEN);
            currentCar.setX(currentX);
            currentCar.setY(currentY);
            checkedBoxes++;
            System.out.println(currentX+" "+currentY+", "+currentCar.getX()+ " "+currentCar.getY()+"\n--------");
        }
    }

    static void setEndPoints(ArrayList<Square> listOfSquares) {
        for (Square square : listOfSquares)
            if ((square.getColor() == Color.GREEN) && square.getPossibleDirections().isEmpty()) {
                square.setColor(Color.PINK);
            }

    }


}