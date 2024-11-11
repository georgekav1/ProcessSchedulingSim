class Process {
    String processID;
    int arrivalTime;
    int executionTime;
    int remainingTime;
    int completionTime;
    int responseTime;
    int turnaroundTime;
    int waitingTime;

    public Process(String processID, int arrivalTime, int executionTime) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.executionTime = executionTime;
        this.remainingTime = executionTime;
    }
}