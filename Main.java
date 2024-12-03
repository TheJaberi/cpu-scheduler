// Adnan Jaberi ID
//
//
//

import java.util.*;
 
class Process {
    int processID;
    int priority;
    int burstTime;
    int arrivalTime;
    int responseTime;
    int remainingTime;
    int startTime;
    int endTime;
    boolean started;
 
    public Process(int processID, int arrivalTime, int burstTime, int priority) {
        this.processID = processID;
        this.priority = priority;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.remainingTime = burstTime;
        this.startTime = -1;
        this.started = false;
    }
}
 
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
 
// Input quantum time for Round Robin
int quantum;
while (true) {
    System.out.println("Write quantum time (q) followed by ENTER:");
    try {
        quantum = Integer.parseInt(scanner.nextLine());
        if (quantum > 0) {
            break;
        } else {
            System.out.println("Please enter a positive number.");
        }
    } catch (NumberFormatException e) {
        System.out.println("Please enter a valid number.");
    }
}
 
   // Input process details
   List<Process> processes = new ArrayList<>();
   System.out.println("Enter process details (PID AT BT Priority), input 0 0 0 0 to end:");
   
   while (true) {
       int pid = 0, at = 0, bt = 0, prio = 0;
       
       try {
           System.out.print("Process ID: ");
           while (!scanner.hasNextInt()) {
               System.out.println("Error: Please enter a valid number for Process ID.");
               System.out.print("Process ID: ");
               scanner.next(); // Clear invalid input
           }
           pid = scanner.nextInt();

           System.out.print("Arrival Time: ");
           while (!scanner.hasNextInt()) {
               System.out.println("Error: Please enter a valid number for Arrival Time.");
               System.out.print("Arrival Time: ");
               scanner.next(); // Clear invalid input
           }
           at = scanner.nextInt();

           System.out.print("Burst Time: ");
           while (!scanner.hasNextInt()) {
               System.out.println("Error: Please enter a valid number for Burst Time.");
               System.out.print("Burst Time: ");
               scanner.next(); // Clear invalid input
           }
           bt = scanner.nextInt();

           System.out.print("Priority: ");
           while (!scanner.hasNextInt()) {
               System.out.println("Error: Please enter a valid number for Priority.");
               System.out.print("Priority: ");
               scanner.next(); // Clear invalid input
           }
           prio = scanner.nextInt();

           // Check for negative values
           if (pid < 0 || at < 0 || bt < 0 || prio < 0) {
               System.out.println("Error: Please enter non-negative values only.");
               continue;
           }

           // Check for zero burst time
           if (bt == 0 && (pid != 0 || at != 0 || prio != 0)) {
               System.out.println("Error: Burst time cannot be 0 for a valid process.");
               continue;
           }

           // Check for termination condition
           if (pid == 0 && at == 0 && bt == 0 && prio == 0) {
               break;
           }

           processes.add(new Process(pid, at, bt, prio));
           System.out.println("Process added successfully. Enter next process details or 0 0 0 0 to end.");

       } catch (Exception e) {
           System.out.println("Error: Invalid input. Please try again.");
           scanner.nextLine(); // Clear the buffer
       }
   }
   
   scanner.close();
 
        if (processes.isEmpty()) {
            System.out.println("No processes to schedule!");
            return;
        }
 
        // Sort processes by arrival time
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
 
        List<Process> allProcesses = new ArrayList<>(processes);
        List<String> ganttChart = new ArrayList<>();
        int currentTime = 0;
        int completedProcesses = 0;
 
        // Priority queue for ready processes
        PriorityQueue<Process> priorityQueue = new PriorityQueue<>((p1, p2) -> {
            if (p1.priority != p2.priority) {
                return p1.priority - p2.priority;
            }
            return Integer.compare(p1.arrivalTime, p2.arrivalTime);
        });
 
        while (completedProcesses < allProcesses.size()) {
            // Add arrived processes to queue
            while (!processes.isEmpty() && processes.get(0).arrivalTime <= currentTime) {
                priorityQueue.add(processes.remove(0));
            }
 
            if (priorityQueue.isEmpty()) {
                ganttChart.add(currentTime + "-idle");
                currentTime++;
                continue;
            }
 
            Process currentProcess = priorityQueue.poll();
            if (!currentProcess.started) {
                currentProcess.startTime = currentTime;
                currentProcess.responseTime = currentTime - currentProcess.arrivalTime;
                currentProcess.started = true;
            }
 
            int timeSlice = Math.min(currentProcess.remainingTime, quantum);
            currentTime += timeSlice;
            currentProcess.remainingTime -= timeSlice;
 
            // Update Gantt chart
            ganttChart.add((currentTime - timeSlice) + "-P" + currentProcess.processID);
 
            // Check for new arrivals
            while (!processes.isEmpty() && processes.get(0).arrivalTime <= currentTime) {
                priorityQueue.add(processes.remove(0));
            }
 
            if (currentProcess.remainingTime > 0) {
                priorityQueue.add(currentProcess);
            } else {
                currentProcess.endTime = currentTime;
                completedProcesses++;
            }
        }
 
        ganttChart.add(currentTime + "-");
 
     // Visualize execution sequence
     StringBuilder timeline = new StringBuilder("\nExecution Timeline:\n");
     for (String segment : ganttChart) {
         timeline.append(segment).append("-");
     }
     System.out.println(timeline.toString().substring(0, timeline.length() - 1));

     // Initialize metrics
     double sumWaitTime = 0;
     double sumTurnaroundTime = 0;
     double sumResponseTime = 0;

     // Create table header for process statistics
     String tableFormat = "| %-8s | %-12s | %-10s | %-12s | %-15s | %-13s |\n";
     String divider = "+" + "-".repeat(10) + "+" + "-".repeat(14) + "+" + 
                     "-".repeat(12) + "+" + "-".repeat(14) + "+" + 
                     "-".repeat(17) + "+" + "-".repeat(15) + "+\n";

     System.out.println("\nDetailed Process Statistics:");
     System.out.print(divider);
     System.out.printf(tableFormat, "Process", "Arrival", "CPU Burst", 
                      "Wait Time", "Turnaround Time", "Response Time");
     System.out.print(divider);

     // Calculate and display individual process metrics
     for (Process proc : allProcesses) {
         int tat = proc.endTime - proc.arrivalTime;
         int wt = tat - proc.burstTime;
         
         sumTurnaroundTime += tat;
         sumWaitTime += wt;
         sumResponseTime += proc.responseTime;

         System.out.printf(tableFormat, 
             "P" + proc.processID,
             proc.arrivalTime,
             proc.burstTime,
             wt,
             tat,
             proc.responseTime
         );
     }
     System.out.print(divider);

     // Display aggregate metrics
     int processCount = allProcesses.size();
     System.out.println("\nPerformance Metrics Summary:");
     System.out.println("═".repeat(50));
     System.out.printf("► Average Turnaround Time │ %.2f units\n", 
         sumTurnaroundTime / processCount);
     System.out.printf("► Average Response Time   │ %.2f units\n", 
         sumResponseTime / processCount);
     System.out.printf("► Average Waiting Time    │ %.2f units\n", 
         sumWaitTime / processCount);
     System.out.println("═".repeat(50));
 }
}