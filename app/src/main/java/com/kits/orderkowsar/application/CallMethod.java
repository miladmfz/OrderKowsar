package com.kits.orderkowsar.application;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kits.orderkowsar.BuildConfig;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.model.UserInfo;
import com.kits.orderkowsar.webService.APIClient_kowsar;
import com.kits.orderkowsar.webService.APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;


public class CallMethod extends Application {
    private final SharedPreferences shPref;
    private SharedPreferences.Editor sEdit;
    Context context;

    Toast toast;
    public CallMethod(Context mContext) {
        this.context = mContext;
        this.shPref = context.getSharedPreferences("profile", Context.MODE_PRIVATE);
    }

    public void EditString(String Key, String Value) {
        sEdit = shPref.edit();
        sEdit.putString(Key, Value);
        sEdit.apply();
    }

    public String ReadString(String Key) {

        return shPref.getString(Key, "");
    }

    public boolean ReadBoolan(String Key) {
        return shPref.getBoolean(Key, true);
    }

    public void EditBoolan(String Key, boolean Value) {
        sEdit = shPref.edit();
        sEdit.putBoolean(Key, Value);
        sEdit.apply();
    }

    public boolean firstStart() {

        return shPref.getBoolean("FirstStart", true);
    }

    public void showToast(String string) {
        if (toast!=null)
            toast.cancel();
        toast = Toast.makeText(context, string, Toast.LENGTH_LONG);
        toast.show();
    }

    public void saveArrayList(ArrayList<String> list, String key) {

        Gson gson = new Gson();
        String json = gson.toJson(list);
        EditString(key, json);
    }

    public ArrayList<String> getArrayList(String key) {
        Gson gson = new Gson();
        String json = shPref.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void Create() {
        sEdit.putBoolean("FirstStart", false);
        EditString("ItemsShow", "3");
        sEdit.apply();
    }

    public void ErrorLog(String ErrorStr) {

        @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(context
                .getContentResolver(), Settings.Secure.ANDROID_ID);

        Log.e("ErrorLog", ErrorStr);

        PersianCalendar calendar1 = new PersianCalendar();
        String version = BuildConfig.VERSION_NAME;

        DatabaseHelper dbh = new DatabaseHelper(context, ReadString("DatabaseName"));
        UserInfo auser = new UserInfo();
        try {
            auser = dbh.LoadPersonalInfo();
        } catch (Exception e) {
            auser.setBrokerCode("0");
        }


        APIInterface apiInterface = APIClient_kowsar.getCleint_log().create(APIInterface.class);
        Call<RetrofitResponse> cl = apiInterface.Errorlog("Errorlog"
                , ErrorStr
                , auser.getBrokerCode()
                , android_id
                , ReadString("PersianCompanyNameUse")
                , calendar1.getPersianShortDateTime()
                , version);
        cl.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {
                assert response.body() != null;
            }


            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                //ErrorLog(t.getMessage());
            }
        });

    }

}

