package com.example.myapplication;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.example.myapplication.AdminDashboard.AdminDashboardActivity;
import com.example.myapplication.EmployerDashboard.EmployerDashboardActivity;
import com.example.myapplication.Invoice.DisplayInvoiceActivity;
import com.example.myapplication.Job.JobDetailsActivity;
import com.example.myapplication.Job.JobListAdapter;
import com.example.myapplication.Job.PostJobActivity;
import com.example.myapplication.Job.Job;
import com.example.myapplication.Message.DisplayUserActivity;
import com.example.myapplication.User.LoginActivity;
import com.example.myapplication.User.ProfileActivity;
import com.example.myapplication.User.SignUpActivity;
import com.example.myapplication.UserHistory.DisplayApplicationHistoryActivity;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,
        OnMapReadyCallback{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar = null;

    private List<Job> allJobList, circleList;
    private List<Marker> allJobMarker;
    private Marker marker, marker1;
    DatabaseReference jobRef;

    private BottomSheetBehavior bottomSheetBehavior;
    private View linearLayout;
    private ToggleButton tbUpDown;
    private RecyclerView recyclerView;
    private JobListAdapter jobListAdapter;
    private ImageView arrowRight;
    private TextView tvBottom;

    private GoogleMap mMap;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    private LocationCallback locationCallback;
    private final float DEFAULT_ZOOM =18;
    private EditText materialSearchBar;
    private View mapView;
    private String title, id, id_marker, marker_prefs;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private int radius;
    private Circle circle ;
    private SharedPreferences prefs;
    private FirebaseUser firebaseUser;
    private Boolean admin, emp, app, studentChecked;
    private String PrefName = "PrefsFile";
    private Menu menus;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menus = navigationView.getMenu();
        hideNavigationItem();

        prefs=getSharedPreferences(PrefName,MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putBoolean("isEmployer", false);
        editor.putBoolean("isApplicant", true);
        editor.putBoolean("isAdmin", false);
        editor.apply();
        getPreferenceData();

        //add marker
        jobRef = FirebaseDatabase.getInstance().getReference("Job");
        allJobList = new ArrayList<>();
        allJobMarker = new ArrayList<>();
        circleList = new ArrayList<>();
        dialog = new Dialog(this);

        //bottom sheet layout
        linearLayout = findViewById(R.id.bottom_sheet1);
        tvBottom = linearLayout.findViewById(R.id.tvBottomsheet);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        tbUpDown = findViewById(R.id.toggleButton);
        tbUpDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bottomSheetBehavior.setPeekHeight(1500);
                    tvBottom.setText("Tap here to close.");
                } else {
                    bottomSheetBehavior.setPeekHeight(150);
                    tvBottom.setText("Tap here to view.");
                }
            }
        });
        arrowRight = (ImageView) findViewById(R.id.job_lv_arrow);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intentViewJob = new Intent(MainActivity.this, JobDetailsActivity.class);
                startActivity(intentViewJob);
            }
        });



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        Places.initialize(MainActivity.this, getString(R.string.google_maps_api));
        placesClient = Places.createClient(this);

        materialSearchBar = findViewById(R.id.search_bar);
        materialSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    searchJob(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).
                    findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 1300);
        }
        //check if gps is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(MainActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });
        task.addOnFailureListener(MainActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MainActivity.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        //enableMyLocationIfPermitted();
        Query query = FirebaseDatabase.getInstance().getReference("Job").orderByChild("job_title");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allJobList.clear();
                allJobMarker.clear();
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    Job job = s.getValue(Job.class);
                    if(job.getJob_status().equals("Active")) {
                        allJobList.add(job);
                        String lat = s.child("latitude").getValue().toString();
                        String lng = s.child("longitude").getValue().toString();
                        Double latitude = Double.parseDouble(lat);
                        Double longitude = Double.parseDouble(lng);
                        id = s.child("job_ID").getValue().toString();
                        title = s.child("job_title").getValue().toString();

                        if (mMap != null) {
                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitude, longitude))
                                    .title(title)
                                    .snippet(id)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                    .visible(true)
                            );
                            allJobMarker.add(marker);
                        }
                    }
                }
                jobListAdapter = new JobListAdapter(MainActivity.this, (ArrayList<Job>) allJobList);
                recyclerView.setAdapter(jobListAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                String jobID = marker.getSnippet();
                Intent intent = new Intent(MainActivity.this, JobDetailsActivity.class);
                intent.putExtra("jobDetails_ID", jobID);
                startActivity(intent);
            }
        });
        enableMyLocationIfPermitted();

    }

    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null)
            mMap.setMyLocationEnabled(true);
    }

    private void getDeviceLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            currentLocation = task.getResult();
                            if (currentLocation != null){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),
                                        currentLocation.getLongitude()), 15));
                                addMapCircle(currentLocation, null);
                            }

                            else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback =  new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);

                                        currentLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),
                                                currentLocation.getLongitude()), 15));
                                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                        addMapCircle(currentLocation, null);
                                    }
                                };
                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    //student job only
    private void filterStudentJob() {
        if(radius == 0){
            Query query = FirebaseDatabase.getInstance().getReference("Job").orderByChild("job_title");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    allJobList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Job job = dataSnapshot1.getValue(Job.class);
                        if(job.getJob_stu().equals("Yes") && job.getJob_status().equals("Active")){
                            allJobList.add(job);
                        }
                    }
                    if(allJobList.size()==0){
                        Toast.makeText(MainActivity.this, "Sorry, we couldn't find any student job. "
                                , Toast.LENGTH_SHORT).show();
                    }else {
                        jobListAdapter = new JobListAdapter(MainActivity.this, (ArrayList<Job>) allJobList);
                        recyclerView.setAdapter(jobListAdapter);

                        setAllMarker(false);
                        for (Job job1 : allJobList) {
                            for (Marker marker2 : allJobMarker) {
                                if (job1.getJob_ID().equals(marker2.getSnippet()))
                                    marker2.setVisible(true);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Unable to get job list.", Toast.LENGTH_SHORT).show();
                }
            });
        }else if(radius !=0){
            Query query = FirebaseDatabase.getInstance().getReference("Job").orderByChild("job_title");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    setAllMarker(false);
                    circleList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Job job = dataSnapshot1.getValue(Job.class);
                        Double latitude = job.getLatitude();
                        Double longitude = job.getLongitude();

                        double distance = SphericalUtil.computeDistanceBetween(new LatLng(latitude, longitude),
                                new LatLng(currentLocation.getLatitude(),
                                        currentLocation.getLongitude()));
                        if (mMap != null && distance <= radius && job.getJob_stu().equals("Yes") && job.getJob_status().equals("Active")) {
                            circleList.add(job);
                            String id1 = job.getJob_ID();

                            for(Marker marker2: allJobMarker){
                                if (id1.equals(marker2.getSnippet()))
                                    marker2.setVisible(true);
                            }
                        }}
                    if(circleList.size()==0) {
                        Toast.makeText(MainActivity.this, "Sorry, we couldn't find any nearby job. "
                                , Toast.LENGTH_SHORT).show();
                    }
                    jobListAdapter = new JobListAdapter(MainActivity.this, (ArrayList<Job>) circleList);
                    recyclerView.setAdapter(jobListAdapter);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Unable to get job list.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //job list adapter for searching
    private void searchJob(final String searchText) {
        List<Job> filteredJob = new ArrayList<Job>();
        setAllMarker(false);
        if(radius ==0){
            for (Job job1 : allJobList) {
                if (job1.getJob_title().contains(searchText) || job1.getCompany_name().contains(searchText)) {
                    filteredJob.add(job1);
                    String id1 = job1.getJob_ID();

                    for(Marker marker2: allJobMarker){
                        if (id1.equals(marker2.getSnippet()))
                            marker2.setVisible(true);
                    }
                }
                if (searchText.isEmpty() || searchText.equals("") || searchText == "")
                    setAllMarker(true);
            }
        }else if(radius !=0 ){
            for (Job job1 : circleList) {
                if (job1.getJob_title().contains(searchText) || job1.getCompany_name().contains(searchText)) {
                    filteredJob.add(job1);
                    String id1 = job1.getJob_ID();

                    for(Marker marker2: allJobMarker){
                        if (id1.equals(marker2.getSnippet()))
                            marker2.setVisible(true);
                    }
                }
            }
        }
        if(filteredJob.size()==0){
            Toast.makeText(MainActivity.this, "Sorry, we couldn't find any job matching the keywords. "
                    , Toast.LENGTH_SHORT).show();
        }
        jobListAdapter = new JobListAdapter(MainActivity.this, (ArrayList<Job>) filteredJob);
        recyclerView.setAdapter(jobListAdapter);
    }

    private void setAllMarker(boolean bool){
        for(Marker marker2: allJobMarker)
            marker2.setVisible(bool);
    }

    private void addMapCircle(final Location currentLocation, Integer radius) {

        SharedPreferences sp = getSharedPreferences(PrefName, MODE_PRIVATE);
        if (sp.contains("prefMarker")) {
            marker_prefs = sp.getString("prefMarker", "showCircle");
            if (marker_prefs.equals("showAll"))
                radius = 0;
            else
                radius = sp.getInt("prefRadius", 100);
        }
        if (radius == null) {
            radius = 0;
        }
        final Integer finalRadius = radius;
        if(finalRadius !=0) {
            if (circle != null) {
                circle.setRadius(radius);
                Toast.makeText(MainActivity.this, "Your new radius is " + radius, Toast.LENGTH_SHORT).show();
            } else if (radius != null) {
                circle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(currentLocation.getLatitude(),
                                currentLocation.getLongitude()))
                        .radius(radius)
                        .strokeWidth(10)
                        .strokeColor(Color.RED)
                        .fillColor(Color.argb(70, 150, 50, 50)));
            }
            setAllMarker(false);
            Query query = FirebaseDatabase.getInstance().getReference("Job").orderByChild("job_title");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    setAllMarker(false);
                    circleList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Job job = dataSnapshot1.getValue(Job.class);
                        Double latitude = job.getLatitude();
                        Double longitude = job.getLongitude();

                        double distance = SphericalUtil.computeDistanceBetween(new LatLng(latitude, longitude),
                                new LatLng(currentLocation.getLatitude(),
                                        currentLocation.getLongitude()));
                        if (mMap != null && distance <= finalRadius && job.getJob_status().equals("Active")) {
                            circleList.add(job);
                            String id1 = job.getJob_ID();

                            for(Marker marker2: allJobMarker){
                                if (id1.equals(marker2.getSnippet()))
                                    marker2.setVisible(true);
                            }
                        }

                    }
                    if(circleList.size()==0) {
                        Toast.makeText(MainActivity.this, "Sorry, we couldn't find any nearby job. "
                                , Toast.LENGTH_SHORT).show();
                    }
                    jobListAdapter = new JobListAdapter(MainActivity.this, (ArrayList<Job>) circleList);
                    recyclerView.setAdapter(jobListAdapter);

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Unable to get job list.", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            jobListAdapter = new JobListAdapter(MainActivity.this, (ArrayList<Job>) allJobList);
            recyclerView.setAdapter(jobListAdapter);
        }
        /*for (Job job1 : allJobList) {
            Double latitude = job1.getLatitude();
            Double longitude = job1.getLongitude();
            id = job1.getJob_ID();


            for (int j = 0; j < allJobMarker.size(); j++) {
                marker1 = allJobMarker.get(j);
                id_marker = marker1.getSnippet();

                if (id.equals(id_marker)) {
                    double distance = SphericalUtil.computeDistanceBetween(new LatLng(latitude, longitude),
                            new LatLng(currentLocation.getLatitude(),
                                    currentLocation.getLongitude()));
                    if (mMap != null && distance <= finalRadius) {
                        marker1.setVisible(true);
                    }else if (finalRadius == 0) {
                        marker1.setVisible(true);
                    }
                    else{
                        allJobList.remove(job1);
                    }

                }
            }
        }*/

        if (sp.contains("prefChecked")) {
            studentChecked = prefs.getBoolean("prefChecked", false);
            if (studentChecked.equals(true))
                filterStudentJob();
        }
    }

    private void addCircleList(final String id2){
        Query query = FirebaseDatabase.getInstance().getReference("Job").orderByChild("job_title");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    Job job = s.getValue(Job.class);
                    if(job.getJob_status().equals("Active") && job.getJob_ID().equals(id2)) {
                        circleList.add(job);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public void hideNavigationItem(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        try {
            if (firebaseUser != null){
                menus.removeItem(R.id.nav_login);
                menus.removeItem(R.id.nav_signUp);
                menus.removeItem(R.id.nav_admin_dashboard);
            }else if(firebaseUser ==null){
                menus.removeItem(R.id.nav_signOut);
                menus.removeItem(R.id.nav_admin_dashboard);
                menus.removeItem(R.id.nav_profile);
                menus.removeItem(R.id.nav_chat);
                menus.removeItem(R.id.nav_application);
                menus.removeItem(R.id.nav_invoice);
                menus.removeItem(R.id.nav_employer_dashboard);
                menus.removeItem(R.id.nav_postJob);
            }else if(admin.equals(true)){
                menus.removeItem(R.id.nav_login);
                menus.removeItem(R.id.nav_signUp);
                menus.removeItem(R.id.nav_employer_dashboard);
                menus.removeItem(R.id.nav_searchJob);
                menus.removeItem(R.id.nav_postJob);
                menus.removeItem(R.id.nav_profile);
                menus.removeItem(R.id.nav_chat);
                menus.removeItem(R.id.switch_user);
            }
        }catch(Exception e){

        }
    }

    private void getPreferenceData(){
        SharedPreferences sp =getSharedPreferences(PrefName,MODE_PRIVATE);

        if(sp.contains("isEmployer")){
            emp=sp.getBoolean("isEmployer", false);
        }
        if(sp.contains("isApplicant")){
            app=sp.getBoolean("isApplicant", false);
        }
        if(sp.contains("isAdmin")){
            admin = sp.getBoolean("isAdmin", false);
        }
        if(sp.contains("prefChecked")){
            studentChecked = sp.getBoolean("prefChecked", false);
        }
        if(sp.contains("prefMarker")){
            marker_prefs = sp.getString("prefMarker", "showCircle");
            if(marker_prefs.equals("showAll"))
                radius = 0;
            else
                radius = sp.getInt("prefRadius",100);
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
                Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(MainActivity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_admin_dashboard:
                Intent intent4 = new Intent(MainActivity.this, AdminDashboardActivity.class);
                startActivity(intent4);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(MainActivity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(MainActivity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user:
                Intent intent9 = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent9);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putBoolean("isEmployer", true);
                editor.putBoolean("isApplicant", false);
                editor.apply();
                break;
            case R.id.nav_invoice:
                Intent intent10 = new Intent(MainActivity.this, DisplayInvoiceActivity.class);
                startActivity(intent10);
                break;
            case R.id.nav_application:
                Intent intent11 = new Intent(MainActivity.this, DisplayApplicationHistoryActivity.class);
                startActivity(intent11);
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(MainActivity.this, MainActivity.class);
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
                Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
                //startActivityForResult(intentSettings, 100);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
