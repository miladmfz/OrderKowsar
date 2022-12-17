package com.kits.orderkowsar.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.activity.SearchActivity;
import com.kits.orderkowsar.activity.TableActivity;
import com.kits.orderkowsar.application.Action;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.application.ImageInfo;
import com.kits.orderkowsar.model.BasketInfo;
import com.kits.orderkowsar.model.NumberFunctions;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.viewholder.ReserveViewHolder;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReserveAdapter extends RecyclerView.Adapter<ReserveViewHolder> {
    private final Context mContext;
    CallMethod callMethod;
    ArrayList<BasketInfo> basketInfos;
    APIInterface apiInterface;
    Intent intent;

    private final ImageInfo image_info;
    Call<RetrofitResponse> call;
    Action action;


    public ReserveAdapter(ArrayList<BasketInfo> BasketInfos, Context context) {
        this.mContext = context;
        this.basketInfos = BasketInfos;
        this.callMethod = new CallMethod(mContext);
        this.image_info = new ImageInfo(mContext);
        this.action = new Action(mContext);
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

    }

    @NonNull
    @Override
    public ReserveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reserve_item_cardview, parent, false);
        return new ReserveViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ReserveViewHolder holder, @SuppressLint("RecyclerView") final int position) {



            holder.tv_reservestart.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getReserveStart()));
            holder.tv_reserveend.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getReserveEnd()));
            holder.tv_reservebrokername.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getRes_BrokerName()));
            holder.tv_reservepersonname.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getPersonName()));
            holder.tv_reserveeplain.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getInfoExplain()));
            holder.tv_reservemobileno.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getMobileNo()));
            holder.tv_reservedate.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getAppBasketInfoDate()));


            holder.rltv.setOnClickListener(v -> {

                new AlertDialog.Builder(mContext)
                        .setTitle("توجه")
                        .setMessage("آیا مایل به سفارش هستید ؟؟")
                        .setPositiveButton("بله", (dialogInterface, i) -> {
                            callMethod.EditString("RstMizName", basketInfos.get(position).getRstMizName()+" (رزرو) ");
                            callMethod.EditString("AppBasketInfoCode", basketInfos.get(position).getAppBasketInfoCode());
                            intent = new Intent(mContext, SearchActivity.class);
                            mContext.startActivity(intent);
                        })
                        .setNegativeButton("خیر", (dialogInterface, i) -> {
                        })
                        .show();
            });

        holder.rltv.setOnLongClickListener(v -> {

            new AlertDialog.Builder(mContext)
                    .setTitle("توجه")
                    .setMessage("آیا رزرو حذف شود؟")
                    .setPositiveButton("بله", (dialogInterface, i) -> {
                        action.DeleteReserveDialog(basketInfos.get(position));
                    })
                    .setNegativeButton("خیر", (dialogInterface, i) -> {
                    })
                    .show();
            return false;
        });

    }

    @Override
    public int getItemCount() {
        return basketInfos.size();
    }


}
