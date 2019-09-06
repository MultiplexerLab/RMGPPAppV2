package ipa.rmgppapp.model;

public class Worker {

    private String workerId;
    private String name;
    private String designation;
    private String department;

    public Worker(String workerId, String name, String designation, String department) {
        this.workerId = workerId;
        this.name = name;
        this.designation = designation;
        this.department = department;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }


}
