package com.example.myapplication.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.AdminDashboard.AdminDashboardActivity;
import com.example.myapplication.EmployerDashboard.EmployerDashboardActivity;
import com.example.myapplication.Invoice.DisplayInvoiceActivity;
import com.example.myapplication.Job.PostJobActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Message.DisplayUserActivity;
import com.example.myapplication.Message.MessageActivity;
import com.example.myapplication.R;
import com.example.myapplication.UserHistory.DisplayApplicationHistoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CircleImageView profilePicture;
    private TextView userName, email,age, phoneNum, location, preferSalary;
    private Button btnChat;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private SharedPreferences prefs;
    private Boolean emp, app, admin;
    String user2ID;
    private Menu menu;
    private FirebaseUser firebaseUser;
    private String myID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        prefs=getSharedPreferences("PrefsFile",MODE_PRIVATE);
        getPreferenceData();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();
        hideNavigationItem();
        MenuItem item = menu.findItem(R.id.switch_user);

        if(emp.equals(true))
            item.setTitle("Applicant");
        else if(app.equals(true))
            item.setTitle("Employer");

        profilePicture = (CircleImageView) findViewById(R.id.viewProfilePicture);
        userName = (TextView) findViewById(R.id.viewProfileUserName);
        email = (TextView) findViewById(R.id.viewProfileEmail);
        age = (TextView) findViewById(R.id.viewProfileAge);
        phoneNum = (TextView) findViewById(R.id.viewProfilePhone);
        location = findViewById(R.id.viewProfileLocation);
        preferSalary = findViewById(R.id.viewProfilePreferSalary);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        user2ID = getIntent().getStringExtra("user_ID");


        DatabaseReference databaseReference = firebaseDatabase.getReference("User").child(user2ID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userAcc = dataSnapshot.getValue(User.class);
                userName.setText(userAcc.getUsername());
                email.setText(userAcc.getEmail());
                age.setText(userAcc.getAge());
                phoneNum.setText(userAcc.getPhone());
                String imageURL = userAcc.getImageURL();
                Picasso.get().load(imageURL).into(profilePicture);

                String latitude = String.valueOf(userAcc.getLatitude());
                String longitude = String.valueOf(userAcc.getLongitude());
                location.setText(String.format("%s,%s", latitude, longitude));
                preferSalary.setText(String.format("RM %.2f",userAcc.getPreferSalary()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ViewProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        btnChat = findViewById(R.id.btnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChat = new Intent(ViewProfileActivity.this, MessageActivity.class);
                intentChat.putExtra("chatAttempt","true");
                intentChat.putExtra("chatReceiverID",user2ID);
                startActivity(intentChat);
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

        switch(id){
            case R.id.nav_login:
                Intent intent1 = new Intent(ViewProfileActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(ViewProfileActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(ViewProfileActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_admin_dashboard:
                Intent intent4 = new Intent(ViewProfileActivity.this, AdminDashboardActivity.class);
                startActivity(intent4);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(ViewProfileActivity.this, MainActivity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(ViewProfileActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(ViewProfileActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(ViewProfileActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user:
                Intent intent9 = new Intent(ViewProfileActivity.this, MainActivity.class);
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
                Intent intent10 = new Intent(ViewProfileActivity.this, DisplayInvoiceActivity.class);
                startActivity(intent10);
                break;
            case R.id.nav_application:
                Intent intent11 = new Intent(ViewProfileActivity.this, DisplayApplicationHistoryActivity.class);
                startActivity(intent11);
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(ViewProfileActivity.this, MainActivity.class);
                startActivity(intent12);
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
