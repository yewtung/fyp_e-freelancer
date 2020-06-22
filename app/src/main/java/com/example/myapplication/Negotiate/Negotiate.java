package com.example.myapplication.Negotiate;

public class Negotiate {

    private String negotiate_ID;
    private String negotiate_jobID;
    private String negotiate_job_title;
    private String user_ID;
    private String negotiate_old_salary;
    private String negotiate_new_salary;
    private String negotiate_date;

    public Negotiate() { }

    public Negotiate(String negotiate_ID, String negotiate_jobID, String negotiate_job_title, String user_ID, String negotiate_old_salary, String negotiate_new_salary, String negotiate_date) {
        this.negotiate_ID = negotiate_ID;
        this.negotiate_jobID = negotiate_jobID;
        this.negotiate_job_title = negotiate_job_title;
        this.user_ID = user_ID;
        this.negotiate_old_salary = negotiate_old_salary;
        this.negotiate_new_salary = negotiate_new_salary;
        this.negotiate_date = negotiate_date;
    }

    public String getNegotiate_ID() { return negotiate_ID; }

    public void setNegotiate_ID(String negotiate_ID) { this.negotiate_ID = negotiate_ID; }

    public String getNegotiate_jobID() { return negotiate_jobID; }

    public void setNegotiate_jobID(String negotiate_jobID) { this.negotiate_jobID = negotiate_jobID; }

    public String getNegotiate_negotiate_job_title() { return negotiate_job_title; }

    public void setNegotiate_negotiate_job_title(String negotiate_job_title) { this.negotiate_job_title = negotiate_job_title; }

    public String getUser_ID() { return user_ID; }

    public void setUser_ID(String user_ID) { this.user_ID = user_ID; }

    public String getNegotiate_old_salary() { return negotiate_old_salary; }

    public void setNegotiate_old_salary(String negotiate_old_salary) { this.negotiate_old_salary = negotiate_old_salary; }

    public String getNegotiate_new_salary() { return negotiate_new_salary; }

    public void setNegotiate_new_salary(String negotiate_new_salary) { this.negotiate_new_salary = negotiate_new_salary; }

    public String getNegotiate_job_title() { return negotiate_job_title; }

    public void setNegotiate_job_title(String negotiate_job_title) { this.negotiate_job_title = negotiate_job_title; }

    public String getNegotiate_date() { return negotiate_date; }

    public void setNegotiate_date(String negotiate_date) { this.negotiate_date = negotiate_date; }
}
