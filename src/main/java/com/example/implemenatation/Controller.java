package com.example.implemenatation;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

//            if user clicked the square then the square is coloured
            if(row % 1 < 1/(1+interval) && column % 1 < 1/(1+interval)){
                System.out.println(((Rectangle) Application.group.getChildren().get(2)).getFill());
                ((Rectangle) Application.group.getChildren().get(nodesInColumn*(int)row+(int)column)).setFill(Color.GREEN);
                System.out.println(checkIfHasNeighbours((int)row, (int)column));

            }
        });
    }

    private static int checkIfHasNeighbours(int row, int column){

        int numberOfNeighbours = 0;

        for (int i=row-1; i<row+2; i++) {
            for (int j = column - 1; j < column + 2; j++) {
//                System.out.println(i+" "+j+" "+ ((Rectangle) Application.group.getChildren().get(i * nodesInColumn + j)).getFill());
                    Rectangle rec = ((Rectangle) Application.group.getChildren().get(i * nodesInColumn + j));
                    System.out.println("in loop "+rec.getFill().equals(Color.GREEN));
                    if (!(i == row && j == column) && ((Rectangle) Application.group.getChildren().get(i * nodesInColumn + j)).getFill()==(Color.GREEN)) {
                        System.out.println("in return"+rec.getFill().equals(Color.GREEN));
                        numberOfNeighbours++;
                    }
                }
            }
        return numberOfNeighbours;
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