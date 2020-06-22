package com.example.myapplication.EmployerDashboard;

import android.app.ProgressDialog;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Job.Job_user;
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

import java.util.ArrayList;
import java.util.List;

public class DisplayEmployerDashboardUserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private ProgressDialog progressDialog;
    private List<Job_user> requesting_job_user_List, hiring_job_user_List;
    private String job_id, user_id, title, username, job_user_id;
    Button button;
    private TextView tvJobUserID, tvStatus;
    private SharedPreferences prefs;
    private String status, myID;
    private Boolean app, emp, admin;
    private Menu menu;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_employer_dashboard_user);
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Displaying");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        firebaseDatabase = FirebaseDatabase.getInstance();
        myID = FirebaseAuth.getInstance().getUid();

        status = getIntent().getStringExtra("intentStatus");
        if(status.equals("Hiring")){
            displayHiringUser();
        }else if(status.equals("Requesting")){
            displayRequestingUser();
        }

    }


    private void addTableRow(List<Job_user> job_user_List) {

        for(Job_user job_user : job_user_List) {
            TableLayout tl = (TableLayout) findViewById(R.id.tl_employer_dashboard_user);
            final TableRow tr = new TableRow(this);

            job_user_id = job_user.getJob_user_ID();

            TextView tvJobTitle = new TextView(this);
            tvJobTitle.setText(job_user.getJob_title());
            tr.addView(tvJobTitle);
            TableRow.LayoutParams params1 = (TableRow.LayoutParams) tvJobTitle.getLayoutParams();
            params1.width=100;
            tvJobTitle.setPadding(5,5,5,5);
            tvJobTitle.setLayoutParams(params1);

            TextView tvUsername = new TextView(this);
            tvUsername.setText(job_user.getEmp_name());
            tr.addView(tvUsername);
            TableRow.LayoutParams params2 = (TableRow.LayoutParams) tvUsername.getLayoutParams();
            params2.width=120;
            tvUsername.setPadding(5,5,5,5);
            tvUsername.setLayoutParams(params2);

            TextView tvApplyDate = new TextView(this);
            tvApplyDate.setText(job_user.getJob_user_date());
            tr.addView(tvApplyDate);
            TableRow.LayoutParams params3 = (TableRow.LayoutParams) tvApplyDate.getLayoutParams();
            params3.width=80;
            tvApplyDate.setPadding(5,5,5,5);
            tvApplyDate.setLayoutParams(params3);

            tvJobUserID = new TextView(this);
            tvJobUserID.setText(job_user.getJob_user_ID());

            tvStatus = new TextView(this);
            tvStatus.setText(job_user.getJob_user_status());

            button = new Button(this);
            button.setText("View");
            tr.addView(button);
            TableRow.LayoutParams params4 = (TableRow.LayoutParams) button.getLayoutParams();
            params4.width=50;
            button.setPadding(5,5,5,5);
            button.setLayoutParams(params3);
            setOnClick(button, job_user);

            tl.addView(tr);
        }
        progressDialog.dismiss();
    }

    private void setOnClick(final Button btn, final Job_user job_user) {

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDetails = new Intent(DisplayEmployerDashboardUserActivity.this,
                        DisplayEmployerDashboardUserDetailsActivity.class);
                intentDetails.putExtra("job_user_id",job_user.getJob_user_ID());
                intentDetails.putExtra("details_status",job_user.getJob_user_status());
                startActivity(intentDetails);
            }
        });
    }

    private void displayRequestingUser(){
        requesting_job_user_List = new ArrayList<>();

        ref = firebaseDatabase.getReference().child("Job_user");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                requesting_job_user_List.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Job_user job_user = snapshot.getValue(Job_user.class);
                    if(job_user.getJob_user_status().equals("Requesting")&& job_user.getEmp_ID().equals(myID))
                        requesting_job_user_List.add(job_user);

                }if(requesting_job_user_List.size()==0){
                    Toast.makeText(DisplayEmployerDashboardUserActivity.this, "Sorry, no results are found."
                            , Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else
                     addTableRow(requesting_job_user_List);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void displayHiringUser(){
        hiring_job_user_List = new ArrayList<>();

        ref = firebaseDatabase.getReference().child("Job_user");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hiring_job_user_List.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Job_user job_user = snapshot.getValue(Job_user.class);
                    if(job_user.getJob_user_status().equals("Hiring"))
                        hiring_job_user_List.add(job_user);
                }
                if(hiring_job_user_List.size()==0){
                    Toast.makeText(DisplayEmployerDashboardUserActivity.this, "Sorry, no results are found."
                            , Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else
                    addTableRow(hiring_job_user_List);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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
                Intent intent1 = new Intent(DisplayEmployerDashboardUserActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(DisplayEmployerDashboardUserActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(DisplayEmployerDashboardUserActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(DisplayEmployerDashboardUserActivity.this, Main2Activity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(DisplayEmployerDashboardUserActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(DisplayEmployerDashboardUserActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(DisplayEmployerDashboardUserActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user:
                Intent intent9 = new Intent(DisplayEmployerDashboardUserActivity.this, MainActivity.class);
                startActivity(intent9);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putBoolean("isEmployer", false);
                editor.putBoolean("isApplicant", true);

                editor.apply();
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(DisplayEmployerDashboardUserActivity.this, MainActivity.class);
                startActivity(intent12);
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    
}
