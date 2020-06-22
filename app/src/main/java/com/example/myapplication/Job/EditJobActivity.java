package com.example.myapplication.Job;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.EmployerDashboard.DisplayEmployerDashboardJobDetailsActivity;
import com.example.myapplication.EmployerDashboard.EmployerDashboardActivity;
import com.example.myapplication.Main2Activity;
import com.example.myapplication.Message.DisplayUserActivity;
import com.example.myapplication.User.LoginActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.User.ProfileActivity;
import com.example.myapplication.R;
import com.example.myapplication.User.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditJobActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences prefs;
    private TextView empName, editCompany, editTitle;
    private EditText editSalary,editLatitude,editLongitude,editJobDescription,editJobRequirements ;
    private RadioGroup rbJobStudent;
    private RadioButton rbYes, rbNo;
    private Button edit;
    private String jobID;
    private ProgressDialog progressDialog;
    private DatabaseReference ref;
    private FirebaseDatabase firebaseDatabase;
    private Boolean app, emp, admin;
    private Menu menu;
    private FirebaseUser firebaseUser;
    private String salary, latitude, longitude, jobDescription, jobRequirements;
    private Double double_salary, double_latitude, double_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);
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

        empName = findViewById(R.id.editEmpName);
        editCompany = findViewById(R.id.editCompanyName);
        editTitle = findViewById(R.id.editTitle);
        editSalary = findViewById(R.id.editSalary);
        editLatitude = findViewById(R.id.editLatitude);
        editLongitude = findViewById(R.id.editLongitude);
        rbJobStudent = (RadioGroup) findViewById(R.id.editRBJobStudent);
        rbYes = findViewById(R.id.editYes);
        rbNo = findViewById(R.id.editNo);
        editJobDescription = findViewById(R.id.editJobDescription);
        editJobRequirements = findViewById(R.id.editJobRequirements);
        edit = findViewById(R.id.btnEditJob);

        jobID = getIntent().getStringExtra("edit_job_id");
        progressDialog = new ProgressDialog(this);
        firebaseDatabase = FirebaseDatabase.getInstance();

        ref = firebaseDatabase.getReference("Job").child(jobID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Job job = dataSnapshot.getValue(Job.class);
                empName.setText(job.getEmployer_name());
                editCompany.setText(job.getCompany_name());
                editTitle.setText(job.getJob_title());
                editSalary.setText(job.getJob_salary());
                editLatitude.setText(job.getLatitude().toString());
                editLongitude.setText(job.getLongitude().toString());
                editJobDescription.setText(job.getJob_description());
                editJobRequirements.setText(job.getJob_requirement());

                String rbStu = job.getJob_stu();
                if(rbStu.equals("Yes"))
                    rbYes.setChecked(true);
                else
                    rbNo.setChecked(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });

    }

    private void uploadData(){
        salary = editSalary.getText().toString().trim();
        latitude = editLatitude.getText().toString().trim();
        longitude = editLongitude.getText().toString().trim();
        jobDescription = editJobDescription.getText().toString().trim();
        jobRequirements = editJobRequirements.getText().toString().trim();
        int radioStuID = rbJobStudent.getCheckedRadioButtonId();
        final String rbStu = ((RadioButton) findViewById(radioStuID)).getText().toString();

        try {
            double_salary = Double.valueOf(salary);
            double_latitude =Double.valueOf(latitude);
            double_longitude =Double.valueOf(longitude);

        } catch (NumberFormatException e) {
            double_salary = 0.00;
            double_latitude = 0.00;
            double_longitude = 0.00;
        }

        if (validate()) {
            ref.child("job_salary").setValue(String.format("%.2f", double_salary));
            ref.child("latitude").setValue(double_latitude);
            ref.child("longitude").setValue(double_longitude);
            ref.child("job_description").setValue(jobDescription);
            ref.child("job_requirement").setValue(jobRequirements);
            ref.child("job_stu").setValue(rbStu);

            Toast.makeText(EditJobActivity.this, "Job is uploaded successfully.",
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(EditJobActivity.this,
                    DisplayEmployerDashboardJobDetailsActivity.class);
            intent.putExtra("job_id", jobID);
            startActivity(intent);
        }
    }

    private boolean validate(){
        String latitude =  editLatitude.getText().toString().trim();
        String longitude =  editLongitude.getText().toString().trim();
        String salary = editSalary.getText().toString().trim();
        String jobDescription = editJobDescription.getText().toString().trim();
        String jobRequirements = editJobRequirements.getText().toString().trim();

        if(latitude.isEmpty()){
            editLatitude.setError("Please enter your location!");
            editLatitude.requestFocus();
        }else if(longitude.isEmpty()){
            editLongitude.setError("Please enter your location!");
            editLongitude.requestFocus();
        }else if(salary.isEmpty()){
            editSalary.setError("Please enter your salary!");
            editSalary.requestFocus();
        }else{
            return true;
        }
        return false;
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

        switch (id) {
            case R.id.nav_login:
                Intent intent1 = new Intent(EditJobActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(EditJobActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(EditJobActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(EditJobActivity.this, Main2Activity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(EditJobActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(EditJobActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(EditJobActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user:
                Intent intent9 = new Intent(EditJobActivity.this, MainActivity.class);
                startActivity(intent9);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putBoolean("isEmployer", false);
                editor.putBoolean("isApplicant", true);
                editor.apply();
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(EditJobActivity.this, MainActivity.class);
                startActivity(intent12);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
