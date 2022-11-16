package com.kits.orderkowsar.viewholder;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.activity.SearchActivity;
import com.kits.orderkowsar.model.Category;
import com.kits.orderkowsar.model.NumberFunctions;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;


public class CategoryViewHolder extends GroupViewHolder {

    private final TextView textView;
    private final ImageView arrow;
    private final ImageView image;


    public CategoryViewHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.item1_tv);
        arrow = itemView.findViewById(R.id.item1_img);
        image = itemView.findViewById(R.id.item1_image);
    }

    public void bind(Category company) {
        textView.setText(NumberFunctions.PerisanNumber(company.getTitle()));
    }

    public void bindimage(Bitmap myBitmap) {
        image.setImageBitmap(myBitmap);
    }

    public void hide(Category company) {

        if (company.childno > 0) {
            arrow.setVisibility(View.VISIBLE);
        } else {
            arrow.setVisibility(View.GONE);
        }
    }


    public void intent(final Category company, final Context mContext) {


        textView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, SearchActivity.class);
            intent.putExtra("scan", "");
            intent.putExtra("id", String.valueOf(company.id));
            intent.putExtra("title", company.name);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);

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
