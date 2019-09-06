package ipa.rmgppapp.model;

public class LineEntry {
    private String hour;
    private String buyer;
    private String styleNo;
    private String orderNumber;
    private String color;
    private String lineInput;
    private String lineOutput;
    private String problemType;
    private String problem;
    private String status;
    private String entryTime;
    private String timeStamp;

    public LineEntry(String buyer, String styleNo, String orderNumber, String color, String hour, String lineOutput, String problemType, String problem, String status, String entryTime, String timeStamp) {
        this.hour = hour;
        this.buyer = buyer;
        this.orderNumber = orderNumber;
        this.color = color;
        this.lineOutput = lineOutput;
        this.problemType = problemType;
        this.status = status;
        this.styleNo = styleNo;
        this.entryTime = entryTime;
        this.problem = problem;
        this.timeStamp = timeStamp;
    }

    public LineEntry(String buyer, String styleNo, String orderNumber, String color, String hour, String lineInput, String entryTime, String timeStamp) {
        this.buyer = buyer;
        this.orderNumber = orderNumber;
        this.color = color;
        this.hour = hour;
        this.styleNo = styleNo;
        this.lineInput = lineInput;
        this.entryTime = entryTime;
        this.timeStamp = timeStamp;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getStyleNo() {
        return styleNo;
    }

    public void setStyleNo(String styleNo) {
        this.styleNo = styleNo;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getLineInput() {
        return lineInput;
    }

    public void setLineInput(String lineInput) {
        this.lineInput = lineInput;
    }

    public String getLineOutput() {
        return lineOutput;
    }

    public void setLineOutput(String lineOutput) {
        this.lineOutput = lineOutput;
    }

    public String getProblemType() {
        return problemType;
    }

    public void setProblemType(String problemType) {
        this.problemType = problemType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }
}
