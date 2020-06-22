package com.example.myapplication.Job;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.AdminDashboard.AdminDashboardActivity;
import com.example.myapplication.EmployerDashboard.EmployerDashboardActivity;
import com.example.myapplication.Invoice.DisplayInvoiceActivity;
import com.example.myapplication.Main2Activity;
import com.example.myapplication.Message.DisplayUserActivity;
import com.example.myapplication.Message.MessageActivity;
import com.example.myapplication.User.LoginActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.User.ProfileActivity;
import com.example.myapplication.R;
import com.example.myapplication.User.SignUpActivity;
import com.example.myapplication.User.User;
import com.example.myapplication.UserHistory.DisplayApplicationHistoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JobDetailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView jobEmpName,jobCompanyName,jobTitle,jobSalary,jobLatitude, jobLongitude, jobDescription;
    private TextView jobRequirements,jobStudent;
    private TextView title, title2;
    private Job_user job_user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference jobRef, ref, userRef, job_userRef;
    private Button apply, negotiate, ok, cancel;
    private String emp_name, emp_id, jobID, job_title, myID;
    private ProgressDialog progressDialog;
    private FirebaseUser user;
    private Double salary;
    private SharedPreferences prefs;
    private Boolean app, emp, admin, apply_boolean;
    private Menu menu;
    private FirebaseUser firebaseUser;
    private String username;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);
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
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        hideNavigationItem();
        MenuItem item = menu.findItem(R.id.switch_user);

        prefs = getSharedPreferences("PrefsFile",MODE_PRIVATE);
        getPreferenceData();

        if(emp.equals(true))
            item.setTitle("Applicant");

        else if(app.equals(true)) {
            item.setTitle("Employer");
        }

        jobEmpName = findViewById(R.id.viewDetailsEmpName);
        jobCompanyName = findViewById(R.id.viewDetailsCompanyName);
        jobTitle = findViewById(R.id.viewDetailsTitle);
        jobSalary = findViewById(R.id.viewDetailsSalary);
        jobLatitude = findViewById(R.id.viewDetailsLatitude);
        jobLongitude = findViewById(R.id.viewDetailsLongitude);
        jobStudent = findViewById(R.id.viewDetailsStudent);
        jobDescription = findViewById(R.id.viewDetailsJobDescription);
        jobRequirements = findViewById(R.id.viewDetailsJobRequirements);
        apply = findViewById(R.id.btnApplyJob);
        negotiate = findViewById(R.id.btnNegotiate);
        cancel = findViewById(R.id.btnCancelJob);

        firebaseDatabase = FirebaseDatabase.getInstance();
        jobID = getIntent().getStringExtra("jobDetails_ID");

        jobRef = firebaseDatabase.getReference("Job").child(jobID);
        jobRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Job job = dataSnapshot.getValue(Job.class);

                job_title = job.getJob_title();
                emp_name = job.getEmployer_name();
                emp_id = job.getEmp_ID();
                salary = Double.parseDouble(job.getJob_salary());
                jobEmpName.setText(emp_name);
                jobCompanyName.setText(job.getCompany_name());
                jobTitle.setText(job_title);
                jobSalary.setText(String.format("RM" +" %.2f",salary));
                jobLatitude.setText((job.getLatitude()).toString());
                jobLongitude.setText((job.getLongitude()).toString());
                jobStudent.setText(job.getJob_stu());
                jobDescription.setText(job.getJob_description());
                jobRequirements.setText(job.getJob_requirement());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        try{
            myID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            userRef = firebaseDatabase.getReference("User").child(myID);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    username = user.getUsername();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            checkDuplicateData();

        }catch(Exception e){
            myID = "null";
            cancel.setVisibility(View.GONE);
            apply.setVisibility(View.VISIBLE);
        }

        dialog = new Dialog(this);
        negotiate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser != null) {
                    Intent intentChat = new Intent(JobDetailsActivity.this, MessageActivity.class);
                    intentChat.putExtra("chatAttempt", "false");
                    intentChat.putExtra("chatReceiverID", emp_id);
                    startActivity(intentChat);
                }else if(firebaseUser == null) {
                    dialog.setContentView(R.layout.pop_up_chat);
                    title = (TextView) dialog.findViewById(R.id.title);
                    title2 = (TextView) dialog.findViewById(R.id.title2);
                    ok = (Button) dialog.findViewById(R.id.btnOK);

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));;
                    dialog.show();
                }
            }
        });

        ref = FirebaseDatabase.getInstance().getReference();
        job_userRef = FirebaseDatabase.getInstance().getReference("Job_user");
        progressDialog = new ProgressDialog(this);
        apply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (firebaseUser != null) {
                    String job_userID = ref.push().getKey();
                    String date = getDateTime();
                    myID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    job_user = new Job_user(job_userID, jobID, job_title , emp_id, emp_name, myID,
                            username,  "Requesting", date, String.format(" %.2f",salary));
                    ref.child("Job_user").child(job_userID).setValue(job_user);

                    Toast.makeText(JobDetailsActivity.this,
                            "Apply request is successfully sent to employer, please wait to approve.",
                            Toast.LENGTH_LONG).show();

                }else if(firebaseUser == null) {
                    dialog.setContentView(R.layout.pop_up_chat);
                    title = (TextView) dialog.findViewById(R.id.title);
                    title2 = (TextView) dialog.findViewById(R.id.title2);
                    ok = (Button) dialog.findViewById(R.id.btnOK);

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                    dialog.show();
                }
            }

        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                job_userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Job_user job_user = snapshot.getValue(Job_user.class);
                            if (job_user.getJob_ID().equals(jobID) &&
                                    job_user.getUser_ID().equals(myID)) {
                                snapshot.getRef().removeValue();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

    }

    private void checkDuplicateData(){
        job_userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Job_user job_user = snapshot.getValue(Job_user.class);
                    if (job_user.getJob_ID().equals(jobID) && job_user.getUser_ID().equals(myID)) {
                        apply_boolean = false;
                        apply.setVisibility(View.GONE);
                        cancel.setVisibility(View.VISIBLE);
                    }
                    else {
                        apply_boolean = true;
                        cancel.setVisibility(View.GONE);
                        apply.setVisibility(View.VISIBLE);
                    }
                }
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

    public void hideNavigationItem(){
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

    //get the current date and time
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
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
                Intent intent1 = new Intent(JobDetailsActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(JobDetailsActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(JobDetailsActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_admin_dashboard:
                Intent intent4 = new Intent(JobDetailsActivity.this, AdminDashboardActivity.class);
                startActivity(intent4);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(JobDetailsActivity.this, MainActivity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(JobDetailsActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(JobDetailsActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(JobDetailsActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user:
                SharedPreferences.Editor editor=prefs.edit();
                editor.putBoolean("isEmployer", true);
                editor.putBoolean("isApplicant", false);
                editor.apply();

                Intent intent9 = new Intent(JobDetailsActivity.this, Main2Activity.class);
                startActivity(intent9);

                break;
            case R.id.nav_invoice:
                Intent intent10 = new Intent(JobDetailsActivity.this, DisplayInvoiceActivity.class);
                startActivity(intent10);
                break;
            case R.id.nav_application:
                Intent intent11 = new Intent(JobDetailsActivity.this, DisplayApplicationHistoryActivity.class);
                startActivity(intent11);
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(JobDetailsActivity.this, MainActivity.class);
                startActivity(intent12);
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu_item, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                /*Intent intentChat = new Intent(JobDetailsActivity.this, MessageActivity.class);
                intentChat.putExtra("chatJobID",jobID);
                intentChat.putExtra("chatReceiverID",emp_id);
                startActivity(intentChat);*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
