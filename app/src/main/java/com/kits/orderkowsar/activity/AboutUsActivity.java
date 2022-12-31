package com.kits.orderkowsar.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.databinding.ActivityAboutusBinding;

import java.util.Locale;


public class AboutUsActivity extends AppCompatActivity {
    CallMethod callMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callMethod = new CallMethod(this);
        ActivityAboutusBinding binding = ActivityAboutusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (callMethod.ReadString("LANG").equals("fa")) {
            binding.aboutusactivity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            binding.aboutusactivity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            binding.aboutusactivity.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        binding.tv1.setText(callMethod.NumberRegion(binding.tv1.getText().toString()));
        binding.tv2.setText(callMethod.NumberRegion(binding.tv2.getText().toString()));
        binding.tv3.setText(callMethod.NumberRegion(binding.tv3.getText().toString()));


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
}
