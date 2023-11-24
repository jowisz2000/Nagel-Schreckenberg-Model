package com.example.implementation;

import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.implementation.Variables.*;

public class Application extends javafx.application.Application {
    /** group that keeps all elements from interface */
    static Group group = new Group();
    /** scene on which all elements are placed */
    static Scene scene = new Scene(group, 800, 600);

    @Override
    public void start(Stage stage){
//        stage.setMaximized(true);
        stage.setTitle("Implementing Nagel–Schreckenberg model");
        stage.setScene(scene);
        stage.show();

        ArrayList<Square> listOfSquares = new ArrayList<>();
        initializeElements(listOfSquares);
        Controller.onRoadSquareClick(stage, listOfSquares);
    }


/** sets up elements on interface */
    public static void initializeElements(ArrayList<Square> listOfSquares){
//        all squares are drawn
        for(int j = 0; j < nodesInColumn; j++) {
            for (int i = 0; i < nodesInRow; i++) {
                Rectangle rectangle = new Rectangle(leftMargin + (1+interval) * sizeOfSquare * i,
                        upperMargin + (1+interval) * sizeOfSquare * j, sizeOfSquare, sizeOfSquare);
                rectangle.setFill(Color.GREEN);

                Square square = new Square(rectangle);
                listOfSquares.add(square);
            }
        }

        ((Rectangle) group.getChildren().get(5*nodesInRow)).setFill(Color.RED);

        Text startPointText = new Text(40, 275, "Start point");
        group.getChildren().add(startPointText);

        setLegendText();

        Slider probabilityOfStopSlider = initializeProbabilitySlider();

        TextField numberOfCars = initializeNumberOfCars();
        group.getChildren().add(numberOfCars);

        AtomicReference<Double> probability = new AtomicReference<>(0.0);
        Controller.handleProbability(probability, probabilityOfStopSlider);

        Timeline timeline = new Timeline();
        boolean[][] isCellOccupied = new boolean[nodesInColumn][nodesInRow];
        ArrayList<Car> carList = new ArrayList<>();

        Button submitButton = initializeSubmitButton();
        Controller.onSubmitClick(probability, numberOfCars, listOfSquares, submitButton, timeline, isCellOccupied, carList);

        Button resetButton = initializeResetButton();
        Controller.onResetClick(resetButton, listOfSquares, timeline, isCellOccupied, carList);
    }

/** method that creates slider that sets probability of braking*/
    private static Slider initializeProbabilitySlider(){
        Slider probabilityOfStopSlider = new Slider();
        probabilityOfStopSlider.setMin(0);
        probabilityOfStopSlider.setMax(1);
        probabilityOfStopSlider.setShowTickLabels(true);
        probabilityOfStopSlider.setShowTickMarks(true);
        probabilityOfStopSlider.setBlockIncrement(0.01);
        probabilityOfStopSlider.setMajorTickUnit(0.2);
        probabilityOfStopSlider.setTranslateX(150);
        probabilityOfStopSlider.setTranslateY(40);
        group.getChildren().add(probabilityOfStopSlider);
        Text probabilityText = new Text(150, 30, "Set up probability of braking");
        group.getChildren().add(probabilityText);
        return probabilityOfStopSlider;
    }

    /** methods that creates field to set up number of cars */
    private static TextField initializeNumberOfCars(){
        TextField numberOfCars = new TextField();
        numberOfCars.setTranslateX(330);
        numberOfCars.setTranslateY(40);
        Text numberOfCarsText = new Text("Set up number of cars");
        numberOfCarsText.setTranslateX(330);
        numberOfCarsText.setTranslateY(30);
        group.getChildren().add(numberOfCarsText);
        return numberOfCars;
    }

    /** method that sets up legend */
    private static void setLegendText(){
        Text legend = new Text("""
                Legend:
                Red square: start point,
                Green square: road,
                Pink square: end point,
                Orange: car""");
        legend.setTranslateX(650);
        legend.setTranslateY(30);
        group.getChildren().add(legend);
    }

    /** method that initializes up legend */
    private static Button initializeSubmitButton(){
        Button submitButton = new Button();
        submitButton.setText("Submit");
        submitButton.setTranslateX(370);
        submitButton.setTranslateY(80);
        Application.group.getChildren().add(submitButton);
        return submitButton;
    }

    private static Button initializeResetButton(){
        Button submitButton = new Button();
        submitButton.setText("Reset animation");
        submitButton.setTranslateX(520);
        submitButton.setTranslateY(80);
        Application.group.getChildren().add(submitButton);
        return submitButton;
    }

    public static void main(String[] args) {
        launch();
    }
}