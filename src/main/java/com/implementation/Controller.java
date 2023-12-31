package com.implementation;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.implementation.Direction.*;
import static com.implementation.Variables.*;

public class Controller {

    private static final ArrayList<Paint> colorsOfRoads = new ArrayList<>(Arrays.asList(Color.BLACK, Color.RED));

    private static final Paint colorOfFreeField = Color.GREEN;
    /**
     * Event handler that checks if user clicked on square and then colours selected square  */

    static void onRoadSquareClick(ArrayList<Square> listOfSquares, Timeline timeline, Pane pane) {
        pane.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            double xWithoutMargin = mouseEvent.getX() - leftMargin;
            double yWithoutMargin = mouseEvent.getY() - upperMargin;

            double column = xWithoutMargin / (sizeOfSquare*(1+interval));
            double row = yWithoutMargin / (sizeOfSquare*(1+interval));

            //            it doesn't allow to colour squares when user clicks over or under squares
            if(row <0 || column <0 || row>nodesInColumn || column>nodesInRow){
                return;
            }

//            it doesn't allow to choose road on start point
            if((int)row==5 && (int)column == 0){
                return;
            }

            if(row % 1 < 1/(1+interval) && column % 1 < 1/(1+interval) && timeline.getStatus() == Animation.Status.RUNNING &&
                    listOfSquares.get((int)row*nodesInRow+(int)column).getColor()==colorOfFreeField){
                return;
            }

            if(numberOfNeighbours((int)row, (int)column, pane) == 0){
                Alert noNeighboursAlert = new Alert(Alert.AlertType.ERROR);
                noNeighboursAlert.setContentText("This cell has no neighbours! Please select one with neighbours.");
                noNeighboursAlert.show();
                return;
            }

            if(disableChoosing((int)row, (int)column, pane)){
                Alert noNeighboursAlert = new Alert(Alert.AlertType.ERROR);
                noNeighboursAlert.setContentText("It is impossible to put road in this square");
                noNeighboursAlert.show();
                return;
            }

            if (((Rectangle) pane.getChildren().get(nodesInRow * (int) row + (int) column)).getFill() != colorOfFreeField) {
                ArrayList<Direction> possibleDirections = new ArrayList<>(listOfSquares.get((int)row*nodesInRow+(int)column).getPossibleDirections());
                StringBuilder stringBuilder = new StringBuilder();
                for (Direction current : possibleDirections) {
                    stringBuilder.append(current).append("\n");
                }
                String allDirectionsText = stringBuilder.toString().trim();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Possible directions for this cell");
                alert.setHeaderText(null);
                TextArea textArea;
                if (listOfSquares.get((int) row * nodesInRow + (int) column).getPossibleDirections().isEmpty()) {
                    textArea = new TextArea("This cell is a dead end");
                } else {
                    textArea = new TextArea(allDirectionsText);
                }

                textArea.setEditable(false);
                GridPane gridPane = new GridPane();
                gridPane.add(textArea, 0, 0);
                alert.getDialogPane().setContent(gridPane);
                alert.getDialogPane().setMaxWidth(250);
                alert.getDialogPane().setMaxHeight(140);
                alert.showAndWait();
                return;
            }

//            if user clicked the square then the square is coloured
            if(row % 1 < 1/(1+interval) && column % 1 < 1/(1+interval)){
                ((Rectangle) pane.getChildren().get(nodesInRow*(int)row+(int)column)).setFill(Color.BLACK);
                setDirection(listOfSquares, (int)row, (int)column, pane);
            }
        });
    }

    /** methods that sets up direction of current square
     * @param listOfSquares list of all drawn squares
     * @param  row row of selected square
     * @param column column of selected square*/
    public static void setDirection(ArrayList<Square> listOfSquares, int row, int column, Pane pane) {
        int numberOfNeighbours = numberOfNeighbours(row, column, pane);

//        the most complicated case: if selected node has 2 neighbours
        if (numberOfNeighbours == 2) {
            if(checkLeftNeighbour(row, column, pane) && checkRightNeighbour(row, column, pane)){
//                first case: if we can only move left or right from selected node
                if(disableChoosing(row-1, column, pane) && disableChoosing(row+1,column, pane)){
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
                //               second case: if we can move left, right from selected node and additionally up or down
                else if(disableChoosing(row-1, column, pane) || disableChoosing(row+1, column, pane)){
                    listOfSquares.get(row * nodesInRow + column - 1).addDirection(Direction.RIGHT);
                    listOfSquares.get(row * nodesInRow + column + 1).addDirection(Direction.LEFT);
                }
            }
//            same checking left or right neighbour
            else if(checkUpperNeighbour(row, column, pane) && checkLowerNeighbour(row, column, pane)){
                if(disableChoosing(row, column-1, pane) && disableChoosing(row,column+1, pane)){
                    ButtonType up = new ButtonType("up", ButtonBar.ButtonData.OK_DONE);
                    ButtonType down = new ButtonType("down", ButtonBar.ButtonData.OK_DONE);
                    Alert alert = new Alert(Alert.AlertType.WARNING,  "Choose direction", up, down);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.orElse(up) == up) {
                        listOfSquares.get((row+1) * nodesInRow + column).addDirection(UP);
                        listOfSquares.get(row * nodesInRow + column).addDirection(UP);
                    }
                    else{
                        listOfSquares.get((row-1) * nodesInRow + column).addDirection(DOWN);
                        listOfSquares.get(row * nodesInRow + column).addDirection(DOWN);
                    }
                }
                else if(disableChoosing(row, column-1, pane) || disableChoosing(row, column+1, pane)){
                    listOfSquares.get((row+1) * nodesInRow + column).addDirection(Direction.UP);
                    listOfSquares.get((row-1) * nodesInRow + column).addDirection(Direction.DOWN);
                }
            }
//            else, we set direction that is based on node's neighbours
            else{
                standardDirectionConfiguration(row, column, listOfSquares, pane);
            }
        }
        //            else, we set direction that is based on node's neighbours
        else{
            standardDirectionConfiguration(row, column, listOfSquares, pane);
        }
    }

    /** method that configures square's neighbours on the basis of its neighbours
     *  @param listOfSquares list of all drawn squares
     *  @param  row row of selected square
     *  @param column column of selected square*/
    private static void standardDirectionConfiguration(int row, int column, ArrayList<Square> listOfSquares, Pane pane){
        if (checkLeftNeighbour(row, column, pane)) {
            listOfSquares.get(row * nodesInRow + column - 1).addDirection(RIGHT);
        }
        if (checkRightNeighbour(row, column, pane)) {
            listOfSquares.get(row * nodesInRow + column + 1).addDirection(LEFT);
        }
        if (checkUpperNeighbour(row, column, pane)) {
            listOfSquares.get((row-1) * nodesInRow + column).addDirection(Direction.DOWN);
        }
        if (checkLowerNeighbour(row, column, pane)) {
            listOfSquares.get((row+1) * nodesInRow + column).addDirection(UP);
        }
    }

    /** method that checks if selected square has left neighbour
     *  @param  row row of selected square
     *  @param column column of selected square
     * */
    private static boolean checkLeftNeighbour(int row, int column, Pane pane){
        try {
            Paint colourOfSquare = ((Rectangle) pane.getChildren().get(row * nodesInRow + column - 1)).getFill();
            if (column-1>=0 && Controller.colorsOfRoads.contains(colourOfSquare)) {
                return true;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            return false;
        }

        return false;
    }

    /** method that checks if selected square has right neighbour
     *  @param  row row of selected square
     *  @param column column of selected square
     * */
    private static boolean checkRightNeighbour(int row, int column, Pane pane){
        try {
            Paint colourOfSquare = ((Rectangle)pane.getChildren().get(row * nodesInRow + column + 1)).getFill();
            if (column+1<nodesInRow && Controller.colorsOfRoads.contains(colourOfSquare)) {
                return true;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            return false;
        }

        return false;
    }

    /** method that checks if selected square has lower neighbour
     *  @param  row row of selected square
     *  @param column column of selected square
     * */
    private static boolean checkLowerNeighbour(int row, int column, Pane pane){
        try {
            Paint colourOfSquare = ((Rectangle) pane.getChildren().get((row+1) * nodesInRow + column)).getFill();
            if (row+1>=0 && Controller.colorsOfRoads.contains(colourOfSquare)) {
                return true;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            return false;
        }
        return false;
    }

    /** method that checks if selected square has upper neighbour
     *  @param  row row of selected square
     *  @param column column of selected square
     * */
    private static boolean checkUpperNeighbour(int row, int column, Pane pane){
        try {
            Paint colourOfSquare = ((Rectangle) pane.getChildren().get((row-1) * nodesInRow + column)).getFill();
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
    private static int numberOfNeighbours(int row, int column, Pane pane){
        int numberOfNeighbours = 0;
//        System.out.println("Coordinates:"+row+", "+column);
        if(checkLeftNeighbour(row, column, pane)){
            numberOfNeighbours++;
        }
        if(checkRightNeighbour(row, column, pane)){
            numberOfNeighbours++;
        }
        if(checkLowerNeighbour(row, column, pane)){
            numberOfNeighbours++;
        }
        if(checkUpperNeighbour(row, column, pane)){
            numberOfNeighbours++;
        }
//        System.out.println("Number of neighbours: "+numberOfNeighbours);
        return numberOfNeighbours;
    }

    /** method that disables choosing square if it has 3 neighbours in the corner */
    private static boolean disableChoosing(int row, int column, Pane pane){

//        bottom right column
        try {
            if (((Rectangle) pane.getChildren().get((row + 1) * nodesInRow + column)).getFill() != colorOfFreeField
                    && ((Rectangle) pane.getChildren().get((row + 1) * nodesInRow + column+1)).getFill() != colorOfFreeField
                    && ((Rectangle) pane.getChildren().get((row) * nodesInRow + column+1)).getFill() != colorOfFreeField) {
                return true;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            return false;
        }

//        top left column
        try {
            if (((Rectangle) pane.getChildren().get((row+1) * nodesInRow + column)).getFill() != colorOfFreeField
                    && ((Rectangle) pane.getChildren().get((row) * nodesInRow + column-1)).getFill() != colorOfFreeField
                    && ((Rectangle) pane.getChildren().get((row + 1) * nodesInRow + column-1)).getFill() != colorOfFreeField) {
                return true;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            return false;
        }

//        top left corner
        try {
            if (((Rectangle) pane.getChildren().get((row) * nodesInRow + column-1)).getFill() != colorOfFreeField
                    && ((Rectangle) pane.getChildren().get((row-1) * nodesInRow + column)).getFill() != colorOfFreeField
                    && ((Rectangle) pane.getChildren().get((row - 1) * nodesInRow + column-1)).getFill() != colorOfFreeField) {
                return true;
            }
        }
        catch(IndexOutOfBoundsException | ClassCastException e){
            return false;
        }

        //        right down corner
        try {
            if (((Rectangle) pane.getChildren().get((row-1) * nodesInRow + column)).getFill() != colorOfFreeField
                    && ((Rectangle) pane.getChildren().get(row * nodesInRow + column+1)).getFill() != colorOfFreeField
                    && ((Rectangle) pane.getChildren().get((row - 1) * nodesInRow + column + 1)).getFill() != colorOfFreeField) {
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
    static void handleProbability(AtomicReference<Double> probability, Slider probabilityOfStopSlider, Pane pane){
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
        pane.getChildren().add(currentProbabilityText);
    }

    /** updates probability variable when submit is clicked
     * @param probability probability to update
     * @param numberOfCars number of cars used to animation
     * @param listOfSquares all drawn squares
     * @param submitButton button that is responsible for starting animation */
    static void onSubmitClick(AtomicReference<Double> probability, TextField numberOfCars, ArrayList<Square>listOfSquares,
                              Button submitButton, Timeline timeline, boolean[][]isCellOccupied, ArrayList<Car> carList,
                              TextField frameLength, XYChart.Series<Number, Number> averageVelocitySeries,
                              XYChart.Series<Number, Number> densitySeries, Label currentVelocityText, Label currentDensityText,
                              ChoiceBox<String> determineNumberOfCars, Slider probabilityOfStopSlider, TextField maxVelocity){
        EventHandler<ActionEvent> event = e -> {

            if(timeline.getStatus() == Animation.Status.RUNNING){
                timeline.stop();
                return;
            }
            else if(!carList.isEmpty()){
                timeline.play();
                setEndPoints(listOfSquares);
                return;
            }

            int actualNumberOfCars;
            setEndPoints(listOfSquares);

            if(determineNumberOfCars.getValue().equals("Number of cars")) {
                if (!numberOfCars.getText().matches("\\d+")) {
                    Alert invalidNumberAlert = new Alert(Alert.AlertType.ERROR);
                    invalidNumberAlert.setContentText("Number of cars has to be natural!");
                    invalidNumberAlert.show();
                    return;
                }
                actualNumberOfCars = Integer.parseInt(numberOfCars.getText());
            }
            else{
                if (!numberOfCars.getText().matches("^(0(?:\\.\\d+)?|1(?:\\.0+)?)$")) {
                    Alert invalidNumberAlert = new Alert(Alert.AlertType.ERROR);
                    invalidNumberAlert.setContentText("Density has to be a number from 0 to 1!");
                    invalidNumberAlert.show();
                    return;
                }
                actualNumberOfCars = (int) (Double.parseDouble(numberOfCars.getText())*lengthOfRoad(listOfSquares));
            }

            if(!frameLength.getText().matches("\\d*\\.?\\d+")){
                Alert invalidNumberAlert = new Alert(Alert.AlertType.ERROR);
                invalidNumberAlert.setContentText("Time of iteration has to be a positive number!");
                invalidNumberAlert.show();
                return;
            }

            if (!maxVelocity.getText().matches("\\d+")) {
                Alert invalidNumberAlert = new Alert(Alert.AlertType.ERROR);
                invalidNumberAlert.setContentText("Maximum velocity has to be a natural number!");
                invalidNumberAlert.show();
                return;
            }

            numberOfCars.setDisable(true);
            frameLength.setDisable(true);
            probabilityOfStopSlider.setDisable(true);
            maxVelocity.setDisable(true);

            try {
                carMovement(actualNumberOfCars, numberOfCars, maxVelocity, listOfSquares, probability, timeline,
                        isCellOccupied, carList, frameLength, averageVelocitySeries,
                        densitySeries, currentVelocityText, currentDensityText, probabilityOfStopSlider);
            }
            catch (InterruptedException | NumberFormatException | ArithmeticException ignored) {}

        };
        submitButton.setOnAction(event);
    }

    /** methods that is responsible for moving car
     * @param numberOfCars number of cars used in animation
     * @param listOfSquares list of printed squares
     * @param probability probability of car's sudden stop*/
    private static void carMovement(int actualNumberOfCars, TextField numberOfCars, TextField maxVelocity, ArrayList<Square> listOfSquares, AtomicReference<Double> probability,
                                    Timeline timeline, boolean[][] isCellOccupied, ArrayList<Car> carList,
                                    TextField frameLengthText, XYChart.Series<Number, Number> averageVelocitySeries,
                                    XYChart.Series<Number, Number> densitySeries, Label currentVelocityText,
                                    Label currentDensityText, Slider probabilityOfStopSlider) throws InterruptedException {

        carList.clear();
        averageVelocitySeries.getData().clear();
        densitySeries.getData().clear();
        for (int i = 0; i <actualNumberOfCars; i++) {
            Car newCar = new Car(5, 0, 0);
            carList.add(newCar);
        }

        timeline.setCycleCount(Timeline.INDEFINITE);
        AtomicReference<Double> summedVelocity = new AtomicReference<>(0.0);
        AtomicReference<Integer> currentIteration = new AtomicReference<>(1);
        double frameLength = Double.parseDouble(frameLengthText.getText());

        if(probability.get() == 1){
            return;
        }

        for(Car currentCar: carList){

            KeyFrame initializeVelocityFrame = new KeyFrame(Duration.ZERO, event -> {
                Random random = new Random();
                double generatedProbability = random.nextDouble();

                if(generatedProbability < probability.get()){
                    currentCar.decrementVelocity();
                }
                else{
                    currentCar.incrementVelocity(Integer.parseInt(maxVelocity.getText()));
                }
            });
            timeline.getKeyFrames().add(initializeVelocityFrame);

            KeyFrame iterationKeyframe = new KeyFrame(Duration.seconds(frameLength/3),
                    e -> iterateInTimeFrame(listOfSquares, currentCar, timeline, isCellOccupied, carList,
                            numberOfCars, frameLengthText, maxVelocity, probabilityOfStopSlider));
            timeline.getKeyFrames().add(iterationKeyframe);

            KeyFrame drawCarsKeyFrame = new KeyFrame(Duration.seconds(frameLength*2/3), event -> {
                if(currentCar.isMoving() && !(currentCar.getX() == 5 && currentCar.getY() == 0)) {
                    isCellOccupied[currentCar.getX()][currentCar.getY()] = true;
                }
            });
            timeline.getKeyFrames().add(drawCarsKeyFrame);

            KeyFrame updateGUI = new KeyFrame(Duration.seconds(frameLength), event ->{
                uploadChanges(isCellOccupied, listOfSquares);
                Controller.updateCharts(summedVelocity, currentCar, densitySeries, averageVelocitySeries,
                        carList.size(), currentIteration, carList, listOfSquares, currentVelocityText, currentDensityText);
            });
            timeline.getKeyFrames().add(updateGUI);

            timeline.play();
        }
    }

    /** makes single iteration
     * @param listOfSquares list of drawn squares
     * @param currentCar coordinates of this car are changed while invoking this method
     * @param timeline holds all animation KeyFrames
     * @param isCellOccupied 2D array of booleans that tells whether cell is occupied by car
     */
    private static void iterateInTimeFrame(ArrayList<Square> listOfSquares, Car currentCar, Timeline timeline,
                                           boolean[][] isCellOccupied, ArrayList<Car> listOfCars, TextField numberOfCars,
                                           TextField frameLength, TextField maxVelocity, Slider probabilityOfStopSLider){
        int currentX = currentCar.getX();
        int currentY = currentCar.getY();
        int checkedBoxes = 0;
        int oldX = currentX;
        int oldY = currentY;

        isCellOccupied[currentX][currentY] = false;

        while (checkedBoxes < currentCar.getVelocity()) {

            currentCar.setDirection(listOfSquares, currentX, currentY);
            Direction roadDirection = currentCar.getDirection();

            if(roadDirection == null){
                timeline.stop();
                return;
            }

            switch (roadDirection) {
                case DOWN -> currentX++;
                case UP -> currentX--;
                case LEFT -> currentY--;
                case RIGHT -> currentY++;
            }

//            if car is not on start point and if square is occupied by another car, then car goes one square back
            if (!(currentX==5 && currentY==0) && isCellOccupied[currentX][currentY]) {
                switch (roadDirection) {
                    case DOWN -> currentX--;
                    case UP -> currentX++;
                    case LEFT -> currentY++;
                    case RIGHT -> currentY--;
                }

                currentCar.setX(currentX);
                currentCar.setY(currentY);
                currentCar.setVelocity(checkedBoxes);
                isCellOccupied[currentCar.getX()][currentCar.getY()] = true;
                return;
            }
//            if squares reaches dead end, then car is removed
            else if (listOfSquares.get(currentX * nodesInRow + currentY).getColor() == Color.PINK) {
                currentCar.setMoving(false);
                isCellOccupied[currentX][currentY] = false;
                isCellOccupied[oldX][oldY] = false;

                if(!isAnyCellOccupied(isCellOccupied)){
                    uploadChanges(isCellOccupied, listOfSquares);
                    listOfSquares.get(5*nodesInRow).setColor(Color.RED);

                    for(Square square: listOfSquares){
                        if(square.getColor()==Color.PINK){
                            square.setColor(Color.BLACK);
                        }
                    }

                    timeline.stop();
                    timeline.getKeyFrames().clear();
                    listOfCars.clear();
                    numberOfCars.setDisable(false);
                    frameLength.setDisable(false);
                    maxVelocity.setDisable(false);
                    probabilityOfStopSLider.setDisable(false);
                    return;
                }
                return;
            }

            currentCar.setMoving(true);

            // old coordinates of car are free
            isCellOccupied[currentCar.getX()][currentCar.getY()] = false;
            currentCar.setX(currentX);
            currentCar.setY(currentY);

            checkedBoxes++;
        }
        isCellOccupied[oldX][oldY] = false;
        isCellOccupied[currentCar.getX()][currentCar.getY()] = true;
    }

    /** searches for end points in given squares */
    static void setEndPoints(ArrayList<Square> listOfSquares) {
        for (Square square : listOfSquares)
            if ((square.getColor() != colorOfFreeField) && square.getPossibleDirections().isEmpty()) {
                square.setColor(Color.PINK);
            }
    }

    private static boolean isAnyCellOccupied(boolean[][]array){
        for(int i=0; i<nodesInColumn; i++){
            for(int j=0; j<nodesInRow; j++){
                if(array[i][j]){
                    return true;
                }
            }
        }
        return false;
    }

    static void onResetClick(Button resetButton, ArrayList<Square> listOfSquares, Timeline timeline,
                             boolean[][]isCellOccupied, ArrayList<Car> carList, XYChart.Series<Number, Number> averageVelocitySeries,
                             XYChart.Series<Number, Number> densitySeries, TextField numberOfCars, TextField frameLength,
                             Slider probabilityOfStopSlider, TextField maxVelocity){
        EventHandler<ActionEvent> event = e -> resetGUI(listOfSquares, timeline, isCellOccupied, carList, averageVelocitySeries, densitySeries,
                numberOfCars, frameLength, probabilityOfStopSlider, maxVelocity);
        resetButton.setOnAction(event);
    }

    private static void updateCharts(AtomicReference<Double> summedVelocity, Car currentCar, XYChart.Series<Number, Number> densitySeries,
                                     XYChart.Series<Number, Number> averageVelocitySeries, int size,
                                     AtomicReference<Integer> currentIteration, ArrayList<Car> carList,
                                     ArrayList<Square> listOfSquares, Label currentVelocityText, Label currentDensityText){

        if(currentCar.isMoving()) {
            summedVelocity.set(summedVelocity.get() + currentCar.getVelocity());
        }

        if(currentIteration.get() % size == 0){
            if (densitySeries.getData().size() > maxPointsInChart) {
                averageVelocitySeries.getData().remove(0);
                densitySeries.getData().remove(0);
            }

            densitySeries.getData().add(new XYChart.Data<>(currentIteration.get()/size, 1.0*stillMovingCars(carList)/lengthOfRoad(listOfSquares)));

            for (XYChart.Data<Number, Number> data : densitySeries.getData()) {
                data.getNode().setStyle("-fx-background-radius: 5px;");
            }

            averageVelocitySeries.getData().add(new XYChart.Data<>(currentIteration.get()/size, summedVelocity.get()/stillMovingCars(carList)));
            if(currentIteration.get()/size > 50){
                ((NumberAxis)densitySeries.getChart().getXAxis()).setForceZeroInRange(false);
                ((NumberAxis)averageVelocitySeries.getChart().getXAxis()).setForceZeroInRange(false);
            }

            DecimalFormat df = new DecimalFormat("#.##");
            currentDensityText.setText("Current density: " + df.format(1.0*stillMovingCars(carList)/lengthOfRoad(listOfSquares)));
            currentVelocityText.setText("Current average velocity: "+ df.format(summedVelocity.get()/stillMovingCars(carList)));
            summedVelocity.set(0.0);
        }
        currentIteration.set(currentIteration.get()+1);
    }

    private static int stillMovingCars(ArrayList<Car>listOfCars){
        int stillMovingCars = 0;
        for(Car currentCar: listOfCars){
            if(currentCar.isMoving()){
                stillMovingCars++;
            }
        }
        return stillMovingCars;
    }

    private static int lengthOfRoad(ArrayList<Square> listOfSquares){
        int length = 0;
        for(Square currentSquare:listOfSquares){
            if(currentSquare.getColor() == Color.BLACK || currentSquare.getColor() == Color.ORANGE){
                length++;
            }
        }
        return length;
    }
    static void onSaveClick(ArrayList<Square> listOfSquares, Button saveButton){
        EventHandler<ActionEvent> event = e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save road configuration");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Data Files", "*.dat"));
            File selectedFile = fileChooser.showSaveDialog(null);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile.getAbsolutePath()))) {
                for (int i=0; i<listOfSquares.size(); i++) {
                    if(listOfSquares.get(i).getColor() != colorOfFreeField){
                        StringBuilder allDirections = new StringBuilder();
                        for(Direction direction: listOfSquares.get(i).getPossibleDirections()){
                            allDirections.append(direction).append(" ");
                        }
                        writer.write(i+" "+allDirections);
                        writer.newLine();
                    }
                }
            } catch (IOException | NullPointerException ignored) {}
        };
        saveButton.setOnAction(event);
    }

    public static void onLoadClick(Button loadButton, ArrayList<Square> listOfSquares, Timeline timeline,
                                   boolean[][]isCellOccupied, ArrayList<Car> carList, XYChart.Series<Number, Number> averageVelocitySeries,
                                   XYChart.Series<Number, Number> densitySeries, TextField numberOfCars, TextField frameLength,
                                   Slider probabilityOfStopSlider, TextField maxVelocity) {
        EventHandler<ActionEvent> event = e -> {

            resetGUI(listOfSquares, timeline, isCellOccupied, carList, averageVelocitySeries, densitySeries,
                    numberOfCars, frameLength, probabilityOfStopSlider, maxVelocity);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose road configuration");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Data Files", "*.dat"));
            try {
                File selectedFile = fileChooser.showOpenDialog(null);
                Scanner scanner = new Scanner(new File(selectedFile.getAbsolutePath()));

                while(scanner.hasNextLine()){
                    String currentSquare = scanner.nextLine();
                    String[] splitInformation = currentSquare.split(" ");
                    int index = Integer.parseInt(splitInformation[0]);

                    HashSet<Direction> loadedDirections = new HashSet<>();
                    for(int i=1; i<splitInformation.length; i++){
                        loadedDirections.add(Direction.valueOf(splitInformation[i]));
                    }

                    listOfSquares.get(index).setColor(Color.BLACK);
                    listOfSquares.get(index).setPossibleDirections(loadedDirections);
                }

            } catch (IOException | NullPointerException ignored) {}
        };
        loadButton.setOnAction(event);
    }

    private static void resetGUI(ArrayList<Square> listOfSquares, Timeline timeline, boolean[][] isCellOccupied,
                                 ArrayList<Car> carList, XYChart.Series<Number, Number> averageVelocitySeries,
                                 XYChart.Series<Number, Number> densitySeries, TextField numberOfCars,
                                 TextField frameLength, Slider probabilityOfStopSlider, TextField maxVelocity) {
        averageVelocitySeries.getData().clear();
        densitySeries.getData().clear();
        timeline.stop();
        timeline.jumpTo(Duration.ZERO);
        timeline.getKeyFrames().clear();

        for (Square currentSquare : listOfSquares) {
            currentSquare.setColor((Color) colorOfFreeField);
            currentSquare.resetDirection();
        }

        listOfSquares.get(5*nodesInRow).setColor(Color.RED);

        for (boolean[] booleans : isCellOccupied) {
            Arrays.fill(booleans, false);
        }
        carList.clear();

        numberOfCars.setDisable(false);
        frameLength.setDisable(false);
        probabilityOfStopSlider.setDisable(false);
        maxVelocity.setDisable(false);
    }

    private static void uploadChanges(boolean[][]isCellOccupied, ArrayList<Square> listOfSquares){
        for(int i=0; i<nodesInColumn; i++){
            for(int j=0; j<nodesInRow; j++){
                if(!listOfSquares.get(i*nodesInRow+j).getPossibleDirections().isEmpty()){
                    if(isCellOccupied[i][j]){
                        listOfSquares.get(i*nodesInRow+j).setColor(Color.ORANGE);
                    }
                    else{
                        listOfSquares.get(i*nodesInRow+j).setColor(Color.BLACK);
                    }
                }
            }
        }
    }
}