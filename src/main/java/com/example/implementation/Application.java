package com.example.implementation;

import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    static Scene scene = new Scene(group, 1200, 800);

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

        Label currentVelocityText = initializeCurrentVelocityText();
        Label currentDensityText = initializeCurrentDensityText();

        setLegendText();

        TextField numberOfCars = initializeNumberOfCars();

        Slider probabilityOfStopSlider = initializeProbabilitySlider();
        AtomicReference<Double> probability = new AtomicReference<>(0.0);
        Controller.handleProbability(probability, probabilityOfStopSlider);

        TextField timeFrameLength = initializeTimeFrame();

        Timeline timeline = new Timeline();
        boolean[][] isCellOccupied = new boolean[nodesInColumn][nodesInRow];
        ArrayList<Car> carList = new ArrayList<>();

        ScatterChart<String, Number> averageVelocityChart = initializeScatterChart();
        XYChart.Series<String, Number> averageVelovitySeries = initializeVelocitySeries(averageVelocityChart);

        ScatterChart<String, Number> densityChart = initializeDensityChart();
        XYChart.Series<String, Number> densitySeries = initializeDensitySeries(densityChart);

        Button submitButton = initializeSubmitButton();
        Controller.onSubmitClick(probability, numberOfCars, listOfSquares, submitButton, timeline, isCellOccupied,
                carList, timeFrameLength, averageVelovitySeries, densitySeries,currentVelocityText, currentDensityText);

        Button resetButton = initializeResetButton();
        Controller.onResetClick(resetButton, listOfSquares, timeline, isCellOccupied, carList, averageVelovitySeries,
                densitySeries);

        Button pauseButton = initializePauseButton();
        Controller.onPauseClick(pauseButton, timeline);

        Button startButton = initializeStartButton();
        Controller.onStartClick(startButton, timeline);

        Button saveButton = initializeSaveButton();
        Controller.onSaveClick(listOfSquares, saveButton);

        Button loadButton = initializeLoadButton();
        Controller.onLoadClick(listOfSquares, loadButton);
    }

/** method that creates slider that sets probability of braking */
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
        numberOfCars.setTranslateX(370);
        numberOfCars.setTranslateY(40);
        numberOfCars.setPrefWidth(40);
        group.getChildren().add(numberOfCars);
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
        legend.setTranslateX(120);
        legend.setTranslateY(630);
        group.getChildren().add(legend);
    }

    /** method that initializes up legend */
    private static Button initializeSubmitButton(){
        Button submitButton = new Button();
        submitButton.setText("Submit");
        submitButton.setTranslateX(360);
        submitButton.setTranslateY(80);
        Application.group.getChildren().add(submitButton);
        return submitButton;
    }

    private static Button initializeResetButton(){
        Button submitButton = new Button();
        submitButton.setText("Reset animation");
        submitButton.setTranslateX(490);
        submitButton.setTranslateY(80);
        Application.group.getChildren().add(submitButton);
        return submitButton;
    }

    private static Button initializePauseButton(){
        Button pauseButton = new Button();
        pauseButton.setText("Pause animation");
        pauseButton.setTranslateX(490);
        pauseButton.setTranslateY(50);
        Application.group.getChildren().add(pauseButton);
        return pauseButton;
    }

    private static Button initializeStartButton(){
        Button pauseButton = new Button();
        pauseButton.setText("Start animation");
        pauseButton.setTranslateX(490);
        pauseButton.setTranslateY(20);
        Application.group.getChildren().add(pauseButton);
        return pauseButton;
    }

    private static TextField initializeTimeFrame(){
        TextField timeFrame = new TextField();
        timeFrame.setTranslateX(780);
        timeFrame.setTranslateY(40);
        timeFrame.setPrefWidth(40);
        group.getChildren().add(timeFrame);
        Text numberOfCarsText = new Text("Set up time of one iteration");
        numberOfCarsText.setTranslateX(780);
        numberOfCarsText.setTranslateY(30);
        group.getChildren().add(numberOfCarsText);
        return timeFrame;
    }

    public static ScatterChart<String, Number> initializeScatterChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Average velocity");
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(5);
        yAxis.setTickUnit(1);
        yAxis.setAutoRanging(false);
        final ScatterChart<String, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTranslateX(900);
        scatterChart.setTranslateY(100);
        scatterChart.setAnimated(false);
        scatterChart.setPrefWidth(300);
        scatterChart.setPrefHeight(250);
        group.getChildren().add(scatterChart);
        return scatterChart;
    }

    private static XYChart.Series<String, Number> initializeVelocitySeries(ScatterChart<String, Number> averageVelocityChart){
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        averageVelocityChart.setLegendVisible(false);
        averageVelocityChart.getData().add(series);
        series.setName("Average velocity");
        return series;
    }

    public static ScatterChart<String, Number> initializeDensityChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Density");
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(1);
        yAxis.setTickUnit(0.2);
        yAxis.setAutoRanging(false);
        final ScatterChart<String, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTranslateX(900);
        scatterChart.setTranslateY(400);
        scatterChart.setAnimated(false);
        scatterChart.setPrefWidth(300);
        scatterChart.setPrefHeight(250);
        group.getChildren().add(scatterChart);
        return scatterChart;
    }

    private static XYChart.Series<String, Number> initializeDensitySeries(ScatterChart<String, Number> densityChart){
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        densityChart.setLegendVisible(false);
        densityChart.getData().add(series);
        series.setName("Density");
        return series;
    }

    private static Label initializeCurrentVelocityText(){
        Label currentVelocityText = new Label("Current velocity: 0.0");
        currentVelocityText.setTranslateX(950);
        currentVelocityText.setTranslateY(80);
        group.getChildren().add(currentVelocityText);
        return currentVelocityText;
    }

    private static Label initializeCurrentDensityText(){
        Label currentDensityText = new Label("Current density: 0.0");
        currentDensityText.setTranslateX(950);
        currentDensityText.setTranslateY(380);
        group.getChildren().add(currentDensityText);
        return currentDensityText;
    }

    private static Button initializeSaveButton(){
        Button renameButton = new Button();
        renameButton.setText("Save road to file");
        renameButton.setTranslateX(650);
        renameButton.setTranslateY(20);
        Application.group.getChildren().add(renameButton);
        return renameButton;
    }

    private static Button initializeLoadButton(){
        Button loadButton = new Button();
        loadButton.setText("Load road from file");
        loadButton.setTranslateX(650);
        loadButton.setTranslateY(50);
        Application.group.getChildren().add(loadButton);
        return loadButton;
    }

    public static void main(String[] args) {
        launch();
    }
}