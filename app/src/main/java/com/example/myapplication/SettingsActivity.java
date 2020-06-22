package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    private TextView textView;
    private SeekBar seekBar;
    private int radius;
    private Button btnSet;
    private SharedPreferences prefs;
    private Boolean emp, app, admin, checkBox;
    private Menu menu;
    private FirebaseUser firebaseUser;
    private RadioButton radioBtn1, radioBtn2;
    private RelativeLayout relativeLayout;
    private CheckBox student;
    private String marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        seekBar = findViewById(R.id.seekBar);
        textView= findViewById(R.id.textView16);
        radioBtn1 = findViewById(R.id.radioButton1);
        radioBtn2 = findViewById(R.id.radioButton2);
        relativeLayout = findViewById(R.id.relLayout1);
        student = findViewById(R.id.checkBoxStu);

        prefs=getSharedPreferences("PrefsFile",MODE_PRIVATE);
        getPreferenceData();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnSet = findViewById(R.id.btnSet);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radius = Integer.parseInt(textView.getText().toString());
                SharedPreferences.Editor editor=prefs.edit();
                editor.putInt("prefRadius", radius);

                if(student.isChecked())
                    editor.putBoolean("prefChecked", true);
                else
                    editor.putBoolean("prefChecked", false);

                editor.apply();
                Intent intentMap = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intentMap);
            }
        });


    }

    private void getPreferenceData(){
        SharedPreferences sp =getSharedPreferences("PrefsFile",MODE_PRIVATE);

        if(sp.contains("prefRadius")){
            int radius=prefs.getInt("prefRadius",100);
            textView.setText(Integer.toString(radius));
            seekBar.setProgress(radius);
            seekBar.setMax(10000);
        }
        if(sp.contains("prefMarker")){
            marker=sp.getString("prefMarker","showAll");
            if(marker.equals("showCircle"))
                radioBtn1.setChecked(true);
            else{
                relativeLayout.setEnabled(false);
                seekBar.setEnabled(false);
                radioBtn2 .setChecked(true);
            }
        }
        if(sp.contains("prefChecked")){
            checkBox=sp.getBoolean("prefChecked", false);
            if(checkBox.equals(true))
                student.setChecked(true);
            else
                student.setChecked(false);
        }
        if(sp.contains("isEmployer"))
            emp=sp.getBoolean("isEmployer", false);
        if(sp.contains("isApplicant"))
            app=sp.getBoolean("isApplicant", false);
        if(sp.contains("isAdmin"))
            admin=sp.getBoolean("isAdmin", false);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radioButton1:
                if (checked){
                    relativeLayout.setEnabled(true);
                    seekBar.setEnabled(true);
                    SharedPreferences.Editor editor=prefs.edit();
                    editor.putString("prefMarker", "showCircle");
                    editor.apply();
                }
                break;
            case R.id.radioButton2:
                if (checked){
                    relativeLayout.setEnabled(false);
                    seekBar.setEnabled(false);

                    SharedPreferences.Editor editor=prefs.edit();
                    editor.putString("prefMarker", "showAll");
                    editor.apply();
                }
                break;
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


}
