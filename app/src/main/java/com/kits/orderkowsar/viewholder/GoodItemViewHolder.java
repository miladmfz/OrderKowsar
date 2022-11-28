package com.kits.orderkowsar.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.orderkowsar.R;

import java.text.DecimalFormat;


public class GoodItemViewHolder extends RecyclerView.ViewHolder {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");

    public ImageView img;
    public MaterialCardView rltv;

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