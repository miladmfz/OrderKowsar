package com.kits.orderkowsar.viewholder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.activity.BasketActivity;
import com.kits.orderkowsar.application.Action;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.application.ImageInfo;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.Good;
import com.kits.orderkowsar.model.NumberFunctions;

import java.io.File;
import java.text.DecimalFormat;

;

public class GoodBasketViewHolder extends RecyclerView.ViewHolder {

    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private long sum = 0;

    private final TextView goodnameTextView;
    private final TextView maxsellpriceTextView;
    private final TextView priceTextView;
    private final TextView total;
    private final TextView maxtotal;
    private final TextView amount;
    private final TextView offer;
    private final Button btndlt;
    private final ImageView img;
    private final MaterialCardView rltv;


    public GoodBasketViewHolder(View itemView) {
        super(itemView);

        goodnameTextView = itemView.findViewById(R.id.good_buy_name);
        maxsellpriceTextView = itemView.findViewById(R.id.good_buy_maxprice);
        priceTextView = itemView.findViewById(R.id.good_buy_price);
        amount = itemView.findViewById(R.id.good_buy_amount);
        total = itemView.findViewById(R.id.good_buy_total);
        maxtotal = itemView.findViewById(R.id.good_buy_maxtotal);
        img = itemView.findViewById(R.id.good_buy_img);
        btndlt = itemView.findViewById(R.id.good_buy_btndlt);
        offer = itemView.findViewById(R.id.good_buy_offer);


        rltv = itemView.findViewById(R.id.good_buy);
    }



}