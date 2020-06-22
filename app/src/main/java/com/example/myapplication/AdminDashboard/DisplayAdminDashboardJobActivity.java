package com.example.myapplication.AdminDashboard;

import android.app.ProgressDialog;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.EmployerDashboard.DisplayEmployerDashboardJobDetailsActivity;
import com.example.myapplication.EmployerDashboard.EmployerDashboardActivity;
import com.example.myapplication.Job.Job;
import com.example.myapplication.Job.PostJobActivity;
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

import java.util.ArrayList;
import java.util.List;

public class DisplayAdminDashboardJobActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private ProgressDialog progressDialog;
    private List<Job> activeJobList, inactiveJobList;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_admin_dashboard_job);
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Displaying");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        firebaseDatabase = FirebaseDatabase.getInstance();

        status = getIntent().getStringExtra("intentStatus");
        if(status.equals("active")){
            displayActiveJob();
        }else if(status.equals("inactive")){
            displayInactiveJob();
        }
    }


    private void addTableRow(List<Job> jobList) {
        for(Job job : jobList) {
            TableLayout tl = (TableLayout) findViewById(R.id.tl_activeJob);
            TableRow tr = new TableRow(this);

            TextView tvJobTitle = new TextView(this);
            tvJobTitle.setText(job.getJob_title());
            tr.addView(tvJobTitle);
            TableRow.LayoutParams params1 = (TableRow.LayoutParams) tvJobTitle.getLayoutParams();
            params1.width=100;
            tvJobTitle.setPadding(5,5,5,5);
            tvJobTitle.setLayoutParams(params1);

            TextView tvJobEmpID = new TextView(this);
            tvJobEmpID.setText(job.getEmployer_name());
            tr.addView(tvJobEmpID);
            TableRow.LayoutParams params2 = (TableRow.LayoutParams) tvJobEmpID.getLayoutParams();
            params2.width=120;
            tvJobEmpID.setPadding(5,5,5,5);
            tvJobEmpID.setLayoutParams(params2);

            TextView tvJobStatus = new TextView(this);
            tvJobStatus.setText(job.getJob_status());
            tr.addView(tvJobStatus);
            TableRow.LayoutParams params3 = (TableRow.LayoutParams) tvJobStatus.getLayoutParams();
            params3.width=140;
            tvJobStatus.setPadding(5,5,5,5);
            tvJobStatus.setLayoutParams(params3);

            Button button = new Button(this);
            button.setText("View");
            tr.addView(button);
            TableRow.LayoutParams params4 = (TableRow.LayoutParams) button.getLayoutParams();
            params4.width=50;
            button.setPadding(5,5,5,5);
            button.setLayoutParams(params3);
            setOnClick(button, job);
            tl.addView(tr);
        }
        progressDialog.dismiss();
    }

    private void setOnClick(final Button btn, final Job job) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDetails = new Intent(DisplayAdminDashboardJobActivity.this,
                        DisplayAdminDashboardJobDetailsActivity.class);
                intentDetails.putExtra("job_id",job.getJob_ID());
                startActivity(intentDetails);
            }
        });
    }

    private void displayActiveJob(){
        activeJobList = new ArrayList<>();

        ref = firebaseDatabase.getReference().child("Job");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                activeJobList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Job job = snapshot.getValue(Job.class);
                    if(job.getJob_status().equals("Active"))
                        activeJobList.add(job);
                }
                if(activeJobList.size()==0){
                    Toast.makeText(DisplayAdminDashboardJobActivity.this, "Sorry, no results are found."
                            , Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                addTableRow(activeJobList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void displayInactiveJob(){
        inactiveJobList = new ArrayList<>();

        ref = firebaseDatabase.getReference().child("Job");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                inactiveJobList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Job job = snapshot.getValue(Job.class);
                    if(job.getJob_status().equals("Inactive"))
                        inactiveJobList.add(job);
                }
                if(inactiveJobList.size()==0){
                    Toast.makeText(DisplayAdminDashboardJobActivity.this, "Sorry, no results are found."
                            , Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                addTableRow(inactiveJobList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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
                Intent intent1 = new Intent(DisplayAdminDashboardJobActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(DisplayAdminDashboardJobActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(DisplayAdminDashboardJobActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(DisplayAdminDashboardJobActivity.this, MainActivity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(DisplayAdminDashboardJobActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(DisplayAdminDashboardJobActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(DisplayAdminDashboardJobActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(DisplayAdminDashboardJobActivity.this, MainActivity.class);
                startActivity(intent12);
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
