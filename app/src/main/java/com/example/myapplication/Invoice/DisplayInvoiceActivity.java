package com.example.myapplication.Invoice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.AdminDashboard.AdminDashboardActivity;
import com.example.myapplication.EmployerDashboard.DisplayEmployerDashboardJobDetailsActivity;
import com.example.myapplication.EmployerDashboard.EmployerDashboardActivity;
import com.example.myapplication.Job.PostJobActivity;
import com.example.myapplication.Main2Activity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Message.DisplayUserActivity;
import com.example.myapplication.R;
import com.example.myapplication.User.LoginActivity;
import com.example.myapplication.User.ProfileActivity;
import com.example.myapplication.User.SignUpActivity;
import com.example.myapplication.UserHistory.DisplayApplicationHistoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayInvoiceActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences prefs;
    private Boolean emp, app, admin;
    private RecyclerView rv_invoice;
    private ImageView arrowRight;
    private List<Invoice> invoiceList;
    private InvoiceAdapter invoiceAdapter;
    private DatabaseReference invoiceRef;
    private String userID, invoiceID, jobUserID, intentRole;
    private FirebaseUser firebaseUser;
    private Menu menu;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_invoice);
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

        if(emp.equals(true)){
            item.setTitle("Applicant");
            intentRole = "Employer";
        }
        else if(app.equals(true)) {
            item.setTitle("Employer");
            intentRole = "Applicant";
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Displaying");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        userID = FirebaseAuth.getInstance().getUid();
        invoiceRef = FirebaseDatabase.getInstance().getReference("Invoice");

        rv_invoice = findViewById(R.id.rv_invoice);
        rv_invoice.setLayoutManager(new LinearLayoutManager(this));
        displayInvoiceRV();

        arrowRight = (ImageView) findViewById(R.id.job_lv_arrow);
        rv_invoice.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intentViewInvoice = new Intent(DisplayInvoiceActivity.this,
                        PaymentInvoiceDetailsActivity.class);
                intentViewInvoice.putExtra("intent_invoice_ID",invoiceID);
                intentViewInvoice.putExtra("intent_job_user_ID",jobUserID);
                startActivity(intentViewInvoice);
            }
        });

    }

    private void displayInvoiceRV() {
        invoiceList = new ArrayList<Invoice>();

        invoiceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                invoiceList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Invoice invoice = dataSnapshot1.getValue(Invoice.class);
                    invoiceID = invoice.getInvoiceID();
                    jobUserID = invoice.getInvoice_job_userID();
                    if (intentRole.equals("Employer")) {
                        if (userID.equals(invoice.getInvoice_employerID()))
                            invoiceList.add(invoice);
                    } else if (intentRole.equals("Applicant")) {
                        if (userID.equals(invoice.getInvoice_employeeID()))
                            invoiceList.add(invoice);
                    }
                }
                if (invoiceList.size() == 0) {
                    Toast.makeText(DisplayInvoiceActivity.this, "Sorry, no results are found."
                            , Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else{
                    invoiceAdapter = new InvoiceAdapter(DisplayInvoiceActivity.this,
                            (ArrayList<Invoice>) invoiceList);
                    rv_invoice.setAdapter(invoiceAdapter);
                    progressDialog.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DisplayInvoiceActivity.this,
                        "Unable to get invoice list.", Toast.LENGTH_SHORT).show();
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
            admin = sp.getBoolean("isAdmin", false);
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
                Intent intent1 = new Intent(DisplayInvoiceActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(DisplayInvoiceActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(DisplayInvoiceActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_admin_dashboard:
                Intent intent4 = new Intent(DisplayInvoiceActivity.this, AdminDashboardActivity.class);
                startActivity(intent4);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(DisplayInvoiceActivity.this, Main2Activity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(DisplayInvoiceActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(DisplayInvoiceActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(DisplayInvoiceActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user:
                Intent intent9 = new Intent(DisplayInvoiceActivity.this, Main2Activity.class);
                startActivity(intent9);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putBoolean("isEmployer", true);
                editor.putBoolean("isApplicant", false);
                editor.apply();
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(DisplayInvoiceActivity.this, MainActivity.class);
                startActivity(intent12);
                break;
            case R.id.nav_invoice:
                Intent intent10 = new Intent(DisplayInvoiceActivity.this, DisplayInvoiceActivity.class);
                startActivity(intent10);
                break;
            case R.id.nav_application:
                Intent intent11 = new Intent(DisplayInvoiceActivity.this, DisplayApplicationHistoryActivity.class);
                startActivity(intent11);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
