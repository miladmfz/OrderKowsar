package com.kits.orderkowsar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.application.Action;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.application.ImageInfo;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.Good;
import com.kits.orderkowsar.viewholder.GoodBasketHistoryViewHolder;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import java.util.ArrayList;

public class GoodBasketHistoryAdapter extends RecyclerView.Adapter<GoodBasketHistoryViewHolder> {
    ArrayList<Good> goods;
    Context mContext;
    CallMethod callMethod;
    String itemposition;
    APIInterface apiInterface;
    ImageInfo image_info;
    DatabaseHelper dbh;
    Action action;


    public GoodBasketHistoryAdapter(ArrayList<Good> goods, String Itemposition, Context mContext) {
        this.mContext = mContext;
        this.goods = goods;
        this.itemposition = Itemposition;
        this.callMethod = new CallMethod(mContext);
        this.image_info = new ImageInfo(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));

        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        action = new Action(mContext);

    }

    @NonNull
    @Override
    public GoodBasketHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (itemposition.equals("0")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_buy_history_line, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_buy_history, parent, false);

        }
        return new GoodBasketHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GoodBasketHistoryViewHolder holder, int position) {

        holder.bind(goods.get(position), itemposition);
        holder.Conditionbind(goods.get(position), image_info, callMethod);

    }

    @Override
    public int getItemCount() {
        return goods.size();
    }


}
