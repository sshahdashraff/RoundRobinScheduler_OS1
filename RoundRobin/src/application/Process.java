package application;

import java.util.ArrayList;
import java.util.List;

public class Process { 
    
    private int id;
    private int arrivalTime;
    private int burstTime;
    private int remainingBurstTime;
    private int waitingTime;
    private int turnaroundTime;
    private int responseTime;
    private int completionTime;
    private double startTime;
    private List<TimeEntry> timeEntries; // List to store entry times
    private boolean isExecuting;

    private static int nextId = 1; 
    
    public Process() {
        this.id = nextId++; 
        this.timeEntries = new ArrayList<>();
    }

    public Process(int arrivalTime, int burstTime) {
        this.id = nextId++; // Assign the current value of nextId and then increment it for the next ID
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingBurstTime = burstTime;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
        this.responseTime = -1; // Initialized to -1 to indicate not yet calculated
        this.completionTime = 0; // Initialized to 0
        this.timeEntries = new ArrayList<>();
        this.isExecuting = false;
    }

    
    public boolean isExecuting() {
        return isExecuting;
    }

    public void setExecuting(boolean executing) {
        isExecuting = executing;
    }
    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public int getRemainingBurstTime() {
        return remainingBurstTime;
    }

    public void setRemainingBurstTime(int remainingBurstTime) {
        this.remainingBurstTime = remainingBurstTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public void setCompletionTime() {
        this.completionTime = this.arrivalTime + this.burstTime;
    }
    
    public int getCompletionTime () {
        return this.completionTime;
    }

    public List<TimeEntry> getTimeEntries() {
        return timeEntries;
    }

    public void addTimeEntry(TimeEntry entry) {
        timeEntries.add(entry);
    }

    static class TimeEntry {
        private double startTime;
        private double endTime;

        public TimeEntry(double startTime, double endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public void setEndTime(double endTime) {
            this.endTime = endTime;
        }
        
        public double getStartTime() {
            return startTime;
        }

        public double getEndTime() {
            return endTime;
        }
    }
}