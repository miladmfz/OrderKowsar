package com.kits.orderkowsar.viewholder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.application.Action;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.application.ImageInfo;
import com.kits.orderkowsar.model.Column;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.Good;
import com.kits.orderkowsar.model.NumberFunctions;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;



public class GoodItemViewHolder extends RecyclerView.ViewHolder {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");

    public ImageView img;
    public LinearLayout rltv;

    public TextView tv_name;
    public TextView tv_price;


    boolean multi_select1;

    public GoodItemViewHolder(View itemView) {
        super(itemView);
        img = itemView.findViewById(R.id.good_item_img);
        tv_name = itemView.findViewById(R.id.good_item_name);
        tv_price = itemView.findViewById(R.id.good_item_price);
        rltv = itemView.findViewById(R.id.good_item);
    }


}