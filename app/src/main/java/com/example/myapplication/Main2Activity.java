package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.myapplication.EmployerDashboard.EmployerDashboardActivity;
import com.example.myapplication.Invoice.PaymentInvoiceDetailsActivity;
import com.example.myapplication.Job.Job;
import com.example.myapplication.Job.JobDetailsActivity;
import com.example.myapplication.Job.PostJobActivity;
import com.example.myapplication.Message.DisplayUserActivity;
import com.example.myapplication.User.LoginActivity;
import com.example.myapplication.User.ProfileActivity;
import com.example.myapplication.User.SignUpActivity;
import com.example.myapplication.User.User;
import com.example.myapplication.User.ViewProfileActivity;
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
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements 
        NavigationView.OnNavigationItemSelectedListener ,
        OnMapReadyCallback {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar = null;
    private GoogleMap mMap;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private PlacesClient placesClient;
    private LocationCallback locationCallback;
    private final float DEFAULT_ZOOM =18;
    private View mapView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private List<User> allUserList;
    private List<Marker> allUserMarker;
    private Marker marker, marker1;
    private DatabaseReference userRef;
    private String id, username, radius, marker_prefs, id_marker;
    private int radius_int;
    private SharedPreferences prefs;
    private Boolean app, emp, admin;
    private Menu menu;
    private FirebaseUser firebaseUser;
    private Circle circle ;
    private String PrefName = "PrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout2);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view2);
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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        allUserList = new ArrayList<>();
        allUserMarker = new ArrayList<>();
        userRef = FirebaseDatabase.getInstance().getReference("User");
        userRef.push().setValue(marker);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Main2Activity.this);
        Places.initialize(Main2Activity.this, getString(R.string.google_maps_api));
        placesClient = Places.createClient(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allUserList.clear();
                allUserMarker.clear();
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    User user = s.getValue(User.class);
                    if(user.getStatus().equals("Active")){
                        allUserList.add(user);
                        for (int i = 0; i < allUserList.size(); i++) {
                            String lat = s.child("latitude").getValue().toString();
                            String lng = s.child("longitude").getValue().toString();
                            Double latitude = Double.parseDouble(lat);
                            Double longitude = Double.parseDouble(lng);
                            id = s.child("id").getValue().toString();
                            username = s.child("username").getValue().toString();

                            if (mMap != null) {
                                marker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(latitude, longitude))
                                        .title(username)
                                        .snippet(id)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                );
                                allUserMarker.add(marker);
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
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
        SettingsClient settingsClient = LocationServices.getSettingsClient(Main2Activity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(Main2Activity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(Main2Activity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(Main2Activity.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                String userID = marker.getSnippet();
                Intent intent = new Intent(Main2Activity.this, ViewProfileActivity.class);
                intent.putExtra("user_ID", userID);
                intent.putExtra("attempt", "true");
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
                            if (currentLocation != null) {
                                addMapCircle2(currentLocation, null);

                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);

                                        currentLocation = locationResult.getLastLocation();

                                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                        addMapCircle2(currentLocation, null);

                                    }
                                };
                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                            }
                        } else {
                            Toast.makeText(Main2Activity.this, "unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addMapCircle2(final Location currentLocation, Integer radius) {
        SharedPreferences sp = getSharedPreferences(PrefName, MODE_PRIVATE);
        if (sp.contains("prefMarker")) {
            marker_prefs = sp.getString("prefMarker", "showCircle");
            if (marker_prefs.equals("showAll"))
                radius = 0;
            else
                radius = sp.getInt("prefRadius", 100);
        }
        if (circle != null) {
            circle.setRadius(radius);
            Toast.makeText(Main2Activity.this, "Your new radius is " + radius, Toast.LENGTH_SHORT).show();
        } else if (radius != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),
                    currentLocation.getLongitude()), 15));
            circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(currentLocation.getLatitude(),
                            currentLocation.getLongitude()))
                    .radius(radius)
                    .strokeWidth(10)
                    .strokeColor(Color.RED)
                    .fillColor(Color.argb(70, 150, 50, 50)));
        }else if(radius == null){
            radius = 0;
        }
        final Integer finalRadius = radius;
        setAllMarker2(false);
        for (int i = 0; i < allUserList.size(); i++) {
            Double latitude = allUserList.get(i).getLatitude();
            Double longitude = allUserList.get(i).getLongitude();
            id = allUserList.get(i).getId();

            for (int j = 0; j < allUserMarker.size(); j++) {
                marker1 = allUserMarker.get(j);
                id_marker = marker1.getSnippet();

                if (id.equals(id_marker)) {
                    double distance = SphericalUtil.computeDistanceBetween(new LatLng(latitude, longitude),
                            new LatLng(currentLocation.getLatitude(),
                                    currentLocation.getLongitude()));
                    if (mMap != null && distance <= finalRadius)
                        marker1.setVisible(true);
                    else if (finalRadius == 0)
                        marker1.setVisible(true);
                }
            }
        }
    }

    private void setAllMarker2(boolean bool){
        for(Marker marker2: allUserMarker)
            marker2.setVisible(bool);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_login:
                Intent intent1 = new Intent(Main2Activity.this, LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_signUp:
                Intent intent2 = new Intent(Main2Activity.this, SignUpActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_employer_dashboard:
                Intent intent3 = new Intent(Main2Activity.this, EmployerDashboardActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_searchJob:
                Intent intent5 = new Intent(Main2Activity.this, Main2Activity.class);
                startActivity(intent5);
                break;
            case R.id.nav_postJob:
                Intent intent6 = new Intent(Main2Activity.this, PostJobActivity.class);
                startActivity(intent6);
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(Main2Activity.this, ProfileActivity.class);
                startActivity(intent7);
                break;
            case R.id.nav_chat:
                Intent intent8 = new Intent(Main2Activity.this, DisplayUserActivity.class);
                startActivity(intent8);
                break;
            case R.id.switch_user:
                Intent intent9 = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(intent9);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putBoolean("isEmployer", false);
                editor.putBoolean("isApplicant", true);
                editor.apply();
                break;
            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent12 = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(intent12);
                break;
        }

        DrawerLayout drawer2 = (DrawerLayout) findViewById(R.id.drawer_layout2);
        drawer2.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings2_menu_item, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings2:
                Intent intentSettings2 = new Intent(Main2Activity.this, Settings2Activity.class);
                startActivity(intentSettings2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
