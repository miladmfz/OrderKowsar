package com.kits.orderkowsar.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.kits.orderkowsar.viewholder.GoodBasketViewHolder;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GoodBasketAdapter extends RecyclerView.Adapter<GoodBasketViewHolder> {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private final APIInterface apiInterface;
    private final ImageInfo image_info;
    private final Context mContext;
    CallMethod callMethod;
    private final ArrayList<Good> goods;
    private final DatabaseHelper dbh;
    Intent intent;
    Action action;
    Call<RetrofitResponse> call;
    public GoodBasketAdapter(ArrayList<Good> goods, Context mContext) {
        this.mContext = mContext;
        this.goods = goods;
        this.callMethod = new CallMethod(mContext);
        this.image_info = new ImageInfo(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        action = new Action(mContext);
    }

    @NonNull
    @Override
    public GoodBasketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_basket_item_cardview, parent, false);
        return new GoodBasketViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final GoodBasketViewHolder holder, @SuppressLint("RecyclerView") int position) {



        holder.tv_goodname.setText(NumberFunctions.PerisanNumber(goods.get(position).getGoodName()));
        holder.tv_amount.setText(NumberFunctions.PerisanNumber(goods.get(position).getAmount()));
        holder.tv_explain.setText(NumberFunctions.PerisanNumber(goods.get(position).getExplain()));

        if (goods.get(position).getExplain().length()>0){
            holder.ll_explain.setVisibility(View.VISIBLE);
        }else {
            holder.ll_explain.setVisibility(View.INVISIBLE);
        }


        holder.tv_amount.setOnClickListener(v -> action.GoodBoxDialog(goods.get(position),"1"));

        holder.btn_dlt.setOnClickListener(v -> new AlertDialog.Builder(mContext)
                .setTitle("توجه")
                .setMessage("خذف شود ؟")
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
                                if (response.body().getText().equals("Done")){
                                    goods.remove( goods.get(position));
                                    notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                             callMethod.log("img_2");
                            callMethod.log(t.getMessage());

                        }
                    });

                })
                .setNegativeButton("خیر", (dialogInterface, i) -> {
                })
                .show());



    }

    @Override
    public int getItemCount() {
        return goods.size();
    }


}
