package com.example.implemenatation;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.implemenatation.Application.scene;
import static com.example.implemenatation.Variables.*;

public class Controller {
    /**
     * Event handler that checks if user clicked on square and then colours selected square
     * @param stage on which squares are placed
     * */

    static void onRoadSquareClick(Stage stage) {
        Robot robot = new Robot();
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
//            created coordinates that don't depend if application is on full screen or not
            double xWithoutMargin = robot.getMouseX() - leftMargin - stage.getX();
            double yWithoutMargin = robot.getMouseY() - upperMargin - 24 - stage.getY();


            double row = xWithoutMargin / (sizeOfSquare*(1+interval));
            double column = yWithoutMargin / (sizeOfSquare*(1+interval));

//            it doesn't allow to colour squares when user clicks over or under squares
            if(row <0 || column <0 || row>nodesInRow || column>nodesInColumn){
                return;
            }

//            it doesn't allow to choose road on start point
            if((int)row==0 && (int)column ==5){
                return;
            }

//            it doesn't allow to choose road on start point
            if((int)row==nodesInRow-1 && (int)column ==5){
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
                ((Rectangle) Application.group.getChildren().get(nodesInColumn*(int)row+(int)column)).setFill(Color.GREEN);
//                System.out.println(numberOfNeighbours((int)row, (int)column));
            }
        });
    }

    /** checks number of square placed on selected row and column */
    private static int numberOfNeighbours(int row, int column){
        int numberOfNeighbours = 4;


//        check left neighbour
        try {

            if (((Rectangle) Application.group.getChildren().get(row * nodesInColumn + column - 1)).getFill() == Color.BLUE) {
                numberOfNeighbours--;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            numberOfNeighbours--;
        }

//        check right neighbour
        try {
            if (((Rectangle) Application.group.getChildren().get(row * nodesInColumn + column + 1)).getFill() == Color.BLUE) {
                numberOfNeighbours--;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            numberOfNeighbours--;
        }

//        check lower neighbour
        try {
            if (((Rectangle) Application.group.getChildren().get((row - 1) * nodesInColumn + column)).getFill() == Color.BLUE) {
                numberOfNeighbours--;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            numberOfNeighbours--;
        }

//        check upper neighbour
        try {
            if (((Rectangle) Application.group.getChildren().get((row + 1) * nodesInColumn + column)).getFill() == Color.BLUE) {
                numberOfNeighbours--;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            numberOfNeighbours--;
        }

        return numberOfNeighbours;
    }

    /** method that disables choosing square if it has 3 neighbours in the corner*/
    private static boolean disableChoosing(int row, int column){

//        top right column
        try {
            if (((Rectangle) Application.group.getChildren().get((row + 1) * nodesInColumn + column)).getFill() != Color.BLUE
                    && ((Rectangle) Application.group.getChildren().get((row + 1) * nodesInColumn + column+1)).getFill() != Color.BLUE
                    &&((Rectangle) Application.group.getChildren().get((row) * nodesInColumn + column+1)).getFill() != Color.BLUE) {
                return true;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            return false;
        }

//        top left column
        try {
            if (((Rectangle) Application.group.getChildren().get((row) * nodesInColumn + column+1)).getFill() != Color.BLUE
                    && ((Rectangle) Application.group.getChildren().get((row) * nodesInColumn + column-1)).getFill() != Color.BLUE
                    &&((Rectangle) Application.group.getChildren().get((row + 1) * nodesInColumn + column-1)).getFill() != Color.BLUE) {
                return true;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            return false;
        }

//        left down corner
        try {
            if (((Rectangle) Application.group.getChildren().get((row) * nodesInColumn + column-1)).getFill() != Color.BLUE
                    && ((Rectangle) Application.group.getChildren().get((row-1) * nodesInColumn + column)).getFill() != Color.BLUE
                    &&((Rectangle) Application.group.getChildren().get((row - 1) * nodesInColumn + column-1)).getFill() != Color.BLUE) {
                return true;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            return false;
        }

        //        right down corner
        try {
            if (((Rectangle) Application.group.getChildren().get((row+1) * nodesInColumn + column)).getFill() != Color.BLUE
                    && ((Rectangle) Application.group.getChildren().get((row+1) * nodesInColumn + column+1)).getFill() != Color.BLUE
                    &&((Rectangle) Application.group.getChildren().get((row - 1) * nodesInColumn + column-1)).getFill() != Color.BLUE) {
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
     * @param probabilityOfStopSlider slider that enables to set probability*/
    static void handleProbability(AtomicReference<Double> probability, Slider probabilityOfStopSlider){
        Label currentProbabilityText = new Label("Current probability: "+probability);
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
     * @param probability probability to update*/
    static void initializeSubmitButton(AtomicReference<Double> probability){
        Button submitButton = new Button();
        submitButton.setText("Submit");
        submitButton.setTranslateX(200);
        submitButton.setTranslateY(100);
        EventHandler<ActionEvent> event = e -> System.out.println("Submitted probability:" + probability);
        submitButton.setOnAction(event);
        Application.group.getChildren().add(submitButton);
    }
}