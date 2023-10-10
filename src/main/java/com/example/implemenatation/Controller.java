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

    static public void onRoadSquareClick(Stage stage) {
        Robot robot = new Robot();
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
//            created coordinates that doesn't depend if application is on full screen or not
            double xWithoutMargin = robot.getMouseX() - leftMargin - stage.getX();
            double yWithoutMargin = robot.getMouseY() - upperMargin - 24 - stage.getY();


            double row = xWithoutMargin / (sizeOfSquare*(1+interval));
            double column = yWithoutMargin / (sizeOfSquare*(1+interval));

            if(row <0 || column <0 || row>nodesInRow || column>nodesInColumn){
                return;
            }

//            if user clicked the square and this square is not the one from which cars start driving then
//            the square is coloured
            if(row % 1 < 1/(1+interval) && column % 1 < 1/(1+interval) && (row!=5 && column !=0)){
                ((Rectangle) Application.group.getChildren().get(nodesInColumn*(int)row+(int)column)).setFill(Color.GREEN);
            }
        });
    }

    /** method that handles events from slider that sets probability of braking
     * @param probability keeps current probability that is on slider
     * @param probabilityOfStopSlider slider that enables to set probability*/
    public static void handleProbability(AtomicReference<Double> probability, Slider probabilityOfStopSlider){
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
    public static void initializeSubmitButton(AtomicReference<Double> probability){
        Button submitButton = new Button();
        submitButton.setText("Submit");
        submitButton.setTranslateX(200);
        submitButton.setTranslateY(100);
        EventHandler<ActionEvent> event = e -> System.out.println("Submitted probability:" + probability);
        submitButton.setOnAction(event);
        Application.group.getChildren().add(submitButton);
    }
}