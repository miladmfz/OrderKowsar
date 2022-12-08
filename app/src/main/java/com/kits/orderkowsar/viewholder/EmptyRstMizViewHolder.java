package com.kits.orderkowsar.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.model.Good;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class EmptyRstMizViewHolder extends RecyclerView.ViewHolder {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");

    public MaterialCardView rltv;
    public TextView tv_name;
    public Button btn_select;



    public EmptyRstMizViewHolder(View itemView) {
        super(itemView);

        rltv = itemView.findViewById(R.id.tableempty_item);
        tv_name = itemView.findViewById(R.id.tableempty_item_name);
        btn_select = itemView.findViewById(R.id.tableempty_item_select);

    }



}