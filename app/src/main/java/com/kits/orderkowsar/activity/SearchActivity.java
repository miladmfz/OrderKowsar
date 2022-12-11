package com.kits.orderkowsar.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kits.orderkowsar.Fragment.GrpFragment;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.adapters.GoodAdapter;
import com.kits.orderkowsar.adapters.InternetConnection;
import com.kits.orderkowsar.application.App;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.databinding.ActivitySearchBinding;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.Good;
import com.kits.orderkowsar.model.GoodGroup;
import com.kits.orderkowsar.model.NumberFunctions;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
    GrpFragment grp_Fragment;
    FrameLayout grp_framelayout;
    Toolbar toolbar;
    int width = 1;


    ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    //*************************************************

    public void Config() {
        callMethod = new CallMethod(App.getContext());
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;

        toolbar = findViewById(R.id.SearchActivity_toolbar);
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        grp_Fragment = new GrpFragment();


        grp_framelayout = findViewById(R.id.searchactivity_framelayout);


    }

    public void intent() {

    }

    public void init() {
        Config();
        GetFirstData();


    }


    private void callGrpfragment(String GroupCode) {

        grp_Fragment.setGroupCode(GroupCode);
        fragmentTransaction.replace(R.id.searchactivity_framelayout, grp_Fragment);
        //fragmentTransaction.addToBackStack(null);
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
            Call<RetrofitResponse> call2 = apiInterface.GetbasketSum(
                    "GetOrderSum",
                    callMethod.ReadString("AppBasketInfoCode")
            );
            call2.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        textCartItemCount.setText(NumberFunctions.PerisanNumber(response.body().getGoods().get(0).getSumFacAmount()));
                        if (Integer.parseInt(response.body().getGoods().get(0).getSumFacAmount()) > 0) {
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
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}




