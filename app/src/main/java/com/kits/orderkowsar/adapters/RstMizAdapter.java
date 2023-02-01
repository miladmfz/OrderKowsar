package com.kits.orderkowsar.adapters;


import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.RecyclerView;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.activity.BasketActivity;
import com.kits.orderkowsar.activity.SearchActivity;
import com.kits.orderkowsar.activity.TableActivity;
import com.kits.orderkowsar.application.Action;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.application.Print;
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

    String changeTable;

    public RstMizAdapter(ArrayList<BasketInfo> BasketInfos, String changeflag, Context context) {
        this.mContext = context;
        this.basketInfos = BasketInfos;
        this.callMethod = new CallMethod(mContext);
        this.action = new Action(mContext);
        this.print = new Print(mContext);
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
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RstMizViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.tv_name.setText(callMethod.NumberRegion(basketInfos.get(position).getRstMizName()));
        Log.e("test",holder.itemView.getWidth()+"");
        Log.e("test",Integer.toString(holder.tv_name.getWidth())+"");
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
                    holder.tv_time.setText(callMethod.NumberRegion(Time));
                    holder.tv_brokername.setText(callMethod.NumberRegion(basketInfos.get(position).getBrokerName()));
                    break;
                default:
                    break;
            }


            holder.btn_select.setOnClickListener(v -> {
                callMethod.EditString("MizType", basketInfos.get(position).getMizType());

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

                                    callMethod.EditString("AppBasketInfoCode", basketInfos.get(position).getAppBasketInfoCode());
                                    callMethod.EditString("RstMizName", basketInfos.get(position).getRstMizName());
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
                    callMethod.EditString("RstMizName", basketInfos.get(position).getRstMizName());

                    callMethod.EditString("AppBasketInfoCode", basketInfos.get(position).getAppBasketInfoCode());
                    intent = new Intent(mContext, SearchActivity.class);
                    mContext.startActivity(intent);
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
                String extraexplain = mContext.getString(R.string.textvalue_transfertext) + basketInfos.get(position).getRstMizName() + ") ";

                call = apiInterface.OrderInfoInsert("OrderInfoInsert", basketInfos.get(position).getBrokerRef(), basketInfos.get(position).getRstmizCode(), basketInfos.get(position).getPersonName(), basketInfos.get(position).getMobileNo(), basketInfos.get(position).getInfoExplain() + extraexplain, basketInfos.get(position).getPrepayed(), basketInfos.get(position).getReserveStart(), basketInfos.get(position).getReserveEnd(), basketInfos.get(position).getToday(), basketInfos.get(position).getInfoState(), basketInfos.get(position).getAppBasketInfoCode());
                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                                callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                            } else {
                                callMethod.EditString("AppBasketInfoCode", basketInfos.get(position).getAppBasketInfoCode());
                                intent = new Intent(mContext, TableActivity.class);
                                intent.putExtra("State", "3");
                                intent.putExtra("EditTable", "1");
                                mContext.startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {

                    }
                });


            });

            holder.btn_explainedit.setOnClickListener(v -> action.EditBasketInfoExplain(basketInfos.get(position)));
        } else {
            holder.btn_select.setOnClickListener(v -> {

                if (!basketInfos.get(position).getMizType().equals(callMethod.ReadString("MizType"))){
                    print.GetHeader_Data("MizType");
                }
                call = apiInterface.OrderInfoInsert("OrderInfoInsert",
                        dbh.ReadConfig("BrokerCode"),
                        basketInfos.get(position).getRstmizCode(),
                        basketInfos.get(position).getPersonName(),
                        basketInfos.get(position).getMobileNo(),
                        basketInfos.get(position).getExplain(),
                        "0",
                        basketInfos.get(position).getReserveStart(),
                        basketInfos.get(position).getReserveEnd(),
                        basketInfos.get(position).getToday(),
                        basketInfos.get(position).getInfoState(),
                        callMethod.ReadString("AppBasketInfoCode")
                );
                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {
                        assert response.body() != null;
                        if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                            callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                        } else {
                            call = apiInterface.Order_CanPrint("Order_CanPrint", callMethod.ReadString("AppBasketInfoCode"), "1");
                            call.enqueue(new Callback<RetrofitResponse>() {
                                @Override
                                public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                                    if (response.isSuccessful()) {
                                        assert response.body() != null;
                                        if (response.body().getText().equals("Done")) {
                                            print.GetHeader_Data("MizType");
                                        }

                                    }
                                }

                                @Override
                                public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {

                                }
                            });

                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    }
                });

            });
        }

    }

    @Override
    public int getItemCount() {
        return basketInfos.size();
    }


}
