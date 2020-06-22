package com.example.myapplication.EmployerDashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.AdminDashboard.AdminDashboardActivity;
import com.example.myapplication.AdminDashboard.DisplayAdminDashboardUserDetailsActivity;
import com.example.myapplication.Invoice.DisplayInvoiceActivity;
import com.example.myapplication.Job.Job;
import com.example.myapplication.Job.JobListAdapter;
import com.example.myapplication.Job.Job_user;
import com.example.myapplication.Main2Activity;
import com.example.myapplication.Message.DisplayUserActivity;
import com.example.myapplication.User.LoginActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Job.PostJobActivity;
import com.example.myapplication.User.ProfileActivity;
import com.example.myapplication.R;
import com.example.myapplication.User.SignUpActivity;
import com.example.myapplication.User.User;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmployerDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CardView hiringUser, requestingUser, invoiceHistory, waitingPaymentUser, activeJob, blockJob, empActivity;
    private SharedPreferences prefs;
    private List<Job_user> jobUserList, descendingList;
    private DatabaseReference ref;
    private FirebaseDatabase firebaseDatabase;
    private String job_user_id, myID;
    private Button button;
    private ProgressDialog progressDialog;
    private TextView tvJobUserID, tvStatus;
    private Boolean app, emp, admin;
    private Menu menu;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_dashboard);
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

        myID = FirebaseAuth.getInstance().getUid();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Displaying");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        hiringUser = (CardView) findViewById(R.id.hiringUser);
        hiringUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayIntent = new Intent(EmployerDashboardActivity.this, DisplayEmployerDashboardUserActivity.class);
                displayIntent.putExtra("intentStatus", "Hiring");
                startActivity(displayIntent);
            }
        });

        requestingUser = (CardView) findViewById(R.id.requestingUser);
        requestingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayIntent2 = new Intent(EmployerDashboardActivity.this, DisplayEmployerDashboardUserActivity.class);
                displayIntent2.putExtra("intentStatus", "Requesting");
                startActivity(displayIntent2);
            }
        });

        invoiceHistory = (CardView) findViewById(R.id.invoiceHistory);
        invoiceHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayIntent3 = new Intent(EmployerDashboardActivity.this, DisplayInvoiceActivity.class);
                startActivity(displayIntent3);
            }
        });

        activeJob = (CardView) findViewById(R.id.activeJob);
        activeJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayIntent5 = new Intent(EmployerDashboardActivity.this, DisplayEmployerDashboardJobActivity.class);
                displayIntent5.putExtra("intentStatus", "Active");
                startActivity(displayIntent5);
            }
        });

        blockJob = (CardView) findViewById(R.id.blockJob);
        blockJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayIntent6 = new Intent(EmployerDashboardActivity.this, DisplayEmployerDashboardJobActivity.class);
                displayIntent6.putExtra("intentStatus", "Block");
                startActivity(displayIntent6);
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        empActivity = findViewById(R.id.empActivity);
        display();

    }
    private void display(){
        jobUserList = new ArrayList<>();
        ref = FirebaseDatabase.getInstance().getReference().child("Job_user");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jobUserList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Job_user job_user = snapshot.getValue(Job_user.class);
                    if(job_user.getEmp_ID().equals(myID))
                        jobUserList.add(job_user);
                }
                if(jobUserList.size()==0){
                    Toast.makeText(EmployerDashboardActivity.this, "Sorry, no results are found."
                            , Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else
                    addTableRow(jobUserList);

        }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void addTableRow(List<Job_user> job_user_List) {

        for(Job_user job_user : job_user_List) {
            TableLayout tl = (TableLayout) findViewById(R.id.tlEmpActivity);
            final TableRow tr = new TableRow(this);

            TextView tvApplicantName = new TextView(this);
            tvApplicantName.setText(job_user.getUsername());
            tvApplicantName.setTextSize(12);
            tr.addView(tvApplicantName);
            TableRow.LayoutParams params1 = (TableRow.LayoutParams) tvApplicantName.getLayoutParams();
            params1.width=55;
            tvApplicantName.setPadding(5,5,5,5);
            tvApplicantName.setLayoutParams(params1);

            TextView tvJobTitle = new TextView(this);
            tvJobTitle.setText(job_user.getJob_title());
            tvJobTitle.setTextSize(12);
            tr.addView(tvJobTitle);
            TableRow.LayoutParams params2 = (TableRow.LayoutParams) tvJobTitle.getLayoutParams();
            params2.width=60;
            tvJobTitle.setPadding(5,5,5,5);
            tvJobTitle.setLayoutParams(params2);

            TextView tvAppliedDate = new TextView(this);
            tvAppliedDate.setText(job_user.getJob_user_date());
            tvAppliedDate.setTextSize(12);
            tr.addView(tvAppliedDate);
            TableRow.LayoutParams params3 = (TableRow.LayoutParams) tvAppliedDate.getLayoutParams();
            params3.width=70;
            tvAppliedDate.setPadding(5,5,5,5);
            tvAppliedDate.setLayoutParams(params3);

            tvStatus = new TextView(this);
            tvStatus.setText(job_user.getJob_user_status());
            tvStatus.setTextSize(12);
            tr.addView(tvStatus);
            TableRow.LayoutParams params4 = (TableRow.LayoutParams) tvStatus.getLayoutParams();
            params4.width=50;
            tvStatus.setPadding(5,5,5,5);
            tvStatus.setLayoutParams(params4);

            button = new Button(this);
            button.setText("View");
            button.setTextSize(12);
            tr.addView(button);
            TableRow.LayoutParams params5 = (TableRow.LayoutParams) button.getLayoutParams();
            params5.width=40;
            button.setPadding(5,5,5,5);
            button.setLayoutParams(params5);
            setOnClick(button, job_user);

            tl.addView(tr);
        }
        progressDialog.dismiss();
    }

    private void setOnClick(final Button btn, final Job_user job_user) {

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDetails = new Intent(EmployerDashboardActivity.this,
                        DisplayEmployerDashboardUserDetailsActivity.class);
                intentDetails.putExtra("job_user_id",job_user.getJob_user_ID());
                intentDetails.putExtra("details_status", job_user.getJob_user_status());
                startActivity(intentDetails);
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
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_login:
                Intent intent1 = new Intent(EmployerDashboardActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(EmployerDashboardActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(EmployerDashboardActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(EmployerDashboardActivity.this, Main2Activity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(EmployerDashboardActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(EmployerDashboardActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(EmployerDashboardActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user:
                Intent intent9 = new Intent(EmployerDashboardActivity.this, MainActivity.class);
                startActivity(intent9);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putBoolean("isEmployer", false);
                editor.putBoolean("isApplicant", true);
                editor.apply();
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(EmployerDashboardActivity.this, MainActivity.class);
                startActivity(intent12);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
