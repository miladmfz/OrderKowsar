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
import android.widget.LinearLayout;
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

   public LinearLayout ll_explain;
   public TextView tv_goodname;
   public TextView tv_amount;
   public TextView tv_explain;
   public Button btn_dlt;


    public GoodBasketViewHolder(View itemView) {
        super(itemView);

        ll_explain = itemView.findViewById(R.id.good_basket_item_ll_explain);
        tv_goodname = itemView.findViewById(R.id.good_basket_item_goodname);
        tv_amount = itemView.findViewById(R.id.good_basket_item_amount);
        tv_explain = itemView.findViewById(R.id.good_basket_item_explain);
        btn_dlt = itemView.findViewById(R.id.good_basket_item_dlt);
    }



}