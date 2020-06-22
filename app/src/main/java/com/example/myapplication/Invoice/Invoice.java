package com.example.myapplication.Invoice;


public class Invoice {

    private String invoiceID;
    private String invoice_date;
    private String invoice_employerID;
    private String invoice_employeeID;
    private String invoice_employerName;
    private String invoice_employeeName;
    private String invoice_employerAddress;
    private String invoice_employeeAddress;
    private String invoice_jobID;
    private String invoice_jobTitle;
    private Double invoice_salaryPerDay;
    private int invoice_day;
    private Double invoice_totalSalary;
    private String invoice_othersComment;
    private String invoice_status;
    private String invoice_job_userID;

    public Invoice(String invoiceID, String invoice_date, String invoice_employerID, String invoice_employeeID,
                   String invoice_employerName, String invoice_employeeName,String invoice_employerAddress,
                   String invoice_employeeAddress, String invoice_jobID, String invoice_jobTitle,
                   Double invoice_salaryPerDay, int invoice_day, Double invoice_totalSalary, String invoice_othersComment,
                   String invoice_status, String invoice_job_userID) {
        this.invoiceID = invoiceID;
        this.invoice_date = invoice_date;
        this.invoice_employerID = invoice_employerID;
        this.invoice_employeeID = invoice_employeeID;
        this.invoice_employerName = invoice_employerName;
        this.invoice_employeeName = invoice_employeeName;
        this.invoice_employerAddress = invoice_employerAddress;
        this.invoice_employeeAddress = invoice_employeeAddress;
        this.invoice_jobID = invoice_jobID;
        this.invoice_jobTitle = invoice_jobTitle;
        this.invoice_salaryPerDay = invoice_salaryPerDay;
        this.invoice_day = invoice_day;
        this.invoice_totalSalary = invoice_totalSalary;
        this.invoice_othersComment = invoice_othersComment;
        this.invoice_status = invoice_status;
        this.invoice_job_userID = invoice_job_userID;
    }

    public Invoice() {
    }

    public String getInvoiceID() { return invoiceID; }

    public void setInvoiceID(String invoiceID) { this.invoiceID = invoiceID; }

    public String getInvoice_date() { return invoice_date; }

    public void setInvoice_date(String invoice_date) { this.invoice_date = invoice_date; }

    public String getInvoice_employerID() { return invoice_employerID; }

    public void setInvoice_employerID(String invoice_employerID) { this.invoice_employerID = invoice_employerID; }

    public String getInvoice_employeeID() { return invoice_employeeID; }

    public void setInvoice_employeeID(String invoice_employeeID) { this.invoice_employeeID = invoice_employeeID; }

    public String getInvoice_employerName() { return invoice_employerName; }

    public void setInvoice_employerName(String invoice_employerName) { this.invoice_employerName = invoice_employerName; }

    public String getInvoice_employeeName() { return invoice_employeeName; }

    public void setInvoice_employeeName(String invoice_employeeName) { this.invoice_employeeName = invoice_employeeName; }

    public String getInvoice_employerAddress() { return invoice_employerAddress; }

    public void setInvoice_employerAddress(String invoice_employerAddress) { this.invoice_employerAddress = invoice_employerAddress; }

    public String getInvoice_employeeAddress() { return invoice_employeeAddress; }

    public void setInvoice_employeeAddress(String invoice_employeeAddress) { this.invoice_employeeAddress = invoice_employeeAddress; }

    public String getInvoice_jobID() { return invoice_jobID; }

    public void setInvoice_jobID(String invoice_jobID) { this.invoice_jobID = invoice_jobID; }

    public String getInvoice_jobTitle() { return invoice_jobTitle; }

    public void setInvoice_jobTitle(String invoice_jobTitle) { this.invoice_jobTitle = invoice_jobTitle; }

    public Double getInvoice_salaryPerDay() { return invoice_salaryPerDay; }

    public void setInvoice_salaryPerDay(Double invoice_salaryPerDay) { this.invoice_salaryPerDay = invoice_salaryPerDay; }

    public int getInvoice_day() { return invoice_day; }

    public void setInvoice_day(int invoice_day) { this.invoice_day = invoice_day; }

    public Double getInvoice_totalSalary() { return invoice_totalSalary; }

    public void setInvoice_totalSalary(Double invoice_totalSalary) { this.invoice_totalSalary = invoice_totalSalary; }

    public String getInvoice_othersComment() { return invoice_othersComment; }

    public void setInvoice_othersComment(String invoice_othersComment) { this.invoice_othersComment = invoice_othersComment; }

    public String getInvoice_status() { return invoice_status; }

    public void setInvoice_status(String invoice_status) { this.invoice_status = invoice_status; }

    public String getInvoice_job_userID() { return invoice_job_userID; }

    public void setInvoice_job_userID(String invoice_job_userID) { this.invoice_job_userID = invoice_job_userID; }
}
