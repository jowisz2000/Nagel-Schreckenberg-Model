package com.example.implementation;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.implementation.Variables.*;

public class Application extends javafx.application.Application {
    /** group that keeps all elements from interface */
    static Group group = new Group();
    /** scene on which all elements are placed */
    static Scene scene = new Scene(group, 800, 600);

    @Override
    public void start(Stage stage) throws IOException {
//        stage.setMaximized(true);
        stage.setTitle("Implementing Nagel–Schreckenberg model");
        stage.setScene(scene);
        stage.show();

        ArrayList<Square> listOfSquares = new ArrayList<>();
        initializeElements(stage, listOfSquares);
        Controller.onRoadSquareClick(stage, listOfSquares);
        Controller.setEndPoints(listOfSquares);
    }


/** sets up elements on interface
 * @param stage stage on which elements are placed */
    public static void initializeElements(Stage stage, ArrayList<Square> listOfSquares){
//        all squares are drawn
        for(int j = 0; j < nodesInColumn; j++) {
            for (int i = 0; i < nodesInRow; i++) {
                Rectangle rectangle = new Rectangle(leftMargin + (1+interval) * sizeOfSquare * i,
                        upperMargin + (1+interval) * sizeOfSquare * j, sizeOfSquare, sizeOfSquare);
                rectangle.setFill(Color.BLUE);

                Square square = new Square(rectangle);
                listOfSquares.add(square);
            }
        }

        ((Rectangle) group.getChildren().get(5*nodesInRow)).setFill(Color.RED);
//        ((Rectangle) group.getChildren().get(6*nodesInRow-1)).setFill(Color.ORANGE);

        Text startPointText = new Text(40, 275, "Start point");
        group.getChildren().add(startPointText);
//        Text endPointText = new Text(100+((1+interval)*sizeOfSquare)*nodesInRow, 275, "End point");
//        group.getChildren().add(endPointText);
        setLegendText();

        Slider probabilityOfStopSlider = initializeProbabilitySlider();

        TextField numberOfCars = initializeNumberOfCars();

        group.getChildren().add(numberOfCars);

        AtomicReference<Double> probability = new AtomicReference<>(0.0);
        Controller.handleProbability(probability, probabilityOfStopSlider);
        Controller.initializeSubmitButton(probability, numberOfCars, listOfSquares);
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

    private static TextField initializeNumberOfCars(){
        TextField numberOfCars = new TextField();
        numberOfCars.setTranslateX(400);
        numberOfCars.setTranslateY(40);
        Text numberOfCarsText = new Text("Set up number of cars");
        numberOfCarsText.setTranslateX(400);
        numberOfCarsText.setTranslateY(30);
        group.getChildren().add(numberOfCarsText);

        return numberOfCars;
    }

    private static void setLegendText(){
        Text legend = new Text("""
                Legend:
                Red square: start point,
                Green square: road,
                Orange square: end point""");
        legend.setTranslateX(120);
        legend.setTranslateY(upperMargin + (nodesInColumn+1) * ((1+interval)*sizeOfSquare));
        group.getChildren().add(legend);
    }

    public static void main(String[] args) {
        launch();
    }
}