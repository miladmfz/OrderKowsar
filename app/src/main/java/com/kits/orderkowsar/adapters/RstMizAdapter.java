package com.kits.orderkowsar.adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.activity.BasketActivity;
import com.kits.orderkowsar.activity.SearchActivity;
import com.kits.orderkowsar.activity.TableActivity;
import com.kits.orderkowsar.application.Action;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.application.ImageInfo;
import com.kits.orderkowsar.model.BasketInfo;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.Good;
import com.kits.orderkowsar.model.NumberFunctions;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.viewholder.RstMizViewHolder;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RstMizAdapter extends RecyclerView.Adapter<RstMizViewHolder> {
    private final Context mContext;
    CallMethod callMethod;
    ArrayList<BasketInfo> basketInfos;
    ArrayList<Good> goods;
    APIInterface apiInterface;
    Intent intent;
    DatabaseHelper dbh;
    String date;
    Dialog dialog;
    LinearLayoutCompat main_layout;
    Call<RetrofitResponse> call;
    Action action;
    Bitmap bitmap_factor;
    String bitmap_factor_base64 = "";
    int width = 500;
    int height = 1;

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
                date = response.body().getText();
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

        Log.e("test=",position+"_"+basketInfos.get(position).getExplain());
        Log.e("test=",position+"_"+basketInfos.get(position).getInfoExplain());

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
                holder.ll_table_print_change.setVisibility(View.GONE);
                holder.btn_cleartable.setVisibility(View.GONE);
                if (basketInfos.get(position).getIsReserved().equals("1")) {
                    holder.btn_cleartable.setVisibility(View.VISIBLE);
                }

                break;
            case "1":
                holder.btn_print.setText("مشاهده و چاپ");
                holder.ll_table_print_change.setVisibility(View.VISIBLE);
                holder.btn_cleartable.setVisibility(View.VISIBLE);
                holder.tv_brokername.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getBrokerName()));

                break;

            case "2":

                holder.btn_print.setText("چاپ مجدد");
                holder.ll_table_print_change.setVisibility(View.VISIBLE);
                holder.btn_cleartable.setVisibility(View.VISIBLE);
                holder.ll_table_timebroker.setVisibility(View.VISIBLE);

                Calendar time_now = Calendar.getInstance();
                Calendar time_factor = Calendar.getInstance();
                Calendar time_duration = Calendar.getInstance();

                time_factor.set(Calendar.HOUR_OF_DAY, Integer.parseInt(basketInfos.get(position).getTimeStart().substring(0, 2)));
                time_factor.set(Calendar.MINUTE, Integer.parseInt(basketInfos.get(position).getTimeStart().substring(3, 5)));


                long bet = (time_now.getTimeInMillis() - time_factor.getTimeInMillis());
                time_duration.set(Calendar.MILLISECOND, Math.toIntExact(bet));

                String thourOfDay, tminute, Time = "";
                thourOfDay = "0" + (bet / (1000 * 60 * 60));
                tminute = "0" + ((bet / (1000 * 60)) % 60);
                Time = thourOfDay.substring(thourOfDay.length() - 2) + ":"
                        + tminute.substring(tminute.length() - 2);

                holder.tv_time.setText(NumberFunctions.PerisanNumber(Time));
                holder.tv_brokername.setText(NumberFunctions.PerisanNumber(basketInfos.get(position).getBrokerName()));

                break;


            default:
                break;
        }


        holder.btn_select.setOnClickListener(v -> {
            if (call.isExecuted()) {
                call.cancel();
            }
            if (basketInfos.get(position).getInfoState().equals("0") || basketInfos.get(position).getInfoState().equals("3")) {

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
                            basketInfos.get(position).getToday(),
                            "1",
                            basketInfos.get(position).getReserve_AppBasketInfoCode()
                    );
                    call.enqueue(new Callback<RetrofitResponse>() {
                        @Override
                        public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                            assert response.body() != null;
                            if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                                callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                            } else {

                                callMethod.EditString("AppBasketInfoCode", basketInfos.get(position).getAppBasketInfoCode());
                                intent = new Intent(mContext, SearchActivity.class);
                                mContext.startActivity(intent);
                            }

                        }

                        @Override
                        public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                        }
                    });
                } else {
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
                            if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                                callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                            } else {
                                Log.e("test_1", basketInfos.get(position).getAppBasketInfoCode());

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


            } else {
                callMethod.EditString("AppBasketInfoCode", basketInfos.get(position).getAppBasketInfoCode());
                intent = new Intent(mContext, SearchActivity.class);
                mContext.startActivity(intent);
            }


        });

        holder.btn_cleartable.setOnClickListener(v -> {
            if (call.isExecuted()) {
                call.cancel();
            }
            Log.e("test", basketInfos.get(position).getIsReserved());

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
                        basketInfos.get(position).getToday(),
                        "3",
                        basketInfos.get(position).getAppBasketInfoCode()
                );
            }

            new AlertDialog.Builder(mContext)
                    .setTitle("توجه")
                    .setMessage("میز خالی شود؟")
                    .setPositiveButton("بله", (dialogInterface, i) -> {


                        call.enqueue(new Callback<RetrofitResponse>() {
                            @Override
                            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                                assert response.body() != null;
                                if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                                    callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                                } else {
                                    TableActivity activity = (TableActivity) mContext;
                                    activity.CallSpinner();
                                    callMethod.showToast("ثبت گردید");
                                }

                            }

                            @Override
                            public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                            }
                        });
                    })
                    .setNegativeButton("خیر", (dialogInterface, i) -> {
                    })
                    .show();
        });

        holder.btn_reserve.setOnClickListener(v -> action.ReserveBoxDialog(basketInfos.get(position)));


        holder.btn_print.setOnClickListener(v -> {
            callMethod.EditString("AppBasketInfoCode", basketInfos.get(position).getAppBasketInfoCode());
            if (basketInfos.get(position).getInfoState().equals("2")) {
                new AlertDialog.Builder(mContext)
                        .setTitle("توجه")
                        .setMessage("مجددا چاپ شود؟")
                        .setPositiveButton("بله", (dialogInterface, i) -> {

                            call = apiInterface.Order_CanPrint(
                                    "Order_CanPrint",
                                    callMethod.ReadString("AppBasketInfoCode"),
                                    "1"
                            );
                            call.enqueue(new Callback<RetrofitResponse>() {
                                @Override
                                public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                                    if (response.isSuccessful()) {
                                        assert response.body() != null;
                                        if (response.body().getText().equals("Done")) {
                                            action.GetFactorPrint();
                                        }

                                    }
                                }

                                @Override
                                public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {

                                    Log.e("test", t.getMessage());
                                }
                            });


                        })
                        .setNegativeButton("خیر", (dialogInterface, i) -> {
                        })
                        .show();
            } else {
                intent = new Intent(mContext, BasketActivity.class);
                mContext.startActivity(intent);
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
                action.EditBasketInfoExplain(basketInfos.get(position));

            }
        });


    }

    @Override
    public int getItemCount() {
        return basketInfos.size();
    }


}
