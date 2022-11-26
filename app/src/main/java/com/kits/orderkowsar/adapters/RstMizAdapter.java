package com.kits.orderkowsar.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.activity.NavActivity;
import com.kits.orderkowsar.activity.SearchActivity;
import com.kits.orderkowsar.application.Action;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.application.ImageInfo;
import com.kits.orderkowsar.model.BasketInfo;
import com.kits.orderkowsar.model.Column;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.Good;
import com.kits.orderkowsar.model.NumberFunctions;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.model.RstMiz;
import com.kits.orderkowsar.viewholder.GoodItemViewHolder;
import com.kits.orderkowsar.viewholder.RstMizViewHolder;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RstMizAdapter extends RecyclerView.Adapter<RstMizViewHolder> {
    private final Context mContext;
    CallMethod callMethod;
    ArrayList<BasketInfo> basketInfos;
    APIInterface apiInterface;
    Intent intent;

    private final ImageInfo image_info;
    Call<RetrofitResponse> call;
    Action action;


    public RstMizAdapter(ArrayList<BasketInfo> BasketInfos, Context context) {
        this.mContext = context;
        this.basketInfos = BasketInfos;
        this.callMethod = new CallMethod(mContext);
        this.image_info = new ImageInfo(mContext);
        this.action = new Action(mContext);
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

    }

    @NonNull
    @Override
    public RstMizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_item_cardview, parent, false);
        return new RstMizViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RstMizViewHolder holder, @SuppressLint("RecyclerView") final int position) {


        holder.tv_name.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getRstMizName()));
        holder.tv_placecount.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getPlaceCount()));


        if (basketInfos.get(position).getExplain().length() > 0) {
            holder.ll_table_mizexplain.setVisibility(View.VISIBLE);
            holder.tv_mizexplain.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getExplain()));
        } else {
            holder.ll_table_mizexplain.setVisibility(View.GONE);
        }

        if (basketInfos.get(position).getInfoExplain().length() > 0) {
            holder.ll_table_infoexplain.setVisibility(View.VISIBLE);
            holder.tv_infoexplain.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getInfoExplain()));
        } else {
            holder.ll_table_infoexplain.setVisibility(View.GONE);
        }

        if (basketInfos.get(position).getRes_BrokerName().length() > 0) {
            holder.ll_table_reserve.setVisibility(View.VISIBLE);
            holder.tv_reservestart.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getReserveStart()));
            holder.tv_reserveend.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getReserveEnd()));
            holder.tv_reservebrokername.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getRes_BrokerName()));
            holder.tv_reservepersonname.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getPersonName()));
            holder.tv_reservemobileno.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getMobileNo()));
        } else {
            holder.ll_table_reserve.setVisibility(View.GONE);
        }


        switch (basketInfos.get(position).getInfoState()) {
            case "0":
            case "3":
                holder.ll_table_timebroker.setVisibility(View.GONE);
                holder.btn_cleartable.setVisibility(View.GONE);
                if (basketInfos.get(position).getIsReserved().equals("1")) {
                    holder.btn_cleartable.setVisibility(View.VISIBLE);
                    // basketInfos.get(position).getReserve_AppBasketInfoCode();
                } else {
                    //new info
                }
                break;
            case "1":
            case "2":
                holder.btn_cleartable.setVisibility(View.VISIBLE);
                holder.ll_table_timebroker.setVisibility(View.VISIBLE);

                holder.tv_time.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getTimeStart()));
                holder.tv_brokername.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getBrokerName()));


                // basketInfos.get(position).getAppBasketInfoCode();
                break;


            default:
                break;
        }


        holder.btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.btn_cleartable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (basketInfos.get(position).getIsReserved().equals("1")) {
                    // clear basketInfos.get(position).getReserve_AppBasketInfoCode();
                } else {
                    // clear basketInfos.get(position).getAppBasketInfoCode();
                }
            }
        });
        holder.btn_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                action.reserve_box_dialog(basketInfos.get(position));
            }
        });

        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.btn_changemiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.btn_explainedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        holder.rltv.setOnClickListener(v -> {
            intent = new Intent(mContext, SearchActivity.class);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return basketInfos.size();
    }


}
