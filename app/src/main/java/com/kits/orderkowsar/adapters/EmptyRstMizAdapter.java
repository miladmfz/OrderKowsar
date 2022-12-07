package com.kits.orderkowsar.adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.activity.BasketActivity;
import com.kits.orderkowsar.activity.SearchActivity;
import com.kits.orderkowsar.activity.TableActivity;
import com.kits.orderkowsar.application.Action;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.application.ImageInfo;
import com.kits.orderkowsar.model.BasketInfo;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.Good;
import com.kits.orderkowsar.model.NumberFunctions;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.viewholder.EmptyRstMizViewHolder;
import com.kits.orderkowsar.viewholder.RstMizViewHolder;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EmptyRstMizAdapter extends RecyclerView.Adapter<EmptyRstMizViewHolder> {
    private final Context mContext;
    CallMethod callMethod;
    ArrayList<BasketInfo> basketInfos;
    ArrayList<Good> goods;
    APIInterface apiInterface;
    Intent intent;
    DatabaseHelper dbh;
    String date;
    Dialog dialog;
    LinearLayoutCompat main_layout;
    Call<RetrofitResponse> call;
    Action action;
    Bitmap bitmap_factor;
    String bitmap_factor_base64 = "";
    int width = 500;
    int height = 1;

    public EmptyRstMizAdapter(ArrayList<BasketInfo> BasketInfos, Context context) {
        this.mContext = context;
        this.basketInfos = BasketInfos;
        this.callMethod = new CallMethod(mContext);
        this.action = new Action(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        call = apiInterface.GetTodeyFromServer("GetTodeyFromServer");

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                assert response.body() != null;
                date = response.body().getText();
            }

            @Override
            public void onFailure(Call<RetrofitResponse> call, Throwable t) {
            }
        });

    }

    @NonNull
    @Override
    public EmptyRstMizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.emptytable_item_cardview, parent, false);
        return new EmptyRstMizViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final EmptyRstMizViewHolder holder, @SuppressLint("RecyclerView") final int position) {


        holder.tv_name.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getRstMizName()));

        holder.btn_select.setOnClickListener(v -> {

            call = apiInterface.OrderInfoInsert(
                    "OrderInfoInsert",
                    dbh.ReadConfig("BrokerCode"),
                    basketInfos.get(position).getRstmizCode(),
                    basketInfos.get(position).getPersonName(),
                    basketInfos.get(position).getMobileNo(),
                    basketInfos.get(position).getExplain(),
                    "0",
                    basketInfos.get(position).getReserveStart(),
                    basketInfos.get(position).getReserveEnd(),
                    basketInfos.get(position).getToday(),
                    basketInfos.get(position).getInfoState(),
                    callMethod.ReadString("AppBasketInfoCode")
            );
            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                    assert response.body() != null;
                    if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                        callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                    } else {

                        callMethod.EditString("AppBasketInfoCode", basketInfos.get(position).getAppBasketInfoCode());
                        intent = new Intent(mContext, SearchActivity.class);
                        mContext.startActivity(intent);
                    }

                }

                @Override
                public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                }
            });

        });


    }

    @Override
    public int getItemCount() {
        return basketInfos.size();
    }


}
