 package com.kits.orderkowsar.application;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.model.GroupLayerTwo;
import com.kits.orderkowsar.model.NumberFunctions;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

 public class GroupLayerTwoViewHolder extends ChildViewHolder {
     private final TextView mtextView;

     public GroupLayerTwoViewHolder(View itemView) {
         super(itemView);
         mtextView=itemView.findViewById(R.id.item2_tv);
     }

     public void bind(GroupLayerTwo product){

         mtextView.setText(NumberFunctions.PerisanNumber(product.name));
     }

     public void intent(final GroupLayerTwo product, final  Context mContext){

         mtextView.setOnClickListener(v -> {
             Toast.makeText(mContext, product.name, Toast.LENGTH_SHORT).show();

         });
     }

 }
