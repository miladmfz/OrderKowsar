package com.kits.orderkowsar.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.adapters.InternetConnection;
import com.kits.orderkowsar.application.App;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.model.DatabaseHelper;

import java.io.File;
import java.util.Locale;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {


    final int PERMISSION_CODE = 1;
    final int PERMISSION_REQUEST_CODE = 1;
    Intent intent;
    CallMethod callMethod;
    Handler handler;
    DatabaseHelper dbh, dbhbase;

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


    //***************************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Config();


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button splash_refresh = findViewById(R.id.splash_refresh);
        InternetConnection ic = new InternetConnection(this);
        if (ic.has()) {
            try {
                init();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            final Dialog dialog1;
            dialog1 = new Dialog(this);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog1.setContentView(R.layout.connection_fail);
            dialog1.show();
            Button to_setting = dialog1.findViewById(R.id.to_setting);

            to_setting.setOnClickListener(view -> startActivity(new Intent(Settings.ACTION_SETTINGS)));
            splash_refresh.setVisibility(View.VISIBLE);

            callMethod.showToast(getString(R.string.toastvalue_dcmessage));
        }

        splash_refresh.setOnClickListener(view -> {
            finish();
            startActivity(getIntent());
        });

    }

    public void Config() {
        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        LinearLayoutCompat ll_activity = findViewById(R.id.splashactivity);
        if (callMethod.ReadString("LANG").equals("fa")) {
            ll_activity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            ll_activity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            ll_activity.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

    }

    @SuppressLint("SdCardPath")
    public void init() {


        if (callMethod.ReadString("ServerURLUse").equals("")) {
            callMethod.EditString("DatabaseName", "");
        }

        if (callMethod.firstStart()) {
            callMethod.EditBoolan("FirstStart", false);
            callMethod.EditString("Delay", "1000");
            callMethod.EditString("TitleSize", "12");
            callMethod.EditString("BodySize", "12");
            callMethod.EditString("Theme", "Green");

            callMethod.EditString("LANG", "");

            callMethod.EditString("AppBasketInfoCode", "0");

            callMethod.EditBoolan("RealAmount", false);
            callMethod.EditBoolan("ActiveStack", false);
            callMethod.EditBoolan("GoodAmount", false);

            callMethod.EditString("ServerURLUse", "");
            callMethod.EditString("SQLiteURLUse", "");
            callMethod.EditString("PersianCompanyNameUse", "");
            callMethod.EditString("EnglishCompanyNameUse", "");
            dbhbase = new DatabaseHelper(App.getContext(), "/data/data/com.kits.orderkowsar/databases/KowsarDb.sqlite");
            dbhbase.CreateActivationDb();


        }
        callMethod.EditString("TitleSize", "8");
        callMethod.EditString("BodySize", "8");
        callMethod.EditString("AppBasketInfoCode", "0");
        callMethod.EditString("MizType", "");
        callMethod.EditString("RstMizName", "");
        requestPermission();


    }

    private void Startapplication() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            File databasedir = new File(getApplicationInfo().dataDir + "/databases/" + callMethod.ReadString("EnglishCompanyNameUse"));
            File temp = new File(databasedir, "/tempDb");
            if (!temp.exists()) {
                if (callMethod.ReadString("DatabaseName").equals("")) {
                    handler = new Handler();
                    handler.postDelayed(() -> {
                        intent = new Intent(this, ChoiceDatabaseActivity.class);
                        startActivity(intent);
                        finish();
                    }, 2000);
                } else {
                    handler = new Handler();
                    handler.postDelayed(() -> {
                        intent = new Intent(this, NavActivity.class);
                        startActivity(intent);
                        finish();
                    }, 2000);
                }
            } else {

                callMethod.EditString("ServerURLUse", "");
                callMethod.EditString("SQLiteURLUse", "");
                callMethod.EditString("PersianCompanyNameUse", "");
                callMethod.EditString("EnglishCompanyNameUse", "");
                callMethod.EditString("DatabaseName", "");
                startActivity(getIntent());
                finish();
            }

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
        }


    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {

                try {
                    intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                    startActivityForResult(intent, 2296);
                } catch (Exception e) {
                    intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, 2296);
                }
            } else {
                Startapplication();
            }
        } else {
            runtimePermission();
        }
    }

    private void runtimePermission() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Startapplication();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    requestPermission();
                    callMethod.showToast(getString(R.string.toastvalue_permissiongrand));

                } else {
                    handler = new Handler();
                    handler.postDelayed(() -> {
                        intent = new Intent(this, SplashActivity.class);
                        finish();
                        startActivity(intent);
                    }, 2000);
                    callMethod.showToast(getString(R.string.toastvalue_permissiondoing));
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callMethod.showToast(getString(R.string.toastvalue_permissionresult));
            } else {
                callMethod.showToast(getString(R.string.toastvalue_unpermissionresult));
            }
            requestPermission();
        } else {
            throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = newBase.getSharedPreferences("profile", Context.MODE_PRIVATE);
        String currentLang = preferences.getString("LANG", "");
        if (currentLang.equals("")) {
            currentLang = getAppLanguage();
        }
        Context context = changeLanguage(newBase, currentLang);
        super.attachBaseContext(context);
    }

    public String getAppLanguage() {
        return Locale.getDefault().getLanguage();
    }


}
