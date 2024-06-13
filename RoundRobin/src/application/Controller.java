package application;

import java.util.*;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Controller {
    
    @FXML
    private AnchorPane anchor;

    @FXML
    private Button insertNo;

    @FXML
    private VBox processContainer;

    @FXML
    private TextField processNo;
    
    @FXML
    private TableView<Process> roundRobinTable;
    
    @FXML
    private TableColumn<Process, Integer> processColumn;
    
    @FXML
    private TableColumn<Process, Integer> arrivalColumn;

    @FXML
    private TableColumn<Process, Integer> burstColumn;

    @FXML
    private TableColumn<Process, Integer> responseColumn;

    @FXML
    private TableColumn<Process, Integer> turnaroundColumn;

    @FXML
    private TableColumn<Process, Integer> waitingColumn;
    
    @FXML
    private VBox results;
    
    @FXML
    private VBox averageTimes;

    @FXML
    void startSimulation(ActionEvent event) {

        // Create ArrayList to hold Process objects
        List<Process> processesList = new ArrayList<>();
    
        // Clear existing content in the processContainer
        processContainer.getChildren().clear();
    
        try {
            // Parse the number of processes
            int numProcesses = Integer.parseInt(processNo.getText());
            
            // Check if the number of processes is non-negative
            if (numProcesses <= 0) {
                throw new IllegalArgumentException("Number of processes must be a positive integer");
            }


            for (int i = 0; i < numProcesses; i++) {
                // Create components for each process
                Text processText = new Text("Process " + (i + 1));
                HBox timeFieldsContainer = new HBox();
                timeFieldsContainer.setSpacing(10);
                TextField arrivalTimeField = new TextField();
                TextField burstTimeField = new TextField();
                Text arrivalLabel = new Text("Arrival Time:");
                Text burstLabel = new Text("Burst Time:");
    
                // Add components to the timeFieldsContainer
                timeFieldsContainer.getChildren().addAll(arrivalLabel, arrivalTimeField, burstLabel, burstTimeField);
    
                // Add components to the processContainer
                processContainer.getChildren().addAll(processText, timeFieldsContainer);
    
                // Create a Process object and add it to the ArrayList
                Process process = new Process();
                processesList.add(process);
            }
    
            // Add quantum time input and submit button
            Text quantumText = new Text("Enter quantum time");
            TextField quantumTimeField = new TextField();
            Button submit = new Button("Submit");
            processContainer.getChildren().addAll(quantumText, quantumTimeField, submit);
    
            // Handle submit button click
            submit.setOnAction(e -> {
                try {
                    int quantumTime = Integer.parseInt(quantumTimeField.getText());
                    if (quantumTime < 0) {
                        throw new IllegalArgumentException("Quantum time cannot be negative");
                    }

                    // Check if all fields are filled and non-negative
                    for (int i = 0; i < numProcesses; i++) {
                        HBox timeFieldsContainer = (HBox) processContainer.getChildren().get(i * 2 + 1);
                        TextField arrivalTimeField = (TextField) timeFieldsContainer.getChildren().get(1);
                        TextField burstTimeField = (TextField) timeFieldsContainer.getChildren().get(3);

                        if (arrivalTimeField.getText().isEmpty() || burstTimeField.getText().isEmpty()) {
                            throw new IllegalArgumentException("Please fill all fields");
                        }

                        int arrivalTime;
                        int burstTime;

                        try {
                            arrivalTime = Integer.parseInt(arrivalTimeField.getText());
                            burstTime = Integer.parseInt(burstTimeField.getText());
                        } catch (NumberFormatException ex) {
                            throw new IllegalArgumentException("Please enter a number for arrival and burst times");
                        }

                        if (arrivalTime < 0 || burstTime < 0) {
                            throw new IllegalArgumentException("Arrival time and burst time cannot be negative");
                        }
                        
                        Process process = processesList.get(i);
                        process.setArrivalTime(arrivalTime);
                        process.setBurstTime(burstTime);
                    }

                    Scheduler.scheduleRoundRobin(processesList, quantumTime);
                    
                    Scheduler.roundrobin(processesList,quantumTime);
                    processColumn.setCellValueFactory(cellData -> {
                    	int index = cellData.getTableView().getItems().indexOf(cellData.getValue()) + 1;
                        return new SimpleIntegerProperty(index).asObject();
                    });
                    processColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
                    arrivalColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getArrivalTime()).asObject());
                    burstColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBurstTime()).asObject());
                    responseColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getResponseTime()).asObject());
                    turnaroundColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTurnaroundTime()).asObject());
                    waitingColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getWaitingTime()).asObject());

                    // Create ObservableList to hold Process objects
                    ObservableList<Process> observableProcesses = FXCollections.observableArrayList(processesList);

                    // Populate the table with data
                    roundRobinTable.setItems(observableProcesses); 
                    
                    Text avgWaiting = new Text("Average waiting time: " + Scheduler.calcAvgWaitingTime(processesList));
                    Text avgTurnaround = new Text("Average turnaround time: " + Scheduler.calcAvgTurnTime(processesList));
                    Text avgResponse = new Text("Average response time: " + Scheduler.calcAvgResponseTime(processesList));
                    Button ganttButton = new Button ("Show Gantt Chart");
                    averageTimes.setSpacing(10);
                    averageTimes.getChildren().clear();
                    averageTimes.getChildren().addAll(avgWaiting, avgTurnaround, avgResponse, ganttButton);
                    
                    ganttButton.setOnAction(e2 -> {
                        // Create a new stage for the Gantt chart
                        Stage ganttStage = new Stage();
                        GanttChartController ganttChartStage = new GanttChartController(processesList);
                        ganttChartStage.start(ganttStage);
                    });
                   
                    
                } catch (NumberFormatException ex) {
                    Text invalidNo = new Text("Please enter a number for quantum time");
                    processContainer.getChildren().add(invalidNo);
                } catch (IllegalArgumentException ex) {
                    Text invalidNo = new Text(ex.getMessage());
                    processContainer.getChildren().add(invalidNo);
                } 
            });

        }  catch (NumberFormatException ex) {
            Text invalidNo = new Text("Please enter a valid number for the number of processes");
            processContainer.getChildren().add(invalidNo);
        } catch (IllegalArgumentException ex) {
            Text invalidNo = new Text(ex.getMessage());
            processContainer.getChildren().add(invalidNo);
        } 
    }
}