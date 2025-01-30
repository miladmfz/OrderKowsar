package com.kits.orderkowsar.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.adapters.ObjectTypeAdapter;
import com.kits.orderkowsar.adapters.ThemeSpinnerAdapter;
import com.kits.orderkowsar.application.Action;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.databinding.ActivityRegistrationBinding;
import com.kits.orderkowsar.model.BasketInfo;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.NumberFunctions;
import com.kits.orderkowsar.model.ObjectType;
import com.kits.orderkowsar.model.PosDriver;
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

    private static final String PREFS_NAME = "ThemePrefs";
    private static final String THEME_KEY = "selectedTheme";
    private int selectedTheme;

    // Theme names
    private static final String[] themeNames = {
            "Default", "Blue", "Red", "Green", "Gray", "Yellow", "Pink", "Purple",
            "BlueOcean", "SunsetOrange", "ForestGreen", "RoyalPurple", "SweetPink",
            "GoldenYellow", "EarthyBrown", "SkyBlue", "FieryRed", "CalmGray",
            "VibrantCyan", "TropicalTeal", "CoralPeach", "LemonLime", "BoldIndigo",
            "RichAmber", "MidnightBlue", "SoftLavender", "WarmBeige", "NeonGreen"
    };

    // Theme colors (primary, secondary, surface)
    private static final int[][] themeColors = {
            {Color.parseColor("#FFFFFF"), Color.parseColor("#E0E0E0"), Color.parseColor("#F5F5F5")}, // Default
            {Color.parseColor("#1976D2"), Color.parseColor("#BBDEFB"), Color.parseColor("#E3F2FD")}, // Blue
            {Color.parseColor("#D32F2F"), Color.parseColor("#FFCDD2"), Color.parseColor("#FFEBEE")}, // Red
            {Color.parseColor("#388E3C"), Color.parseColor("#C8E6C9"), Color.parseColor("#E8F5E9")}, // Green
            {Color.parseColor("#9E9E9E"), Color.parseColor("#E0E0E0"), Color.parseColor("#F5F5F5")}, // Gray
            {Color.parseColor("#FFC107"), Color.parseColor("#FFECB3"), Color.parseColor("#FFF8E1")}, // Yellow
            {Color.parseColor("#E91E63"), Color.parseColor("#F8BBD0"), Color.parseColor("#FCE4EC")}, // Pink
            {Color.parseColor("#673AB7"), Color.parseColor("#D1C4E9"), Color.parseColor("#EDE7F6")}, // Purple
            {Color.parseColor("#1976D2"), Color.parseColor("#BBDEFB"), Color.parseColor("#E3F2FD")}, // BlueOcean
            {Color.parseColor("#FF5722"), Color.parseColor("#FFCCBC"), Color.parseColor("#FBE9E7")}, // SunsetOrange
            {Color.parseColor("#388E3C"), Color.parseColor("#C8E6C9"), Color.parseColor("#E8F5E9")}, // ForestGreen
            {Color.parseColor("#673AB7"), Color.parseColor("#D1C4E9"), Color.parseColor("#EDE7F6")}, // RoyalPurple
            {Color.parseColor("#E91E63"), Color.parseColor("#F8BBD0"), Color.parseColor("#FCE4EC")}, // SweetPink
            {Color.parseColor("#FFC107"), Color.parseColor("#FFECB3"), Color.parseColor("#FFF8E1")}, // GoldenYellow
            {Color.parseColor("#795548"), Color.parseColor("#D7CCC8"), Color.parseColor("#EFEBE9")}, // EarthyBrown
            {Color.parseColor("#03A9F4"), Color.parseColor("#B3E5FC"), Color.parseColor("#E1F5FE")}, // SkyBlue
            {Color.parseColor("#D32F2F"), Color.parseColor("#FFCDD2"), Color.parseColor("#FFEBEE")}, // FieryRed
            {Color.parseColor("#9E9E9E"), Color.parseColor("#E0E0E0"), Color.parseColor("#F5F5F5")}, // CalmGray
            {Color.parseColor("#00BCD4"), Color.parseColor("#B2EBF2"), Color.parseColor("#E0F7FA")}, // VibrantCyan
            {Color.parseColor("#00796B"), Color.parseColor("#B2DFDB"), Color.parseColor("#E0F2F1")}, // TropicalTeal
            {Color.parseColor("#FF7043"), Color.parseColor("#FFAB91"), Color.parseColor("#FBE9E7")}, // CoralPeach
            {Color.parseColor("#CDDC39"), Color.parseColor("#F0F4C3"), Color.parseColor("#F9FBE7")}, // LemonLime
            {Color.parseColor("#3F51B5"), Color.parseColor("#C5CAE9"), Color.parseColor("#E8EAF6")}, // BoldIndigo
            {Color.parseColor("#FFC107"), Color.parseColor("#FFD740"), Color.parseColor("#FFF8E1")}, // RichAmber
            {Color.parseColor("#303F9F"), Color.parseColor("#7986CB"), Color.parseColor("#E8EAF6")}, // MidnightBlue
            {Color.parseColor("#CE93D8"), Color.parseColor("#E1BEE7"), Color.parseColor("#F3E5F5")}, // SoftLavender
            {Color.parseColor("#A1887F"), Color.parseColor("#D7CCC8"), Color.parseColor("#EFEBE9")}, // WarmBeige
            {Color.parseColor("#00C853"), Color.parseColor("#69F0AE"), Color.parseColor("#E8F5E9")}  // NeonGreen
    };

    // Theme styles
    private static final int[] themeArray = {
            R.style.DefaultTheme, R.style.BlueTheme, R.style.RedTheme, R.style.GreenTheme, R.style.GrayTheme,
            R.style.YellowTheme, R.style.PinkTheme, R.style.PurpleTheme, R.style.BlueOceanTheme,
            R.style.SunsetOrangeTheme, R.style.ForestGreenTheme, R.style.RoyalPurpleTheme, R.style.SweetPinkTheme,
            R.style.GoldenYellowTheme, R.style.EarthyBrownTheme, R.style.SkyBlueTheme, R.style.FieryRedTheme,
            R.style.CalmGrayTheme, R.style.VibrantCyanTheme, R.style.TropicalTealTheme, R.style.CoralPeachTheme,
            R.style.LemonLimeTheme, R.style.BoldIndigoTheme, R.style.RichAmberTheme, R.style.MidnightBlueTheme,
            R.style.SoftLavenderTheme, R.style.WarmBeigeTheme, R.style.NeonGreenTheme
    };














    DatabaseHelper dbh;
    CallMethod callMethod;
    Action action;
    ActivityRegistrationBinding binding;
    APIInterface apiInterface;
    ArrayList<String> lang_array = new ArrayList<>();
    ArrayList<String> SellBroker_Names = new ArrayList<>();
    ArrayList<SellBroker> SellBrokers = new ArrayList<>();
    ArrayList<PosDriver> posDrivers = new ArrayList<>();
    ArrayList<String> pos_list=new ArrayList<>();

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
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.DefaultTheme));

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

        binding.ordRegistrAPozcode.setText(callMethod.NumberRegion(callMethod.ReadString("PosCode")));
        binding.ordRegistrAPozname.setText(callMethod.NumberRegion(callMethod.ReadString("PosName")));


        binding.ordRegistrAActivereserv.setChecked(callMethod.ReadBoolan("ReserveActive"));
        binding.ordRegistrACanfreetable.setChecked(callMethod.ReadBoolan("CanFreeTable"));
        binding.ordRegistrAPospayment.setChecked(callMethod.ReadBoolan("PosPayment"));


        binding.ordRegistrAPospayment.setOnCheckedChangeListener((compoundButton, b) -> {

            if ((callMethod.ReadString("PosCode").equals("0"))||(callMethod.ReadString("PosName").equals(""))){
                binding.ordRegistrAPospayment.setChecked(false);
                callMethod.showToast("دستگاه پوز امتخاب نشده");
            }else {
                if (callMethod.ReadBoolan("PosPayment")) {

                    callMethod.EditBoolan("PosPayment", false);
                    callMethod.showToast("خیر");

                } else {

                    callMethod.EditBoolan("PosPayment", true);
                    callMethod.showToast("بله");

                }
            }

        });


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




        Call<RetrofitResponse> call1;
        call1=apiInterface.OrderGetPosDriver("OrderGetPosDriver");


        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                pos_list.add("بدون پرینتر");

                if(response.isSuccessful()) {
                    assert response.body() != null;
                    posDrivers=response.body().getPosDrivers();
                    for ( PosDriver posDriver: posDrivers) {
                        pos_list.add(posDriver.getPosName());
                    }
                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(RegistrationActivity.this,
                            android.R.layout.simple_spinner_item, pos_list);
                    spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.ordRegistrASpinnerpoz.setAdapter(spinner_adapter);
                    int targetIndex = 0;
                    for (int i = 0; i < pos_list.size(); i++) {
                        if (pos_list.get(i).equals(callMethod.ReadString("PosName"))) {
                            targetIndex = i;
                            break;
                        }
                    }
                    binding.ordRegistrASpinnerpoz.setSelection(targetIndex); // Set selection baraye item ke matnash "همه" ast

                }

            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                Log.e("kowsar_onFailure",t.getMessage());
            }
        });

        binding.ordRegistrASpinnerpoz.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                for ( PosDriver posDriver: posDrivers) {
                    if(posDriver.getPosName().equals(pos_list.get(position))){
                        callMethod.EditString("PosName",posDriver.getPosName());
                        callMethod.EditString("PosCode",posDriver.getPosDriverCode());
                        binding.ordRegistrAPozcode.setText(callMethod.ReadString("PosName"));
                        binding.ordRegistrAPozname.setText(callMethod.ReadString("PosCode"));
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
            Call<RetrofitResponse> call2 = apiInterface.kowsar_info("kowsar_info", "AppOrder_DefaultGroupCode");
            call2.enqueue(new Callback<RetrofitResponse>() {
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



        Spinner themeSpinner = findViewById(R.id.themeSpinner);

        Button applyButton = findViewById(R.id.applyButton);

        // Set custom adapter
        ThemeSpinnerAdapter adapter = new ThemeSpinnerAdapter(this, themeNames, themeColors);
        themeSpinner.setAdapter(adapter);


        // Set Spinner selection based on the saved theme
        int themePosition = getThemePosition(selectedTheme);
        themeSpinner.setSelection(themePosition);

        // Handle theme selection
        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTheme = getThemeFromPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Save the selected theme and restart the activity
        applyButton.setOnClickListener(v -> {
            getSharedPreferences("ThemePrefs", MODE_PRIVATE).edit().putInt(THEME_KEY, selectedTheme).apply();
            recreate();
        });



    }

    private int getThemeFromPosition(int position) {
        if (position < 0 || position >= themeArray.length) {
            return R.style.DefaultTheme;
        }
        return themeArray[position];
    }

    private int getThemePosition(int theme) {
        for (int i = 0; i < themeArray.length; i++) {
            if (themeArray[i] == theme) {
                return i;
            }
        }
        return 0; // Default position
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
