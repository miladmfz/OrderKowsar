package com.kits.orderkowsar.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.adapters.InternetConnection;
import com.kits.orderkowsar.adapters.RstMizAdapter;
import com.kits.orderkowsar.application.App;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.model.BasketInfo;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.model.RstMiz;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TableActivity extends AppCompatActivity {

    CallMethod callMethod;
    APIInterface apiInterface;
    Intent intent;
    RecyclerView recyclerView;
    ArrayList<RstMiz> rstMizs;
    ArrayList<BasketInfo> basketInfos;
    RstMizAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        Config();

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

        recyclerView=findViewById(R.id.tableactivity_recy);


    }

    public void intent() {

    }

    public void init() {
        Call<RetrofitResponse> call1 = apiInterface.GetRstMiz("GetRstMiz");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    basketInfos=response.body().getBasketInfos();
                    callrecycler ();
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {

            }
        });



    }





    private void callrecycler (){
        adapter = new RstMizAdapter(basketInfos, this);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }




}