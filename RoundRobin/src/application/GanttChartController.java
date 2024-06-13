package application;

import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

public class GanttChartController extends Application {

    private List<Process> processesList;
    
    public GanttChartController () {
    	
    }

    public GanttChartController(List<Process> processesList) {
        this.processesList = processesList;
    }

    @Override
    public void start(Stage primaryStage) {
        // Create a Gantt chart based on the process list
        GanttChart<Number, String> ganttChart = null;
        if (processesList != null) {
            ganttChart = createGanttChart();
        } else {
            // Handle the case where processesList is null
            // For example, show an error message
            System.err.println("No process list provided");
        }

        // Create a new scene with the Gantt chart if ganttChart is not null
        if (ganttChart != null) {
            Scene scene = new Scene(ganttChart, 700, 600);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

            // Set the scene in the stage
            primaryStage.setScene(scene);
            primaryStage.setTitle("Gantt Chart");
            primaryStage.show();
        }
    }

    private GanttChart<Number, String> createGanttChart() {
        // Create a new Gantt chart
        final NumberAxis xAxis = new NumberAxis();
        final CategoryAxis yAxis = new CategoryAxis();
        final GanttChart<Number, String> ganttChart = new GanttChart<>(xAxis, yAxis);

        // Configure the Gantt chart
        xAxis.setLabel("Time");
        xAxis.setTickLabelFill(javafx.scene.paint.Color.BLACK);
        yAxis.setLabel("Process");
         
        // Define colors for processes
        String[] colors = {"status-red", "status-green", "status-blue", "status-yellow", "status-purple","status-pink","status-black"}; // Predefined colors

        // Add process data to the Gantt chart
        for (int i = 0; i < processesList.size(); i++) {
            Process process = processesList.get(i);
            // Get the start and end times for the process
            List<Process.TimeEntry> entryTimes = process.getTimeEntries();

            // Create a series for the process
            XYChart.Series<Number, String> series = new XYChart.Series<>();

            // Add the time entries to the series
            for (Process.TimeEntry entry : entryTimes) {
                double startTime = entry.getStartTime();
                double endTime = entry.getEndTime();

                // Determine the color for the process
                String color = colors[i % colors.length];

                // Add the time entry to the series
                series.getData().add(new XYChart.Data<>(startTime, "Process " + process.getId(), new GanttChart.ExtraData((long) (endTime - startTime), color)));
            }

            // Add the series for the process to the Gantt chart
            ganttChart.getData().add(series);
        }

        return ganttChart;
    }


    public static void main(String[] args) {
        launch(args);
    }
}