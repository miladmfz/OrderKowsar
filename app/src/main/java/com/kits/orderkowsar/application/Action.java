package com.kits.orderkowsar.application;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.activity.NavActivity;
import com.kits.orderkowsar.activity.SearchActivity;
import com.kits.orderkowsar.activity.TableActivity;
import com.kits.orderkowsar.adapters.GoodAdapter;
import com.kits.orderkowsar.adapters.GoodBoxItemAdapter;
import com.kits.orderkowsar.adapters.ReserveAdapter;
import com.kits.orderkowsar.adapters.RstMizAdapter;
import com.kits.orderkowsar.model.BasketInfo;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.DistinctValue;
import com.kits.orderkowsar.model.Good;
import com.kits.orderkowsar.model.NumberFunctions;
import com.kits.orderkowsar.model.ObjectType;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.time.RadialPickerLayout;
import com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Action extends Activity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    public APIInterface apiInterface;

    private final Context mContext;
    CallMethod callMethod;
    DatabaseHelper dbh;
    Intent intent;
    Integer il;
    public Call<RetrofitResponse> call;
    PersianCalendar persianCalendar;
    String date;
    Dialog dialog;
    Calendar cldr;
    TimePickerDialog picker;
    TextView tv_reservestart;
    TextView tv_reserveend;
    TextView tv_date;
    int ehour = 0;
    int eminutes = 0;
    ArrayList<DistinctValue> values = new ArrayList<>();
    ArrayList<String> values_array = new ArrayList<>();
    ArrayList<Good> Goods;
    ArrayList<Good> good_box_items = new ArrayList<>();

    public Action(Context mContext) {
        this.mContext = mContext;
        this.il = 0;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        this.persianCalendar = new PersianCalendar();
        this.dialog = new Dialog(mContext);


    }


    public void ReserveBoxDialog(BasketInfo basketInfo) {

        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.reserve_box);

        EditText ed_personname = dialog.findViewById(R.id.reserve_box_personname);
        EditText ed_mobileno = dialog.findViewById(R.id.reserve_box_mobileno);
        EditText ed_explain = dialog.findViewById(R.id.reserve_box_explain);
        tv_reservestart = dialog.findViewById(R.id.reserve_box_reservestart);
        tv_reserveend = dialog.findViewById(R.id.reserve_box_reserveend);
        tv_date = dialog.findViewById(R.id.reserve_box_date);

        TextView tv_showrecycler = dialog.findViewById(R.id.reserve_box_show_recycler);
        RecyclerView recycler = dialog.findViewById(R.id.reserve_box_recycler);

        Button btn_reserve = dialog.findViewById(R.id.reserve_box_btn_send);

        call = apiInterface.OrderReserveList("OrderReserveList", basketInfo.getRstmizCode());
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                assert response.body() != null;
                ReserveAdapter adapter = new ReserveAdapter(response.body().getBasketInfos(), mContext);
                recycler.setLayoutManager(new GridLayoutManager(mContext, 1));
                recycler.setAdapter(adapter);
                recycler.setItemAnimator(new DefaultItemAnimator());
                recycler.setAdapter(adapter);

            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

            }
        });


        call = apiInterface.GetTodeyFromServer("GetTodeyFromServer");

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                assert response.body() != null;
                date = response.body().getText();
                tv_date.setText(NumberFunctions.PerisanNumber(date));
            }

            @Override
            public void onFailure(Call<RetrofitResponse> call, Throwable t) {
            }
        });

        tv_reservestart.setOnClickListener(v -> {

            cldr = Calendar.getInstance();
            int hour = cldr.get(Calendar.HOUR_OF_DAY);
            int minutes = cldr.get(Calendar.MINUTE);
            new TimePickerDialog();
            picker = TimePickerDialog.newInstance((view, hourOfDay, minute) -> {
                String thourOfDay, tminute, Time = "";
                thourOfDay = "0" + hourOfDay;
                tminute = "0" + minute;
                Time = thourOfDay.substring(thourOfDay.length() - 2) + ":"
                        + tminute.substring(tminute.length() - 2);

                tv_reservestart.setText(NumberFunctions.PerisanNumber(Time));
                call = apiInterface.kowsar_info(
                        "kowsar_info",
                        "AppOrder_ValidReserveTime"
                );
                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                        String ehourOfDay = "", eminute = "", eTime = "";
                        if (minute + Integer.parseInt(response.body().getText()) > 60) {
                            eminute = String.valueOf(minute + Integer.parseInt(response.body().getText()) - 60);
                            if ((hourOfDay + 1) > 23) {
                                ehourOfDay = String.valueOf(hourOfDay);
                                eminute = "59";
                            } else {
                                ehourOfDay = String.valueOf(hourOfDay + 1);
                            }
                        } else {
                            eminute = String.valueOf(minute + Integer.parseInt(response.body().getText()));
                        }

                        ehour = Integer.parseInt(ehourOfDay);
                        eminutes = Integer.parseInt(eminute);

                        ehourOfDay = "0" + ehourOfDay;
                        eminute = "0" + eminute;
                        eTime = ehourOfDay.substring(ehourOfDay.length() - 2) + ":"
                                + eminute.substring(eminute.length() - 2);

                        tv_reserveend.setText(NumberFunctions.PerisanNumber(eTime));


                    }

                    @Override
                    public void onFailure(Call<RetrofitResponse> call, Throwable t) {

                    }
                });


            }, hour, minutes, true);
            picker.show(((Activity) mContext).getFragmentManager(), "Timepickerdialog");


        });

        tv_reserveend.setOnClickListener(v -> {


            new TimePickerDialog();
            picker = TimePickerDialog.newInstance((view, ehour, eminutes) -> {
                String thourOfDay, tminute, Time = "";
                thourOfDay = "0" + ehour;
                tminute = "0" + eminutes;
                Time = thourOfDay.substring(thourOfDay.length() - 2) + ":"
                        + tminute.substring(tminute.length() - 2);
                tv_reserveend.setText(NumberFunctions.PerisanNumber(Time));


            }, ehour, eminutes, true);
            picker.show(((Activity) mContext).getFragmentManager(), "Timepickerdialog");


        });

        tv_date.setOnClickListener(v -> {

            PersianCalendar persianCalendar1 = new PersianCalendar();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    this,
                    persianCalendar1.getPersianYear(),
                    persianCalendar1.getPersianMonth(),
                    persianCalendar1.getPersianDay()
            );
            datePickerDialog.show(((Activity) mContext).getFragmentManager(), "Datepickerdialog");

        });

        tv_showrecycler.setOnClickListener(v -> {

            if (recycler.getVisibility() == View.GONE) {
                recycler.setVisibility(View.VISIBLE);
            } else {
                recycler.setVisibility(View.GONE);
            }
        });


        btn_reserve.setOnClickListener(v -> {

            call = apiInterface.OrderInfoInsert(
                    "OrderInfoInsert",
                    dbh.ReadConfig("BrokerCode"),
                    basketInfo.getRstmizCode(),
                    NumberFunctions.EnglishNumber(ed_personname.getText().toString()),
                    NumberFunctions.EnglishNumber(ed_mobileno.getText().toString()),
                    NumberFunctions.EnglishNumber(ed_explain.getText().toString()),
                    "0",
                    NumberFunctions.EnglishNumber(tv_reservestart.getText().toString()),
                    NumberFunctions.EnglishNumber(tv_reserveend.getText().toString()),
                    NumberFunctions.EnglishNumber(tv_date.getText().toString()),
                    "4",
                    "0"
            );

            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                    assert response.body() != null;
                    if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                        callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                    } else {
                        dialog.dismiss();
                        TableActivity activity = (TableActivity) mContext;
                        activity.CallSpinner();
                        callMethod.showToast("ثبت گردید");
                    }
                }

                @Override
                public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                }
            });

        });

        dialog.show();

    }


    public void GoodBoxDialog(Good good,String Flag) {

        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.order_box_good);

        TextView ed_orderbox_goodname = dialog.findViewById(R.id.orderboxgood_goodname);
        EditText ed_orderbox_amount = dialog.findViewById(R.id.orderboxgood_amount);
        EditText ed_orderbox_explain = dialog.findViewById(R.id.orderboxgood_explain);
        Spinner spinner_orderbox = dialog.findViewById(R.id.orderboxgood_spinnerexplain);
        RecyclerView rc_orderbox = dialog.findViewById(R.id.orderboxgood_rc);
        Button btn_orderbox = dialog.findViewById(R.id.orderboxgood_btn);

        if(Flag.equals("1")){
            ed_orderbox_amount.setText(NumberFunctions.PerisanNumber(good.getAmount()));
            ed_orderbox_explain.setText(NumberFunctions.PerisanNumber(good.getExplain()));
            btn_orderbox.setText("اصلاح سفارش");
        }


        ed_orderbox_goodname.setText(good.getGoodName());

        call = apiInterface.GetDistinctValues("GetDistinctValues", "AppBasket", "Explain", "");
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull Response<RetrofitResponse> response) {

                assert response.body() != null;
                values_array.clear();
                values = response.body().getValues();
                for (DistinctValue value : values) {
                    values_array.add(NumberFunctions.PerisanNumber(value.getValue()));
                }

                ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(mContext,
                        android.R.layout.simple_spinner_item, values_array);
                spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_orderbox.setAdapter(spinner_adapter);
                spinner_orderbox.setSelection(0);

                spinner_orderbox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        ed_orderbox_explain.setText(values_array.get(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

            }
        });

        call = apiInterface.OrderGet(
                "OrderGet",
                callMethod.ReadString("AppBasketInfoCode"),
                "3"
        );
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    good_box_items.clear();
                    for (Good g : response.body().getGoods()) {
                        if (g.getGoodCode().equals(good.getGoodCode())) {
                            good_box_items.add(g);
                        }
                    }

                    GoodBoxItemAdapter adapter = new GoodBoxItemAdapter(good_box_items, mContext);
                    rc_orderbox.setLayoutManager(new GridLayoutManager(mContext, 1));
                    rc_orderbox.setAdapter(adapter);
                    rc_orderbox.setItemAnimator(new DefaultItemAnimator());
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {

            }
        });


        btn_orderbox.setOnClickListener(v -> {
            String amo = NumberFunctions.EnglishNumber(ed_orderbox_amount.getText().toString());
            String explain = NumberFunctions.EnglishNumber(ed_orderbox_explain.getText().toString());


            if (!amo.equals("")) {
                if (Float.parseFloat(amo) > 0) {

                    Call<RetrofitResponse> call = apiInterface.OrderRowInsert("OrderRowInsert",
                            good.getGoodCode(),
                            amo,
                            good.getMaxSellPrice(),
                            good.getGoodUnitRef(),
                            good.getDefaultUnitValue(),
                            explain,
                            callMethod.ReadString("AppBasketInfoCode"),
                            good.getRowCode()
                    );
                    call.enqueue(new Callback<RetrofitResponse>() {
                        @Override
                        public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                            assert response.body() != null;

                            Goods = response.body().getGoods();
                            if (Integer.parseInt(Goods.get(0).getErrCode()) > 0) {
                                callMethod.showToast(Goods.get(0).getErrDesc());
                            } else {
                                callMethod.showToast("ثبت گردید");
                                dialog.dismiss();
                                SearchActivity activity = (SearchActivity) mContext;
                                activity.RefreshState();
                            }

                        }

                        @Override
                        public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                        }
                    });

                } else {
                    callMethod.showToast("لطفا تعداد صحیح را وارد کنید");
                }
            } else {
                callMethod.showToast("تعداد را وارد کنید");
            }


        });

        dialog.show();


    }

    public void lottiereceipt() {

        Dialog dialog1 = new Dialog(mContext);
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.lottie);
        LottieAnimationView animationView = dialog1.findViewById(R.id.lottie_name);
        animationView.setAnimation(R.raw.receipt);
        dialog1.show();
        animationView.setRepeatCount(0);

        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dialog1.dismiss();

            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });


    }

    public void lottieok() {

        Dialog dialog1 = new Dialog(mContext);
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.lottie);
        LottieAnimationView animationView = dialog1.findViewById(R.id.lottie_name);
        animationView.setAnimation(R.raw.oklottie);
        dialog1.show();
        animationView.setRepeatCount(0);

        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dialog1.dismiss();
                intent = new Intent(mContext, NavActivity.class);
                ((Activity) mContext).finish();
                ((Activity) mContext).overridePendingTransition(0, 0);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });


    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String tmonthOfYear, tdayOfMonth;
        tmonthOfYear = "0" + (monthOfYear + 1);
        tdayOfMonth = "0" + dayOfMonth;

        date = year + "/"
                + tmonthOfYear.substring(tmonthOfYear.length() - 2) + "/"
                + tdayOfMonth.substring(tdayOfMonth.length() - 2);

        tv_date.setText(NumberFunctions.PerisanNumber(date));
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

    }
}
