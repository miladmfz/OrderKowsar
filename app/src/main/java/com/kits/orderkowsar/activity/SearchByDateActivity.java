package com.kits.orderkowsar.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.adapters.GoodAdapter;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.databinding.ActivitySearchDateDetailBinding;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.Good;
import com.kits.orderkowsar.model.NumberFunctions;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;


public class SearchByDateActivity extends AppCompatActivity {

    CallMethod callMethod;
    private ArrayList<Good> Moregoods = new ArrayList<>();

    private final Integer conter = 0;
    private Integer grid;
    private String date;
    public String PageMoreData = "0";
    private boolean loading = true;
    private String lastDate;
    ArrayList<String[]> Multi_buy = new ArrayList<>();
    private ArrayList<Good> goods = new ArrayList<>();
    DatabaseHelper dbh;
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    GoodAdapter adapter;
    GridLayoutManager gridLayoutManager;


    int pastVisiblesItems = 0, visibleItemCount, totalItemCount;
    Menu item_multi;

    String year;
    String mount;
    String day;

    PersianCalendar calendar1;
    ArrayList<Good> Multi_Good = new ArrayList<>();

    public static String scan = "";
    public String title = "";
    Intent intent;

//    Toolbar toolbar;
//    TextView tv_customer;
//    TextView tv_sumfac;
//    SwitchMaterial sm_goodamount;
//    Button btn_search;
//    EditText ed_search;
//    RecyclerView recyclerView;
//    FloatingActionButton fab;
//    LinearLayoutCompat llsumfactor;
//
//    LottieAnimationView lottieAnimationView;
//    TextView tvstatus;


    boolean defultenablesellprice;
ActivitySearchDateDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchDateDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Dialog dialog1;
        dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.rep_prog);
        TextView repw = dialog1.findViewById(R.id.rep_prog_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();

        intent();
        Config();
        try {
            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);
            handler.postDelayed(dialog1::dismiss, 1000);
        } catch (Exception e) {
            callMethod.ErrorLog(e.getMessage());
        }


    }

    //***************************************************


    public void Config() {
        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        calendar1 = new PersianCalendar();

        setSupportActionBar(binding.searchbydateactivityToolbar);

    }


    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        date = data.getString("date");
    }

    @SuppressLint("SetTextI18n")
    public void init() {


        calendar1.setPersianDate(
                calendar1.getPersianYear(),
                calendar1.getPersianMonth(),
                calendar1.getPersianDay() - Integer.parseInt(date)
        );

        year = "";
        mount = "0";
        day = "0";

        year = year + calendar1.getPersianYear();

        if (String.valueOf(calendar1.getPersianMonth()).equals("11")) {
            mount = "12";
        } else if (String.valueOf(calendar1.getPersianMonth()).equals("00")) {
            mount = "01";
        } else {
            mount = mount + (calendar1.getPersianMonth() + 1);
        }
        day = day + (calendar1.getPersianDay());
        lastDate = year + "/" + mount.substring(mount.length() - 2) + "/" + day.substring(day.length() - 2);


        grid = Integer.parseInt(callMethod.ReadString("Grid"));
        binding.searchbydateactivityDate.setText(date);
        GetDataFromDataBase();


        binding.searchbydateactivityBtn.setOnClickListener(view -> {
            goods.clear();

            calendar1 = new PersianCalendar();
            if (!binding.searchbydateactivityDate.getText().toString().equals("")) {
                date = binding.searchbydateactivityDate.getText().toString();
            } else {
                date = "7";
            }

            calendar1.setPersianDate(
                    calendar1.getPersianYear(),
                    calendar1.getPersianMonth(),
                    calendar1.getPersianDay() - Integer.parseInt(date)
            );

            year = "";
            mount = "0";
            day = "0";

            year = year + calendar1.getPersianYear();

            if (String.valueOf(calendar1.getPersianMonth()).equals("11")) {
                mount = "12";
            } else if (String.valueOf(calendar1.getPersianMonth()).equals("00")) {
                mount = "01";
            } else {
                mount = mount + (calendar1.getPersianMonth() + 1);
            }
            day = day + (calendar1.getPersianDay());
            lastDate = year + "/" + mount.substring(mount.length() - 2) + "/" + day.substring(day.length() - 2);
            GetDataFromDataBase();

        });


        if (callMethod.ReadBoolan("GoodAmount")) {
            binding.searchbydateactivitySwitchAmount.setChecked(true);
            binding.searchbydateactivitySwitchAmount.setText("موجود");
        } else {
            binding.searchbydateactivitySwitchAmount.setChecked(false);
            binding.searchbydateactivitySwitchAmount.setText("هردو");
        }

        binding.searchbydateactivitySwitchAmount.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                binding.searchbydateactivitySwitchAmount.setText("موجود");
                callMethod.EditBoolan("GoodAmount", true);
            } else {
                binding.searchbydateactivitySwitchAmount.setText("هردو");
                callMethod.EditBoolan("GoodAmount", false);
            }
            if (conter == 0) {
                goods.clear();
                PageMoreData = "0";
                GetDataFromDataBase();
            }
        });

        binding.searchbydateactivityFab.setOnClickListener(v ->  {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.box_multi_buy);
            Button boxbuy = dialog.findViewById(R.id.box_multi_buy_btn);
            final EditText amount_mlti = dialog.findViewById(R.id.box_multi_buy_amount);
            final EditText unitratio_mlti = dialog.findViewById(R.id.box_multi_unitratio);
            final TextView tv = dialog.findViewById(R.id.box_multi_buy_factor);
            String tempvalue = "";
            defultenablesellprice = false;

            for (Good good : Multi_Good) {
                Log.e("test_",Multi_Good.size()+"");
                Log.e("test_",good.getGoodFieldValue("GoodCode")+"");

                Good goodtempdata = dbh.getGooddata(good.getGoodFieldValue("GoodCode"));



                if (Multi_Good.get(0).equals(good)) {
                    if(goodtempdata.getGoodFieldValue("SellPrice" + dbh.getPricetipCustomer(callMethod.ReadString("PreFactorCode"))).equals("")){
                        tempvalue ="100.0";
                    }else{
                        tempvalue = goodtempdata.getGoodFieldValue("Sellprice" + dbh.getPricetipCustomer(callMethod.ReadString("PreFactorCode")));
                    }
                }

                if (!tempvalue.equals(goodtempdata.getGoodFieldValue("Sellprice" + dbh.getPricetipCustomer(callMethod.ReadString("PreFactorCode"))))) {
                    defultenablesellprice = true;
                }

            }

            if (defultenablesellprice) {
                unitratio_mlti.setHint(NumberFunctions.PerisanNumber("بر اساس نرخ فروش"));
            } else {
                unitratio_mlti.setText(NumberFunctions.PerisanNumber(String.valueOf(100 - Integer.parseInt(tempvalue.substring(0, tempvalue.length() - 2)))));
            }

            tv.setText(dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode")));
            dialog.show();
            amount_mlti.requestFocus();
            amount_mlti.postDelayed(() -> {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(amount_mlti, InputMethodManager.SHOW_IMPLICIT);
            }, 500);

            boxbuy.setOnClickListener(view -> {
                if(unitratio_mlti.getText().toString().equals("بر اساس نرخ فروش")){
                    unitratio_mlti.setText("100.0");
                }
                String AmountMulti = amount_mlti.getText().toString();
                if (!AmountMulti.equals("")) {

                    if (Integer.parseInt(AmountMulti) != 0) {

                        for (Good good : Multi_Good) {
                            Good gooddata = dbh.getGooddata(good.getGoodFieldValue("GoodCode"));
                            String temppercent;
                            if(gooddata.getGoodFieldValue("SellPrice" + dbh.getPricetipCustomer(callMethod.ReadString("PreFactorCode"))).equals("")){
                                temppercent ="100.0";
                            }else{
                                temppercent = gooddata.getGoodFieldValue("Sellprice" + dbh.getPricetipCustomer(callMethod.ReadString("PreFactorCode")));
                            }

                            if (unitratio_mlti.getText().toString().equals("")) {
                                temppercent = String.valueOf(100 - Integer.parseInt(temppercent.substring(0, temppercent.length() - 2)));
                            } else {
                                temppercent = NumberFunctions.EnglishNumber(unitratio_mlti.getText().toString());
                            }
                            if (Integer.parseInt(good.getGoodFieldValue("MaxSellPrice")) > 0) {
                                long Pricetemp = (long) Integer.parseInt(good.getGoodFieldValue("MaxSellPrice")) - ((long) Integer.parseInt(good.getGoodFieldValue("MaxSellPrice")) * Integer.parseInt(temppercent) / 100);
                                dbh.InsertPreFactorwithPercent(callMethod.ReadString("PreFactorCode"),
                                        good.getGoodFieldValue("GoodCode"),
                                        AmountMulti,
                                        String.valueOf(Pricetemp),
                                        "0");
                            } else {
                                dbh.InsertPreFactor(callMethod.ReadString("PreFactorCode"),
                                        good.getGoodFieldValue("GoodCode"),
                                        AmountMulti,
                                        "0",
                                        "0");
                            }
                        }
                        callMethod.showToast("به سبد خرید اضافه شد");

                        dialog.dismiss();
                        item_multi.findItem(R.id.menu_multi).setVisible(false);
                        for (Good good : goods) {
                            good.setCheck(false);
                        }
                        Multi_Good.clear();
                        adapter = new GoodAdapter(goods, this);
                        adapter.multi_select = false;
                        gridLayoutManager = new GridLayoutManager(this, grid);
                        gridLayoutManager.scrollToPosition(pastVisiblesItems + 2);
                        binding.searchbydateactivityRecycler.setLayoutManager(gridLayoutManager);
                        binding.searchbydateactivityRecycler.setAdapter(adapter);
                        binding.searchbydateactivityRecycler.setItemAnimator(new DefaultItemAnimator());
                        binding.searchbydateactivityFab.setVisibility(View.GONE);

                    } else {
                        callMethod.showToast("تعداد مورد نظر صحیح نمی باشد.");
                    }
                } else {
                    callMethod.showToast("تعداد مورد نظر صحیح نمی باشد.");
                }
            });


        });


        binding.searchbydateactivityRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount - 1) {
                            loading = false;
                            PageMoreData = String.valueOf(Integer.parseInt(PageMoreData) + 1);
                            GetMoreDataFromDataBase();
                        }
                    }
                }
            }
        });
        GetDataFromDataBase();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        item_multi = menu;
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.bag_shop) {
            if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                intent = new Intent(this, BasketActivity.class);
                intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
                intent.putExtra("showflag", "2");
                startActivity(intent);

            } else {
                callMethod.showToast("فاکتوری انتخاب نشده است");
            }
            return true;
        }
        if (item.getItemId() == R.id.menu_multi) {
            item_multi.findItem(R.id.menu_multi).setVisible(false);
            for (Good good : goods) {
                good.setCheck(false);
            }
            Multi_buy.clear();
            adapter = new GoodAdapter(goods, this);
            adapter.multi_select = false;

            gridLayoutManager = new GridLayoutManager(this, grid);
            gridLayoutManager.scrollToPosition(pastVisiblesItems + 2);
            binding.searchbydateactivityRecycler.setLayoutManager(gridLayoutManager);
            binding.searchbydateactivityRecycler.setAdapter(adapter);
            binding.searchbydateactivityRecycler.setItemAnimator(new DefaultItemAnimator());
            binding.searchbydateactivityFab.setVisibility(View.GONE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("NotifyDataSetChanged")
    public void CallRecyclerView() {
        adapter = new GoodAdapter(goods, this);
        if (adapter.getItemCount() == 0) {
            binding.searchbydateactivityTvstatus.setText("کالایی یافت نشد");
            binding.searchbydateactivityTvstatus.setVisibility(View.VISIBLE);
            binding.searchbydateactivityLottie.setVisibility(View.VISIBLE);
        } else {
            binding.searchbydateactivityLottie.setVisibility(View.GONE);
            binding.searchbydateactivityTvstatus.setVisibility(View.GONE);
        }
        gridLayoutManager = new GridLayoutManager(this, grid);
        binding.searchbydateactivityRecycler.setLayoutManager(gridLayoutManager);
        binding.searchbydateactivityRecycler.setAdapter(adapter);
        binding.searchbydateactivityRecycler.setItemAnimator(new DefaultItemAnimator());
    }

    public void GetDataFromDataBase() {
        Moregoods.clear();
        Moregoods = dbh.getAllGood_ByDate(lastDate, PageMoreData);
        if (goods.isEmpty()) {
            goods.addAll(Moregoods);
        }
        CallRecyclerView();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void GetMoreDataFromDataBase() {
        loading = true;
        Moregoods.clear();
        Moregoods = dbh.getAllGood_ByDate(lastDate, PageMoreData);

        if (Moregoods.size() > 0) {
            if (goods.isEmpty()) {
                goods.addAll(Moregoods);
            }
            if (goods.size() > (Integer.parseInt(callMethod.ReadString("Grid")) * 10)) {
                goods.addAll(Moregoods);
            }
            adapter.notifyDataSetChanged();
        } else {
            callMethod.showToast("کالایی بیشتری یافت نشد");
            PageMoreData = String.valueOf(Integer.parseInt(PageMoreData) - 1);
        }

    }


    public void good_select_function(Good good) {

        if (!Multi_Good.contains(good)) {
            Multi_Good.add(good);

            binding.searchbydateactivityFab.setVisibility(View.VISIBLE);
            item_multi.findItem(R.id.menu_multi).setVisible(true);
        } else {
            Multi_Good.remove(good);

            if (Multi_Good.size() < 1) {
                binding.searchbydateactivityFab.setVisibility(View.GONE);
                adapter.multi_select = false;
                item_multi.findItem(R.id.menu_multi).setVisible(false);
            }
        }
    }

    public void factorState() {
        if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) == 0) {
            binding.searchbydateactivityCustomer.setText("فاکتوری انتخاب نشده");
            binding.searchbydateactivityLlSumFactor.setVisibility(View.GONE);
        } else {
            binding.searchbydateactivityLlSumFactor.setVisibility(View.VISIBLE);
            binding.searchbydateactivityCustomer.setText(NumberFunctions.PerisanNumber(dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode"))));
            binding.searchbydateactivitySumFactor.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(dbh.getFactorSum(callMethod.ReadString("PreFactorCode"))))));
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        factorState();
        super.onWindowFocusChanged(hasFocus);
    }


}
