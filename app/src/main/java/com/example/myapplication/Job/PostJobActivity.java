package com.example.myapplication.Job;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.EmployerDashboard.DisplayEmployerDashboardJobDetailsActivity;
import com.example.myapplication.EmployerDashboard.EmployerDashboardActivity;
import com.example.myapplication.Invoice.PaymentInvoiceDetailsActivity;
import com.example.myapplication.Main2Activity;
import com.example.myapplication.Message.DisplayUserActivity;
import com.example.myapplication.User.LoginActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.User.ProfileActivity;
import com.example.myapplication.R;
import com.example.myapplication.User.SignUpActivity;
import com.example.myapplication.User.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostJobActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText etCompanyName, etTitle,etSalary,etLatitude,etLongitude,etJobDescription,etJobRequirements ;
    private RadioGroup rbJobStudent;
    private Button btnPublish;
    private String employer, String_salary, companyName, title, salary, jobDescription, jobRequirements, latitude, longitude, jobID;
    private Double double_salary, double_latitude, double_longitude;
    private ProgressDialog progressDialog;
    private Job job;
    private TextView empName;
    private String employer_name, emp_ID;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    // Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, jobReference;
    private DatabaseReference myJobReference;

    private SharedPreferences prefs;
    private Boolean app, emp, admin, job_boolean;
    private Menu menu;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();
        hideNavigationItem();
        MenuItem item = menu.findItem(R.id.switch_user);

        prefs = getSharedPreferences("PrefsFile",MODE_PRIVATE);
        getPreferenceData();

        if(emp.equals(true))
            item.setTitle("Applicant");

        else if(app.equals(true)) {
            item.setTitle("Employer");
        }

        //initializing Firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        empName = (TextView)findViewById(R.id.etEmpName);
        etCompanyName = (EditText)findViewById(R.id.etCompanyName);
        etTitle = (EditText)findViewById(R.id.etTitle);
        etSalary = (EditText)findViewById(R.id.etSalary);
        etLatitude = (EditText)findViewById(R.id.etLatitude);
        etLongitude = (EditText)findViewById(R.id.etLongitude);
        etJobDescription = (EditText)findViewById(R.id.etJobDescription);
        etJobRequirements = (EditText)findViewById(R.id.etJobRequirements);
        rbJobStudent = (RadioGroup) findViewById(R.id.rbJobStudent);
        btnPublish = (Button)findViewById(R.id.btnPublish);

        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        jobReference = FirebaseDatabase.getInstance().getReference();
        myJobReference = FirebaseDatabase.getInstance().getReference();

        user = firebaseAuth.getCurrentUser();
        emp_ID = user.getUid();


        databaseReference = firebaseDatabase.getReference("User").child(emp_ID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userAcc = dataSnapshot.getValue(User.class);
                employer_name = userAcc.getUsername();
                empName.setText(employer_name);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                job_boolean = true;
                checkDuplicateData();
            }
        });

    }

    private void checkDuplicateData(){
        companyName = etCompanyName.getText().toString().trim();
        title = etTitle.getText().toString().trim();
        salary = etSalary.getText().toString().trim();
        latitude = etLatitude.getText().toString().trim();
        longitude = etLongitude.getText().toString().trim();
        jobDescription = etJobDescription.getText().toString().trim();
        jobRequirements = etJobRequirements.getText().toString().trim();
        int radioStuID = rbJobStudent.getCheckedRadioButtonId();
        final String rbStu = ((RadioButton) findViewById(radioStuID)).getText().toString();
        final String status = "Inactive";
        final String date = getDateTime();

        try {
            double_salary = Double.valueOf(salary);
            double_latitude =Double.valueOf(latitude);
            double_longitude =Double.valueOf(longitude);

        } catch (NumberFormatException e) {
            double_salary = 0.00;
            double_latitude = 0.00;
            double_longitude = 0.00;
        }

        DatabaseReference jobRef = FirebaseDatabase.getInstance().getReference("Job");
        jobRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Job job = snapshot.getValue(Job.class);
                    if (job.getJob_title().equals(title) && job.getEmp_ID().equals(emp_ID)) {
                        job_boolean = false;
                        etTitle.setError("Job title is existed, please enter another job title.");
                        etTitle.requestFocus();
                        progressDialog.dismiss();
                    }
                }
                if (validate() && job_boolean.equals(true)) {
                    jobID = databaseReference.push().getKey();

                    job = new Job(jobID, employer_name, companyName, title, String.format("%.2f",
                            double_salary), "0.00", double_latitude, double_longitude, rbStu,
                            jobDescription, jobRequirements, status, date, emp_ID);
                    jobReference.child("Job").child(jobID).setValue(job);

                    Toast.makeText(PostJobActivity.this,
                            "Job is uploaded successfully, please wait administrator to approve.",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(PostJobActivity.this,
                            DisplayEmployerDashboardJobDetailsActivity.class);
                    intent.putExtra("job_id", jobID);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private boolean validate(){
        String companyName = etCompanyName.getText().toString().trim();
        String title = etTitle.getText().toString().trim();
        String latitude =  etLatitude.getText().toString().trim();
        String longitude =  etLongitude.getText().toString().trim();
        String salary = etSalary.getText().toString().trim();
        String jobDescription = etJobDescription.getText().toString().trim();
        String jobRequirements = etJobRequirements.getText().toString().trim();

        if(companyName.isEmpty()) {
            etCompanyName.setError("Please enter your company name!");
            etCompanyName.requestFocus();
        }else if(title.isEmpty()){
            etTitle.setError("Please enter your job title!");
            etTitle.requestFocus();
        }else if(latitude.isEmpty()){
            etLatitude.setError("Please enter your location!");
            etLatitude.requestFocus();
        }else if(longitude.isEmpty()){
            etLongitude.setError("Please enter your location!");
            etLongitude.requestFocus();
        }else if(salary.isEmpty()){
            etSalary.setError("Please enter your salary!");
            etSalary.requestFocus();
        }else if(jobDescription.isEmpty() ){
            etJobDescription.setError("Please enter your job description!");
            etJobDescription.requestFocus();
        }else if(jobRequirements.isEmpty()){
            etJobRequirements.setError("Please enter your job requirement!");
            etJobRequirements.requestFocus();

        }else{
            return true;
        }
        return false;

    }

    //get the current date and time
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void getPreferenceData(){
        SharedPreferences sp =getSharedPreferences("PrefsFile",MODE_PRIVATE);

        if(sp.contains("isEmployer")){
            emp=sp.getBoolean("isEmployer", false);
        }
        if(sp.contains("isApplicant")){
            app=sp.getBoolean("isApplicant", false);
        }
        if(sp.contains("isAdmin")){
            admin=sp.getBoolean("isAdmin", false);
        }
    }

    public void hideNavigationItem(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        try {
            if (firebaseUser != null){
                menu.removeItem(R.id.nav_login);
                menu.removeItem(R.id.nav_signUp);
                menu.removeItem(R.id.nav_admin_dashboard);
            }else if(firebaseUser ==null){
                menu.removeItem(R.id.nav_signOut);
                menu.removeItem(R.id.nav_admin_dashboard);
                menu.removeItem(R.id.nav_profile);
                menu.removeItem(R.id.nav_chat);
                menu.removeItem(R.id.nav_application);
                menu.removeItem(R.id.nav_invoice);
                menu.removeItem(R.id.nav_employer_dashboard);
                menu.removeItem(R.id.nav_postJob);
            }else if(admin.equals(true)){
                menu.removeItem(R.id.nav_login);
                menu.removeItem(R.id.nav_signUp);
                menu.removeItem(R.id.nav_employer_dashboard);
                menu.removeItem(R.id.nav_searchJob);
                menu.removeItem(R.id.nav_postJob);
                menu.removeItem(R.id.nav_profile);
                menu.removeItem(R.id.nav_chat);
                menu.removeItem(R.id.switch_user);
            }
        }catch(Exception e){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id){
            case R.id.nav_login:
                Intent intent1 = new Intent(PostJobActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(PostJobActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(PostJobActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(PostJobActivity.this, Main2Activity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(PostJobActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(PostJobActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(PostJobActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user:
                Intent intent9 = new Intent(PostJobActivity.this, MainActivity.class);
                startActivity(intent9);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putBoolean("isEmployer", false);
                editor.putBoolean("isApplicant", true);
                editor.apply();
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(PostJobActivity.this, MainActivity.class);
                startActivity(intent12);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
