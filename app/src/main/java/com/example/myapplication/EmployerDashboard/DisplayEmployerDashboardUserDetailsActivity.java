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

import com.example.myapplication.Invoice.PaymentInvoiceActivity;
import com.example.myapplication.Job.Job_user;
import com.example.myapplication.Job.PostJobActivity;
import com.example.myapplication.Main2Activity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Message.DisplayUserActivity;
import com.example.myapplication.User.LoginActivity;
import com.example.myapplication.User.ProfileActivity;
import com.example.myapplication.User.SignUpActivity;
import com.example.myapplication.R;
import com.example.myapplication.User.ViewProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DisplayEmployerDashboardUserDetailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String job_userID, status, jobID, userID;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, ref;
    private TextView id, job_id, job_title, user_id, username, date;
    private Button hire, reject, pay, view;
    private SharedPreferences prefs;
    private Boolean app, emp, admin;
    private Menu menu;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_employer_dashboard_user_details);
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

        id = findViewById(R.id.tvRequestingID);
        job_id = findViewById(R.id.tvRequestingJobID);
        job_title = findViewById(R.id.tvRequestingJobTitle);
        user_id = findViewById(R.id.tvRequestingUserID);
        username = findViewById(R.id.tvRequestingUsername);
        date = findViewById(R.id.tvRequestingDate);
        hire = findViewById(R.id.btnHire);
        reject = findViewById(R.id.btnReject);
        pay = findViewById(R.id.btnPay);
        view = findViewById(R.id.btnViewProfile);

        job_userID  = getIntent().getStringExtra("job_user_id");
        status  = getIntent().getStringExtra("details_status");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Job_user").child(job_userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Job_user job_user = dataSnapshot.getValue(Job_user.class);

                jobID = job_user.getJob_ID();
                userID = job_user.getUser_ID();

                id.setText(job_userID);
                job_id.setText(jobID);
                job_title.setText(job_user.getJob_title());
                user_id.setText(userID);
                username.setText(job_user.getUsername());
                date.setText(job_user.getJob_user_date());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(status.equals("Hiring")){
            hire.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
        }else if(status.equals("Requesting")){
            pay.setVisibility(View.GONE);
        }

        hire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("job_user_status").setValue("Hiring");
                Intent intent = new Intent(DisplayEmployerDashboardUserDetailsActivity.this,
                        DisplayEmployerDashboardUserDetailsActivity.class);
                intent.putExtra("job_user_id",job_userID);
                intent.putExtra("details_status", "Hiring");
                startActivity(intent);
                Toast.makeText(DisplayEmployerDashboardUserDetailsActivity.this,
                        "Hire user successfully.", Toast.LENGTH_SHORT).show();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("job_user_status").setValue("Rejected");
                Toast.makeText(DisplayEmployerDashboardUserDetailsActivity.this,
                        "Reject user successfully.", Toast.LENGTH_SHORT).show();
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayEmployerDashboardUserDetailsActivity.this,
                        PaymentInvoiceActivity.class);
                intent.putExtra("payment_user_id",userID);
                intent.putExtra("payment_job_id",jobID);
                intent.putExtra("payment_job_user_id",job_userID);
                startActivity(intent);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayEmployerDashboardUserDetailsActivity.this,
                        ViewProfileActivity.class);
                intent.putExtra("user_ID", userID);
                intent.putExtra("attempt", "true");
                startActivity(intent);
            }
        }
        );

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
                Intent intent1 = new Intent(DisplayEmployerDashboardUserDetailsActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(DisplayEmployerDashboardUserDetailsActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(DisplayEmployerDashboardUserDetailsActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(DisplayEmployerDashboardUserDetailsActivity.this, Main2Activity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(DisplayEmployerDashboardUserDetailsActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(DisplayEmployerDashboardUserDetailsActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(DisplayEmployerDashboardUserDetailsActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user:
                Intent intent9 = new Intent(DisplayEmployerDashboardUserDetailsActivity.this, MainActivity.class);
                startActivity(intent9);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putBoolean("isEmployer", false);
                editor.putBoolean("isApplicant", true);
                editor.apply();
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(DisplayEmployerDashboardUserDetailsActivity.this, MainActivity.class);
                startActivity(intent12);
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
