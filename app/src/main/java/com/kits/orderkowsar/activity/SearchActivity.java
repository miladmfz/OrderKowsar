package com.kits.orderkowsar.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kits.orderkowsar.Fragment.SearchViewFragment;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.adapters.InternetConnection;
import com.kits.orderkowsar.application.App;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.databinding.ActivitySearchBinding;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchActivity extends AppCompatActivity {


    CallMethod callMethod;
    APIInterface apiInterface;
    DatabaseHelper dbh;
    Intent intent;
    TextView textCartItemCount;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    SearchViewFragment grp_Fragment;
    FrameLayout grp_framelayout;
    Toolbar toolbar;
    int width = 1;


    ActivitySearchBinding binding;

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

    //*************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getSharedPreferences("ThemePrefs", MODE_PRIVATE).getInt("selectedTheme", R.style.DefaultTheme));

        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        InternetConnection ic = new InternetConnection(this);
        if (ic.has()) {
            try {
                init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            finish();
        }


    }

    public void Config() {
        callMethod = new CallMethod(App.getContext());
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));

        if (callMethod.ReadString("LANG").equals("fa")) {
            binding.searchactivity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            binding.searchactivity.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            binding.searchactivity.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;

        toolbar = findViewById(R.id.SearchActivity_toolbar);
        toolbar.setTitle(callMethod.NumberRegion(getString(R.string.textvalue_tablelable) + callMethod.ReadString("RstMizName")));
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        grp_Fragment = new SearchViewFragment();


        grp_framelayout = findViewById(R.id.searchactivity_framelayout);


    }

    public void intent() {

    }

    public void init() {
        Config();
        GetFirstData();


    }

    private void callGrpfragment(String GroupCode) {

        grp_Fragment.setParent_GourpCode(GroupCode);
        grp_Fragment.setGood_GourpCode(GroupCode);
        fragmentTransaction.replace(R.id.searchactivity_framelayout, grp_Fragment);
        fragmentTransaction.commit();


    }

    public void GetFirstData() {

        callGrpfragment(dbh.ReadConfig("GroupCodeDefult"));

    }

    public void RefreshState() {
        if (textCartItemCount != null) {

            if (textCartItemCount.getVisibility() != View.GONE) {
                textCartItemCount.setVisibility(View.GONE);
            }
            Call<RetrofitResponse> call2 = apiInterface.OrderGetSummmary("OrderGetSummmary", callMethod.ReadString("AppBasketInfoCode"));
            call2.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        textCartItemCount.setText(callMethod.NumberRegion(response.body().getBasketInfos().get(0).getSumFacAmount()));
                        if (Integer.parseInt(response.body().getBasketInfos().get(0).getSumFacAmount()) > 0) {
                            if (textCartItemCount.getVisibility() != View.VISIBLE) {
                                textCartItemCount.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {

                }
            });
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        RefreshState();
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.basket_menu);
        View actionView = menuItem.getActionView();
        textCartItemCount = actionView.findViewById(R.id.cart_badge);
        RefreshState();
        actionView.setOnClickListener(v -> {
            onOptionsItemSelected(menuItem);
        });
        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.basket_menu) {
            intent = new Intent(this, BasketActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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




