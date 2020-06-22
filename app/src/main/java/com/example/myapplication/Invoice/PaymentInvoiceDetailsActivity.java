package com.example.myapplication.Invoice;

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
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.example.myapplication.AdminDashboard.AdminDashboardActivity;
import com.example.myapplication.EmployerDashboard.EmployerDashboardActivity;
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

public class PaymentInvoiceDetailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private SharedPreferences prefs;
    private Boolean emp, app, admin;
    private TextView invoiceID, invoiceDate, invoiceStatus, employerID, employerName, employerAddress, employeeID, employeeName;
    private TextView employeeAddress, jobTitle, salaryPerDay, day, totalSalary, othersComment;
    private DatabaseReference invoiceRef, databaseReference;
    private String empID, appID, jobID, invoice_ID, job_userID,  total_salary;
    private FirebaseDatabase firebaseDatabase;
    private Button btnConfirm;
    private RequestQueue requestQueue;
    private String url = "https://fcm.googleapis.com/fcm/send";
    private Menu menu;
    private FirebaseUser firebaseUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_invoice_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        prefs=getSharedPreferences("PrefsFile",MODE_PRIVATE);
        getPreferenceData();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();
        hideNavigationItem();
        MenuItem item = menu.findItem(R.id.switch_user);

        invoiceID = findViewById(R.id.tvInvoiceID);
        invoiceDate = findViewById(R.id.tvInvoiceDate);
        invoiceStatus = findViewById(R.id.tvInvoiceStatus);
        employerID = findViewById(R.id.tvEmpID);
        employerName = findViewById(R.id.tvEmpName);
        employerAddress = findViewById(R.id.tvEmpAddress);
        employeeID = findViewById(R.id.tvAppID);
        employeeName = findViewById(R.id.tvAppName);
        employeeAddress = findViewById(R.id.tvAppAddress);
        jobTitle = findViewById(R.id.tvJobTitle2);
        salaryPerDay = findViewById(R.id.tvSalary2);
        day = findViewById(R.id.tvDay2);
        totalSalary = findViewById(R.id.tvTotalSalary2);
        othersComment = findViewById(R.id.tvOthersComment2);
        btnConfirm = findViewById(R.id.btnConfirm);

        if(emp.equals(true))
            item.setTitle("Applicant");

        else if(app.equals(true)) {
            item.setTitle("Employer");
            btnConfirm.setVisibility(View.GONE);
        }

        invoice_ID = getIntent().getStringExtra("intent_invoice_ID");
        invoiceID.setText(invoice_ID);
        job_userID = getIntent().getStringExtra("intent_job_user_ID");

        firebaseDatabase = FirebaseDatabase.getInstance();
        invoiceRef = firebaseDatabase.getReference("Invoice").child(invoice_ID);
        invoiceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Invoice invoice = dataSnapshot.getValue(Invoice.class);

                total_salary = String.format(" %.2f",invoice.getInvoice_totalSalary());

                invoiceDate.setText(invoice.getInvoice_date());
                invoiceStatus.setText(invoice.getInvoice_status());
                employerID.setText(invoice.getInvoice_employerID());
                employerName.setText(invoice.getInvoice_employerName());
                employerAddress.setText(invoice.getInvoice_employerAddress());
                employeeID.setText(invoice.getInvoice_employeeID());
                employeeName.setText(invoice.getInvoice_employeeName());
                employeeAddress.setText(invoice.getInvoice_employeeAddress());
                jobTitle.setText(invoice.getInvoice_jobTitle());
                salaryPerDay.setText(String.format("RM%.2f", invoice.getInvoice_salaryPerDay()));
                day.setText(String.valueOf(invoice.getInvoice_day()));
                totalSalary.setText(String.format("RM%s", total_salary));
                othersComment.setText(invoice.getInvoice_othersComment());

                if(invoice.getInvoice_status().equals("Payment is completed"))
                    btnConfirm.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentInvoiceDetailsActivity.this, PaymentActivity.class);
                intent.putExtra("intent_job_user_ID",job_userID);
                intent.putExtra("intent_invoice_ID2",invoice_ID);
                intent.putExtra("intent_salary",total_salary);
                startActivity(intent);
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
                Intent intent1 = new Intent(PaymentInvoiceDetailsActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(PaymentInvoiceDetailsActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(PaymentInvoiceDetailsActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_admin_dashboard:
                Intent intent4 = new Intent(PaymentInvoiceDetailsActivity.this, AdminDashboardActivity.class);
                startActivity(intent4);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(PaymentInvoiceDetailsActivity.this, Main2Activity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(PaymentInvoiceDetailsActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(PaymentInvoiceDetailsActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(PaymentInvoiceDetailsActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user:
                Intent intent9 = new Intent(PaymentInvoiceDetailsActivity.this, Main2Activity.class);
                startActivity(intent9);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putBoolean("isEmployer", true);
                editor.putBoolean("isApplicant", false);
                editor.apply();
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(PaymentInvoiceDetailsActivity.this, MainActivity.class);
                startActivity(intent12);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
