package com.kits.orderkowsar.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.adapters.ObjectTypeAdapter;
import com.kits.orderkowsar.application.Action;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.databinding.ActivityRegistrationBinding;
import com.kits.orderkowsar.model.BasketInfo;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.NumberFunctions;
import com.kits.orderkowsar.model.ObjectType;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.model.SellBroker;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegistrationActivity extends AppCompatActivity {

    DatabaseHelper dbh;
    CallMethod callMethod;
    Action action;
    ActivityRegistrationBinding binding;
    APIInterface apiInterface;
    ArrayList<String> lang_array = new ArrayList<>();
    ArrayList<String> SellBroker_Names = new ArrayList<>();
    ArrayList<SellBroker> SellBrokers = new ArrayList<>();

    Integer lang_position = 0;

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
//*******************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Config();
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void Config() {

        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        action = new Action(this);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        SellBroker_Names.clear();
        if (callMethod.ReadString("LANG").equals("fa")) {
            binding.orderRegistrActivity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            binding.orderRegistrActivity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            binding.orderRegistrActivity.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        Call<RetrofitResponse> call1 = apiInterface.GetSellBroker("GetSellBroker");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    SellBrokers.clear();
                    SellBrokers = response.body().getSellBrokers();
                    SellBroker sellBroker= new SellBroker();
                    sellBroker.setBrokerCode("0");
                    sellBroker.setBrokerNameWithoutType("بازاریاب تعریف نشده");
                    SellBrokers.add(sellBroker);
                    for (SellBroker sb : SellBrokers) {
                        SellBroker_Names.add(sb.getBrokerNameWithoutType());
                    }
                    brokerViewConfig();
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                SellBroker sellBroker= new SellBroker();
                sellBroker.setBrokerCode("0");
                sellBroker.setBrokerNameWithoutType("ویتری تعریف نشده");
                SellBrokers.add(sellBroker);
                brokerViewConfig();

            }
        });


        lang_array.add(getString(R.string.textvalue_langdefult));
        lang_array.add(getString(R.string.textvalue_langenglish));
        lang_array.add(getString(R.string.textvalue_langpersian));
        lang_array.add(getString(R.string.textvalue_langarabic));

        switch (callMethod.ReadString("LANG")) {
            case "":
                lang_position = 0;
                break;
            case "en":
                lang_position = 1;
                break;
            case "fa":
                lang_position = 2;
                break;
            case "ar":
                lang_position = 3;
                break;
        }

    }

    public void brokerViewConfig() {
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SellBroker_Names);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.ordRegistrASpinnerbroker.setAdapter(spinner_adapter);
        int possellbroker=0;
        for (SellBroker sellBroker:SellBrokers){
            if (sellBroker.getBrokerCode().equals(dbh.ReadConfig("BrokerCode"))){
                possellbroker=SellBrokers.indexOf(sellBroker);
            }
        }

        binding.ordRegistrASpinnerbroker.setSelection(possellbroker);
        binding.ordRegistrASpinnerbroker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                dbh.SaveConfig("BrokerCode",SellBrokers.get(position).getBrokerCode());
                binding.ordRegistrABroker.setText(callMethod.NumberRegion(dbh.ReadConfig("BrokerCode")));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void init() {


        binding.ordRegistrABroker.setText(callMethod.NumberRegion(dbh.ReadConfig("BrokerCode")));
        binding.ordRegistrAGroupcode.setText(callMethod.NumberRegion(dbh.ReadConfig("GroupCodeDefult")));
        binding.ordRegistrADelay.setText(callMethod.NumberRegion(callMethod.ReadString("Delay")));
        binding.ordRegistrADbname.setText(callMethod.NumberRegion(callMethod.ReadString("PersianCompanyNameUse")));
        binding.ordRegistrATitlesize.setText(callMethod.NumberRegion(callMethod.ReadString("TitleSize")));

        binding.ordRegistrAActivereserv.setChecked(callMethod.ReadBoolan("ReserveActive"));
        binding.ordRegistrACanfreetable.setChecked(callMethod.ReadBoolan("CanFreeTable"));


        binding.ordRegistrACanfreetable.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("CanFreeTable")) {
                callMethod.EditBoolan("CanFreeTable", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("CanFreeTable", true);
                callMethod.showToast("بله");
            }
        });

        binding.ordRegistrAActivereserv.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("ReserveActive")) {
                callMethod.EditBoolan("ReserveActive", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("ReserveActive", true);
                callMethod.showToast("بله");
            }
        });



        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lang_array);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.ordRegistrASpinnerlang.setAdapter(spinner_adapter);

        binding.ordRegistrASpinnerlang.setSelection(lang_position);
        binding.ordRegistrASpinnerlang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        callMethod.EditString("LANG", "");
                        break;
                    case 1:
                        callMethod.EditString("LANG", "en");
                        break;
                    case 2:
                        callMethod.EditString("LANG", "fa");
                        break;
                    case 3:
                        callMethod.EditString("LANG", "ar");
                        break;
                }

                if (!(lang_position == position)) {
                    startActivity(getIntent());
                    finish();
                }

            }









            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        binding.ordRegistrADefultsetting.setOnClickListener(v -> {

            Dialog dialogProg = new Dialog(this);
            dialogProg.setContentView(R.layout.rep_prog);
            TextView tv_rep = dialogProg.findViewById(R.id.rep_prog_text);
            tv_rep.setText(R.string.textvalue_receiveinformation);
            dialogProg.show();
            Call<RetrofitResponse> call1 = apiInterface.kowsar_info("kowsar_info", "AppOrder_DefaultGroupCode");
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;

                        Log.e("kowsar",response.body().getText());
                        if (!response.body().getText().equals(dbh.ReadConfig("GroupCodeDefult"))) {
                            dbh.SaveConfig("GroupCodeDefult", response.body().getText());
                            binding.ordRegistrAGroupcode.setText(callMethod.NumberRegion(dbh.ReadConfig("GroupCodeDefult")));

                            Call<RetrofitResponse> call2 = apiInterface.kowsar_info("kowsar_info", "AppOrder_CanFree_FromTablet");
                            call2.enqueue(new Callback<RetrofitResponse>() {
                                @Override
                                public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                                    if (response.isSuccessful()) {
                                        assert response.body() != null;

                                        Log.e("kowsar",response.body().getText());
                                        callMethod.EditBoolan("CanFreeTable", !response.body().getText().equals("0"));
                                        binding.ordRegistrACanfreetable.setChecked(callMethod.ReadBoolan("CanFreeTable"));
                                        callMethod.showToast(getString(R.string.textvalue_resived));
                                        dialogProg.dismiss();
                                    }
                                }

                                @Override
                                public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                                }
                            });
                        }
                        dialogProg.dismiss();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                }
            });
        });


        binding.ordRegistrABtn.setOnClickListener(view -> {

            callMethod.EditString("TitleSize", NumberFunctions.EnglishNumber(binding.ordRegistrATitlesize.getText().toString()));
            callMethod.EditString("BodySize", NumberFunctions.EnglishNumber(binding.ordRegistrABodysize.getText().toString()));
            callMethod.EditString("Delay", NumberFunctions.EnglishNumber(binding.ordRegistrADelay.getText().toString()));

            if (!dbh.ReadConfig("BrokerCode").equals(NumberFunctions.EnglishNumber(binding.ordRegistrABroker.getText().toString()))) {
                dbh.SaveConfig("BrokerCode", NumberFunctions.EnglishNumber(binding.ordRegistrABroker.getText().toString()));
            }
            if (!dbh.ReadConfig("GroupCodeDefult").equals(NumberFunctions.EnglishNumber(binding.ordRegistrAGroupcode.getText().toString()))) {
                dbh.SaveConfig("GroupCodeDefult", NumberFunctions.EnglishNumber(binding.ordRegistrAGroupcode.getText().toString()));
            }
            finish();

        });


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
