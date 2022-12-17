package com.kits.orderkowsar.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.application.Action;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.application.ImageInfo;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.Good;
import com.kits.orderkowsar.model.NumberFunctions;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.viewholder.GoodBoxItemViewHolder;
import com.kits.orderkowsar.viewholder.GoodItemViewHolder;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GoodBoxItemAdapter extends RecyclerView.Adapter<GoodBoxItemViewHolder> {
    DecimalFormat decimalFormat = new DecimalFormat("0,000");
    Context mContext;
    CallMethod callMethod;
    ArrayList<Good> goods;
    DatabaseHelper dbh;

    APIInterface apiInterface;
    Call<RetrofitResponse> call2;
    Action action;
    Call<RetrofitResponse> call;


    public GoodBoxItemAdapter(ArrayList<Good> goods, Context context) {
        this.mContext = context;
        this.goods = goods;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        this.action = new Action(mContext);
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

    }

    @NonNull
    @Override
    public GoodBoxItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_box_good_cardview, parent, false);

        return new GoodBoxItemViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final GoodBoxItemViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.tv_name.setText(NumberFunctions.PerisanNumber(goods.get(position).getGoodName()));
        holder.tv_amount.setText(NumberFunctions.PerisanNumber(goods.get(position).getAmount()));
        holder.tv_explain.setText(NumberFunctions.PerisanNumber(goods.get(position).getExplain()));

        if (goods.get(position).getFactorCode() == null) {
            holder.img_dlt.setVisibility(View.VISIBLE);
            holder.img_dlt.setOnClickListener(v -> new AlertDialog.Builder(mContext)
                    .setTitle("توجه")
                    .setMessage("حذف شود ؟")
                    .setPositiveButton("بله", (dialogInterface, i) -> {

                        call = apiInterface.DeleteGoodFromBasket(
                                "DeleteGoodFromBasket",
                                goods.get(position).getRowCode(),
                                goods.get(position).getAppBasketInfoRef()
                        );
                        call.enqueue(new Callback<RetrofitResponse>() {
                            @Override
                            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                                if (response.isSuccessful()) {
                                    assert response.body() != null;
                                    if (response.body().getText().equals("Done")) {
                                        goods.remove(goods.get(position));
                                        notifyDataSetChanged();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {

                            }
                        });

                    })
                    .setNegativeButton("خیر", (dialogInterface, i) -> {
                    })
                    .show());
        } else {
            holder.img_dlt.setVisibility(View.INVISIBLE);

        }

    }

    @Override
    public int getItemCount() {
        return goods.size();
    }


}
