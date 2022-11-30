package com.kits.orderkowsar.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.activity.SearchActivity;
import com.kits.orderkowsar.activity.TableActivity;
import com.kits.orderkowsar.application.Action;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.application.ImageInfo;
import com.kits.orderkowsar.model.BasketInfo;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.NumberFunctions;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.viewholder.RstMizViewHolder;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RstMizAdapter extends RecyclerView.Adapter<RstMizViewHolder> {
    private final Context mContext;
    CallMethod callMethod;
    ArrayList<BasketInfo> basketInfos;
    APIInterface apiInterface;
    Intent intent;
    DatabaseHelper dbh;
    String date;

    Call<RetrofitResponse> call;
    Action action;


    public RstMizAdapter(ArrayList<BasketInfo> BasketInfos, Context context) {
        this.mContext = context;
        this.basketInfos = BasketInfos;
        this.callMethod = new CallMethod(mContext);
        this.action = new Action(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        call = apiInterface.GetTodeyFromServer("GetTodeyFromServer");

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                assert response.body() != null;
                date=response.body().getText();
            }
            @Override
            public void onFailure(Call<RetrofitResponse> call, Throwable t) {
            }
        });

    }

    @NonNull
    @Override
    public RstMizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_item_cardview, parent, false);
        return new RstMizViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RstMizViewHolder holder, @SuppressLint("RecyclerView") final int position) {


        holder.tv_name.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getRstMizName()));
        holder.tv_placecount.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getPlaceCount()));


        if (basketInfos.get(position).getExplain().length() > 0) {
            holder.ll_table_mizexplain.setVisibility(View.VISIBLE);
            holder.tv_mizexplain.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getExplain()));
        } else {
            holder.ll_table_mizexplain.setVisibility(View.GONE);
        }

        if (basketInfos.get(position).getInfoExplain().length() > 0) {
            holder.ll_table_infoexplain.setVisibility(View.VISIBLE);
            holder.tv_infoexplain.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getInfoExplain()));
        } else {
            holder.ll_table_infoexplain.setVisibility(View.GONE);
        }

        if (basketInfos.get(position).getRes_BrokerName().length() > 0) {
            holder.ll_table_reserve.setVisibility(View.VISIBLE);
            holder.tv_reservestart.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getReserveStart()));
            holder.tv_reservebrokername.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getRes_BrokerName()));
            holder.tv_reservemobileno.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getMobileNo()));
        } else {
            holder.ll_table_reserve.setVisibility(View.GONE);
        }


        switch (basketInfos.get(position).getInfoState()) {
            case "0":
            case "3":
                holder.ll_table_timebroker.setVisibility(View.GONE);
                holder.btn_cleartable.setVisibility(View.GONE);
                if (basketInfos.get(position).getIsReserved().equals("1")) {
                    holder.btn_cleartable.setVisibility(View.VISIBLE);
                }

                break;
            case "1":
            case "2":
                holder.btn_cleartable.setVisibility(View.VISIBLE);
                holder.ll_table_timebroker.setVisibility(View.VISIBLE);
                holder.tv_time.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getTimeStart()));
                holder.tv_brokername.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getBrokerName()));

                break;


            default:
                break;
        }


        holder.btn_select.setOnClickListener(v -> {
            if (call.isExecuted()){
                call.cancel();
            }
            if(basketInfos.get(position).getInfoState().equals("0")||basketInfos.get(position).getInfoState().equals("3")){

                if(basketInfos.get(position).getIsReserved().equals("1")){
                    call = apiInterface.OrderInfoInsert(
                            "OrderInfoInsert",
                            dbh.ReadConfig("BrokerCode"),
                            basketInfos.get(position).getRstmizCode(),
                            basketInfos.get(position).getPersonName(),
                            basketInfos.get(position).getMobileNo(),
                            basketInfos.get(position).getExplain(),
                            "0",
                            basketInfos.get(position).getReserveStart(),
                            basketInfos.get(position).getReserveEnd(),
                            basketInfos.get(position).getToday(),
                            "1",
                            basketInfos.get(position).getReserve_AppBasketInfoCode()
                    );
                    call.enqueue(new Callback<RetrofitResponse>() {
                        @Override
                        public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                            assert response.body() != null;
                            if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode())>0){
                                callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                            }else{
                                Log.e("test_0",basketInfos.get(position).getAppBasketInfoCode());
                                callMethod.EditString("AppBasketInfoCode", basketInfos.get(position).getAppBasketInfoCode());
                                intent = new Intent(mContext, SearchActivity.class);
                                mContext.startActivity(intent);
                            }
                             
                        }
                        @Override
                        public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                        }
                    });
                }else
                {
                    call = apiInterface.OrderInfoInsert(
                            "OrderInfoInsert",
                            dbh.ReadConfig("BrokerCode"),
                            basketInfos.get(position).getRstmizCode(),
                            "",
                            "",
                            "",
                            "0",
                            "",
                            "",
                            basketInfos.get(position).getToday(),
                            "1",
                            "0"
                    );
                    call.enqueue(new Callback<RetrofitResponse>() {
                        @Override
                        public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                            assert response.body() != null;
                            if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode())>0){
                                callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                            }else{
                                Log.e("test_1",basketInfos.get(position).getAppBasketInfoCode());

                                callMethod.EditString("AppBasketInfoCode", response.body().getBasketInfos().get(0).getAppBasketInfoCode());
                                intent = new Intent(mContext, SearchActivity.class);
                                mContext.startActivity(intent);
                            }
                            
                             
                        }
                        @Override
                        public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                        }
                    });
                }


            }else{
                callMethod.EditString("AppBasketInfoCode", basketInfos.get(position).getAppBasketInfoCode());
                intent = new Intent(mContext, SearchActivity.class);
                mContext.startActivity(intent);
            }






        });

        holder.btn_cleartable.setOnClickListener(v -> {
            if (call.isExecuted()){
                call.cancel();
            }
            if (basketInfos.get(position).getIsReserved().equals("1")) {
                call = apiInterface.OrderInfoInsert(
                        "OrderInfoInsert",
                        dbh.ReadConfig("BrokerCode"),
                        basketInfos.get(position).getRstmizCode(),
                        basketInfos.get(position).getPersonName(),
                        basketInfos.get(position).getMobileNo(),
                        basketInfos.get(position).getExplain(),
                        "0",
                        basketInfos.get(position).getReserveStart(),
                        basketInfos.get(position).getReserveEnd(),
                        date,
                        "3",
                        basketInfos.get(position).getReserve_AppBasketInfoCode()
                );
            } else {
                call = apiInterface.OrderInfoInsert(
                        "OrderInfoInsert",
                        dbh.ReadConfig("BrokerCode"),
                        basketInfos.get(position).getRstmizCode(),
                        basketInfos.get(position).getPersonName(),
                        basketInfos.get(position).getMobileNo(),
                        basketInfos.get(position).getExplain(),
                        "0",
                        basketInfos.get(position).getReserveStart(),
                        basketInfos.get(position).getReserveEnd(),
                        date,
                        "3",
                        basketInfos.get(position).getAppBasketInfoCode()
                );
            }

            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                    assert response.body() != null;
                    if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode())>0){
                        callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                    }else{
                        TableActivity activity = (TableActivity) mContext;
                        activity.CallSpinner();
                        callMethod.showToast("ثبت گردید");
                    }
                     
                }
                @Override
                public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                }
            });

            callMethod.showToast("btn_cleartable");

        });

        holder.btn_reserve.setOnClickListener(v -> action.ReserveBoxDialog(basketInfos.get(position)));


        holder.btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callMethod.showToast("btn_print");

            }
        });

        holder.btn_changemiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callMethod.showToast("btn_changemiz");

            }
        });

        holder.btn_explainedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callMethod.showToast("btn_explainedit");
            }
        });


    }

    @Override
    public int getItemCount() {
        return basketInfos.size();
    }


}
