package com.example.myapplication.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.AdminDashboard.AdminDashboardActivity;
import com.example.myapplication.EmployerDashboard.EmployerDashboardActivity;
import com.example.myapplication.Invoice.DisplayInvoiceActivity;
import com.example.myapplication.Job.Job;
import com.example.myapplication.Job.PostJobActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Message.DisplayUserActivity;
import com.example.myapplication.R;
import com.example.myapplication.UserHistory.DisplayApplicationHistoryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class  LoginActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText loginEmail,loginPassword;
    Button btnLogin;
    NavigationView navigationView;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TextView forgotPass, sendToSignUpLink;
    private CheckBox rememberMe;
    private static final String PrefName="PrefsFile";
    private SharedPreferences prefs;
    private Boolean emp, app, admin;
    private Menu menu;
    private FirebaseUser firebaseUser;
    private DatabaseReference jobRef;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        loginEmail = (EditText)findViewById(R.id.loginEmail);
        loginPassword = (EditText)findViewById(R.id.loginPassword);
        forgotPass = (TextView) findViewById(R.id.forgotPassword);
        sendToSignUpLink = (TextView) findViewById(R.id.newUser);
        rememberMe = (CheckBox) findViewById(R.id.rmbPass) ;
        btnLogin = (Button)findViewById(R.id.btnLogin);

        prefs=getSharedPreferences(PrefName,MODE_PRIVATE);
        getPreferenceData();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();
        hideNavigationItem();
        MenuItem item = menu.findItem(R.id.switch_user);

        if(!emp && !app){
            if(emp.equals(true))
                item.setTitle("Applicant");
            else if(app.equals(true))
                item.setTitle("Employer");
        }
        
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                if(email.isEmpty()){
                    loginEmail.setError("Please enter your email!");
                    loginEmail.requestFocus();
                }else if(password.isEmpty()){
                    loginPassword.setError("Please enter your password!");
                    loginPassword.requestFocus();
                }else {
                    if (rememberMe.isChecked()) {
                        Boolean boolChecked = rememberMe.isChecked();
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("prefEmail", email);
                        editor.putString("prefPass", password);
                        editor.putBoolean("prefChecked", boolChecked);
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Account Info have been save",
                                Toast.LENGTH_SHORT).show();

                    } else {
                        prefs.edit().clear().apply();
                    }
                    if (email.equals("admin")) {
                        Intent intent4 = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("isAdmin", true);
                        editor.apply();
                        startActivity(intent4);
                    } else {
                        validate(email, password);
                    }
                }
            }
        });

        sendToSignUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resetIntent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(resetIntent);
            }
        });
    }

    private void validate(String userName, String userPassword) {
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    checkEmailVerification();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void checkEmailVerification(){
        String myID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference ref = firebaseDatabase.getReference("User").child(myID);

        if(firebaseUser.isEmailVerified()){
            finish();

            ref.child("status").setValue("Active");
            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(LoginActivity.this, "Login Failed, please verify your email", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
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

        switch(id){
            case R.id.nav_login:
                Intent intent1 = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(LoginActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_admin_dashboard:
                Intent intent4 = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                startActivity(intent4);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(LoginActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(LoginActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(LoginActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user:
                Intent intent9 = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent9);
                SharedPreferences.Editor editor=prefs.edit();
                if(emp.equals(true)){
                    editor.putBoolean("isEmployer", false);
                    editor.putBoolean("isApplicant", true);
                }
                else if(app.equals(true)){
                    editor.putBoolean("isEmployer", true);
                    editor.putBoolean("isApplicant", false);
                }
                editor.apply();
                break;
            case R.id.nav_invoice:
                Intent intent10 = new Intent(LoginActivity.this, DisplayInvoiceActivity.class);
                startActivity(intent10);
                break;
            case R.id.nav_application:
                Intent intent11 = new Intent(LoginActivity.this, DisplayApplicationHistoryActivity.class);
                startActivity(intent11);
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent12);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getPreferenceData(){
        SharedPreferences sp =getSharedPreferences(PrefName,MODE_PRIVATE);
        if(sp.contains("prefEmail")){
            String email=sp.getString("prefEmail","not found");
            loginEmail.setText(email);
        }
        if(sp.contains("prefPass")){
            String pass=sp.getString("prefPass", "not found");
            loginPassword.setText(pass.toString());
        }
        if(sp.contains("prefCheck")){
            Boolean check=sp.getBoolean("prefCheck", false);
            rememberMe.setChecked(check);
        }
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
}
