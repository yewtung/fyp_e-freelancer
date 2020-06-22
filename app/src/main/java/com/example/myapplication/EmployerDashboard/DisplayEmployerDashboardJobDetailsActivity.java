package com.example.myapplication.EmployerDashboard;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.myapplication.Job.EditJobActivity;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DisplayEmployerDashboardJobDetailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String jobID, status;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private TextView job_id, emp_id, emp_name, company_name, job_title;
    private TextView job_created_date, salary, salary_type, stu, latitude, longitude;
    private Button edit, delete;
    private SharedPreferences prefs;
    private Boolean app, emp, admin;
    private Menu menu;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_employer_dashboard_job_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
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

        job_id = findViewById(R.id.tvEmployerActiveJobDetailsID);
        emp_id = findViewById(R.id.tvEmployerActiveJobDetailsEmployerID);
        emp_name = findViewById(R.id.tvEmployerActiveJobDetailsEmployerName);
        company_name = findViewById(R.id.tvEmployerActiveJobDetailsCompanyName);
        job_title = findViewById(R.id.tvEmployerActiveJobDetailsTitle);
        job_created_date = findViewById(R.id.tvEmployerActiveJobDetailsCreatedDate);
        salary = findViewById(R.id.tvEmployerActiveJobDetailsSalary);
        stu = findViewById(R.id.tvEmployerActiveJobDetailsStu);
        latitude = findViewById(R.id.tvEmployerActiveJobDetailsLatitude);
        longitude = findViewById(R.id.tvEmployerActiveJobDetailsLongitude);
        edit = findViewById(R.id.btnEdit);
        delete = findViewById(R.id.btnDelete);

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
                stu.setText(job.getJob_stu());
                latitude.setText((job.getLatitude()).toString());
                longitude.setText((job.getLongitude()).toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DisplayEmployerDashboardJobDetailsActivity.this, EditJobActivity.class);
                intent1.putExtra("edit_job_id", jobID);
                startActivity(intent1);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren())
                            snapshot.getRef().removeValue();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(DisplayEmployerDashboardJobDetailsActivity.this,
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

    public void hideNavigationItem() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        try {
            if (firebaseUser != null) {
                menu.removeItem(R.id.nav_login);
                menu.removeItem(R.id.nav_signUp);
                menu.removeItem(R.id.nav_admin_dashboard);
            } else if (firebaseUser == null) {
                menu.removeItem(R.id.nav_signOut);
                menu.removeItem(R.id.nav_admin_dashboard);
                menu.removeItem(R.id.nav_profile);
                menu.removeItem(R.id.nav_chat);
                menu.removeItem(R.id.nav_application);
                menu.removeItem(R.id.nav_invoice);
            } else if (admin.equals(true)) {
                menu.removeItem(R.id.nav_login);
                menu.removeItem(R.id.nav_signUp);
                menu.removeItem(R.id.nav_employer_dashboard);
                menu.removeItem(R.id.nav_searchJob);
                menu.removeItem(R.id.nav_postJob);
                menu.removeItem(R.id.nav_profile);
                menu.removeItem(R.id.nav_chat);
                menu.removeItem(R.id.switch_user);
            }
        } catch (Exception e) {
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id){
            case R.id.nav_login:
                Intent intent1 = new Intent(DisplayEmployerDashboardJobDetailsActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(DisplayEmployerDashboardJobDetailsActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(DisplayEmployerDashboardJobDetailsActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(DisplayEmployerDashboardJobDetailsActivity.this, Main2Activity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(DisplayEmployerDashboardJobDetailsActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(DisplayEmployerDashboardJobDetailsActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(DisplayEmployerDashboardJobDetailsActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user:
                Intent intent9 = new Intent(DisplayEmployerDashboardJobDetailsActivity.this, MainActivity.class);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putBoolean("isEmployer", false);
                editor.putBoolean("isApplicant", true);
                startActivity(intent9);
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(DisplayEmployerDashboardJobDetailsActivity.this, MainActivity.class);
                startActivity(intent12);
                break;

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
