package com.example.myapplication.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText etUserName,etAge,etPhone,etEmail,etPassword, etConfirmPassword, etLatitude, etLongitude, etPreferSalary;
    RadioGroup rbStu;
    Button btnSignIn;
    TextView loginLink;
    ImageView profilePic;
    String username, age, phone, email, password, pass, stu;
    double latitude, longitude, preferSalary;
    private static int PICK_IMAGE_REQUEST = 123;
    Uri pickedImgUri;
    private FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    private ProgressDialog progressDialog;
    private SharedPreferences prefs;
    private Boolean emp, app, admin;
    private Menu menu;
    private FirebaseUser firebaseUser;
    private NavigationView navigationView;

/*    private void mockData() {

        etUserName = (EditText)findViewById(R.id.etApplicantUserName);
        etAge = (EditText)findViewById(R.id.etApplicantAge);
        etPhone = (EditText)findViewById(R.id.etApplicantPhone);
        etEmail = (EditText)findViewById(R.id.etApplicantEmail);
        etPassword = (EditText)findViewById(R.id.etApplicantPassword);
        etConfirmPassword = (EditText)findViewById(R.id.etApplicantConfirmPassword);

        String email = "test+" + new Date().getTime() + "@test.com";

        etUserName.setText("test 123");
        etAge.setText("22");
        etPhone.setText("123-3456789");
        etEmail.setText(email);
        etPassword.setText("123123");
        etConfirmPassword.setText("123123");
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        progressDialog = new ProgressDialog(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        prefs=getSharedPreferences("PrefsFile",MODE_PRIVATE);
        getPreferenceData();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();
        hideNavigationItem();
        MenuItem item = menu.findItem(R.id.switch_user);

        if(emp.equals(true))
            item.setTitle("Applicant");
        else if(app.equals(true))
            item.setTitle("Employer");

        etUserName = (EditText)findViewById(R.id.etApplicantUserName);
        etAge = (EditText)findViewById(R.id.etApplicantAge);
        etPhone = (EditText)findViewById(R.id.etApplicantPhone);
        etEmail = (EditText)findViewById(R.id.etApplicantEmail);
        etPassword = (EditText)findViewById(R.id.etApplicantPassword);
        etConfirmPassword = (EditText)findViewById(R.id.etApplicantConfirmPassword);
        rbStu = (RadioGroup)findViewById(R.id.rbStu);
        etLatitude = (EditText)findViewById(R.id.etLatitude);
        etLongitude = (EditText)findViewById(R.id.etLongitude);
        etPreferSalary = (EditText)findViewById(R.id.etApplicantPreferSalary);

        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        loginLink = (TextView)findViewById(R.id.tvLoginLink);
        profilePic = (ImageView)findViewById(R.id.signUpProfilePicture);


        // todo : remove
        //mockData();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }

    private void register() {
        try{
            progressDialog.setTitle("Creating new account");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            username = etUserName.getText().toString().trim();
            age = etAge.getText().toString().trim();
            phone = etPhone.getText().toString().trim();
            email = etEmail.getText().toString().trim();
            password = etPassword.getText().toString().trim();
            int radioStuID = rbStu.getCheckedRadioButtonId();
            stu = ((RadioButton) findViewById(radioStuID)).getText().toString();
            latitude =Double.parseDouble(etLatitude.getText().toString().trim());
            longitude = Double.parseDouble(etLongitude.getText().toString().trim());
            preferSalary = Double.parseDouble(etPreferSalary.getText().toString().trim());

            if(validate() && pickedImgUri !=null) {

                Pattern phonePattern = Pattern.compile("\\d{3}-\\d{7}");
                Pattern emailPattern = Pattern.compile("[a-zA-z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" + "[a-zA-z0-9][a-zA-z0-9\\-]{0,64}" +
                        "(" + "\\." + "[a-zA-z0-9][a-zA-z0-9\\-]{0,25}" + ")");
                Matcher phoneMatcher = phonePattern.matcher(phone);
                Matcher emailMatcher = emailPattern.matcher(email);

                if (!phoneMatcher.matches()) {
                    etPhone.setError("Please enter a valid phone number");
                    etPhone.requestFocus();
                } else if (!emailMatcher.matches()) {
                    etEmail.setError("Please enter a valid email");
                    etEmail.requestFocus();
                } else {

                    Task<AuthResult> result = firebaseAuth.createUserWithEmailAndPassword(email, password);
                    result.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                    result.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendEmailVerification();
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }}
            else {
                Toast.makeText(SignUpActivity.this, "Cannot create account", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }catch(Exception e){
            Toast.makeText(SignUpActivity.this, "Error!", Toast.LENGTH_SHORT).show();
        }
    }

    //to check validate information for registration
    private boolean validate(){
        String userName = etUserName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String latitude =  etLatitude.getText().toString().trim();
        String longitude =  etLongitude.getText().toString().trim();
        String preferSalary = etPreferSalary.getText().toString().trim();

        if(userName.isEmpty()){
            etUserName.setError("Please enter your user name!");
            etUserName.requestFocus();
        }else if(age.isEmpty()) {
            etAge.setError("Please enter your age!");
            etAge.requestFocus();
        }else if(phone.isEmpty()){
            etPhone.setError("Please enter your phone number!");
            etPhone.requestFocus();
        }else if(email.isEmpty() ){
            etEmail.setError("Please enter your email!");
            etEmail.requestFocus();
        }else if(password.isEmpty()){
            etPassword.setError("Please enter your password!");
            etPassword.requestFocus();
        }else if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Please enter the same password!");
            etConfirmPassword.requestFocus();
        }else if(latitude.isEmpty()){
            etLatitude.setError("Please enter your location!");
            etLatitude.requestFocus();
        }else if(longitude.isEmpty()){
            etLongitude.setError("Please enter your location!");
            etLongitude.requestFocus();
        }else if(preferSalary.isEmpty()){
            etPreferSalary.setError("Please enter your prefer salary");
            etPreferSalary.requestFocus();
        }else{
            return true;
        }
        return false;
    }

    //get the current date and time
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            pickedImgUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pickedImgUri);
                profilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendUserData();
                    } else {
                        Toast.makeText(SignUpActivity.this,
                                "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void sendUserData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = firebaseDatabase.getReference();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        final String firebaseID = firebaseAuth.getUid();
        if(firebaseID !=null && pickedImgUri !=null) {
            final StorageReference imageReference = storageReference.child("ProfilePictures/" +
                    firebaseID + pickedImgUri.getLastPathSegment());
            UploadTask uploadTask = imageReference.putFile(pickedImgUri);

            User userProfile = new User(firebaseID, username, " ", age, email, phone, stu,
                    "Inactive", getDateTime(), latitude, longitude,
                    Double.valueOf(String.format(" %.2f",preferSalary)));
            myRef.child("User").child(firebaseID).setValue(userProfile);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String imageURL = downloadUri.toString();
                        myRef.child("User").child(firebaseID).child("imageURL").setValue(imageURL);
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        progressDialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "Upload successful, please verify your email.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
                Intent intent1 = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(SignUpActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(SignUpActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_admin_dashboard:
                Intent intent4 = new Intent(SignUpActivity.this, AdminDashboardActivity.class);
                startActivity(intent4);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(SignUpActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(SignUpActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(SignUpActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user:
                Intent intent9 = new Intent(SignUpActivity.this, MainActivity.class);
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
                Intent intent10 = new Intent(SignUpActivity.this, DisplayInvoiceActivity.class);
                startActivity(intent10);
                break;
            case R.id.nav_application:
                Intent intent11 = new Intent(SignUpActivity.this, DisplayApplicationHistoryActivity.class);
                startActivity(intent11);
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent12);
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
