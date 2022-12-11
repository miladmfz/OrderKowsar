package com.kits.orderkowsar.viewholder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.model.Good;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GoodItemViewHolder extends RecyclerView.ViewHolder {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");

    public ImageView img;
    public MaterialCardView rltv;
    Context mContex;
    public TextView tv_name;
    public TextView tv_price;
    public Call<RetrofitResponse> call;
    public APIInterface apiInterface;

    CallMethod callMethod;
    boolean multi_select1;

    public GoodItemViewHolder(View itemView, Context context) {
        super(itemView);
        this.mContex=context;
        this.callMethod = new CallMethod(context);
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        img = itemView.findViewById(R.id.good_item_img);
        tv_name = itemView.findViewById(R.id.good_item_name);
        tv_price = itemView.findViewById(R.id.good_item_price);
        rltv = itemView.findViewById(R.id.good_item);

    }

    public void callimage(Good good){

        if (!good.getGoodImageName().equals("")) {


            Glide.with(img)
                    .asBitmap()
                    .load(Base64.decode(good.getGoodImageName(), Base64.DEFAULT))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(img);


        }
        else {

            call = apiInterface.GetImage(
                    "getImage",
                    good.getGoodCode(),
                    "TGood",
                    "0",
                    "200"
            );
            call.enqueue(new Callback<RetrofitResponse>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {

                        assert response.body() != null;
                        if (!response.body().getText().equals("no_photo")) {
                            good.setGoodImageName(response.body().getText());
                        } else {
                            good.setGoodImageName(String.valueOf(R.string.no_photo));

                        }
                        Glide.with(img)
                                .asBitmap()
                                .load(Base64.decode(good.getGoodImageName(), Base64.DEFAULT))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .fitCenter()
                                .into(img);

                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {

                }
            });
        }

    }


}