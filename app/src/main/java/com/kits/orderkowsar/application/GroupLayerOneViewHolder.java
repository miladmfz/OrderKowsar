package com.kits.orderkowsar.application;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.kits.orderkowsar.R;
import com.kits.orderkowsar.model.GroupLayerOne;
import com.kits.orderkowsar.model.NumberFunctions;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;


public class GroupLayerOneViewHolder extends GroupViewHolder {

    private final TextView textView ;
    private final ImageView arrow;


    public GroupLayerOneViewHolder(View itemView) {
        super(itemView);
        textView=itemView.findViewById(R.id.item1_tv);
        arrow=itemView.findViewById(R.id.item1_img);
    }

    public void bind (GroupLayerOne company){
        textView.setText(NumberFunctions.PerisanNumber(company.getTitle()));
    }

    public void hide (GroupLayerOne company){
        if(company.childno>0) {
            arrow.setVisibility(View.VISIBLE);
        }else {
            arrow.setVisibility(View.GONE);
        }
    }



    public void intent(final GroupLayerOne company, final  Context mContext){

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mContext, company.getTitle(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    public void expand() {
        animateExpand();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

}
