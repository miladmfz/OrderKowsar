package com.kits.orderkowsar.application;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.activity.BasketActivity;
import com.kits.orderkowsar.activity.NavActivity;
import com.kits.orderkowsar.activity.SearchActivity;
import com.kits.orderkowsar.activity.TableActivity;
import com.kits.orderkowsar.adapters.GoodAdapter;
import com.kits.orderkowsar.adapters.GoodBoxItemAdapter;
import com.kits.orderkowsar.adapters.ReserveAdapter;
import com.kits.orderkowsar.adapters.RstMizAdapter;
import com.kits.orderkowsar.model.AppPrinter;
import com.kits.orderkowsar.model.BasketInfo;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.DistinctValue;
import com.kits.orderkowsar.model.Factor;
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

import java.io.ByteArrayOutputStream;
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
    Dialog dialog, dialogProg;
    Dialog dialogprint;
    Calendar cldr;
    TimePickerDialog picker;
    TextView tv_reservestart;
    TextView tv_reserveend;
    TextView tv_date;
    int ehour = 0;
    int eminutes = 0;
    int printerconter = 0;
    ArrayList<DistinctValue> values = new ArrayList<>();
    ArrayList<String> values_array = new ArrayList<>();
    ArrayList<Good> Goods;
    ArrayList<Good> good_box_items = new ArrayList<>();
    ArrayList<Factor> Factor_header = new ArrayList<>();
    ArrayList<Factor> Factor_row = new ArrayList<>();
    ArrayList<AppPrinter> AppPrinters = new ArrayList<>();
    int width = 500;
    LinearLayoutCompat main_layout;
    Bitmap bitmap_factor;
    String bitmap_factor_base64 = "";
    TextView tv_rep;

    public Action(Context mContext) {
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
        dialogProg.show();
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
            dialogProg();
            tv_rep.setText("در حال ارسال اطلاعات");
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
                        dialogProg.dismiss();
                    } else {
                        dialog.dismiss();
                        dialogProg.dismiss();
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


    public void GoodBoxDialog(Good good, String Flag) {

        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.order_box_good);

        TextView ed_orderbox_goodname = dialog.findViewById(R.id.orderboxgood_goodname);
        EditText ed_orderbox_amount = dialog.findViewById(R.id.orderboxgood_amount);
        EditText ed_orderbox_explain = dialog.findViewById(R.id.orderboxgood_explain);
        Spinner spinner_orderbox = dialog.findViewById(R.id.orderboxgood_spinnerexplain);
        RecyclerView rc_orderbox = dialog.findViewById(R.id.orderboxgood_rc);
        Button btn_orderbox = dialog.findViewById(R.id.orderboxgood_btn);

        if (Flag.equals("1")) {
            ed_orderbox_amount.setText(NumberFunctions.PerisanNumber(good.getAmount()));
            ed_orderbox_explain.setText(NumberFunctions.PerisanNumber(good.getExplain()));
            btn_orderbox.setText("اصلاح سفارش");
        } else {
            good.setRowCode("0");
            btn_orderbox.setText("اضافه به سفارش");
        }

        ed_orderbox_amount.selectAll();
        ed_orderbox_amount.requestFocus();
        ed_orderbox_amount.postDelayed(() -> {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(ed_orderbox_amount, InputMethodManager.SHOW_IMPLICIT);
        }, 500);
        ed_orderbox_amount.setOnClickListener(v -> ed_orderbox_amount.selectAll());

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
                    dialogProg();
                    tv_rep.setText("در حال ارسال اطلاعات");
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
                                dialogProg.dismiss();
                            } else {
                                if (Flag.equals("0")) {
                                    callMethod.showToast("ثبت گردید");
                                    dialog.dismiss();
                                    dialogProg.dismiss();
                                    SearchActivity activity = (SearchActivity) mContext;
                                    activity.RefreshState();
                                } else {
                                    intent = new Intent(mContext, BasketActivity.class);
                                    ((Activity) mContext).finish();
                                    ((Activity) mContext).overridePendingTransition(0, 0);
                                    mContext.startActivity(intent);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                            if (Flag.equals("0")) {
                                callMethod.showToast("ثبت نگردید");
                                dialog.dismiss();
                                dialogProg.dismiss();
                                SearchActivity activity = (SearchActivity) mContext;
                                activity.RefreshState();
                            } else {
                                intent = new Intent(mContext, BasketActivity.class);
                                ((Activity) mContext).finish();
                                ((Activity) mContext).overridePendingTransition(0, 0);
                                mContext.startActivity(intent);
                            }
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


    public void OrderToFactor() {
        dialogProg();
        tv_rep.setText("در حال ارسال اطلاعات");
        Call<RetrofitResponse> call = apiInterface.OrderToFactor(
                "OrderToFactor",
                callMethod.ReadString("AppBasketInfoCode")
        );

        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                        callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                        dialogProg.dismiss();
                    } else {
                        GetFactorPrint();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                intent = new Intent(mContext, BasketActivity.class);
                ((Activity) mContext).finish();
                ((Activity) mContext).overridePendingTransition(0, 0);
                mContext.startActivity(intent);
            }
        });

    }


    public void OrderGetAppPrinterList() {
        call = apiInterface.OrderGetAppPrinter("OrderGetAppPrinter");
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    printerconter = 0;
                    AppPrinters = response.body().getAppPrinters();
                    OrderPrint();
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                OrderGetAppPrinterList();

            }
        });
    }

    public void OrderPrint() {

        if (printerconter < (AppPrinters.size())) {

            call = apiInterface.OrderGetFactorRow(
                    "OrderGetFactorRow",
                    callMethod.ReadString("AppBasketInfoCode"),
                    AppPrinters.get(printerconter).getGoodGroups(),
                    AppPrinters.get(printerconter).getWhereClause()
            );

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
                            OrderPrint();
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                    printerconter++;
                    OrderPrint();
                }
            });

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
                            callMethod.showToast("ثبت گردید");
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
                    OrderPrint();
                }
            });

        }


    }

    public void GetFactorPrint() {
        dialogProg();
        tv_rep.setText("در حال چاپ سفارش");
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
                    OrderGetAppPrinterList();
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                GetFactorPrint();
            }
        });

    }


    public void EditBasketInfoExplain(BasketInfo basketInfo) {


        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.basketinfo_explain);
        Button explain_btn = dialog.findViewById(R.id.basketinfo_explain_btn);
        explain_btn.setText("ثبت توضیحات");
        final EditText explain_tv = dialog.findViewById(R.id.basketinfo_explain_tv);

        explain_tv.setText(NumberFunctions.PerisanNumber(basketInfo.getInfoExplain()));
        dialog.show();
        explain_tv.requestFocus();
        explain_tv.postDelayed(() -> {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(explain_tv, InputMethodManager.SHOW_IMPLICIT);
        }, 500);

        explain_btn.setOnClickListener(view -> {

            dialogProg();
            tv_rep.setText("در حال ارسال اطلاعات");
            call = apiInterface.OrderInfoInsert(
                    "OrderInfoInsert",
                    dbh.ReadConfig("BrokerCode"),
                    basketInfo.getRstmizCode(),
                    basketInfo.getPersonName(),
                    basketInfo.getMobileNo(),
                    NumberFunctions.EnglishNumber(explain_tv.getText().toString()),
                    basketInfo.getPrepayed(),
                    basketInfo.getReserveStart(),
                    basketInfo.getReserveEnd(),
                    basketInfo.getToday(),
                    basketInfo.getInfoState(),
                    basketInfo.getAppBasketInfoCode()
            );

            if (!basketInfo.getInfoExplain().equals(NumberFunctions.EnglishNumber(explain_tv.getText().toString()))) {
                call.enqueue(new Callback<RetrofitResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            if (Integer.parseInt(response.body().getBasketInfos().get(0).getErrCode()) > 0) {
                                callMethod.showToast(response.body().getBasketInfos().get(0).getErrDesc());
                                dialogProg.dismiss();
                            } else {
                                dialog.dismiss();
                                dialogProg.dismiss();
                                callMethod.showToast("ثبت گردید");
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                        dialog.dismiss();
                        dialogProg.dismiss();
                    }
                });
            } else {
                dialog.dismiss();
            }

        });


    }


    @SuppressLint("RtlHardcoded")
    public void printDialogView() {

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        //height = displayMetrics.heightPixels;
        //width = displayMetrics.widthPixels;

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


        TextView company_tv = new TextView(mContext);
        company_tv.setText(NumberFunctions.PerisanNumber(AppPrinters.get(printerconter).getPrinterExplain()));
        company_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        company_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")) + 8);
        company_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        company_tv.setGravity(Gravity.CENTER);
        company_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        company_tv.setPadding(0, 0, 0, 15);


        boby_good_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        good_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        total_layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));


        good_layout.setOrientation(LinearLayoutCompat.HORIZONTAL);
        boby_good_layout.setOrientation(LinearLayoutCompat.VERTICAL);
        total_layout.setOrientation(LinearLayoutCompat.VERTICAL);

        good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        boby_good_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        total_layout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


        ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, 3));
        ViewPager.setBackgroundResource(R.color.colorPrimaryDark);
        ViewPager_rast.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        ViewPager_rast.setBackgroundResource(R.color.red_800);
        ViewPager_chap.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        ViewPager_chap.setBackgroundResource(R.color.green_800);


        TextView customername_tv = new TextView(mContext);
        customername_tv.setText(NumberFunctions.PerisanNumber(" میز :   " + Factor_header.get(0).getRstMizName()));
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
        factorcode_tv.setText(NumberFunctions.PerisanNumber(" کد فاکتور :   " + Factor_header.get(0).getDailyCode() + "             " + Time));
        factorcode_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        factorcode_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")) + 5);
        factorcode_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        factorcode_tv.setGravity(Gravity.RIGHT);
        factorcode_tv.setPadding(0, 0, 0, 15);
        factorcode_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


        TextView factordate_tv = new TextView(mContext);
        factordate_tv.setText(NumberFunctions.PerisanNumber(" زمان فاکتور :   " + Factor_header.get(0).getTimeStart() + "_" + Factor_header.get(0).getFactorDate()));
        factordate_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        factordate_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")) + 5);
        factordate_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        factordate_tv.setGravity(Gravity.RIGHT);
        factordate_tv.setPadding(0, 0, 0, 35);
        factordate_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


        TextView explain_tv = new TextView(mContext);
        explain_tv.setText(NumberFunctions.PerisanNumber(Factor_header.get(0).getFactorExplain()));
        explain_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        explain_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
        explain_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
        explain_tv.setGravity(Gravity.RIGHT);
        explain_tv.setPadding(0, 0, 0, 35);
        explain_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        title_layout.addView(company_tv);
        title_layout.addView(factordate_tv);
        title_layout.addView(factorcode_tv);
        title_layout.addView(customername_tv);


        if (Factor_header.get(0).getFactorExplain().length() > 0) {
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

            TextView radif = new TextView(mContext);
            radif.setText(NumberFunctions.PerisanNumber(String.valueOf(CounterGood)));
            radif.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 5));
            radif.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
            radif.setGravity(Gravity.CENTER);
            radif.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
            radif.setBackgroundColor(mContext.getColor(R.color.grey_500));
            radif.setPadding(0, 10, 0, Integer.parseInt(callMethod.ReadString("TitleSize")));
            radif.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            androidx.viewpager.widget.ViewPager ViewPager_goodname = new ViewPager(mContext);
            ViewPager_goodname.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
            ViewPager_goodname.setBackgroundResource(R.color.colorPrimaryDark);

            TextView good_name_tv = new TextView(mContext);
            String goodname = "";
            if (FactorRow_detail.getIsExtra().equals("1")) {
                goodname = FactorRow_detail.getGoodName() + "  (سفارش مجدد)  ";
            } else {
                goodname = FactorRow_detail.getGoodName();
            }
            good_name_tv.setText(NumberFunctions.PerisanNumber(goodname));
            good_name_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 1));
            good_name_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
            good_name_tv.setGravity(Gravity.RIGHT);
            good_name_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
            good_name_tv.setPadding(0, 10, 5, 0);
            good_name_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


            LinearLayoutCompat detail = new LinearLayoutCompat(mContext);
            detail.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            detail.setOrientation(LinearLayoutCompat.HORIZONTAL);
            detail.setWeightSum(9);
            detail.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


            TextView good_amount_tv = new TextView(mContext);
            good_amount_tv.setText(NumberFunctions.PerisanNumber(FactorRow_detail.getFacAmount()));
            good_amount_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 6));
            good_amount_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
            good_amount_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
            good_amount_tv.setGravity(Gravity.CENTER);
            good_amount_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            TextView good_RowExplain_tv = new TextView(mContext);
            good_RowExplain_tv.setText(NumberFunctions.PerisanNumber(FactorRow_detail.getRowExplain()));
            good_RowExplain_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 3));
            good_RowExplain_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(callMethod.ReadString("TitleSize")));
            good_RowExplain_tv.setTextColor(mContext.getColor(R.color.colorPrimaryDark));
            good_RowExplain_tv.setPadding(0, 0, 0, 10);
            good_RowExplain_tv.setGravity(Gravity.CENTER);
            good_RowExplain_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


            androidx.viewpager.widget.ViewPager ViewPager_sell2 = new ViewPager(mContext);
            ViewPager_sell2.setLayoutParams(new LinearLayoutCompat.LayoutParams(2, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
            ViewPager_sell2.setBackgroundResource(R.color.colorPrimaryDark);


            androidx.viewpager.widget.ViewPager extra_ViewPager = new ViewPager(mContext);
            extra_ViewPager.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 2));
            extra_ViewPager.setBackgroundResource(R.color.colorPrimaryDark);

            androidx.viewpager.widget.ViewPager extra_ViewPager1 = new ViewPager(mContext);
            extra_ViewPager1.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 2));
            extra_ViewPager1.setBackgroundResource(R.color.colorPrimaryDark);


            name_detail.addView(radif);
            name_detail.addView(ViewPager_goodname);
            name_detail.addView(good_name_tv);

            detail.addView(good_RowExplain_tv);
            detail.addView(ViewPager_sell2);
            detail.addView(good_amount_tv);


            first_layout.addView(name_detail);
            first_layout.addView(extra_ViewPager);
            first_layout.addView(detail);
            first_layout.addView(extra_ViewPager1);

            boby_good_layout.addView(first_layout);


        }
        good_layout.addView(ViewPager_rast);
        good_layout.addView(boby_good_layout);
        good_layout.addView(ViewPager_chap);


        main_layout.addView(title_layout);
        main_layout.addView(good_layout);
        main_layout.addView(total_layout);
        bitmap_factor = loadBitmapFromView(main_layout);


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap_factor.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();


        bitmap_factor_base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        //SendFactorPrint(appbasketinfocode,bitmap_factor_base64);
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
                    OrderPrint();
                }
            }

            @Override
            public void onFailure(Call<RetrofitResponse> call, Throwable t) {
                printerconter++;
                OrderPrint();
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

    }
}
