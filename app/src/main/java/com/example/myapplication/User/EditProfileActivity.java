package com.example.myapplication.User;

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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.AdminDashboard.AdminDashboardActivity;
import com.example.myapplication.EmployerDashboard.EmployerDashboardActivity;
import com.example.myapplication.Invoice.DisplayInvoiceActivity;
import com.example.myapplication.Job.PostJobActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Message.DisplayUserActivity;
import com.example.myapplication.R;
import com.example.myapplication.UserHistory.DisplayApplicationHistoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText  editAge, editPhone, editPreferSalary, editLatitude, editLongitude;
    private RadioGroup editRBStu;
    private RadioButton rbYes, rbNo;
    private TextView username, email;
    private Button save;
    private Boolean emp, app, admin;
    private SharedPreferences prefs;
    private String myID;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;
    private String rbStu, age, phone, preferSalary, latitude, longitude;
    private FirebaseUser firebaseUser;
    private Menu menu;
    private Double double_preferSalary, double_latitude,double_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
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

        prefs=getSharedPreferences("",MODE_PRIVATE);
        getPreferenceData();

        if(emp.equals(true))
            item.setTitle("Applicant");
        else if(app.equals(true))
            item.setTitle("Employer");

        firebaseDatabase = FirebaseDatabase.getInstance();
        myID = FirebaseAuth.getInstance().getUid();

        username = findViewById(R.id.editUserName);
        email = findViewById(R.id.editEmail);
        editAge = findViewById(R.id.editAge);
        editPhone = findViewById(R.id.editPhone);
        editPreferSalary = findViewById(R.id.editPreferSalary);
        editLatitude = findViewById(R.id.editLatitude);
        editLongitude = findViewById(R.id.editLongitude);
        editRBStu = findViewById(R.id.editProfileRBStu);
        rbYes = findViewById(R.id.editProfileRBYes1);
        rbNo = findViewById(R.id.editProfileRBNo1);
        save = findViewById(R.id.btnSave);

        userRef = firebaseDatabase.getReference("User").child(myID);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                email.setText(user.getEmail());
                editAge.setText(user.getAge());
                editPhone.setText(user.getPhone());
                editPreferSalary.setText(String.valueOf(user.getPreferSalary()));
                editLatitude.setText(String.valueOf(user.getLatitude()));
                editLongitude.setText(String.valueOf(user.getLongitude()));
                rbStu = user.getStu();
                if(rbStu.equals("Yes"))
                    rbYes.setChecked(true);
                else
                    rbNo.setChecked(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    uploadData();
                }
            }
        });
    }

    private void uploadData(){
        age = editAge.getText().toString().trim();
        phone = editPhone.getText().toString().trim();
        preferSalary = editPreferSalary.getText().toString().trim();
        latitude = editLatitude.getText().toString().trim();
        longitude = editLongitude.getText().toString().trim();
        int radioStuID = editRBStu.getCheckedRadioButtonId();
        final String rbStu = ((RadioButton) findViewById(radioStuID)).getText().toString();

        try {
            double_preferSalary = Double.valueOf(preferSalary);
            double_latitude =Double.valueOf(latitude);
            double_longitude =Double.valueOf(longitude);

        } catch (NumberFormatException e) {
            double_preferSalary = 0.00;
            double_latitude = 0.00;
            double_longitude = 0.00;
        }


        if (validate()) {
            Pattern phonePattern = Pattern.compile("\\d{3}-\\d{7}");
            Matcher phoneMatcher = phonePattern.matcher(phone);

            if (!phoneMatcher.matches()) {
                editPhone.setError("Please enter a valid phone number");
                editPhone.requestFocus();
            } else {
                userRef.child("age").setValue(age);
                userRef.child("phone").setValue(phone);
                userRef.child("preferSalary").setValue(Double.valueOf(String.format(" %.2f", preferSalary)));
                userRef.child("latitude").setValue(double_latitude);
                userRef.child("longitude").setValue(double_longitude);
                userRef.child("stu").setValue(rbStu);

                Toast.makeText(EditProfileActivity.this, "Profile is uploaded successfully.",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        }

    }

    private boolean validate(){
        String age =  editAge.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String latitude = editLatitude.getText().toString().trim();
        String longitude = editLongitude.getText().toString().trim();
        String preferSalary = editPreferSalary.getText().toString().trim();;

        if(age.isEmpty()){
            editAge.setError("Please enter your age!");
            editAge.requestFocus();
        }else if(phone.isEmpty()){
            editPhone.setError("Please enter your phone number!");
            editPhone.requestFocus();
        }else if(latitude.isEmpty() ){
            editLatitude.setError("Please enter your location!");
            editLatitude.requestFocus();
        }else if(longitude.isEmpty()){
            editLongitude.setError("Please enter your location!");
            editLongitude.requestFocus();
        }else if(preferSalary.isEmpty()){
            editPreferSalary.setError("Please enter your prefer salary!");
            editPreferSalary.requestFocus();
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

        switch(id){
            case R.id.nav_login:
                Intent intent1 = new Intent(EditProfileActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(EditProfileActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(EditProfileActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_admin_dashboard:
                Intent intent4 = new Intent(EditProfileActivity.this, AdminDashboardActivity.class);
                startActivity(intent4);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(EditProfileActivity.this, MainActivity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(EditProfileActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(EditProfileActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(EditProfileActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user:
                Intent intent9 = new Intent(EditProfileActivity.this, MainActivity.class);
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
                Intent intent10 = new Intent(EditProfileActivity.this, DisplayInvoiceActivity.class);
                startActivity(intent10);
                break;
            case R.id.nav_application:
                Intent intent11 = new Intent(EditProfileActivity.this, DisplayApplicationHistoryActivity.class);
                startActivity(intent11);
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(EditProfileActivity.this, MainActivity.class);
                startActivity(intent12);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
