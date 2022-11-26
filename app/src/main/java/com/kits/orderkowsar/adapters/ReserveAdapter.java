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
import com.kits.orderkowsar.viewholder.ReserveViewHolder;
import com.kits.orderkowsar.viewholder.RstMizViewHolder;
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
            holder.tv_reservemobileno.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getMobileNo()));

    }

    @Override
    public int getItemCount() {
        return basketInfos.size();
    }


}
