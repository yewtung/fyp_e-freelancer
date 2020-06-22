package com.example.myapplication.Invoice;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PaymentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Menu menu;
    private SharedPreferences prefs;
    private FirebaseUser firebaseUser;
    private Boolean emp, app, admin;
    private EditText etCardName, etCardNum, etCvv;
    private TextView amount, title, title2;
    private Button btnPay, ok;
    private String salary, invoiceID, job_userID;
    private String cardName, cardNum, cvv;
    private Dialog dialog;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        etCardName = findViewById(R.id.etCardHolderName);
        etCardNum = findViewById(R.id.etCardNumber);
        etCvv = findViewById(R.id.etCVV);
        amount = findViewById(R.id.tvPaymentAmount2);
        btnPay = findViewById(R.id.btnPay);


        firebaseDatabase = FirebaseDatabase.getInstance();

        salary = getIntent().getStringExtra("intent_salary");
        invoiceID = getIntent().getStringExtra("intent_invoice_ID2");
        job_userID = getIntent().getStringExtra("intent_job_user_ID");

        amount.setText(salary);

        dialog = new Dialog(this);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    dialog.setContentView(R.layout.pop_up_payment);
                    title = (TextView) dialog.findViewById(R.id.title);
                    title2 = (TextView) dialog.findViewById(R.id.title2);
                    ok = (Button) dialog.findViewById(R.id.btnOK);

                    DatabaseReference ref1 = firebaseDatabase.getReference("Job_user").child(job_userID);
                    ref1.child("job_user_status").setValue("Payment is completed");

                    DatabaseReference ref2 = firebaseDatabase.getReference("Invoice").child(invoiceID);
                    ref2.child("invoice_status").setValue("Payment is completed");

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Intent intent = new Intent (PaymentActivity.this, PaymentInvoiceDetailsActivity.class);
                            intent.putExtra("intent_invoice_ID", invoiceID);
                            intent.putExtra("intent_job_user_ID", job_userID);
                            startActivity(intent);
                        }
                    });
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                    dialog.show();
                }
            }
        });


    }

    private boolean validate(){

        String cardName = etCardName.getText().toString().trim();
        String cardNum = etCardNum.getText().toString().trim();
        String cvv = etCvv.getText().toString().trim();

        if(cardName.isEmpty()){
            etCardName.setError("Please enter your card holder name!");
            etCardName.requestFocus();
        }else if(cardNum.isEmpty()) {
            etCardNum.setError("Please enter your card number!");
            etCardNum.requestFocus();
        }else if(cvv.isEmpty()){
            etCvv.setError("Please enter your CVV!");
            etCvv.requestFocus();
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
                Intent intent1 = new Intent(PaymentActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(PaymentActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(PaymentActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(PaymentActivity.this, Main2Activity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(PaymentActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(PaymentActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(PaymentActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user:
                Intent intent9 = new Intent(PaymentActivity.this, MainActivity.class);
                startActivity(intent9);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putBoolean("isEmployer", false);
                editor.putBoolean("isApplicant", true);
                editor.apply();
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(PaymentActivity.this, MainActivity.class);
                startActivity(intent12);
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
