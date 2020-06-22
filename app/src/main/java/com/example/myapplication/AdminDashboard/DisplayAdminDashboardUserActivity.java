package com.example.myapplication.AdminDashboard;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.example.myapplication.EmployerDashboard.EmployerDashboardActivity;
import com.example.myapplication.Job.Job;
import com.example.myapplication.Job.PostJobActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Message.DisplayUserActivity;
import com.example.myapplication.User.LoginActivity;
import com.example.myapplication.User.ProfileActivity;
import com.example.myapplication.User.SignUpActivity;
import com.example.myapplication.R;
import com.example.myapplication.User.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.List;

public class DisplayAdminDashboardUserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseFunctions firebaseFunctions;
    private ProgressDialog progressDialog;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_admin_dashboard_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menus = navigationView.getMenu();
        menus.removeItem(R.id.nav_login);
        menus.removeItem(R.id.nav_signUp);
        menus.removeItem(R.id.nav_employer_dashboard);
        menus.removeItem(R.id.nav_searchJob);
        menus.removeItem(R.id.nav_postJob);
        menus.removeItem(R.id.nav_profile);
        menus.removeItem(R.id.nav_chat);
        menus.removeItem(R.id.switch_user);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Displaying User");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        firebaseFunctions = FirebaseFunctions.getInstance("asia-east2");
        display();
        status = getIntent().getStringExtra("intentStatus");
    }

    private Task<List<HashMap<String, String>>> listAllUsers() {
        return firebaseFunctions
                .getHttpsCallable("listAllUsers")
                .call()
                .continueWith(new Continuation<HttpsCallableResult, List<HashMap<String, String>>>() {
                    @Override
                    public List<HashMap<String, String>>
                    then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        List<HashMap<String, String>> result =
                                (List<HashMap<String, String>>) task.getResult().getData();
                        return result;
                    }
                });
    }

    private void addActiveTableRow(List<HashMap<String, String>> result) {
        for(HashMap<String, String> user : result) {
            TableLayout tl = (TableLayout) findViewById(R.id.tl_AdminDashboard_user);
            TableRow tr = new TableRow(this);

            String emailVerified = String.valueOf(user.get("emailVerified"));

            if(emailVerified.equals("true")){

                final String id = user.get("uid");
                String email = user.get("email");

                TextView tvUserID = new TextView(this);
                tvUserID.setText(id);
                tr.addView(tvUserID);
                TableRow.LayoutParams params1 = (TableRow.LayoutParams) tvUserID.getLayoutParams();
                params1.width=140;
                tvUserID.setPadding(5,5,5,5);
                tvUserID.setLayoutParams(params1);

                TextView tvEmail = new TextView(this);
                tvEmail.setText(email);
                tr.addView(tvEmail);
                TableRow.LayoutParams params2 = (TableRow.LayoutParams) tvEmail.getLayoutParams();
                params2.width=140;
                tvEmail.setPadding(5,5,5,5);
                tvEmail.setLayoutParams(params2);

                Button button = new Button(this);
                button.setText("View");
                tr.addView(button);
                TableRow.LayoutParams params3 = (TableRow.LayoutParams) button.getLayoutParams();
                params3.width=50;
                button.setPadding(5,5,5,5);
                button.setLayoutParams(params3);
                setOnClick(button, user);
                tl.addView(tr);
            }

        } progressDialog.dismiss();
    }

    private void addInactiveTableRow(List<HashMap<String, String>> result) {
        for(HashMap<String, String> user : result) {
            TableLayout tl = (TableLayout) findViewById(R.id.tl_AdminDashboard_user);
            TableRow tr = new TableRow(this);

            String emailVerified = String.valueOf(user.get("emailVerified"));

            if(emailVerified.equals("false")){

                final String id = user.get("uid");
                String email = user.get("email");

                TextView tvUserID = new TextView(this);
                tvUserID.setText(id);
                tr.addView(tvUserID);
                TableRow.LayoutParams params1 = (TableRow.LayoutParams) tvUserID.getLayoutParams();
                params1.width=140;
                tvUserID.setPadding(5,5,5,5);
                tvUserID.setLayoutParams(params1);

                TextView tvEmail = new TextView(this);
                tvEmail.setText(email);
                tr.addView(tvEmail);
                TableRow.LayoutParams params2 = (TableRow.LayoutParams) tvEmail.getLayoutParams();
                params2.width=140;
                tvEmail.setPadding(5,5,5,5);
                tvEmail.setLayoutParams(params2);

                Button button = new Button(this);
                button.setText("View");
                tr.addView(button);
                TableRow.LayoutParams params3 = (TableRow.LayoutParams) button.getLayoutParams();
                params3.width=50;
                button.setPadding(5,5,5,5);
                button.setLayoutParams(params3);
                setOnClick(button, user);
                tl.addView(tr);
            }

        }progressDialog.dismiss();
    }

    private void setOnClick(final Button btn, final HashMap<String, String> user) {
        final String id = user.get("uid");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDetails = new Intent(DisplayAdminDashboardUserActivity.this,
                        DisplayAdminDashboardUserDetailsActivity.class);
                intentDetails.putExtra("userID",id);
                startActivity(intentDetails);
            }
        });
    }

    private void display(){
        listAllUsers().addOnCompleteListener(new OnCompleteListener<List<HashMap<String, String>>>() {
            @Override
            public void onComplete(@NonNull Task<List<HashMap<String, String>>> task) {
                if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    progressDialog.dismiss();
                    Toast.makeText(DisplayAdminDashboardUserActivity.this, "Get data failed."+ e,
                            Toast.LENGTH_SHORT).show();
                }
                List<HashMap<String, String>> result = task.getResult();

                if(status.equals("active")){
                    addActiveTableRow(result);
                }else if(status.equals("inactive")){
                    addInactiveTableRow(result);
                }
            }

        });
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
                Intent intent1 = new Intent(DisplayAdminDashboardUserActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(DisplayAdminDashboardUserActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(DisplayAdminDashboardUserActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_admin_dashboard:
                Intent intent4 = new Intent(DisplayAdminDashboardUserActivity.this, AdminDashboardActivity.class);
                startActivity(intent4);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(DisplayAdminDashboardUserActivity.this, MainActivity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(DisplayAdminDashboardUserActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(DisplayAdminDashboardUserActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(DisplayAdminDashboardUserActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(DisplayAdminDashboardUserActivity.this, MainActivity.class);
                startActivity(intent12);
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
