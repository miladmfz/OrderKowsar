package com.kits.orderkowsar.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.activity.DetailActivity;
import com.kits.orderkowsar.activity.PrefactoropenActivity;
import com.kits.orderkowsar.activity.SearchActivity;
import com.kits.orderkowsar.activity.SearchByDateActivity;
import com.kits.orderkowsar.application.Action;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.application.ImageInfo;
import com.kits.orderkowsar.model.Column;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.Good;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.viewholder.GoodItemViewHolder;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GoodAdapter extends RecyclerView.Adapter<GoodItemViewHolder> {
    private final Context mContext;
    CallMethod callMethod;
    private final ArrayList<Good> goods;
    DatabaseHelper dbh;

    APIInterface apiInterface;
    private final ImageInfo image_info;
    Call<RetrofitResponse> call2;
    public boolean multi_select;
    Action action;
    ArrayList<Column> Columns;


    public GoodAdapter(ArrayList<Good> goods, Context context) {
        this.mContext = context;
        this.goods = goods;
        this.callMethod = new CallMethod(mContext);
        this.image_info = new ImageInfo(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        this.action = new Action(mContext);
        this.Columns = dbh.GetColumns("id", "", "1");
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

    }

    @NonNull
    @Override
    public GoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_item_cardview, parent, false);
//        GoodItemCardviewBinding binding = GoodItemCardviewBinding.inflate(
//                LayoutInflater.from(parent.getContext())
//        );
//        return new GoodItemViewHolder(binding);
        return new GoodItemViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final GoodItemViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        String imagecode = dbh.GetLastksrImageCode(goods.get(position).getGoodFieldValue("GoodCode"));

        holder.bind(Columns, goods.get(position), mContext, callMethod);

        holder.Action(goods.get(position)
                , mContext
                , dbh
                , callMethod
                , action
                , image_info
                , multi_select, imagecode
        );


        if (!image_info.Image_exist(imagecode)) {

            call2 = apiInterface.GetImageFromKsr("GetImageFromKsr", goods.get(position).getGoodFieldValue("KsrImageCode"));
            call2.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {

                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (!response.body().getText().equals("no_photo")) {
                            image_info.SaveImage(
                                    BitmapFactory.decodeByteArray(
                                            Base64.decode(response.body().getText(), Base64.DEFAULT),
                                            0,
                                            Base64.decode(response.body().getText(), Base64.DEFAULT).length
                                    ),
                                    imagecode
                            );

                            notifyItemChanged(position);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                    callMethod.ErrorLog(t.getMessage());

                }
            });
        }


        holder.rltv.setOnClickListener(v -> {

            if (multi_select) {
                if (goods.get(position).getGoodFieldValue("ActiveStack").equals("1")) {
                    holder.rltv.setChecked(!holder.rltv.isChecked());
                    goods.get(position).setCheck(!goods.get(position).isCheck());
                    if (goods.get(position).isCheck()) {
                        if (mContext.getClass().getName().equals("com.kits.orderkowsar.activity.SearchActivity")) {
                            SearchActivity activity = (SearchActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }
                        if (mContext.getClass().getName().equals("com.kits.orderkowsar.activity.Search_date_detailActivity")) {
                            SearchByDateActivity activity = (SearchByDateActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }
                    } else {
                        if (mContext.getClass().getName().equals("com.kits.orderkowsar.activity.SearchActivity")) {
                            SearchActivity activity = (SearchActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }
                        if (mContext.getClass().getName().equals("com.kits.orderkowsar.activity.Search_date_detailActivity")) {
                            SearchByDateActivity activity = (SearchByDateActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }
                    }
                }else{
                    callMethod.showToast("این کالا غیر فعال می باشد");
                }
            } else {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("id", goods.get(position).getGoodFieldValue("GoodCode"));
                intent.putExtra("ws", goods.get(position).getGoodFieldValue("Shortage"));
                mContext.startActivity(intent);
            }

        });


        holder.rltv.setChecked(goods.get(position).isCheck());

        holder.rltv.setOnLongClickListener(view ->
        {
            if (goods.get(position).getGoodFieldValue("ActiveStack").equals("1")) {
                if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                    multi_select = true;
                    holder.rltv.setChecked(!holder.rltv.isChecked());
                    goods.get(position).setCheck(!goods.get(position).isCheck());

                    if (goods.get(position).isCheck()) {
                        if (mContext.getClass().getName().equals("com.kits.orderkowsar.activity.SearchActivity")) {
                            SearchActivity activity = (SearchActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }
                        if (mContext.getClass().getName().equals("com.kits.orderkowsar.activity.Search_date_detailActivity")) {
                            SearchByDateActivity activity = (SearchByDateActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }

                    } else {
                        if (mContext.getClass().getName().equals("com.kits.orderkowsar.activity.SearchActivity")) {
                            SearchActivity activity = (SearchActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }
                        if (mContext.getClass().getName().equals("com.kits.orderkowsar.activity.Search_date_detailActivity")) {
                            SearchByDateActivity activity = (SearchByDateActivity) mContext;
                            activity.good_select_function(goods.get(position));
                        }


                    }
                } else {

                    Intent intent = new Intent(mContext, PrefactoropenActivity.class);
                    intent.putExtra("fac", "0");
                    mContext.startActivity(intent);

                }
            }else{
                callMethod.showToast("این کالا غیر فعال می باشد");
            }

            return true;
        });


    }

    @Override
    public int getItemCount() {
        return goods.size();
    }


}
