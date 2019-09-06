package ipa.rmgppapp.model;

public class HourlyEntry {
    private String hour;
    private String workerId;
    private String workerName;
    private String processName;
    private String hourlyTarget;
    private String problemType;
    private String problem;
    private int quantity;
    private String entryTime;
    private String timeStamp;
    private String note;
    private int factoryCode;

    public HourlyEntry(String hour, String workerId, String workerName, String processName, String hourlyTarget, int quantity, String entryTime, String problemType, String problem,
                       String timeStamp, String note, int factoryCode) {
        this.hour = hour;
        this.workerId = workerId;
        this.workerName = workerName;
        this.processName = processName;
        this.hourlyTarget = hourlyTarget;
        this.quantity = quantity;
        this.entryTime = entryTime;
        this.problemType = problemType;
        this.problem = problem;
        this.timeStamp = timeStamp;
        this.note = note;
        this.factoryCode = factoryCode;
    }

    public int getFactoryCode() {
        return factoryCode;
    }

    public void setFactoryCode(int factoryCode) {
        this.factoryCode = factoryCode;
    }

    public String getHourlyTarget() {
        return hourlyTarget;
    }

    public void setHourlyTarget(String hourlyTarget) {
        this.hourlyTarget = hourlyTarget;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getProblemType() {
        return problemType;
    }

    public void setProblemType(String problemType) {
        this.problemType = problemType;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.hour = timeStamp;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
