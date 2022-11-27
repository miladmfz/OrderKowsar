package com.kits.orderkowsar.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.kits.orderkowsar.BuildConfig;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.application.Action;
import com.kits.orderkowsar.application.App;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.GoodGroup;
import com.kits.orderkowsar.model.NumberFunctions;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    APIInterface apiInterface;

    private Action action;
    private boolean doubleBackToExitPressedOnce = false;
    private Intent intent;
    CallMethod callMethod;
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    DatabaseHelper dbh;
    ArrayList<GoodGroup> menugrp;
    LinearLayoutCompat llsumfactor;
    Toolbar toolbar;
    NavigationView navigationView;
    TextView tv_versionname;
    TextView tv_dbname;
    TextView tv_brokercode;
    Button btn_changedb;
    TextView customer;
    TextView sumfac;

    TextView Getmizlist_btn0;
    TextView Getmizlist_btn1;
    TextView Getmizlist_btn2;
    TextView Getmizlist_btn3;
    TextView Getmizlist_btn4;

    Button btn_test;
    TextView tv_test;

    PersianCalendar persianCalendar = new PersianCalendar();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        init();


    }

    //************************************************************


    @SuppressLint("WrongViewCast")
    public void Config() {


        action = new Action(this);
        callMethod = new CallMethod(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

        toolbar = findViewById(R.id.MainActivity_toolbar);

        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.NavActivity_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.NavActivity_nav);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        tv_versionname = hView.findViewById(R.id.header_versionname);
        tv_dbname = hView.findViewById(R.id.header_dbname);
        tv_brokercode = hView.findViewById(R.id.header_brokercode);
        btn_changedb = hView.findViewById(R.id.header_changedb);



         Getmizlist_btn0 = findViewById(R.id.mainactivity_btn0);
         Getmizlist_btn1 = findViewById(R.id.mainactivity_btn1);
         Getmizlist_btn2 = findViewById(R.id.mainactivity_btn2);
         Getmizlist_btn3 = findViewById(R.id.mainactivity_btn3);
         Getmizlist_btn4 = findViewById(R.id.mainactivity_btn4);



    }



    public void init() {
        Config();


        tv_versionname.setText(NumberFunctions.PerisanNumber(BuildConfig.VERSION_NAME));
        tv_dbname.setText(callMethod.ReadString("PersianCompanyNameUse"));
        toolbar.setTitle(callMethod.ReadString("PersianCompanyNameUse"));



        Getmizlist_btn0.setOnClickListener(v -> {

            intent = new Intent(NavActivity.this, TableActivity.class);
            intent.putExtra("State", "0");
            startActivity(intent);
        });


        Getmizlist_btn1.setOnClickListener(v -> {

            intent = new Intent(NavActivity.this, TableActivity.class);
            intent.putExtra("State", "1");
            startActivity(intent);
        });


        Getmizlist_btn2.setOnClickListener(v -> {

            intent = new Intent(NavActivity.this, TableActivity.class);
            intent.putExtra("State", "2");
            startActivity(intent);
        });


        Getmizlist_btn3.setOnClickListener(v -> {

            intent = new Intent(NavActivity.this, TableActivity.class);
            intent.putExtra("State", "3");
            startActivity(intent);
        });


        Getmizlist_btn4.setOnClickListener(v -> {

            intent = new Intent(NavActivity.this, TableActivity.class);
            intent.putExtra("State", "4");
            startActivity(intent);
        });




        btn_changedb.setOnClickListener(v -> {
            callMethod.EditString("PersianCompanyNameUse", "");
            callMethod.EditString("EnglishCompanyNameUse", "");
            callMethod.EditString("ServerURLUse", "");
            callMethod.EditString("DatabaseName", "");
            intent = new Intent(this, SplashActivity.class);
            finish();
            startActivity(intent);
        });

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.NavActivity_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "برای خروج مجددا کلیک کنید", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        final int id = item.getItemId();

        if (id == R.id.aboutus) {
            intent = new Intent(this, AboutUsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_cfg) {
            intent = new Intent(this, RegistrationActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.NavActivity_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;


    }

}


