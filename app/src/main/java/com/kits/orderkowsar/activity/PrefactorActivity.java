package com.kits.orderkowsar.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.github.juanlabrador.badgecounter.BadgeCounter;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.adapters.PreFactorHeaderAdapter;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.databinding.ActivityPrefactorBinding;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.NumberFunctions;
import com.kits.orderkowsar.model.PreFactor;

import java.util.ArrayList;
import java.util.Objects;


public class PrefactorActivity extends AppCompatActivity {

    private Integer pfcode;
    private Intent intent;
    private Handler handler;

    private ArrayList<PreFactor> preFactors = new ArrayList<>();
    private DatabaseHelper dbh;
    PreFactorHeaderAdapter adapter;
    GridLayoutManager gridLayoutManager;
    CallMethod callMethod;

    String search_target = "";

    ActivityPrefactorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPrefactorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        final Dialog dialog1;
        dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.rep_prog);
        TextView repw = dialog1.findViewById(R.id.rep_prog_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();

        Config();
        try {
            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);
            handler.postDelayed(dialog1::dismiss, 1000);
        } catch (Exception e) {
            callMethod.ErrorLog(e.getMessage());
        }


    }


    //**************************************************

    public void Config() {

        handler = new Handler();
        callMethod = new CallMethod(this);
        pfcode = Integer.parseInt(callMethod.ReadString("PreFactorCode"));
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        Toolbar toolbar = findViewById(R.id.PrefactorActivity_toolbar);
        setSupportActionBar(toolbar);


    }

    public void init() {


        binding.PrefactorActivityLastfactor.setText(NumberFunctions.PerisanNumber(String.valueOf(pfcode)));
        callfactor();

        binding.PrefactorActivityRefresh.setOnClickListener(view -> {
            finish();
            startActivity(getIntent());
        });

        binding.PrefactorActivityEdtsearch.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(final Editable editable) {
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(() -> {
                            search_target = NumberFunctions.EnglishNumber(editable.toString());
                            callfactor();
                        }, Integer.parseInt(callMethod.ReadString("Delay")));
                        handler.postDelayed(() -> binding.PrefactorActivityEdtsearch.selectAll(), 5000);
                    }
                });

        binding.PrefactorActivityAddfactor.setOnClickListener(view -> {
            intent = new Intent(this, CustomerActivity.class);
            intent.putExtra("edit", "0");
            intent.putExtra("factor_code", "0");
            intent.putExtra("id", "0");
            startActivity(intent);

        });


    }


    public void callfactor() {
        preFactors = dbh.getAllPrefactorHeader(search_target);
        adapter = new PreFactorHeaderAdapter(preFactors, this);
        gridLayoutManager = new GridLayoutManager(this, 1);
        binding.PrefactorActivityRecyclerView.setLayoutManager(gridLayoutManager);
        binding.PrefactorActivityRecyclerView.setAdapter(adapter);
        binding.PrefactorActivityRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        BadgeCounter.hide(menu.findItem(R.id.bag_shop));
        return true;
    }

    @Override
    protected void onRestart() {
        finish();
        startActivity(getIntent());
        super.onRestart();
    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.bag_shop) {
            if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                intent = new Intent(this, BasketActivity.class);
                intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
                intent.putExtra("showflag", "2");
            } else {
                if (pfcode != 0) {
                    intent = new Intent(this, SearchActivity.class);
                    intent.putExtra("scan", "");
                    intent.putExtra("id", "0");
                    intent.putExtra("title", "جستجوی کالا");

                } else {
                    callMethod.showToast("سبد خرید خالی می باشد");
                    intent = new Intent(this, PrefactoropenActivity.class);
                }
            }
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
