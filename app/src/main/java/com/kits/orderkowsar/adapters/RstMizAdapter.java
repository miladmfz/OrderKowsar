package com.kits.orderkowsar.adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.activity.BasketActivity;
import com.kits.orderkowsar.activity.SearchActivity;
import com.kits.orderkowsar.activity.TableActivity;
import com.kits.orderkowsar.application.Action;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.application.Print;
import com.kits.orderkowsar.application.PrintChangeTable;
import com.kits.orderkowsar.model.BasketInfo;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.viewholder.RstMizViewHolder;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;

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
    Print print;
    PrintChangeTable printchange;
    NotificationManager notificationManager;
    String channel_id = "Kowsarmobile";
    String channel_name = "home";
    String changeTable;

    public RstMizAdapter(ArrayList<BasketInfo> BasketInfos, String changeflag, Context context) {
        this.mContext = context;
        this.basketInfos = BasketInfos;
        this.callMethod = new CallMethod(mContext);
        this.action = new Action(mContext);
        this.print = new Print(mContext);
        this.printchange = new PrintChangeTable(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        call = apiInterface.GetTodeyFromServer("GetTodeyFromServer");
        this.changeTable = changeflag;
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                assert response.body() != null;
                date = response.body().getText();
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
            }
        });

    }

    @NonNull
    @Override
    public RstMizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (changeTable.equals("0")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_item_cardview, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tableempty_item_cardview, parent, false);
        }
        if (callMethod.ReadString("LANG").equals("fa")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        return new RstMizViewHolder(view);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull final RstMizViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.tv_name.setText(callMethod.NumberRegion(basketInfos.get(position).getRstMizName()));
        holder.btn_reserve.setVisibility(View.GONE);


        if (changeTable.equals("0")) {
            holder.tv_placecount.setText(callMethod.NumberRegion(basketInfos.get(position).getPlaceCount()));

            if (basketInfos.get(position).getExplain().length() > 0) {
                holder.ll_table_mizexplain.setVisibility(View.VISIBLE);
                holder.tv_mizexplain.setText(callMethod.NumberRegion(basketInfos.get(position).getExplain()));
            } else {
                holder.ll_table_mizexplain.setVisibility(View.GONE);
            }

            if (basketInfos.get(position).getInfoExplain().length() > 0) {
                holder.ll_table_infoexplain.setVisibility(View.VISIBLE);
                holder.tv_infoexplain.setText(callMethod.NumberRegion(basketInfos.get(position).getInfoExplain()));
            } else {
                holder.ll_table_infoexplain.setVisibility(View.GONE);
            }

            if (basketInfos.get(position).getRes_BrokerName().length() > 0) {
                holder.ll_table_reserve.setVisibility(View.VISIBLE);
                holder.tv_reservestart.setText(callMethod.NumberRegion(basketInfos.get(position).getReserveStart()));
                holder.tv_reservebrokername.setText(callMethod.NumberRegion(basketInfos.get(position).getPersonName()));
                holder.tv_reservemobileno.setText(callMethod.NumberRegion(basketInfos.get(position).getMobileNo()));
            } else {
                holder.ll_table_reserve.setVisibility(View.GONE);
            }


            switch (basketInfos.get(position).getInfoState()) {
                case "0":
                case "3":
                    holder.ll_table_timebroker.setVisibility(View.GONE);
                    holder.ll_table_print_change.setVisibility(View.GONE);
                    holder.ll_table_mizexplain.setVisibility(View.GONE);
                    holder.ll_table_infoexplain.setVisibility(View.GONE);
                    holder.btn_cleartable.setVisibility(View.GONE);
                    if (basketInfos.get(position).getIsReserved().equals("1")) {
                        holder.btn_cleartable.setVisibility(View.VISIBLE);
                    }
                    break;
                case "1":
                    holder.btn_print.setText(R.string.rstmiz_seeandprintbtn);
                    holder.ll_table_print_change.setVisibility(View.VISIBLE);
                    holder.btn_cleartable.setVisibility(View.VISIBLE);
                    holder.tv_brokername.setText(callMethod.NumberRegion(basketInfos.get(position).getBrokerName()));
                    break;
                case "2":
                    holder.btn_print.setText(R.string.rstmiz_reprint);
                    holder.btn_cleartable.setVisibility(View.GONE);
                    holder.ll_table_print_change.setVisibility(View.VISIBLE);
                    holder.ll_table_timebroker.setVisibility(View.VISIBLE);
                    Calendar time_now = Calendar.getInstance();
                    Calendar time_factor = Calendar.getInstance();
                    Calendar time_duration = Calendar.getInstance();
                    time_factor.set(Calendar.HOUR_OF_DAY, Integer.parseInt(basketInfos.get(position).getTimeStart().substring(0, 2)));
                    time_factor.set(Calendar.MINUTE, Integer.parseInt(basketInfos.get(position).getTimeStart().substring(3, 5)));
                    long bet = (time_now.getTimeInMillis() - time_factor.getTimeInMillis());
                    time_duration.set(Calendar.MILLISECOND, Math.toIntExact(bet));
                    String thourOfDay, tminute, Time;
                    thourOfDay = "0" + (bet / (1000 * 60 * 60));
                    tminute = "0" + ((bet / (1000 * 60)) % 60);
                    Time = thourOfDay.substring(thourOfDay.length() - 2) + ":" + tminute.substring(tminute.length() - 2);
                    basketInfos.get(position).setTime(Time);
                    holder.tv_time.setText(callMethod.NumberRegion(basketInfos.get(position).getTime()));
                    holder.tv_brokername.setText(callMethod.NumberRegion(basketInfos.get(position).getBrokerName()));
                    if (Integer.parseInt(basketInfos.get(position).getTime().substring(0, 2)) > 1) {
                        noti_Messaging("اتمام زمان ", basketInfos.get(position).getRstMizName());
                    }


                    break;
                default:
                    break;
            }


            holder.btn_select.setOnClickListener(v -> {
                callMethod.EditString("RstMizName", basketInfos.get(position).getRstMizName());
                callMethod.EditString("AppBasketInfoCode", basketInfos.get(position).getAppBasketInfoCode());

                if (call.isExecuted()) {
                    call.cancel();
                }

                if (basketInfos.get(position).getInfoState().equals("0") || basketInfos.get(position).getInfoState().equals("3")) {

                    if (basketInfos.get(position).getIsReserved().equals("1")) {
                        call = apiInterface.OrderInfoInsert("OrderInfoInsert", dbh.ReadConfig("BrokerCode"), basketInfos.get(position).getRstmizCode(), basketInfos.get(position).getPersonName(), basketInfos.get(position).getMobileNo(), basketInfos.get(position).getExplain(), "0", basketInfos.get(position).getReserveStart(), basketInfos.get(position).getReserveEnd(), basketInfos.get(position).getToday(), "1", basketInfos.get(position).getReserve_AppBasketInfoCode());
                        call.enqueue(new Callback<RetrofitResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                assert response.body() != null;
                                if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                                    callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                                } else {

                                    callMethod.EditString("RstMizName", basketInfos.get(position).getRstMizName());
                                    callMethod.EditString("MizType", basketInfos.get(position).getMizType());

                                    callMethod.EditString("RstmizCode", basketInfos.get(position).getRstmizCode());
                                    callMethod.EditString("PersonName", basketInfos.get(position).getPersonName());
                                    callMethod.EditString("MobileNo", basketInfos.get(position).getMobileNo());
                                    callMethod.EditString("InfoExplain", basketInfos.get(position).getInfoExplain());
                                    callMethod.EditString("Prepayed", basketInfos.get(position).getPrepayed());
                                    callMethod.EditString("ReserveStart", basketInfos.get(position).getReserveStart());
                                    callMethod.EditString("ReserveEnd", basketInfos.get(position).getReserveEnd());
                                    callMethod.EditString("Today", basketInfos.get(position).getToday());
                                    callMethod.EditString("InfoState", basketInfos.get(position).getInfoState());
                                    callMethod.EditString("AppBasketInfoCode", basketInfos.get(position).getInfoState());


                                    intent = new Intent(mContext, SearchActivity.class);
                                    mContext.startActivity(intent);
                                }

                            }

                            @Override
                            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                            }
                        });
                    } else {
                        call = apiInterface.OrderInfoInsert("OrderInfoInsert", dbh.ReadConfig("BrokerCode"), basketInfos.get(position).getRstmizCode(), "", "", "", "0", "", "", basketInfos.get(position).getToday(), "1", "0");
                        call.enqueue(new Callback<RetrofitResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                                assert response.body() != null;
                                if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                                    callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                                } else {
                                    callMethod.EditString("RstMizName", basketInfos.get(position).getRstMizName());

                                    callMethod.EditString("AppBasketInfoCode", response.body().getBasketInfos().get(0).getAppBasketInfoCode());
                                    intent = new Intent(mContext, SearchActivity.class);
                                    mContext.startActivity(intent);
                                }


                            }

                            @Override
                            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                            }
                        });
                    }


                } else {

                    if (Integer.parseInt(basketInfos.get(position).getTime().substring(0, 2)) < 2) {



                        intent = new Intent(mContext, SearchActivity.class);
                        mContext.startActivity(intent);
                    } else {
                        new AlertDialog.Builder(mContext)
                                .setTitle(R.string.textvalue_allert)
                                .setMessage("زمان میز تمام شده است مایل به سفارش می باشید ؟")
                                .setPositiveButton(R.string.textvalue_yes, (dialogInterface, i) -> {

                                    intent = new Intent(mContext, SearchActivity.class);
                                    mContext.startActivity(intent);
                                }).setNegativeButton(R.string.textvalue_no, (dialogInterface, i) -> {

                                }).show();
                    }
                }


            });

            holder.btn_cleartable.setOnClickListener(v -> {
                if (call.isExecuted()) {
                    call.cancel();
                }

                if (basketInfos.get(position).getIsReserved().equals("1")) {
                    call = apiInterface.OrderInfoInsert("OrderInfoInsert", dbh.ReadConfig("BrokerCode"), basketInfos.get(position).getRstmizCode(), basketInfos.get(position).getPersonName(), basketInfos.get(position).getMobileNo(), basketInfos.get(position).getExplain(), "0", basketInfos.get(position).getReserveStart(), basketInfos.get(position).getReserveEnd(), date, "3", basketInfos.get(position).getReserve_AppBasketInfoCode());
                } else {
                    call = apiInterface.OrderInfoInsert("OrderInfoInsert", dbh.ReadConfig("BrokerCode"), basketInfos.get(position).getRstmizCode(), basketInfos.get(position).getPersonName(), basketInfos.get(position).getMobileNo(), basketInfos.get(position).getExplain(), "0", basketInfos.get(position).getReserveStart(), basketInfos.get(position).getReserveEnd(), basketInfos.get(position).getToday(), "3", basketInfos.get(position).getAppBasketInfoCode());
                }

                new AlertDialog.Builder(mContext).setTitle(R.string.textvalue_allert).setMessage(R.string.textvalue_freetable).setPositiveButton(R.string.textvalue_yes, (dialogInterface, i) -> {


                    call.enqueue(new Callback<RetrofitResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                            assert response.body() != null;
                            if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                                callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                            } else {
                                TableActivity activity = (TableActivity) mContext;
                                activity.CallSpinner();
                                callMethod.showToast(activity.getString(R.string.textvalue_recorded));
                            }

                        }

                        @Override
                        public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                        }
                    });
                }).setNegativeButton(R.string.textvalue_no, (dialogInterface, i) -> {
                }).show();
            });

            holder.btn_reserve.setOnClickListener(v -> action.ReserveBoxDialog(basketInfos.get(position)));


            holder.btn_print.setOnClickListener(v -> {
                callMethod.EditString("AppBasketInfoCode", basketInfos.get(position).getAppBasketInfoCode());
                if (basketInfos.get(position).getInfoState().equals("2")) {
                    new AlertDialog.Builder(mContext).setTitle(R.string.textvalue_allert).setMessage(R.string.textvalue_reprinting).setPositiveButton(R.string.textvalue_yes, (dialogInterface, i) -> {

                        call = apiInterface.Order_CanPrint("Order_CanPrint", callMethod.ReadString("AppBasketInfoCode"), "1");
                        call.enqueue(new Callback<RetrofitResponse>() {
                            @Override
                            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                                if (response.isSuccessful()) {
                                    assert response.body() != null;
                                    if (response.body().getText().equals("Done")) {
                                        print.GetHeader_Data("");
                                    }

                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {

                            }
                        });


                    }).setNegativeButton(R.string.textvalue_no, (dialogInterface, i) -> {
                    }).show();
                } else {
                    intent = new Intent(mContext, BasketActivity.class);
                    mContext.startActivity(intent);
                }
            });

            holder.btn_changemiz.setOnClickListener(v -> {


                callMethod.EditString("RstMizName", basketInfos.get(position).getRstMizName());
                callMethod.EditString("MizType", basketInfos.get(position).getMizType());

                callMethod.EditString("RstmizCode", basketInfos.get(position).getRstmizCode());
                callMethod.EditString("PersonName", basketInfos.get(position).getPersonName());
                callMethod.EditString("MobileNo", basketInfos.get(position).getMobileNo());
                callMethod.EditString("InfoExplain", basketInfos.get(position).getInfoExplain());
                callMethod.EditString("Prepayed", "0");

                callMethod.EditString("ReserveStart", basketInfos.get(position).getReserveStart());
                callMethod.EditString("ReserveEnd", basketInfos.get(position).getReserveEnd());
                callMethod.EditString("Today", basketInfos.get(position).getToday());
                callMethod.EditString("InfoState", basketInfos.get(position).getInfoState());
                callMethod.EditString("AppBasketInfoCode", basketInfos.get(position).getAppBasketInfoCode());

                intent = new Intent(mContext, TableActivity.class);
                intent.putExtra("State", "3");
                intent.putExtra("EditTable", "1");
                mContext.startActivity(intent);


            });

            holder.btn_explainedit.setOnClickListener(v -> action.EditBasketInfoExplain(basketInfos.get(position)));
        } else {


            holder.btn_select.setOnClickListener(v -> {


                String explainvalue="";

                if (callMethod.ReadString("InfoExplain").contains("*")) {
                    int startsub = callMethod.ReadString("InfoExplain").indexOf("*");
                    String temp = callMethod.ReadString("InfoExplain").substring(startsub);
                    int endsub = temp.indexOf("*");
                    explainvalue = temp.substring(0, endsub);
                }

                String extraexplain = mContext.getString(R.string.textvalue_transfertext) + callMethod.ReadString("RstMizName") + mContext.getString(R.string.textvalue_transfer_to) + basketInfos.get(position).getRstMizName() + ") ";

                call = apiInterface.OrderInfoInsert("OrderInfoInsert",
                        dbh.ReadConfig("BrokerCode"),
                        callMethod.ReadString("RstmizCode"),
                        callMethod.ReadString("PersonName"),
                        callMethod.ReadString("MobileNo"),
                        explainvalue + extraexplain,
                        "0",
                        callMethod.ReadString("ReserveStart"),
                        callMethod.ReadString("ReserveEnd"),
                        callMethod.ReadString("Today"),
                        callMethod.ReadString("InfoState"),
                        callMethod.ReadString("AppBasketInfoCode")
                );
                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                                callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                            } else {
                                callMethod.EditString("InfoExplain", callMethod.ReadString("InfoExplain") + extraexplain);
                                call = apiInterface.Order_CanPrint("Order_CanPrint", callMethod.ReadString("AppBasketInfoCode"), "1");
                                call.enqueue(new Callback<RetrofitResponse>() {
                                    @Override
                                    public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                                        if (response.isSuccessful()) {
                                            assert response.body() != null;
                                            if (response.body().getText().equals("Done")) {
                                                printchange.GetHeader_Data("MizType", basketInfos.get(position));

                                            }

                                        }
                                    }

                                    @Override
                                    public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {

                                    }
                                });


                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                        Log.e("test", t.getMessage());

                    }
                });


            });
        }

    }

    @Override
    public int getItemCount() {
        return basketInfos.size();
    }

    public void noti_Messaging(String title, String message) {

        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel Channel = new NotificationChannel(channel_id, channel_name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(Channel);
        }
        Intent notificationIntent = new Intent(mContext, TableActivity.class);

        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notcompat = new NotificationCompat.Builder(mContext, channel_id)
                .setContentTitle(title)
                .setContentText(message)
                .setOnlyAlertOnce(false)
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(contentIntent);

        notificationManager.notify(1, notcompat.build());
    }


}
