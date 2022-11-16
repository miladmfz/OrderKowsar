package com.kits.orderkowsar.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.adapters.PreFactorHeaderAdapter;
import com.kits.orderkowsar.adapters.PreFactorHeaderOpenAdapter;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.databinding.ActivityPrefactoropenBinding;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.NumberFunctions;
import com.kits.orderkowsar.model.PreFactor;

import java.util.ArrayList;
import java.util.Objects;


public class PrefactoropenActivity extends AppCompatActivity {


    DatabaseHelper dbh;
    private String fac;
    private Intent intent;
    GridLayoutManager gridLayoutManager;
    CallMethod callMethod;
    ArrayList<PreFactor> preFactors;


    ActivityPrefactoropenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPrefactoropenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Dialog dialog1;
        dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.rep_prog);
        TextView repw = dialog1.findViewById(R.id.rep_prog_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();

        intent();
        Config();
        try {
            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);
            handler.postDelayed(dialog1::dismiss, 1000);
        } catch (Exception e) {
            callMethod.ErrorLog(e.getMessage());
        }


    }

    //*********************************************


    public void Config() {
        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));


    }

    public void init() {


        preFactors = dbh.getAllPrefactorHeaderopen();
        binding.PrefactoropenActivityAmount.setText((NumberFunctions.PerisanNumber("" + preFactors.size())));


        gridLayoutManager = new GridLayoutManager(this, 1);
        binding.PrefactoropenActivityRecyclerView.setLayoutManager(gridLayoutManager);
        if (Integer.parseInt(fac) != 0) {
            PreFactorHeaderAdapter adapter = new PreFactorHeaderAdapter(preFactors, this);
            binding.PrefactoropenActivityRecyclerView.setAdapter(adapter);
        } else {
            PreFactorHeaderOpenAdapter adapter = new PreFactorHeaderOpenAdapter(preFactors, this);
            binding.PrefactoropenActivityRecyclerView.setAdapter(adapter);
        }
        binding.PrefactoropenActivityRecyclerView.setItemAnimator(new DefaultItemAnimator());


        binding.PrefactoropenActivityRefresh.setOnClickListener(view -> {
            finish();
            startActivity(getIntent());
        });

        binding.PrefactoropenActivityDeleteempty.setOnClickListener(view -> {
            dbh.DeleteEmptyPreFactor();
            finish();
            startActivity(getIntent());
        });


        binding.PrefactoropenActivityBtn.setOnClickListener(view -> {
            intent = new Intent(this, CustomerActivity.class);
            intent.putExtra("edit", "0");
            intent.putExtra("factor_code", "0");
            intent.putExtra("id", "0");
            startActivity(intent);
        });


    }


    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        fac = data.getString("fac");
    }


}
