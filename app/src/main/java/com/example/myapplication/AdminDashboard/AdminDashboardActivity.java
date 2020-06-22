package com.example.myapplication.AdminDashboard;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.example.myapplication.EmployerDashboard.DisplayEmployerDashboardJobDetailsActivity;
import com.example.myapplication.EmployerDashboard.DisplayEmployerDashboardUserDetailsActivity;
import com.example.myapplication.EmployerDashboard.EmployerDashboardActivity;
import com.example.myapplication.Job.Job;
import com.example.myapplication.Job.Job_user;
import com.example.myapplication.MainActivity;
import com.example.myapplication.User.LoginActivity;
import com.example.myapplication.R;
import com.example.myapplication.User.SignUpActivity;
import com.example.myapplication.User.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseFunctions firebaseFunctions;
    private CardView activeUser, inactiveUser, activeJob, inactiveJob;
    private TableLayout tlJob, tlUser;
    private List<User> userList;
    private  List<Job> jobList;
    private DatabaseReference ref, ref1;
    private FirebaseDatabase firebaseDatabase;
    private String job_user_id, job_id;
    private TextView tvUserID, tvJobID;
    private Button buttonUser, buttonJob;
    private Menu menus;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        menus = navigationView.getMenu();
        hideNavigationItem();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Displaying");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        activeUser = (CardView) findViewById(R.id.activeUser);
        activeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayIntent = new Intent(AdminDashboardActivity.this, DisplayAdminDashboardUserActivity.class);
                displayIntent.putExtra("intentStatus", "active");
                startActivity(displayIntent);
            }
        });

        inactiveUser = (CardView) findViewById(R.id.inactiveUser);
        inactiveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayIntent2 = new Intent(AdminDashboardActivity.this, DisplayAdminDashboardUserActivity.class);
                displayIntent2.putExtra("intentStatus", "inactive");
                startActivity(displayIntent2);
            }
        });

        activeJob = (CardView) findViewById(R.id.activeJob);
        activeJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayIntent3 = new Intent(AdminDashboardActivity.this, DisplayAdminDashboardJobActivity.class);
                displayIntent3.putExtra("intentStatus", "active");
                startActivity(displayIntent3);
            }
        });
        inactiveJob = (CardView) findViewById(R.id.inactiveJob);
        inactiveJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent displayIntent4 = new Intent(AdminDashboardActivity.this, DisplayAdminDashboardJobActivity.class);
                displayIntent4.putExtra("intentStatus", "inactive");
                startActivity(displayIntent4);
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        tlJob = findViewById(R.id.tableJob);
        tlUser = findViewById(R.id.tableUser);
        display();
    }

    private void display(){
        userList = new ArrayList<>();
        jobList = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference().child("User");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    if(user != null)
                        userList.add(user);
                }
                if(userList.size()==0){
                    Toast.makeText(AdminDashboardActivity.this, "Sorry, no results are found."
                            , Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }else
                    addUserTableRow(userList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        ref1 = FirebaseDatabase.getInstance().getReference().child("Job");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jobList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Job job = snapshot.getValue(Job.class);
                    if(job != null)
                        jobList.add(job);
                }
                if(jobList.size()==0){
                    Toast.makeText(AdminDashboardActivity.this, "Sorry, no results are found."
                            , Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else
                    addJobTableRow(jobList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void addUserTableRow(List<User> user_List) {

        for(User user : user_List) {
            TableLayout tl = (TableLayout) findViewById(R.id.tableUser);
            final TableRow tr = new TableRow(this);

            TextView tvUserName = new TextView(this);
            tvUserName.setText(user.getUsername());
            tvUserName.setTextSize(12);
            tr.addView(tvUserName);
            TableRow.LayoutParams params1 = (TableRow.LayoutParams) tvUserName.getLayoutParams();
            params1.width=65;
            params1.height=80;
            tvUserName.setPadding(5,5,5,5);
            tvUserName.setLayoutParams(params1);

            TextView tvAppliedDate = new TextView(this);
            tvAppliedDate.setText(user.getSignUpDate());
            tvAppliedDate.setTextSize(12);
            tr.addView(tvAppliedDate);
            TableRow.LayoutParams params3 = (TableRow.LayoutParams) tvAppliedDate.getLayoutParams();
            params3.width=70;
            tvAppliedDate.setPadding(5,5,5,5);
            tvAppliedDate.setLayoutParams(params3);

            TextView tvStatus = new TextView(this);
            tvStatus.setText(user.getStatus());
            tvStatus.setTextSize(12);
            tr.addView(tvStatus);
            TableRow.LayoutParams params4 = (TableRow.LayoutParams) tvStatus.getLayoutParams();
            params4.width=100;
            tvStatus.setPadding(5,5,5,5);
            tvStatus.setLayoutParams(params4);

            tvUserID = new TextView(this);
            tvUserID.setText(user.getId());

            buttonUser = new Button(this);
            buttonUser.setText("View");
            buttonUser.setTextSize(12);
            tr.addView(buttonUser);
            TableRow.LayoutParams params5 = (TableRow.LayoutParams) buttonUser.getLayoutParams();
            params5.width=40;
            buttonUser.setPadding(5,5,5,5);
            buttonUser.setLayoutParams(params5);
            setUserOnClick(buttonUser, user);
            tl.addView(tr);
        }
        progressDialog.dismiss();
    }

    private void addJobTableRow(List<Job> job_List) {

        for(Job job : job_List) {
            TableLayout tl = (TableLayout) findViewById(R.id.tableJob);
            final TableRow tr = new TableRow(this);

            TextView tvEmployerName = new TextView(this);
            tvEmployerName.setText(job.getEmployer_name());
            tvEmployerName.setTextSize(12);
            tr.addView(tvEmployerName);
            TableRow.LayoutParams params1 = (TableRow.LayoutParams) tvEmployerName.getLayoutParams();
            params1.width=55;
            params1.height=80;
            tvEmployerName.setPadding(5,5,5,5);
            tvEmployerName.setLayoutParams(params1);

            TextView tvJobTitle1 = new TextView(this);
            tvJobTitle1.setText(job.getJob_title());
            tvJobTitle1.setTextSize(12);
            tr.addView(tvJobTitle1);
            TableRow.LayoutParams params2 = (TableRow.LayoutParams) tvJobTitle1.getLayoutParams();
            params2.width=60;
            tvJobTitle1.setPadding(5,5,5,5);
            tvJobTitle1.setLayoutParams(params2);

            TextView tvCreatedDate = new TextView(this);
            tvCreatedDate.setText(job.getJob_created_date());
            tvCreatedDate.setTextSize(12);
            tr.addView(tvCreatedDate);
            TableRow.LayoutParams params3 = (TableRow.LayoutParams) tvCreatedDate.getLayoutParams();
            params3.width=70;
            tvCreatedDate.setPadding(5,5,5,5);
            tvCreatedDate.setLayoutParams(params3);

            TextView tvStatus1 = new TextView(this);
            tvStatus1.setText(job.getJob_status());
            tvStatus1.setTextSize(12);
            tr.addView(tvStatus1);
            TableRow.LayoutParams params4 = (TableRow.LayoutParams) tvStatus1.getLayoutParams();
            params4.width=50;
            tvStatus1.setPadding(5,5,5,5);
            tvStatus1.setLayoutParams(params4);

            tvJobID = new TextView(this);
            tvJobID.setText(job.getJob_ID());

            buttonJob = new Button(this);
            buttonJob.setText("View");
            buttonJob.setTextSize(12);
            tr.addView(buttonJob);
            TableRow.LayoutParams params5 = (TableRow.LayoutParams) buttonJob.getLayoutParams();
            params5.width=40;
            buttonJob.setPadding(5,5,5,5);
            buttonJob.setLayoutParams(params5);
            setJobOnClick(buttonJob, job);
            tl.addView(tr);
        }
        progressDialog.dismiss();
    }

    private void setUserOnClick(final Button btn, final User user) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDetails = new Intent(AdminDashboardActivity.this,
                        DisplayAdminDashboardUserDetailsActivity.class);
                intentDetails.putExtra("userID",user.getId());
                startActivity(intentDetails);
            }
        });
    }

    private void setJobOnClick(final Button btn, final Job job) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDetails = new Intent(AdminDashboardActivity.this,
                        DisplayAdminDashboardJobDetailsActivity.class);
                intentDetails.putExtra("job_id",job.getJob_ID());
                startActivity(intentDetails);
            }
        });
    }

    private void hideNavigationItem(){
        menus.removeItem(R.id.nav_login);
        menus.removeItem(R.id.nav_signUp);
        menus.removeItem(R.id.nav_employer_dashboard);
        menus.removeItem(R.id.nav_searchJob);
        menus.removeItem(R.id.nav_postJob);
        menus.removeItem(R.id.nav_profile);
        menus.removeItem(R.id.nav_chat);
        menus.removeItem(R.id.switch_user);
        menus.removeItem(R.id.nav_application);
        menus.removeItem(R.id.nav_invoice);
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

        switch(id) {
            case R.id.nav_login:
                Intent intent1 = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(AdminDashboardActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            /*case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(AdminDashboardActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;*/
            case R.id.nav_admin_dashboard:
                Intent intent4 = new Intent(AdminDashboardActivity.this, AdminDashboardActivity.class);
                startActivity(intent4);
                break;
            /*case R.id.nav_searchJob:
                Intent intent5 = new Intent(AdminDashboardActivity.this, MainActivity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(AdminDashboardActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(AdminDashboardActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(AdminDashboardActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user2:
                Intent intent9 = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(intent9);
                break;*/
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(AdminDashboardActivity.this, MainActivity.class);
                startActivity(intent12);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}