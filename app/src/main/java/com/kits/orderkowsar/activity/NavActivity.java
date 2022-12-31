package com.kits.orderkowsar.activity;


import static com.kits.orderkowsar.R.string.textvalue_exitmessage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.android.material.navigation.NavigationView;
import com.kits.orderkowsar.BuildConfig;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.application.Action;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;
import java.util.Locale;



public class NavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    APIInterface apiInterface;

    Action action;
    private boolean doubleBackToExitPressedOnce = false;
    private Intent intent;
    CallMethod callMethod;

    Toolbar toolbar;
    NavigationView navigationView;
    TextView tv_versionname;
    TextView tv_dbname;
    TextView tv_brokercode;
    Button btn_changedb;


    TextView Getmizlist_btn0;
    TextView Getmizlist_btn1;
    TextView Getmizlist_btn2;
    TextView Getmizlist_btn3;
    TextView Getmizlist_btn4;


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


        LinearLayoutCompat ll_activity_main = findViewById(R.id.mainactivity);

        DrawerLayout drawer = findViewById(R.id.NavActivity_drawer_layout);

        if ( callMethod.ReadString("LANG").equals("fa")) {
            ll_activity_main.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            drawer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if ( callMethod.ReadString("LANG").equals("ar")) {
            ll_activity_main.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            drawer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            ll_activity_main.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            drawer.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }



        toolbar = findViewById(R.id.MainActivity_toolbar);
        setSupportActionBar(toolbar);
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


        tv_versionname.setText(callMethod.NumberRegion(BuildConfig.VERSION_NAME));
        tv_dbname.setText(callMethod.ReadString("PersianCompanyNameUse"));
        toolbar.setTitle(callMethod.ReadString("PersianCompanyNameUse"));

        Getmizlist_btn0.setOnClickListener(v -> {

            intent = new Intent(NavActivity.this, TableActivity.class);
            intent.putExtra("State", "0");
            intent.putExtra("EditTable", "0");
            startActivity(intent);
        });


        Getmizlist_btn1.setOnClickListener(v -> {

            intent = new Intent(NavActivity.this, TableActivity.class);
            intent.putExtra("State", "1");
            intent.putExtra("EditTable", "0");
            startActivity(intent);
        });


        Getmizlist_btn2.setOnClickListener(v -> {

            intent = new Intent(NavActivity.this, TableActivity.class);
            intent.putExtra("State", "2");
            intent.putExtra("EditTable", "0");
            startActivity(intent);
        });


        Getmizlist_btn3.setOnClickListener(v -> {

            intent = new Intent(NavActivity.this, TableActivity.class);
            intent.putExtra("State", "3");
            intent.putExtra("EditTable", "0");
            startActivity(intent);
        });


        Getmizlist_btn4.setOnClickListener(v -> {

            intent = new Intent(NavActivity.this, TableActivity.class);
            intent.putExtra("State", "4");
            intent.putExtra("EditTable", "0");
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
        Toast.makeText(this, textvalue_exitmessage, Toast.LENGTH_SHORT).show();

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




    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = newBase.getSharedPreferences("profile", Context.MODE_PRIVATE);
        String currentLang = preferences.getString("LANG", "");
        if (currentLang.equals("")){
            currentLang=getAppLanguage();
        }
        Context context = changeLanguage(newBase, currentLang);
        super.attachBaseContext(context);
    }

    @SuppressLint("ObsoleteSdkInt")
    public static ContextWrapper changeLanguage(Context context, String lang) {

        Locale currentLocal;
        Resources res = context.getResources();
        Configuration conf = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentLocal = conf.getLocales().get(0);
        } else {
            currentLocal = conf.locale;
        }

        if (!lang.equals("") && !currentLocal.getLanguage().equals(lang)) {
            Locale newLocal = new Locale(lang);
            Locale.setDefault(newLocal);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                conf.setLocale(newLocal);
            } else {
                conf.locale = newLocal;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                context = context.createConfigurationContext(conf);
            } else {
                res.updateConfiguration(conf, context.getResources().getDisplayMetrics());
            }


        }

        return new ContextWrapper(context);
    }

    public String getAppLanguage() {
        return Locale.getDefault().getLanguage();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startActivity(getIntent());
        finish();

    }
}


