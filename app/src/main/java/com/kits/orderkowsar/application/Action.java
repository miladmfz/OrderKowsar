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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.activity.NavActivity;
import com.kits.orderkowsar.adapters.ReserveAdapter;
import com.kits.orderkowsar.adapters.RstMizAdapter;
import com.kits.orderkowsar.model.BasketInfo;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.time.RadialPickerLayout;
import com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.text.DecimalFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Action extends Activity implements DatePickerDialog.OnDateSetListener {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    public APIInterface apiInterface;

    Context mContext;
    CallMethod callMethod;
    DatabaseHelper dbh;
    Intent intent;
    Integer il;
    String url;
    public Call<RetrofitResponse> call;
    PersianCalendar persianCalendar;
    String date;
    Dialog dialog;

    public Action(Context mContext) {
        this.mContext = mContext;
        this.il = 0;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        this.persianCalendar = new PersianCalendar();
        this.dialog = new Dialog(mContext);



        String tmonthOfYear, tdayOfMonth;
        tmonthOfYear = "0" + (persianCalendar.getPersianMonth() + 1);
        tdayOfMonth = "0" + persianCalendar.getPersianDay();
        date = persianCalendar.getPersianYear() + "/"
                + tmonthOfYear.substring(tmonthOfYear.length() - 2) + "/"
                + tdayOfMonth.substring(tdayOfMonth.length() - 2);
    }


    public void reserve_box_dialog(BasketInfo basketInfo) {

        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.reserve_box);

        EditText ed_personname = dialog.findViewById(R.id.reserve_box_personname);
        EditText ed_mobileno = dialog.findViewById(R.id.reserve_box_mobileno);
        EditText ed_explain = dialog.findViewById(R.id.reserve_box_explain);
        TextView tv_reservestart = dialog.findViewById(R.id.reserve_box_reservestart);
        TextView tv_reserveend = dialog.findViewById(R.id.reserve_box_reserveend);
        TextView tv_date = dialog.findViewById(R.id.reserve_box_date);

        TextView tv_showrecycler = dialog.findViewById(R.id.reserve_box_show_recycler);
        RecyclerView  recycler = dialog.findViewById(R.id.reserve_box_recycler);

        Button btn_reserve = dialog.findViewById(R.id.reserve_box_btn_send);
        DisplayMetrics metrics = new DisplayMetrics();


        tv_reservestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TimePickerDialog timePickerDialog = new TimePickerDialog();
                timePickerDialog.show(((Activity) mContext).getFragmentManager(), "Datepickerdialog");
                callMethod.showToast("ed_reservestart");
            }
        });

        tv_reserveend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                callMethod.showToast("ed_reserveend");

            }
        });

        btn_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               callMethod.showToast("btn_reserve");

            }
        });

        tv_date.setOnClickListener(v -> {


            PersianCalendar persianCalendar1 = new PersianCalendar();
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    null,
                    persianCalendar1.getPersianYear(),
                    persianCalendar1.getPersianMonth(),
                    persianCalendar1.getPersianDay()
            );


            datePickerDialog.show(((Activity) mContext).getFragmentManager(), "Datepickerdialog");

        });
        call = apiInterface.OrderReserveList("OrderReserveList", basketInfo.getRstmizCode());
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {

                assert response.body() != null;
                ReserveAdapter adapter = new ReserveAdapter(response.body().getBasketInfos(), mContext);
                recycler.setLayoutManager(new GridLayoutManager(mContext,1));
                recycler.setAdapter(adapter);
                recycler.setItemAnimator(new DefaultItemAnimator());
                recycler.setAdapter(adapter);

            }
            @Override
            public void onFailure(Call<RetrofitResponse> call, Throwable t) {

            }
        });

        tv_showrecycler.setOnClickListener(v -> {

            if(recycler.getVisibility()==View.GONE){
                recycler.setVisibility(View.VISIBLE);
            }else{
                recycler.setVisibility(View.GONE);
            }
        });


        dialog.show();

    }

    public void OrderInfoInsertNew(BasketInfo basketInfo) {

        call = apiInterface.OrderInfoInsert(
                "Insertbasket",
                dbh.ReadConfig("BrokerCode"),
                basketInfo.getRstmizCode(),
                "",
                "",
                "",
                "",
                "",
                "",
                date,
                "0",
                "0"
        );

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {

            }

            @Override
            public void onFailure(Call<RetrofitResponse> call, Throwable t) {

            }
        });

    }


    public void OrderInfoInsertFactor(BasketInfo basketInfo) {

        call = apiInterface.OrderInfoInsert(
                "Insertbasket",
                dbh.ReadConfig("BrokerCode"),
                basketInfo.getRstmizCode(),
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "1",
                basketInfo.getAppBasketInfoCode()
        );
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {

            }

            @Override
            public void onFailure(Call<RetrofitResponse> call, Throwable t) {

            }
        });
    }


    public void OrderInfoInsertReserve() {

        call = apiInterface.OrderInfoInsert(
                "Insertbasket",
                dbh.ReadConfig("BrokerCode"),
                "",
                "PersonName",
                "mobile",
                "InfoExplain",
                "PrePayed",
                "rstime",
                "retime",
                "",
                "2",
                "0"
        );
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {

            }

            @Override
            public void onFailure(Call<RetrofitResponse> call, Throwable t) {

            }
        });
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
    }


}
