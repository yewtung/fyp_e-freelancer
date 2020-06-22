package com.example.myapplication.Job;

public class Job {
    private String job_ID;
    private String employer_name;
    private String company_name;
    private String job_title;
    private String job_salary;
    private String job_new_salary;
    private double latitude;
    private double longitude;
    private String job_stu;
    private String job_description;
    private String job_requirement;
    private String job_status;
    private String job_created_date;
    private String emp_ID;

    public Job(){

    }

    public Job(String job_ID, String employer_name, String company_name, String job_title, String job_salary, String job_new_salary,
                Double latitude, Double longitude, String job_stu, String job_description, String job_requirement,
               String job_status, String job_created_date, String emp_ID) {
        this.job_ID = job_ID;
        this.employer_name = employer_name;
        this.company_name = company_name;
        this.job_title = job_title;
        this.job_salary = job_salary;
        this.job_new_salary = job_new_salary;
        this.latitude = latitude;
        this.longitude = longitude;
        this.job_stu = job_stu;
        this.job_description = job_description;
        this.job_requirement = job_requirement;
        this.job_status = job_status;
        this.job_created_date = job_created_date;
        this.emp_ID = emp_ID;
    }


    public String getJob_ID() {
        return job_ID;
    }

    public void setJob_ID(String job_ID) {
        this.job_ID = job_ID;
    }

    public String getEmployer_name() { return employer_name; }

    public void setEmployer_name(String employer_name) { this.employer_name = employer_name; }

    public String getCompany_name() { return company_name; }

    public void setCompany_name(String company_name) { this.company_name = company_name; }

    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public String getJob_salary() {
        return job_salary;
    }

    public void setJob_salary(String job_salary) {
        this.job_salary = job_salary;
    }

    public String getJob_new_salary() {
        return job_new_salary;
    }

    public void setJob_new_salary(String job_new_salary) {
        this.job_new_salary = job_new_salary;
    }

    public String getJob_description() {
        return job_description;
    }

    public void setJob_description(String job_description) { this.job_description = job_description; }

    public String getJob_requirement() {
        return job_requirement;
    }

    public void setJob_requirement(String job_requirement) { this.job_requirement = job_requirement; }

    public String getJob_stu() {
        return job_stu;
    }

    public void setJob_stu(String job_stu) {
        this.job_stu = job_stu;
    }

    public String getJob_status() {
        return job_status;
    }

    public void setJob_status(String job_status) {
        this.job_status = job_status;
    }

    public String getJob_created_date() {
        return job_created_date;
    }

    public void setJob_created_date(String job_created_date) { this.job_created_date = job_created_date; }

    public String getEmp_ID() {return emp_ID;}

    public void setEmp_ID(String emp_ID) { this.emp_ID = emp_ID; }

    public Double getLatitude() { return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

}
