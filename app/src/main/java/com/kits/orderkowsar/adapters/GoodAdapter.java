package com.kits.orderkowsar.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.application.Action;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.application.ImageInfo;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.Good;
import com.kits.orderkowsar.model.NumberFunctions;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.viewholder.GoodItemViewHolder;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GoodAdapter extends RecyclerView.Adapter<GoodItemViewHolder> {
    DecimalFormat decimalFormat = new DecimalFormat("0,000");

    private final Context mContext;
    CallMethod callMethod;
    private final ArrayList<Good> goods;
    DatabaseHelper dbh;

    APIInterface apiInterface;
    private final ImageInfo image_info;
    Call<RetrofitResponse> call2;
    Action action;



    public GoodAdapter(ArrayList<Good> goods, Context context) {
        this.mContext = context;
        this.goods = goods;
        this.callMethod = new CallMethod(mContext);
        this.image_info = new ImageInfo(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        this.action = new Action(mContext);
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

    }

    @NonNull
    @Override
    public GoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_item_cardview, parent, false);
        if ( callMethod.ReadString("LANG").equals("fa")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if ( callMethod.ReadString("LANG").equals("ar")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        return new GoodItemViewHolder(view,mContext);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final GoodItemViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.tv_name.setText(callMethod.NumberRegion(goods.get(position).getGoodName()));
        holder.tv_price.setText(callMethod.NumberRegion(decimalFormat.format(Integer.parseInt(goods.get(position).getMaxSellPrice()))));
        holder.rltv.setOnClickListener(v -> action.GoodBoxDialog(goods.get(position),"0"));
        holder.callimage(goods.get(position));
        
    }

    @Override
    public int getItemCount() {
        return goods.size();
    }



    @Override
    public void onViewDetachedFromWindow(@NonNull GoodItemViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.call.isExecuted()) {
            holder.call.cancel();
        }
    }

}
