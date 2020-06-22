package com.example.myapplication.Job;

public class Job_user {
    private String job_user_ID;
    private String job_ID;
    private String job_title;
    private String emp_ID;
    private String user_ID;
    private String emp_name;
    private String username;
    private String job_user_status;
    private String job_user_date;
    private String job_user_salary;
    private String rating;

    public Job_user() {
    }

    public Job_user(String job_user_ID, String job_ID, String job_title, String emp_ID, String emp_name, String user_ID,
                    String username, String job_user_status, String job_user_date, String job_user_salary) {
        this.job_user_ID = job_user_ID;
        this.job_ID = job_ID;
        this.job_title = job_title;
        this.emp_ID = emp_ID;
        this.user_ID = user_ID;
        this.emp_name = emp_name;
        this.username = username;
        this.job_user_status = job_user_status;
        this.job_user_date = job_user_date;
        this.job_user_salary = job_user_salary;

    }

    public String getJob_user_ID() { return job_user_ID; }

    public void setJob_user_ID(String job_user_ID) { this.job_user_ID = job_user_ID; }

    public String getJob_ID() { return job_ID; }

    public void setJob_ID(String job_ID) { this.job_ID = job_ID; }

    public String getJob_title() { return job_title; }

    public void setJob_title(String job_title) { this.job_title = job_title; }

    public String getEmp_ID() { return emp_ID; }

    public void setEmp_ID(String emp_ID) { this.emp_ID = emp_ID; }

    public String getUser_ID() { return user_ID; }

    public void setUser_ID(String user_ID) { this.user_ID = user_ID; }

    public String getEmp_name() { return emp_name; }

    public void setEmp_name(String emp_name) { this.emp_name = emp_name; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getJob_user_status() { return job_user_status; }

    public void setJob_user_status(String job_user_status) { this.job_user_status = job_user_status; }

    public String getJob_user_date() { return job_user_date; }

    public void setJob_user_date(String job_user_date) { this.job_user_date = job_user_date; }

    public String getJob_user_salary() { return job_user_salary; }

    public void setJob_user_salary(String job_user_salary) { this.job_user_salary = job_user_salary; }

}
