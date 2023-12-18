package com.example.implementation;

import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.implementation.Variables.*;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage){

        Group group = new Group();

        Scene scene = new Scene(group, 1200, 800);

        stage.setMaximized(true);
        stage.setTitle("Implementing Nagel–Schreckenberg model");
        stage.setScene(scene);
        stage.show();
        Timeline timeline = new Timeline();
        timeline.play();

        ArrayList<Square> listOfSquares = new ArrayList<>();
        initializeElements(listOfSquares, timeline, group);
        Controller.onRoadSquareClick(stage, listOfSquares, timeline, group, scene);
    }


    /** sets up elements on interface */
    public static void initializeElements(ArrayList<Square> listOfSquares, Timeline timeline, Group group){
//        all squares are drawn
        for(int j = 0; j < nodesInColumn; j++) {
            for (int i = 0; i < nodesInRow; i++) {
                Rectangle rectangle = new Rectangle(leftMargin + (1+interval) * sizeOfSquare * i,
                        upperMargin + (1+interval) * sizeOfSquare * j, sizeOfSquare, sizeOfSquare);
                rectangle.setFill(Color.GREEN);

                Square square = new Square(rectangle, group);
                listOfSquares.add(square);
            }
        }

        ((Rectangle) group.getChildren().get(5*nodesInRow)).setFill(Color.RED);

        Text startPointText = new Text(40, 275, "Start point");
        group.getChildren().add(startPointText);

        Label currentVelocityText = initializeCurrentVelocityText(group);
        Label currentDensityText = initializeCurrentDensityText(group);

        ChoiceBox<String> determineNumberOfCars = initializeChoiceBox(group);

        setLegendText(group);

        TextField numberOfCars = initializeNumberOfCars(group);

        Slider probabilityOfStopSlider = initializeProbabilitySlider(group);
        AtomicReference<Double> probability = new AtomicReference<>(0.0);
        Controller.handleProbability(probability, probabilityOfStopSlider, group);

        TextField timeFrameLength = initializeTimeFrame(group);

        boolean[][] isCellOccupied = new boolean[nodesInColumn][nodesInRow];
        ArrayList<Car> carList = new ArrayList<>();

        TextField maxVelocity = initializeMaxVelocity(group);

        ScatterChart<Number, Number> averageVelocityChart = initializeScatterChart(group);
        XYChart.Series<Number, Number> averageVelocitySeries = initializeVelocitySeries(averageVelocityChart);

        ScatterChart<Number, Number> densityChart = initializeDensityChart(group);
        XYChart.Series<Number, Number> densitySeries = initializeDensitySeries(densityChart);

        Button submitButton = initializeSubmitButton(group);
        Controller.onSubmitClick(probability, numberOfCars, listOfSquares, submitButton, timeline, isCellOccupied,
                carList, timeFrameLength, averageVelocitySeries, densitySeries, currentVelocityText, currentDensityText,
                determineNumberOfCars, probabilityOfStopSlider, maxVelocity);

        Button resetButton = initializeResetButton(group);
        Controller.onResetClick(resetButton, listOfSquares, timeline, isCellOccupied, carList, averageVelocitySeries,
                densitySeries, numberOfCars, timeFrameLength, probabilityOfStopSlider, maxVelocity);

        Button saveButton = initializeSaveButton(group);
        Controller.onSaveClick(listOfSquares, saveButton);

        Button loadButton = initializeLoadButton(group);
        Controller.onLoadClick(loadButton, listOfSquares, timeline, isCellOccupied, carList, averageVelocitySeries,
                densitySeries, numberOfCars, timeFrameLength, probabilityOfStopSlider, maxVelocity);
    }


    /** method that creates slider that sets probability of braking */
    private static Slider initializeProbabilitySlider(Group group){
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
    private static TextField initializeNumberOfCars(Group group){
        TextField numberOfCars = new TextField();
        numberOfCars.setTranslateX(500);
        numberOfCars.setTranslateY(45);
        numberOfCars.setPrefWidth(120);
        group.getChildren().add(numberOfCars);
        Label l = new Label("Choose option to determine number of cars");
        group.getChildren().add(l);
        l.setTranslateX(370);
        l.setTranslateY(20);
        return numberOfCars;
    }

    /** method that sets up legend */
    private static void setLegendText(Group group){
        Text legend = new Text("""
                Legend:
                Red square: start point,
                Black square: road,
                Pink square: end point,
                Orange: car""");
        legend.setTranslateX(120);
        legend.setTranslateY(630);
        group.getChildren().add(legend);
    }

    /** method that initializes up legend */
    private static Button initializeSubmitButton(Group group){
        Button submitButton = new Button();
        submitButton.setText("Start/pause animation");
        submitButton.setTranslateX(290);
        submitButton.setTranslateY(620);
        submitButton.setPrefWidth(140);
        group.getChildren().add(submitButton);
        return submitButton;
    }

    private static Button initializeResetButton(Group group){
        Button resetButton = new Button();
        resetButton.setText("Reset map");
        resetButton.setTranslateX(290);
        resetButton.setTranslateY(650);
        resetButton.setPrefWidth(140);
        group.getChildren().add(resetButton);
        return resetButton;
    }
    private static TextField initializeTimeFrame(Group group){
        TextField timeFrame = new TextField();
        timeFrame.setTranslateX(650);
        timeFrame.setTranslateY(45);
        timeFrame.setPrefWidth(120);
        group.getChildren().add(timeFrame);
        Text numberOfCarsText = new Text("Set up time of one iteration");
        numberOfCarsText.setTranslateX(640);
        numberOfCarsText.setTranslateY(35);
        group.getChildren().add(numberOfCarsText);
        return timeFrame;
    }

    public static ScatterChart<Number, Number> initializeScatterChart(Group group) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Average velocity");
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(5);
        yAxis.setTickUnit(1);
        yAxis.setAutoRanging(true);
        final ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTranslateX(900);
        scatterChart.setTranslateY(100);
        scatterChart.setAnimated(false);
        scatterChart.setPrefWidth(500);
        scatterChart.setPrefHeight(250);
        group.getChildren().add(scatterChart);
        return scatterChart;
    }

    private static XYChart.Series<Number, Number> initializeVelocitySeries(ScatterChart<Number, Number> averageVelocityChart){
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        averageVelocityChart.setLegendVisible(false);
        averageVelocityChart.getData().add(series);
        series.setName("Average velocity");
        return series;
    }

    public static ScatterChart<Number, Number> initializeDensityChart(Group group) {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Density");
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(1);
        yAxis.setTickUnit(0.2);
        yAxis.setAutoRanging(true);
        final ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTranslateX(900);
        scatterChart.setTranslateY(400);
        scatterChart.setAnimated(false);
        scatterChart.setPrefWidth(500);
        scatterChart.setPrefHeight(250);
        group.getChildren().add(scatterChart);
        return scatterChart;
    }

    private static XYChart.Series<Number, Number> initializeDensitySeries(ScatterChart<Number, Number> densityChart){
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        densityChart.setLegendVisible(false);
        densityChart.getData().add(series);
        series.setName("Density");
        return series;
    }

    private static Label initializeCurrentVelocityText(Group group){
        Label currentVelocityText = new Label("Current velocity: 0.0");
        currentVelocityText.setTranslateX(950);
        currentVelocityText.setTranslateY(80);
        group.getChildren().add(currentVelocityText);
        return currentVelocityText;
    }

    private static Label initializeCurrentDensityText(Group group){
        Label currentDensityText = new Label("Current density: 0.0");
        currentDensityText.setTranslateX(950);
        currentDensityText.setTranslateY(380);
        group.getChildren().add(currentDensityText);
        return currentDensityText;
    }

    private static Button initializeSaveButton(Group group){
        Button saveButton = new Button();
        saveButton.setText("Save map to file");
        saveButton.setTranslateX(460);
        saveButton.setTranslateY(620);
        saveButton.setPrefWidth(140);
        group.getChildren().add(saveButton);
        return saveButton;
    }

    private static Button initializeLoadButton(Group group){
        Button loadButton = new Button();
        loadButton.setText("Load map from file");
        loadButton.setTranslateX(460);
        loadButton.setTranslateY(650);
        loadButton.setPrefWidth(140);
        group.getChildren().add(loadButton);
        return loadButton;
    }

    private static ChoiceBox<String> initializeChoiceBox(Group group){
        ArrayList<String> options = new ArrayList<>();
        options.add("Number of cars");
        options.add("Density");
        ChoiceBox<String> choiceBox = new ChoiceBox<>(FXCollections.observableArrayList(options));
        group.getChildren().add(choiceBox);
        choiceBox.setTranslateX(370);
        choiceBox.setTranslateY(45);
        choiceBox.setValue("Number of cars");
        return choiceBox;
    }

    private static TextField initializeMaxVelocity(Group group) {
        TextField maxVelocity = new TextField();
        maxVelocity.setTranslateX(820);
        maxVelocity.setTranslateY(45);
        maxVelocity.setPrefWidth(120);
        group.getChildren().add(maxVelocity);
        Text maxVelocityText = new Text("Set up maximum velocity");
        maxVelocityText.setTranslateX(820);
        maxVelocityText.setTranslateY(35);
        group.getChildren().add(maxVelocityText);
        return maxVelocity;
    }

    public static void main(String[] args) {
        launch();
    }
}