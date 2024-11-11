import java.util.*;

public class Main {
    private Scanner scanner = new Scanner(System.in);
    private List<Process> processList = new ArrayList<>();

    public static void main(String[] args) {
        Main sim = new Main();
        sim.receiveDetails();
        sim.selectAlg();
        sim.closeScanner();
    }

    // Method to receive process details
    public void receiveDetails() {
        int numProcesses = 0;

        // Prompt the user to enter the number of processes between 1 and 5
        while (numProcesses < 1 || numProcesses > 5) {
            System.out.println("Enter the number of processes (1-5):");
            try {
                numProcesses = scanner.nextInt();
                if (numProcesses < 1 || numProcesses > 5) {
                    System.out.println("Invalid input. Please enter a number between 1 and 5.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine();
            }
        }

        // Loop to get details for each process
        for (int i = 0; i < numProcesses; i++) {
            System.out.println("--------------------------");
            System.out.println("Enter details for Process " + (i + 1));
            scanner.nextLine();

            // Get the process ID
            System.out.print("Process ID: ");
            String processID = scanner.nextLine();

            // Get the arrival time
            int arrivalTime = -1;
            while (arrivalTime < 0) {
                System.out.print("Arrival Time: ");
                try {
                    arrivalTime = scanner.nextInt();
                    if (arrivalTime < 0) {
                        System.out.println("Arrival time cannot be negative. Please enter a valid integer.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid integer.");
                    scanner.nextLine();
                }
            }

            // Get the execution time
            int executionTime = -1;
            while (executionTime < 0) {
                System.out.print("Execution Time: ");
                try {
                    executionTime = scanner.nextInt();
                    if (executionTime < 0) {
                        System.out.println("Execution time cannot be negative. Please enter a valid integer.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid integer.");
                    scanner.nextLine();
                }
            }

            // Add the process to the list
            processList.add(new Process(processID, arrivalTime, executionTime));
        }
    }

    // Method to select scheduling algorithm
    public void selectAlg() {
        int choice;

        // Prompt the user to select the scheduling algorithm
        System.out.println("Select which algorithm to use:");
        System.out.println("1. First In, First Out (FIFO)");
        System.out.println("2. Shortest Job First (SJF)");

        try {
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("You selected First In, First Out (FIFO)");
                    System.out.println("--------------------------");
                    runFIFO();
                    break;
                case 2:
                    System.out.println("You selected Shortest Job First (SJF)");
                    System.out.println("--------------------------");
                    runSJF();
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid integer.");
            scanner.nextLine();
        }
    }

    // First In, First Out (FIFO) algorithm
    public void runFIFO() {
        int currentTime = 0;
        int totalExecutionTime = 0;
        double cpuUtilization;

        processList.sort(Comparator.comparingInt(p -> p.arrivalTime));

        for (Process process : processList) {
            if (currentTime < process.arrivalTime) {
                currentTime = process.arrivalTime; // CPU idle time
            }

            // Calculate times
            process.responseTime = currentTime - process.arrivalTime;
            currentTime += process.executionTime;
            process.completionTime = currentTime;
            process.turnaroundTime = process.completionTime - process.arrivalTime;
            process.waitingTime = process.turnaroundTime - process.executionTime;

            totalExecutionTime += process.executionTime;
        }

        // Calculate CPU utilization
        cpuUtilization = (double) totalExecutionTime / currentTime * 100;

        displayOutput(cpuUtilization);
    }

    // Shortest Job First (SJF) algorithm
    public void runSJF() {
        int currentTime = 0;
        int totalExecutionTime = 0;
        double cpuUtilization;

        List<Process> remainingProcesses = new ArrayList<>(processList);
        List<Process> readyQueue = new ArrayList<>();
        
        while (!remainingProcesses.isEmpty() || !readyQueue.isEmpty()) {
            // Move processes that have arrived by the current time to the ready queue
            for (Iterator<Process> iterator = remainingProcesses.iterator(); iterator.hasNext();) {
                Process process = iterator.next();
                if (process.arrivalTime <= currentTime) {
                    readyQueue.add(process);
                    iterator.remove();
                }
            }

            // If there are no processes ready, advance time to the next process arrival
            if (readyQueue.isEmpty()) {
                remainingProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));
                currentTime = remainingProcesses.get(0).arrivalTime;
                continue;
            }

            // Select the process with the shortest execution time from the ready queue
            Process nextProcess = readyQueue.stream()
                    .min(Comparator.comparingInt(p -> p.executionTime))
                    .orElseThrow();

            // Calculate times
            nextProcess.responseTime = currentTime - nextProcess.arrivalTime;
            currentTime += nextProcess.executionTime;
            nextProcess.completionTime = currentTime;
            nextProcess.turnaroundTime = nextProcess.completionTime - nextProcess.arrivalTime;
            nextProcess.waitingTime = nextProcess.turnaroundTime - nextProcess.executionTime;

            totalExecutionTime += nextProcess.executionTime;

            // Remove the process from the ready queue after execution
            readyQueue.remove(nextProcess);
        }

        // Calculate CPU utilization
        cpuUtilization = (double) totalExecutionTime / currentTime * 100;

        displayOutput(cpuUtilization);
    }

    // Method to display output statistics
    public void displayOutput(double cpuUtilization) {
        int totalResponseTime = 0;
        int totalTurnaroundTime = 0;

        System.out.println("PROCESS EXECUTION DETAILS:");
        for (Process process : processList) {
            System.out.println("Process ID: " + process.processID);
            System.out.println("Arrival Time: " + process.arrivalTime);
            System.out.println("Execution Time: " + process.executionTime);
            System.out.println("Turnaround Time: " + process.turnaroundTime);
            System.out.println("Response Time: " + process.responseTime);
            System.out.println("Waiting Time: " + process.waitingTime);
            System.out.println("Process Completion Time: " + process.completionTime);
            System.out.println("--------------------------");

            totalResponseTime += process.responseTime;
            totalTurnaroundTime += process.turnaroundTime;
        }

        // Calculate and display averages
        double averageResponseTime = (double) totalResponseTime / processList.size();
        double averageTurnaroundTime = (double) totalTurnaroundTime / processList.size();

        System.out.println("Average Response Time: " + averageResponseTime);
        System.out.println("Average Turnaround Time: " + averageTurnaroundTime);
        System.out.println("CPU Utilization: " + cpuUtilization + "%");
    }

    public void closeScanner() {
        scanner.close();
    }
}