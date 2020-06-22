package com.example.myapplication.UserHistory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.AdminDashboard.AdminDashboardActivity;
import com.example.myapplication.EmployerDashboard.EmployerDashboardActivity;
import com.example.myapplication.Invoice.DisplayInvoiceActivity;
import com.example.myapplication.Job.Job_user;
import com.example.myapplication.Job.PostJobActivity;
import com.example.myapplication.Main2Activity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Message.DisplayUserActivity;
import com.example.myapplication.R;
import com.example.myapplication.User.LoginActivity;
import com.example.myapplication.User.ProfileActivity;
import com.example.myapplication.User.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DisplayApplicationHistoryDetailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private TextView jobTitle, empName, companyName, salary, date, status;
    private Boolean emp, app, admin;
    private Menu menu;
    private FirebaseUser firebaseUser;
    private String userID, jobUserID, jobUser_status;
    private RatingBar ratingBar;
    private Button cancel, rate;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_application_history_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
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

        userID = FirebaseAuth.getInstance().getUid();

        jobTitle = findViewById(R.id.tvApplicationJobTitle);
        empName = findViewById(R.id.tvApplicationEmpName);
        salary = findViewById(R.id.tvApplicationSalary);
        date = findViewById(R.id.tvApplicationDate);
        status = findViewById(R.id.tvApplicationStatus);
        cancel = findViewById(R.id.btnCancel);

        jobUserID = getIntent().getStringExtra("application_job_user_ID");
        jobUser_status = getIntent().getStringExtra("application_job_user_status");

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference("Job_user").child(jobUserID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Job_user job_user = dataSnapshot.getValue(Job_user.class);

                double application_salary = Double.parseDouble(job_user.getJob_user_salary());

                jobTitle.setText(job_user.getJob_title());
                empName.setText(job_user.getEmp_name());
                salary.setText(String.format("RM%.2f", application_salary));
                date.setText(job_user.getJob_user_date());
                status.setText(job_user.getJob_user_status());

                if(jobUser_status.equals("Payment is completed"))
                    cancel.setVisibility(View.GONE);
                else
                    cancel.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren())
                            snapshot.getRef().removeValue();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(DisplayApplicationHistoryDetailsActivity.this,
                                databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

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
                Intent intent1 = new Intent(DisplayApplicationHistoryDetailsActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(DisplayApplicationHistoryDetailsActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(DisplayApplicationHistoryDetailsActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_admin_dashboard:
                Intent intent4 = new Intent(DisplayApplicationHistoryDetailsActivity.this, AdminDashboardActivity.class);
                startActivity(intent4);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(DisplayApplicationHistoryDetailsActivity.this, MainActivity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(DisplayApplicationHistoryDetailsActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(DisplayApplicationHistoryDetailsActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(DisplayApplicationHistoryDetailsActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user:
                Intent intent9 = new Intent(DisplayApplicationHistoryDetailsActivity.this, Main2Activity.class);
                startActivity(intent9);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putBoolean("isEmployer", true);
                editor.putBoolean("isApplicant", false);
                editor.apply();
                break;
            case R.id.nav_invoice:
                Intent intent10 = new Intent(DisplayApplicationHistoryDetailsActivity.this, DisplayInvoiceActivity.class);
                startActivity(intent10);
                break;
            case R.id.nav_application:
                Intent intent11 = new Intent(DisplayApplicationHistoryDetailsActivity.this, DisplayApplicationHistoryActivity.class);
                startActivity(intent11);
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(DisplayApplicationHistoryDetailsActivity.this, MainActivity.class);
                startActivity(intent12);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
