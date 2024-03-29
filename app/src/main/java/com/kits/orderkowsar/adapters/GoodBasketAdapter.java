package com.kits.orderkowsar.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.activity.BasketActivity;
import com.kits.orderkowsar.application.Action;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.model.Good;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.viewholder.GoodBasketViewHolder;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GoodBasketAdapter extends RecyclerView.Adapter<GoodBasketViewHolder> {
    private final APIInterface apiInterface;
    private final Context mContext;
    private final ArrayList<Good> goods;
    CallMethod callMethod;
    Action action;
    Call<RetrofitResponse> call;

    public GoodBasketAdapter(ArrayList<Good> goods, Context mContext) {
        this.mContext = mContext;
        this.goods = goods;
        this.callMethod = new CallMethod(mContext);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        action = new Action(mContext);
    }

    @NonNull
    @Override
    public GoodBasketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_basket_item_cardview, parent, false);
        if (callMethod.ReadString("LANG").equals("fa")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        return new GoodBasketViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final GoodBasketViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.tv_goodname.setText(callMethod.NumberRegion(goods.get(position).getGoodName()));
        holder.tv_amount.setText(goods.get(position).getAmount());
        holder.tv_explain.setText(callMethod.NumberRegion(goods.get(position).getExplain()));

        if (goods.get(position).getExplain().length() > 0) {
            holder.ll_explain.setVisibility(View.VISIBLE);
        } else {
            holder.ll_explain.setVisibility(View.INVISIBLE);
        }

        holder.ll_amount.setOnClickListener(v -> action.GoodBoxDialog(goods.get(position), "1"));
        holder.tv_explain.setOnClickListener(v -> action.GoodBoxDialog(goods.get(position), "1"));
        holder.tv_goodname.setOnClickListener(v -> action.GoodBoxDialog(goods.get(position), "1"));


        if (goods.get(position).getFactorCode() == null) {
            holder.btn_dlt.setVisibility(View.VISIBLE);
        } else if (Integer.parseInt(goods.get(position).getFactorCode()) > 0) {
            holder.btn_dlt.setVisibility(View.INVISIBLE);
        }


        holder.btn_dlt.setOnClickListener(v ->{
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
            builder.setTitle(R.string.textvalue_allert);
            builder.setMessage(R.string.textvalue_ifdelete);

            builder.setPositiveButton(R.string.textvalue_yes, (dialog, which) -> {
                call = apiInterface.DeleteGoodFromBasket(
                        "DeleteGoodFromBasket",
                        goods.get(position).getRowCode(),
                        goods.get(position).getAppBasketInfoRef()
                );
                call.enqueue(new Callback<RetrofitResponse>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            if (response.body().getText().equals("Done")) {
                                goods.remove(goods.get(position));
                                notifyDataSetChanged();
                                BasketActivity activity = (BasketActivity) mContext;
                                activity.RefreshState();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {

                    }
                });
            });

            builder.setNegativeButton(R.string.textvalue_no, (dialog, which) -> {
                // code to handle negative button click
            });

            AlertDialog dialog = builder.create();
            dialog.show();




        });


    }

    @Override
    public int getItemCount() {
        return goods.size();
    }


}
