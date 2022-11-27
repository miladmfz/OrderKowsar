package com.kits.orderkowsar.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kits.orderkowsar.R;

import java.text.DecimalFormat;


public class ObjectTypeViewHolder extends RecyclerView.ViewHolder {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");


    public LinearLayout rltv;

    public TextView tv_name;



    boolean multi_select1;

    public ObjectTypeViewHolder(View itemView) {
        super(itemView);
        tv_name = itemView.findViewById(R.id.objecttype_item_itype);
        rltv = itemView.findViewById(R.id.objecttype_item);
    }


}