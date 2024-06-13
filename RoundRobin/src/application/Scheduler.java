package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;



public class Scheduler {
	
	private ArrayList<Process> processes;

    public Scheduler() {
        processes = new ArrayList<>();
    }

    public ArrayList<Process> getProcesses() {
        return processes;
    }

    public void setProcesses(ArrayList<Process> processes) {
        this.processes = processes;
    }

    public static void scheduleRoundRobin(List<Process> processesList, int tq) {
        Collections.sort(processesList, Comparator.comparingInt(Process::getArrivalTime));
        int n = processesList.size();
        int[] wait = new int[n];
        int[] turn = new int[n];
        int[] queue = new int[n]; // Ensure the queue array has a minimum size of 1
        int[] tempBurst = new int[n];
        boolean[] complete = new boolean[n];
        int[] response = new int[n]; // Array to store response times
        int timer = 0, maxProcessIndex = 0;

        for (int i = 0; i < n; i++) {
            complete[i] = false;
            queue[i] = 0;
            tempBurst[i] = processesList.get(i).getBurstTime();
            response[i] = -1; // Initialize response time to -1
        }

        // Increment timer until the first process arrives
        while (timer < processesList.get(0).getArrivalTime())
            timer++;
        queue[0] = 1;

        // Main scheduling loop
        while (true) {
            boolean flag = true;
            for (int i = 0; i < n; i++) {
                if (tempBurst[i] != 0) {
                    flag = false;
                    break;
                }
            }
            if (flag)
                break;

            for (int i = 0; (i < n) && (queue[i] != 0); i++) {
                int ctr = 0;
                while ((ctr < tq) && (tempBurst[queue[0] - 1] > 0)) {
                    if (response[queue[0] - 1] == -1) {
                        // Set response time when a process starts executing for the first time
                        response[queue[0] - 1] = timer - processesList.get(queue[0] - 1).getArrivalTime();
                    }
                    tempBurst[queue[0] - 1]--;
                    timer++;
                    ctr++;

                    

                    // Update the ready queue until all processes arrive
                    maxProcessIndex = checkNewArrival(timer, processesList, n, maxProcessIndex, queue);
                }
                if ((tempBurst[queue[0] - 1] == 0) && (!complete[queue[0] - 1])) {
                    turn[queue[0] - 1] = timer; // turn currently stores exit times
                    complete[queue[0] - 1] = true;
                }

                // Check if CPU is idle
                boolean idle = true;
                for (int k = 0; k < n && queue[k] != 0; k++) {
                    if (!complete[queue[k] - 1]) {
                        idle = false;
                        break;
                    }
                }
                if (idle) {
                    timer++;
                    maxProcessIndex = checkNewArrival(timer, processesList, n, maxProcessIndex, queue);
                }

                // Maintain the entries of processes after each preemption in the ready queue
                queueMaintenance(queue, n);
            }
        }

        // Calculate waiting and turnaround times
        for (int i = 0; i < n; i++) {
            turn[i] = turn[i] - processesList.get(i).getArrivalTime();
            wait[i] = turn[i] - processesList.get(i).getBurstTime();
        }
        // Set waiting, turnaround, and response times for each process
        for (int i = 0; i < n; i++) {
            Process process = processesList.get(i);
            process.setWaitingTime(wait[i]);
            process.setTurnaroundTime(turn[i]);
            process.setResponseTime(response[i]);
        }
    }


    public static int checkNewArrival(int timer, List<Process> processesList, int n, int maxProcessIndex, int queue[]) {
        boolean newArrival = false;
        for (int j = maxProcessIndex; j < n; j++) {
            if (processesList.get(j).getArrivalTime() <= timer) {
                if (!isInQueue(j + 1, queue)) {
                    // Find the first available position in the queue
                    int queueIndex = 0;
                    while (queue[queueIndex] != 0) {
                        queueIndex++;
                    }
                    queue[queueIndex] = j + 1;
                    newArrival = true;
                }
                if (maxProcessIndex < j) {
                    maxProcessIndex = j;
                }
            }
        }
        return newArrival ? maxProcessIndex : Math.max(maxProcessIndex - 1, 0); // Ensure maxProcessIndex is never less than 0
    }

	    public static boolean isInQueue(int process, int queue[]) {
	        for (int i : queue) {
	            if (i == process) {
	                return true;
	            }
	        }
	        return false;
	    }

	    public static void queueMaintenance(int queue[], int n) {
	        for (int i = 0; (i < n - 1) && (queue[i + 1] != 0); i++) {
	            int temp = queue[i];
	            queue[i] = queue[i + 1];
	            queue[i + 1] = temp;
	        }
	    }

        public static void queueUpdation(int queue[], int timer, int arrival[], int n, int maxProcessIndex) {
            int zeroIndex = -1;
            for (int i = 0; i < n; i++) {
                if (queue[i] == 0) {
                    zeroIndex = i;
                    break;
                }
            }
            if (zeroIndex == -1)
                return;
            queue[zeroIndex] = maxProcessIndex + 1;
        }
        
        public static float calcAvgWaitingTime(List<Process> processesList) {
            int n = processesList.size();
            float totalWait = 0;

            for (int i = 0; i < n; i++) {
                totalWait += processesList.get(i).getTurnaroundTime() - processesList.get(i).getBurstTime();
            }

            return totalWait / n;
        }
        
        public static float calcAvgTurnTime(List<Process> processesList) {
            int n = processesList.size();
            float totalTurnaround = 0;

            for (Process process : processesList) {
                totalTurnaround += process.getTurnaroundTime();
            }

            return totalTurnaround / n;
        }

        public static float calcAvgResponseTime(List<Process> processesList) {
            int n = processesList.size();
            float totalResponse = 0;

            for (Process process : processesList) {
                totalResponse += process.getResponseTime();
            }

            return totalResponse / n;
        }
        
        
       

        public static void roundrobin(List<Process> processesList, int quantumTime) {
            int n = processesList.size();
            int[] remainingTime = new int[n];
            int[] arrival = new int[n];
            int[] burst = new int[n];

           
            for (int i = 0; i < n; i++) {
                Process process = processesList.get(i);
                remainingTime[i] = process.getBurstTime();
                arrival[i] = process.getArrivalTime();
                burst[i] = process.getBurstTime();
            }

            int currentTime = 0;
            boolean newProcessArrived = true;

            while (true) {
                boolean allProcessesFinished = true;

                for (int i = 0; i < n; i++) {
                    if (arrival[i] <= currentTime && remainingTime[i] > 0) {
                        allProcessesFinished = false;
                        newProcessArrived = true; 

                        
                        int startTime = currentTime;
                        int endTime = Math.min(startTime + quantumTime, currentTime + remainingTime[i]);

                       
                        remainingTime[i] -= endTime - startTime;

                        
                        processesList.get(i).addTimeEntry(new Process.TimeEntry(startTime, endTime));

                       
                        currentTime = endTime;
                    }
                }

                if (allProcessesFinished) {
                    break;
                }

                // If no new process arrived during the current quantum, allow the current process to continue until its quantum time expires
                if (!newProcessArrived) {
                    for (int i = 0; i < n; i++) {
                        if (arrival[i] <= currentTime && remainingTime[i] > 0) {
                           
                            int startTime = currentTime;
                            int endTime = Math.min(startTime + quantumTime, currentTime + remainingTime[i]);

                            
                            remainingTime[i] -= endTime - startTime;

                            
                            processesList.get(i).addTimeEntry(new Process.TimeEntry(startTime, endTime));

                           
                            currentTime = endTime;
                        }
                    }
                }

               
                newProcessArrived = false;
            }
        }
    
}

