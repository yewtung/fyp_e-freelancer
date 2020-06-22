package com.example.myapplication.Invoice;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.AdminDashboard.AdminDashboardActivity;
import com.example.myapplication.EmployerDashboard.EmployerDashboardActivity;
import com.example.myapplication.Job.Job;
import com.example.myapplication.Job.PostJobActivity;
import com.example.myapplication.Main2Activity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Message.DisplayUserActivity;
import com.example.myapplication.R;
import com.example.myapplication.User.LoginActivity;
import com.example.myapplication.User.ProfileActivity;
import com.example.myapplication.User.SignUpActivity;
import com.example.myapplication.User.User;
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


public class PaymentInvoiceActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences prefs;
    private Boolean emp, app, admin;
    private TextView etAppAddress, etEmpID, etAppID, etJobID, etJobTitle, etInvoiceDate;
    private EditText etEmpAddress, etSalary, etSalaryDay, etOthersComment;
    private String appID, jobID, userID, jobUserID;
    private FirebaseDatabase firebaseDatabase;
    private Button btnView;
    private String salary, empName, appName;
    private ProgressDialog progressDialog;
    private DatabaseReference invoiceRef;
    private Menu menu;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_invoice);
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

        if(emp.equals(true))
            item.setTitle("Applicant");
        else if(app.equals(true))
            item.setTitle("Employer");

        etEmpAddress = findViewById(R.id.etEmpAddress);
        etAppAddress = findViewById(R.id.etAppAddress);
        etEmpID = findViewById(R.id.etEmpID);
        etAppID = findViewById(R.id.etAppID);
        etJobID = findViewById(R.id.etJobID);
        etJobTitle = findViewById(R.id.etJobTitle);
        etSalary = findViewById(R.id.etSalary);
        etSalaryDay = findViewById(R.id.etSalaryDay);
        etInvoiceDate = findViewById(R.id.etInvoiceDate);
        etOthersComment = findViewById(R.id.etOthersComment);
        btnView = findViewById(R.id.btnView);

        appID = getIntent().getStringExtra("payment_user_id");
        jobID = getIntent().getStringExtra("payment_job_id");
        jobUserID  = getIntent().getStringExtra("payment_job_user_id");

        userID = FirebaseAuth.getInstance().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        invoiceRef = FirebaseDatabase.getInstance().getReference();

        etEmpID.setText(userID);
        etAppID.setText(appID);
        etJobID.setText(jobID);
        etInvoiceDate.setText(getDateTime());
        getDatabaseData();

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });
    }

    private void sendData(){
        String emp_address = etEmpAddress.getText().toString().trim();
        String app_address = etAppAddress.getText().toString().trim();
        String emp_ID = etEmpID.getText().toString().trim();
        String job_ID = etJobID.getText().toString().trim();
        String job_title = etJobTitle.getText().toString().trim();
        Double salary_per_day =Double.parseDouble(etSalary.getText().toString().trim());
        int salary_day = Integer.parseInt(etSalaryDay.getText().toString().trim());
        String invoice_date = etInvoiceDate.getText().toString().trim();
        String others_comment = etOthersComment.getText().toString().trim();

        Double salary = Double.parseDouble(String.format(" %.2f",salary_per_day));
        Double total_salary = Double.parseDouble(String.format(" %.2f",salary * salary_day));
        String invoiceID = invoiceRef.push().getKey();

        if(validate()){
            Invoice invoice = new Invoice(invoiceID, invoice_date, emp_ID, appID, empName, appName,
                    emp_address, app_address,job_ID,job_title, salary, salary_day, total_salary,
                    others_comment, "Payment not complete", jobUserID);
            invoiceRef.child("Invoice").child(invoiceID).setValue(invoice);

            DatabaseReference ref = firebaseDatabase.getReference("Job_user").child(jobUserID);
            ref.child("job_user_status").setValue("Payment not complete");
            ref.child("job_user_salary").setValue(String.valueOf(total_salary));

            Intent intent = new Intent(PaymentInvoiceActivity.this,
                    PaymentInvoiceDetailsActivity.class);
            intent.putExtra("intent_invoice_ID",invoiceID);
            intent.putExtra("intent_job_user_ID",jobUserID);
            startActivity(intent);
        }
    }


    private void getDatabaseData(){
        DatabaseReference databaseReference = firebaseDatabase.getReference("User").child(appID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                appName = user.getUsername();
                etAppAddress.setText(user.getLatitude() + "," + user.getLongitude());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PaymentInvoiceActivity.this, databaseError.getCode(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference databaseReference1 = firebaseDatabase.getReference("Job").child(jobID);
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Job job = dataSnapshot.getValue(Job.class);
                etJobTitle.setText(job.getJob_title());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PaymentInvoiceActivity.this, databaseError.getCode(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference databaseReference2 = firebaseDatabase.getReference("User").child(userID);
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                empName = user.getUsername();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PaymentInvoiceActivity.this, databaseError.getCode(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String date1 = dateFormat.format(date).toString();
        return date1;
    }

    private boolean validate(){

        String empAddress = etEmpAddress.getText().toString().trim();
        String salary = etSalary.getText().toString().trim();
        String salaryDay = etSalaryDay.getText().toString().trim();

        if(empAddress.isEmpty()){
            etEmpAddress.setError("Please enter your address!");
            etEmpAddress.requestFocus();
        }else if(salary.isEmpty()) {
            etSalary.setError("Please enter your salary!");
            etSalary.requestFocus();
        }else if(salaryDay.isEmpty()){
            etSalaryDay.setError("Please enter your a day!");
            etSalaryDay.requestFocus();
        }else{
            return true;
        }
        return false;
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
                Intent intent1 = new Intent(PaymentInvoiceActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(PaymentInvoiceActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(PaymentInvoiceActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_admin_dashboard:
                Intent intent4 = new Intent(PaymentInvoiceActivity.this, AdminDashboardActivity.class);
                startActivity(intent4);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(PaymentInvoiceActivity.this, Main2Activity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(PaymentInvoiceActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(PaymentInvoiceActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(PaymentInvoiceActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user:
                Intent intent9 = new Intent(PaymentInvoiceActivity.this, Main2Activity.class);
                startActivity(intent9);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putBoolean("isEmployer", true);
                editor.putBoolean("isApplicant", false);
                editor.apply();
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(PaymentInvoiceActivity.this, MainActivity.class);
                startActivity(intent12);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
