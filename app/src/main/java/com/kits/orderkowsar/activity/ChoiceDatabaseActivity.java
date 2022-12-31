package com.kits.orderkowsar.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Status;
import com.google.android.material.button.MaterialButton;
import com.kits.orderkowsar.BuildConfig;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.application.App;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.databinding.ActivityChoiceDatabaseBinding;
import com.kits.orderkowsar.model.Activation;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.webService.APIClient_kowsar;
import com.kits.orderkowsar.webService.APIInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

public class ChoiceDatabaseActivity extends AppCompatActivity {

    APIInterface apiInterface = APIClient_kowsar.getCleint_log().create(APIInterface.class);
    CallMethod callMethod;
    Activation activation;
    DatabaseHelper dbh;
    DatabaseHelper dbhbase;
    TextView tv_rep;
    TextView tv_step;
    Dialog dialog;
    ArrayList<Activation> activations;
    Button btn_prog;
    Intent intent;
    int downloadId;


    ActivityChoiceDatabaseBinding binding;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChoiceDatabaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Config();

        try {
            init();
        } catch (Exception ignored) {
        }

    }

    //*****************************************************************************************
    @SuppressLint("SdCardPath")
    public void Config() {
        callMethod = new CallMethod(this);
        dialog = new Dialog(this);
        activation = new Activation();

        dbhbase = new DatabaseHelper(App.getContext(), "/data/data/com.kits.orderkowsar/databases/KowsarDb.sqlite");
        dbhbase.CreateActivationDb();

        if (callMethod.ReadString("LANG").equals("fa")) {
            binding.activitionactivity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            binding.activitionactivity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            binding.activitionactivity.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        dialog.setContentView(R.layout.rep_prog);
        tv_rep = dialog.findViewById(R.id.rep_prog_text);
        tv_step = dialog.findViewById(R.id.rep_prog_step);
        btn_prog = dialog.findViewById(R.id.rep_prog_btn);

    }

    @SuppressLint("SdCardPath")
    public void init() {

        activations = dbhbase.getActivation();
        binding.activitionVersion.setText(callMethod.NumberRegion(getString(R.string.textvalue_versionname) + BuildConfig.VERSION_NAME));
        for (Activation singleactive : activations) {
            CreateView(singleactive);
        }


        binding.activitionBtn.setOnClickListener(v -> {
            Call<RetrofitResponse> call1 = apiInterface.Activation("ActivationCode", Objects.requireNonNull(binding.activitionEdittext.getText()).toString());
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        activation = response.body().getActivations().get(0);
                        dbhbase.InsertActivation(activation);
                        finish();
                        startActivity(getIntent());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                }
            });

        });


    }

    public void DownloadRequest(Activation activation) {
        btn_prog.setOnClickListener(view -> DownloadRequest(activation));

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder().setDatabaseEnabled(true).build();

        PRDownloader.initialize(getApplicationContext(), config);

        // Setting timeout globally for the download network requests:
        PRDownloaderConfig config1 = PRDownloaderConfig.newBuilder().setReadTimeout(30_000).setConnectTimeout(30_000).build();
        PRDownloader.initialize(getApplicationContext(), config1);

        downloadId = PRDownloader.download(activation.getSQLiteURL(), activation.getDatabaseFolderPath(), "KowsarDbTemp.sqlite")

                .build().setOnStartOrResumeListener(() -> {
                    dialog.show();
                    dialog.setCancelable(false);
                }).setOnPauseListener(() -> {

                }).setOnCancelListener(() -> {
                    File DownloadTemp = new File(activation.getDatabaseFolderPath() + "/KowsarDbTemp.sqlite");
                    DownloadTemp.delete();
                })

                .setOnProgressListener(progress -> {
                    tv_rep.setText(getString(R.string.textvalue_downloading));
                    tv_step.setVisibility(View.VISIBLE);
                    tv_step.setText(callMethod.NumberRegion((((progress.currentBytes) * 100) / progress.totalBytes) + "/100"));
                })


                .start(new OnDownloadListener() {
                    @SuppressLint("SdCardPath")
                    @Override

                    public void onDownloadComplete() {
                        File DownloadTemp = new File(activation.getDatabaseFolderPath() + "/KowsarDbTemp.sqlite");


                        File CompletefILE = new File(activation.getDatabaseFolderPath() + "/KowsarDb.sqlite");
                        DownloadTemp.renameTo(CompletefILE);
                        callMethod.EditString("DatabaseName", activation.getDatabaseFilePath());
                        dbh = new DatabaseHelper(App.getContext(), callMethod.ReadString("DatabaseName"));
                        dbh.DatabaseCreate();

                        callMethod.EditString("PersianCompanyNameUse", activation.getPersianCompanyName());
                        callMethod.EditString("EnglishCompanyNameUse", activation.getEnglishCompanyName());
                        callMethod.EditString("ServerURLUse", activation.getServerURL());
                        intent = new Intent(App.getContext(), SplashActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                    }


                    @Override
                    public void onError(Error error) {
                        btn_prog.setVisibility(View.VISIBLE);
                        File DownloadTemp = new File(activation.getDatabaseFolderPath() + "/KowsarDbTemp.sqlite");
                        DownloadTemp.delete();
                        tv_step.setText(R.string.textvalue_disconnectmessage);
                    }
                });

    }

    @SuppressLint("SetTextI18n")
    public void CreateView(Activation singleactive) {

        String serverip = singleactive.getServerURL().substring(singleactive.getServerURL().indexOf("//") + 2, singleactive.getServerURL().indexOf("/login") - 6);


        LinearLayoutCompat ll_main = new LinearLayoutCompat(this);
        LinearLayoutCompat ll_tv = new LinearLayoutCompat(this);
        LinearLayoutCompat ll_btn = new LinearLayoutCompat(this);
        TextView tv_PersianCompanyName = new TextView(this);
        TextView tv_EnglishCompanyName = new TextView(this);
        TextView tv_ServerURL = new TextView(this);
        MaterialButton btn_login = new MaterialButton(this);
        MaterialButton btn_update = new MaterialButton(this);
        MaterialButton btn_gap = new MaterialButton(this);

        LinearLayoutCompat.LayoutParams margin_10 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        LinearLayoutCompat.LayoutParams margin_5 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);

        ll_main.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, (float) 0.3));
        ll_btn.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, (float) 0.7));
        tv_PersianCompanyName.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_EnglishCompanyName.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_ServerURL.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        btn_login.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, (float) 0.3));
        btn_update.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, (float) 0.3));
        btn_gap.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, (float) 0.4));

        tv_PersianCompanyName.setTextColor(ContextCompat.getColor(this, R.color.grey_800));
        tv_PersianCompanyName.setTextColor(ContextCompat.getColor(this, R.color.grey_800));
        tv_EnglishCompanyName.setTextColor(ContextCompat.getColor(this, R.color.grey_800));
        tv_ServerURL.setTextColor(ContextCompat.getColor(this, R.color.grey_800));


        ll_main.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_tv.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_btn.setOrientation(LinearLayoutCompat.HORIZONTAL);


        margin_10.setMargins(10, 10, 10, 10);
        margin_5.setMargins(5, 5, 5, 5);


        ll_main.setBackgroundResource(R.color.grey_20);
        tv_PersianCompanyName.setBackgroundResource(R.color.grey_20);
        tv_EnglishCompanyName.setBackgroundResource(R.color.grey_20);
        tv_ServerURL.setBackgroundResource(R.color.grey_20);
        btn_login.setBackgroundResource(R.color.white);
        btn_update.setBackgroundResource(R.color.white);
        btn_gap.setBackgroundResource(R.color.white);


        tv_PersianCompanyName.setTextSize(26);
        tv_EnglishCompanyName.setTextSize(16);
        tv_ServerURL.setTextSize(16);
        btn_login.setTextSize(18);
        btn_update.setTextSize(18);

        ll_main.setPadding(20, 20, 20, 20);
        ll_btn.setPadding(0, 20, 0, 0);

        ll_main.setWeightSum(1);
        ll_btn.setWeightSum(1);

        btn_gap.setVisibility(View.INVISIBLE);


        tv_PersianCompanyName.setText(callMethod.NumberRegion(singleactive.getPersianCompanyName()));
        tv_EnglishCompanyName.setText(getString(R.string.textvalue_imagefolername) + singleactive.getEnglishCompanyName());
        tv_ServerURL.setText(getString(R.string.textvalue_ipserver) + serverip);
        btn_login.setText(R.string.textvalue_login);
        btn_update.setText(R.string.textvalue_edit);


        btn_login.setOnClickListener(v -> {


            if (!new File(singleactive.getDatabaseFilePath()).exists()) {
                DownloadRequest(singleactive);
            } else {
                callMethod.EditString("PersianCompanyNameUse", singleactive.getPersianCompanyName());
                callMethod.EditString("EnglishCompanyNameUse", singleactive.getEnglishCompanyName());
                callMethod.EditString("ServerURLUse", singleactive.getServerURL());
                callMethod.EditString("DatabaseName", singleactive.getDatabaseFilePath());
                intent = new Intent(this, SplashActivity.class);
                startActivity(intent);
                finish();
            }
        });


        btn_update.setOnClickListener(v -> {

            Call<RetrofitResponse> call1 = apiInterface.Activation("ActivationCode", singleactive.getActivationCode());
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        activation = response.body().getActivations().get(0);
                        dbhbase.InsertActivation(activation);
                        finish();
                        startActivity(getIntent());

                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

                }
            });
        });


        ll_btn.addView(btn_login);
        ll_btn.addView(btn_gap);
        ll_btn.addView(btn_update);

        ll_tv.addView(tv_PersianCompanyName);
        ll_tv.addView(tv_EnglishCompanyName);
        ll_tv.addView(tv_ServerURL);

        ll_tv.addView(ll_btn, margin_5);

        ll_main.addView(ll_tv);


        binding.activitionLine.addView(ll_main, margin_10);
    }

    @Override
    protected void onDestroy() {
        if (PRDownloader.getStatus(downloadId) == Status.RUNNING) {
            PRDownloader.cancel(downloadId);
        }
        super.onDestroy();

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