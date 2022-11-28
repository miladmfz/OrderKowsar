package com.kits.orderkowsar.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.adapters.InternetConnection;
import com.kits.orderkowsar.adapters.ObjectTypeAdapter;
import com.kits.orderkowsar.adapters.RstMizAdapter;
import com.kits.orderkowsar.application.App;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.model.BasketInfo;
import com.kits.orderkowsar.model.ObjectType;
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
    RecyclerView recyclerView_object,recyclerView_Table;
    ArrayList<BasketInfo> basketInfos= new ArrayList<>();
    ArrayList<ObjectType> objectTypes= new ArrayList<>();
    RstMizAdapter adapter;
    public String State = "0";
    public String mizType = "0";
    ArrayList<String> InfoState_array = new ArrayList<>();
    Spinner spinner;


    LottieAnimationView progressBar;
    LottieAnimationView img_lottiestatus;
    TextView tv_lottiestatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        intent();
        Config();

        InternetConnection ic = new InternetConnection(this);
        if (!ic.has()) {
            intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            finish();
        }else {
            init();
        }
    }

    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        State = data.getString("State");
    }

    public void Config() {

        callMethod = new CallMethod(App.getContext());
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        spinner = findViewById(R.id.tableactivity_spinner);

        progressBar = findViewById(R.id.tableactivity_prog);
        img_lottiestatus = findViewById(R.id.tableactivity_lottie);
        tv_lottiestatus = findViewById(R.id.tableactivity_tvstatus);

        recyclerView_Table = findViewById(R.id.tableactivity_mizlist_recy);
        recyclerView_object = findViewById(R.id.tableactivity_miztype_recy);

        InfoState_array.add("همه میز ها");
        InfoState_array.add("در حال سفارش");
        InfoState_array.add("ثبت شده ها");
        InfoState_array.add("میز های خالی");
        InfoState_array.add("رزرو شده ها");

    }


    public void CallTable() {

        basketInfos.clear();
        progressBar.setVisibility(View.VISIBLE);
        img_lottiestatus.setVisibility(View.GONE);
        tv_lottiestatus.setVisibility(View.GONE);

        Call<RetrofitResponse> call1 = apiInterface.OrderMizList("OrderMizList", State,mizType);
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    basketInfos = response.body().getBasketInfos();
                    progressBar.setVisibility(View.GONE);
                    callrecycler();
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {

                progressBar.setVisibility(View.GONE);
                callrecycler();
            }
        });
    }

    public void CallSpinner() {

        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, InfoState_array);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);
        spinner.setSelection(Integer.parseInt(State));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                State=String.valueOf(position);
                CallTable();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    public void init() {


        Call<RetrofitResponse> call1 = apiInterface.GetObjectTypeFromDbSetup("GetObjectTypeFromDbSetup", "RstMiz_MizType");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    objectTypes = response.body().getObjectTypes();
                    Log.e("test",objectTypes.size()+"-1");
                    for (ObjectType objectType : objectTypes){
                        if(objectType.getIsDefault().equals("1")){
                            mizType=objectType.getaType();
                            State = "0";
                            CallSpinner();
                        }
                    }

                    ObjectTypeAdapter objectadapter = new ObjectTypeAdapter(objectTypes, TableActivity.this);
                    recyclerView_object.setLayoutManager(new GridLayoutManager(TableActivity.this, 1));
                    recyclerView_object.setAdapter(objectadapter);
                    recyclerView_object.setItemAnimator(new DefaultItemAnimator());


                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                Log.e("test",t.getMessage());
                progressBar.setVisibility(View.GONE);

            }
        });







    }


    private void callrecycler() {
        Log.e("test","6-");
        adapter = new RstMizAdapter(basketInfos, TableActivity.this);

        if (adapter.getItemCount() == 0) {
            tv_lottiestatus.setText("میزی با این وضعیت وجود ندارد");
            img_lottiestatus.setVisibility(View.VISIBLE);
            tv_lottiestatus.setVisibility(View.VISIBLE);
        } else {
            img_lottiestatus.setVisibility(View.GONE);
            tv_lottiestatus.setVisibility(View.GONE);
        }
        recyclerView_Table.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView_Table.setAdapter(adapter);
        recyclerView_Table.setItemAnimator(new DefaultItemAnimator());

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        CallSpinner();
        super.onWindowFocusChanged(hasFocus);
    }

}