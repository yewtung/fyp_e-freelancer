package com.example.myapplication.AdminDashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.EmployerDashboard.DisplayEmployerDashboardUserDetailsActivity;
import com.example.myapplication.EmployerDashboard.EmployerDashboardActivity;
import com.example.myapplication.Job.Job;
import com.example.myapplication.Job.PostJobActivity;
import com.example.myapplication.Main2Activity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Message.DisplayUserActivity;
import com.example.myapplication.User.LoginActivity;
import com.example.myapplication.User.ProfileActivity;
import com.example.myapplication.User.SignUpActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DisplayAdminDashboardJobDetailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String jobID;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private TextView job_id, emp_id, emp_name, company_name, job_title;
    private TextView job_created_date, salary, status,stu, latitude, longitude;
    private Button active, block, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_admin_dashboard_job_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menus = navigationView.getMenu();
        menus.removeItem(R.id.nav_login);
        menus.removeItem(R.id.nav_signUp);
        menus.removeItem(R.id.nav_employer_dashboard);
        menus.removeItem(R.id.nav_searchJob);
        menus.removeItem(R.id.nav_postJob);
        menus.removeItem(R.id.nav_profile);
        menus.removeItem(R.id.nav_chat);
        menus.removeItem(R.id.switch_user);

        job_id = findViewById(R.id.tvAdminJobDetailsID);
        emp_id = findViewById(R.id.tvAdminJobDetailsEmployerID);
        emp_name = findViewById(R.id.tvAdminJobDetailsEmployerName);
        company_name = findViewById(R.id.tvAdminJobDetailsCompanyName);
        job_title = findViewById(R.id.tvAdminJobDetailsTitle);
        job_created_date = findViewById(R.id.tvAdminJobDetailsCreatedDate);
        salary = findViewById(R.id.tvAdminJobDetailsSalary);
        status = findViewById(R.id.tvAdminJobDetailsStatus);
        stu = findViewById(R.id.tvAdminJobDetailsStu);
        latitude = findViewById(R.id.tvAdminJobDetailsLatitude);
        longitude = findViewById(R.id.tvAdminJobDetailsLongitude);
        active = findViewById(R.id.btnAdminActiveJob);
        block = findViewById(R.id.btnAdminBlockJob);
        delete = findViewById(R.id.btnAdminDeleteJob);

        jobID  = getIntent().getStringExtra("job_id");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Job").child(jobID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Job job = dataSnapshot.getValue(Job.class);
                job_id.setText(job.getJob_ID());
                emp_id.setText(job.getEmp_ID());
                emp_name.setText(job.getEmployer_name());
                company_name.setText(job.getCompany_name());
                job_title.setText(job.getJob_title());
                job_created_date.setText(job.getJob_created_date());
                salary.setText(job.getJob_salary());
                status.setText(job.getJob_status());
                stu.setText(job.getJob_stu());
                latitude.setText((job.getLatitude()).toString());
                longitude.setText((job.getLongitude()).toString());

                if(job.getJob_status().equals("Active"))
                    active.setVisibility(View.GONE);
                else if (job.getJob_status().equals("Block"))
                    block.setVisibility(View.GONE);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("job_status").setValue("Active");
                Toast.makeText(DisplayAdminDashboardJobDetailsActivity.this,
                        "Active job successfully.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DisplayAdminDashboardJobDetailsActivity.this,
                        DisplayAdminDashboardJobDetailsActivity.class);
                intent.putExtra("job_id",jobID);
                startActivity(intent);
            }
        });

        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("job_status").setValue("Block");
                Toast.makeText(DisplayAdminDashboardJobDetailsActivity.this,
                        "Block job successfully.", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(DisplayAdminDashboardJobDetailsActivity.this,
                        DisplayAdminDashboardJobDetailsActivity.class);
                intent2.putExtra("job_id",jobID);
                startActivity(intent2);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            snapshot.getRef().removeValue();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id){
            case R.id.nav_login:
                Intent intent1 = new Intent(DisplayAdminDashboardJobDetailsActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(DisplayAdminDashboardJobDetailsActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(DisplayAdminDashboardJobDetailsActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_admin_dashboard:
                Intent intent4 = new Intent(DisplayAdminDashboardJobDetailsActivity.this, AdminDashboardActivity.class);
                startActivity(intent4);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(DisplayAdminDashboardJobDetailsActivity.this, Main2Activity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(DisplayAdminDashboardJobDetailsActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(DisplayAdminDashboardJobDetailsActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(DisplayAdminDashboardJobDetailsActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user:
                Intent intent9 = new Intent(DisplayAdminDashboardJobDetailsActivity.this, MainActivity.class);
                startActivity(intent9);
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(DisplayAdminDashboardJobDetailsActivity.this, MainActivity.class);
                startActivity(intent12);
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
