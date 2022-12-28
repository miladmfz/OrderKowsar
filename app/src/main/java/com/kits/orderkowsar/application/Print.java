package com.kits.orderkowsar.application;

import static com.kits.orderkowsar.R.string.textvalue_tabletag;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.activity.TableActivity;
import com.kits.orderkowsar.model.AppPrinter;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.DistinctValue;
import com.kits.orderkowsar.model.Factor;
import com.kits.orderkowsar.model.Good;
import com.kits.orderkowsar.model.NumberFunctions;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Print {


    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    public APIInterface apiInterface;
    private final Context mContext;
    CallMethod callMethod;
    DatabaseHelper dbh;
    Intent intent;
    Integer il;
    public Call<RetrofitResponse> call;
    PersianCalendar persianCalendar;
    Dialog dialog, dialogProg;
    Dialog dialogprint;
    Calendar cldr;
    int printerconter = 0;
    ArrayList<Factor> Factor_header = new ArrayList<>();
    ArrayList<Factor> Factor_row = new ArrayList<>();
    ArrayList<AppPrinter> AppPrinters = new ArrayList<>();
    int width = 500;
    LinearLayoutCompat main_layout;
    Bitmap bitmap_factor;
    String bitmap_factor_base64 = "";
    String Filter_print = "";
    TextView tv_rep;

    public Print(Context mContext) {
        this.mContext = mContext;
        this.il = 0;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        this.persianCalendar = new PersianCalendar();
        this.dialog = new Dialog(mContext);
        this.dialogProg = new Dialog(mContext);
        this.AppPrinters = new ArrayList<>();
        printerconter = 0;

    }

    public void dialogProg() {
        dialogProg.setContentView(R.layout.rep_prog);
        tv_rep = dialogProg.findViewById(R.id.rep_prog_text);
        LottieAnimationView animationView = dialogProg.findViewById(R.id.lottie_raw);
        animationView.setAnimation(R.raw.receipt);
        dialogProg.show();

    }

    public void GetHeader_Data(String Filter) {
        Filter_print = Filter;
        dialogProg();
        tv_rep.setText(R.string.textvalue_printing);
        call = apiInterface.OrderGetFactor(
                "OrderGetFactor",
                callMethod.ReadString("AppBasketInfoCode")
        );
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Factor_header = response.body().getFactors();
                    AppPrinters.clear();
                    GetAppPrinterList();
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                GetHeader_Data("");
            }
        });

    }


    public void GetAppPrinterList() {
        call = apiInterface.OrderGetAppPrinter("OrderGetAppPrinter");
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    printerconter = 0;
                    AppPrinters = response.body().getAppPrinters();
                    GetRow_Data();
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                GetAppPrinterList();

            }
        });
    }


    public void GetRow_Data() {

        if (printerconter < (AppPrinters.size())) {
            call = apiInterface.OrderGetFactorRow(
                    "OrderGetFactorRow",
                    callMethod.ReadString("AppBasketInfoCode"),
                    AppPrinters.get(printerconter).getGoodGroups(),
                    AppPrinters.get(printerconter).getWhereClause()
            );


            if (Filter_print.length() > 0) {
                if (AppPrinters.get(printerconter).getWhereClause().contains(Filter_print)) {
                    call.enqueue(new Callback<RetrofitResponse>() {
                        @Override
                        public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                Factor_row = response.body().getFactors();
                                if (Factor_row.size() > 0) {
                                    printDialogView();
                                } else {
                                    printerconter++;
                                    GetRow_Data();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                            printerconter++;
                            GetRow_Data();
                        }
                    });

                } else {
                    printerconter++;
                    GetRow_Data();
                }
            } else {
                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            Factor_row = response.body().getFactors();
                            if (Factor_row.size() > 0) {
                                printDialogView();
                            } else {
                                printerconter++;
                                GetRow_Data();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                        printerconter++;
                        GetRow_Data();
                    }
                });
            }


        } else {
            call = apiInterface.Order_CanPrint(
                    "Order_CanPrint",
                    callMethod.ReadString("AppBasketInfoCode"),
                    "0"
            );
            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (response.body().getText().equals("Done")) {
                            callMethod.showToast(String.valueOf(R.string.textvalue_recorded));
                            ((Activity) mContext).finish();
                            dialogProg.dismiss();
                            intent = new Intent(mContext, TableActivity.class);
                            intent.putExtra("State", "0");
                            intent.putExtra("EditTable", "0");
                            mContext.startActivity(intent);
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                    GetRow_Data();
                }
            });
        }
    }


    @SuppressLint("RtlHardcoded")
    public void printDialogView() {

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);


        dialogprint = new Dialog(mContext);
        dialogprint.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogprint.setContentView(R.layout.print_layout_view);
        main_layout = dialogprint.findViewById(R.id.print_layout_view_ll);
        CreateView();

    }


    @SuppressLint("RtlHardcoded")
    public void CreateView() {

        main_layout.removeAllViews();

        LinearLayoutCompat title_layout = new LinearLayoutCompat(mContext);
        LinearLayoutCompat boby_good_layout = new LinearLayoutCompat(mContext);
        LinearLayoutCompat good_layout = new LinearLayoutCompat(mContext);
        LinearLayoutCompat total_layout = new LinearLayoutCompat(mContext);
        ViewPager ViewPager = new ViewPager(mContext);
        ViewPager ViewPager_rast = new ViewPager(mContext);
        ViewPager ViewPager_chap = new ViewPager(mContext);


        title_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        title_layout.setOrientation(LinearLayoutCompat.VERTICAL);
        title_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


        TextView tv_printCount = new TextView(mContext);
        tv_printCount.setText(R.string.textvalue_printagain);
        tv_printCount.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        tv_printCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")) + 8);
        tv_printCount.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        tv_printCount.setGravity(Gravity.CENTER);
        tv_printCount.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tv_printCount.setPadding(0, 0, 0, 15);


        TextView company_tv = new TextView(mContext);
        company_tv.setText(callMethod.NumberRegion(AppPrinters.get(printerconter).getPrinterExplain()));
        company_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        company_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")) + 8);
        company_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        company_tv.setGravity(Gravity.CENTER);
        company_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        company_tv.setPadding(0, 0, 0, 15);


        boby_good_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        good_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width - 8, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        total_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));


        good_layout.setOrientation(LinearLayoutCompat.HORIZONTAL);
        boby_good_layout.setOrientation(LinearLayoutCompat.VERTICAL);
        total_layout.setOrientation(LinearLayoutCompat.HORIZONTAL);

        good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        boby_good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        total_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


        ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, 4));
        ViewPager.setBackgroundResource(R.color.colorPrimaryDark);
        ViewPager_rast.setLayoutParams(new LinearLayoutCompat.LayoutParams(4, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        ViewPager_rast.setBackgroundResource(R.color.colorPrimaryDark);
        ViewPager_chap.setLayoutParams(new LinearLayoutCompat.LayoutParams(4, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        ViewPager_chap.setBackgroundResource(R.color.colorPrimaryDark);


        TextView customername_tv = new TextView(mContext);
        customername_tv.setText(callMethod.NumberRegion(mContext.getString(textvalue_tabletag) + Factor_header.get(0).getRstMizName()));
        customername_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        customername_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")) + 5);
        customername_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        customername_tv.setGravity(Gravity.RIGHT);
        customername_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        customername_tv.setPadding(0, 0, 0, 15);


        cldr = Calendar.getInstance();
        int hour = cldr.get(Calendar.HOUR_OF_DAY);
        int minutes = cldr.get(Calendar.MINUTE);
        String thourOfDay, tminute, Time = "";
        thourOfDay = "0" + hour;
        tminute = "0" + minutes;
        Time = thourOfDay.substring(thourOfDay.length() - 2) + ":"
                + tminute.substring(tminute.length() - 2);


        TextView factorcode_tv = new TextView(mContext);
        factorcode_tv.setText(callMethod.NumberRegion(mContext.getString(R.string.textvalue_factorcodetag) + Factor_header.get(0).getDailyCode() + "             " + Time));
        factorcode_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        factorcode_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")) + 5);
        factorcode_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        factorcode_tv.setGravity(Gravity.RIGHT);
        factorcode_tv.setPadding(0, 0, 0, 15);
        factorcode_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


        TextView factordate_tv = new TextView(mContext);
        if (Factor_header.get(0).getInfoState().equals("2")) {
            factordate_tv.setText(callMethod.NumberRegion(mContext.getString(R.string.textvalue_factortimetag) + Factor_header.get(0).getTimeStart() + "_" + Factor_header.get(0).getAppBasketInfoDate()));
        } else if (Factor_header.get(0).getInfoState().equals("4")) {
            factordate_tv.setText(callMethod.NumberRegion(mContext.getString(R.string.textvalue_factortimetag) + Factor_header.get(0).getReserveStart() + "_" + Factor_header.get(0).getAppBasketInfoDate()));
        }

        factordate_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        factordate_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")) + 5);
        factordate_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        factordate_tv.setGravity(Gravity.RIGHT);
        factordate_tv.setPadding(0, 0, 0, 35);
        factordate_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


        TextView explain_tv = new TextView(mContext);
        explain_tv.setText(callMethod.NumberRegion(Factor_header.get(0).getInfoExplain()));
        explain_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        explain_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")) + 8);
        explain_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        explain_tv.setGravity(Gravity.RIGHT);
        explain_tv.setPadding(0, 0, 0, 35);
        explain_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        if (Integer.parseInt(Factor_header.get(0).getInfoPrintCount()) > 0) {
            title_layout.addView(tv_printCount);
        }
        title_layout.addView(company_tv);
        title_layout.addView(factordate_tv);
        title_layout.addView(factorcode_tv);
        title_layout.addView(customername_tv);


        if (Factor_header.get(0).getInfoExplain().length() > 0) {
            title_layout.addView(explain_tv);
        }

        title_layout.addView(ViewPager);


        int CounterGood = 0;
        for (Factor FactorRow_detail : Factor_row) {

            CounterGood++;
            LinearLayoutCompat first_layout = new LinearLayoutCompat(mContext);
            first_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            first_layout.setOrientation(LinearLayoutCompat.VERTICAL);

            LinearLayoutCompat name_detail = new LinearLayoutCompat(mContext);
            name_detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            name_detail.setOrientation(LinearLayoutCompat.HORIZONTAL);
            name_detail.setWeightSum(6);
            name_detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


            TextView good_amount_tv = new TextView(mContext);
            good_amount_tv.setText(callMethod.NumberRegion(FactorRow_detail.getFacAmount()));
            good_amount_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 5));
            good_amount_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")) + 4);
            good_amount_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
            good_amount_tv.setGravity(Gravity.CENTER);
            good_amount_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


            androidx.viewpager.widget.ViewPager ViewPager_goodname = new ViewPager(mContext);
            ViewPager_goodname.setLayoutParams(new LinearLayoutCompat.LayoutParams(1, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
            ViewPager_goodname.setBackgroundResource(R.color.colorPrimaryDark);

            TextView good_name_tv = new TextView(mContext);
            String goodname = "";
            if (FactorRow_detail.getIsExtra().equals("1")) {
                goodname = FactorRow_detail.getGoodName() + mContext.getString(R.string.textvalue_orderagaintag);
            } else {
                goodname = FactorRow_detail.getGoodName();
            }

            good_name_tv.setText(callMethod.NumberRegion(goodname));
            good_name_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1));
            good_name_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")) + 4);
            good_name_tv.setGravity(Gravity.RIGHT);
            good_name_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
            good_name_tv.setPadding(0, 10, 5, 0);
            good_name_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            LinearLayoutCompat detail = new LinearLayoutCompat(mContext);
            detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            detail.setOrientation(LinearLayoutCompat.HORIZONTAL);
            detail.setWeightSum(9);
            detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

            TextView good_RowExplain_tv = new TextView(mContext);
            good_RowExplain_tv.setText(callMethod.NumberRegion(FactorRow_detail.getRowExplain()));
            good_RowExplain_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            good_RowExplain_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
            good_RowExplain_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
            good_RowExplain_tv.setPadding(0, 0, 0, 10);
            good_RowExplain_tv.setGravity(Gravity.CENTER);
            good_RowExplain_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            androidx.viewpager.widget.ViewPager ViewPager_sell2 = new ViewPager(mContext);
            ViewPager_sell2.setLayoutParams(new LinearLayoutCompat.LayoutParams(1, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
            ViewPager_sell2.setBackgroundResource(R.color.colorPrimaryDark);

            androidx.viewpager.widget.ViewPager extra_ViewPager = new ViewPager(mContext);
            extra_ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 2));
            extra_ViewPager.setBackgroundResource(R.color.colorPrimaryDark);

            androidx.viewpager.widget.ViewPager extra_ViewPager1 = new ViewPager(mContext);
            extra_ViewPager1.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 4));
            extra_ViewPager1.setBackgroundResource(R.color.colorPrimaryDark);


            name_detail.addView(good_name_tv);
            name_detail.addView(ViewPager_goodname);
            name_detail.addView(good_amount_tv);


            detail.addView(good_RowExplain_tv);


            first_layout.addView(name_detail);
            first_layout.addView(extra_ViewPager);
            first_layout.addView(detail);
            first_layout.addView(extra_ViewPager1);

            boby_good_layout.addView(first_layout);


        }

        good_layout.addView(boby_good_layout);

        total_layout.addView(ViewPager_rast);
        total_layout.addView(good_layout);
        total_layout.addView(ViewPager_chap);


        main_layout.addView(title_layout);
        main_layout.addView(total_layout);
        bitmap_factor = loadBitmapFromView(main_layout);


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap_factor.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();


        bitmap_factor_base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        Call<RetrofitResponse> call = apiInterface.OrderSendImage("OrderSendImage",
                bitmap_factor_base64,
                callMethod.ReadString("AppBasketInfoCode"),
                AppPrinters.get(printerconter).getPrinterName(),
                AppPrinters.get(printerconter).getPrintCount()

        );

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                assert response.body() != null;
                if (response.body().getText().equals("Done")) {
                    printerconter++;
                    GetRow_Data();
                }
            }

            @Override
            public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                printerconter++;
                GetRow_Data();
            }
        });


    }

    public Bitmap loadBitmapFromView(View v) {
        v.measure(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        Bitmap b = Bitmap.createBitmap(width, v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);
        v.layout(0, 0, width, v.getMeasuredHeight());
        v.draw(c);
        return b;
    }


}
