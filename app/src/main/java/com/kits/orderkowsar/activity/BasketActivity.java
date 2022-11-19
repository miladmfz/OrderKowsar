package com.kits.orderkowsar.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.adapters.GoodBasketAdapter;
import com.kits.orderkowsar.adapters.InternetConnection;
import com.kits.orderkowsar.application.Action;
import com.kits.orderkowsar.application.App;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.databinding.ActivityBuyBinding;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.Good;
import com.kits.orderkowsar.model.NumberFunctions;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BasketActivity extends AppCompatActivity {

    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    RecyclerView re;
    ArrayList<Good> Goods;
    APIInterface apiInterface ;
    CallMethod callMethod;
    TextView Buy_row,Buy_price,Buy_amount;
    LottieAnimationView prog;
    GridLayoutManager gridLayoutManager;
    int id=0;
    Intent intent;
    ArrayList<Good> Goods_sum;
    public ArrayList<String> Goods_shortage=new ArrayList<>();
    GoodBasketAdapter adapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);


        InternetConnection ic =new  InternetConnection(this);
        if(ic.has()){
            try {
                init();
            }catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            finish();
        }


    }

//***********************************************************************


    public void init() {


        callMethod = new CallMethod(App.getContext());
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

        Buy_row = findViewById(R.id.BuyActivity_total_row_buy);
        Buy_price = findViewById(R.id.BuyActivity_total_price_buy);
        Buy_amount = findViewById(R.id.BuyActivity_total_amount_buy);
        Button total_delete = findViewById(R.id.BuyActivity_total_delete);
        Button final_buy_test = findViewById(R.id.BuyActivity_test);
        re = findViewById(R.id.BuyActivity_R1);
        prog = findViewById(R.id.BuyActivity_prog);

        Toolbar toolbar = findViewById(R.id.BuyActivity_toolbar);
        setSupportActionBar(toolbar);


        Call<RetrofitResponse> call = apiInterface.GetBasketFromTable(
                "BasketGet",
                "AppBasketInfoCode"
        );

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call,@NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    //todo
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
            }
        });





        final_buy_test.setOnClickListener(view -> {
            //todo insert to shopfactor
        });


        total_delete.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle("توجه")
                .setMessage("آیا مایل به خالی کردن سبد خرید می باشید؟")
                .setPositiveButton("بله", (dialogInterface, i) -> {
//
//                    Call<RetrofitResponse> call1 = apiInterface.Basketdeleteall(
//                            "Basketdeleteall",
//                            GetShared.ReadString("mobile")
//                    );
//                    call1.enqueue(new Callback<RetrofitResponse>() {
//                        @Override
//                        public void onResponse(@NotNull  Call<RetrofitResponse> call1, @NotNull  Response<RetrofitResponse> response) {
//                            if (response.isSuccessful()) {
//                                assert response.body() != null;
//                                if (response.body().getText().equals("done")) {
//                                    App.showToast("سبد خرید حذف گردید");
//                                    finish();
//                                }
//                            }
//                        }
//                        @Override
//                        public void onFailure(@NotNull Call<RetrofitResponse> call1,@NotNull  Throwable t) {
//                        }
//                    });
//

                })
                .setNegativeButton("خیر", (dialogInterface, i) -> {
                })
                .show());


    }

}
