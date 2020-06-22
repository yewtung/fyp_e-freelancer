package com.example.myapplication.User;

public class User {

    private String id;
    private String username;
    private String imageURL;
    private String age;
    private String email;
    private String phone;
    private String stu;
    private String signUpDate;
    private String status;
    private double latitude;
    private double longitude;
    private double preferSalary;

    public User(){

    }

    public User(String id, String username, String imageURL, String age , String email , String phone ,
                String stu , String status , String signUpDate, double latitude, double longitude, double preferSalary){
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.age = age;
        this.email = email;
        this.phone = phone;
        this.stu = stu;
        this.signUpDate = signUpDate;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.preferSalary = preferSalary;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getImageURL() { return imageURL; }

    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    public String getAge() { return age; }

    public void setAge(String age) { this.age = age; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getStu() { return stu; }

    public void setStu(String stu) { this.stu = stu; }

    public String getSignUpDate() { return signUpDate; }

    public void setSignUpDate(String signUpDate) { this.signUpDate = signUpDate; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getPreferSalary() { return preferSalary; }

    public void setPreferSalary(double preferSalary) { this.preferSalary = preferSalary; }

}

