package com.kits.orderkowsar.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.activity.BasketActivity;
import com.kits.orderkowsar.adapters.GoodAdapter;
import com.kits.orderkowsar.adapters.GrpAdapter;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.Good;
import com.kits.orderkowsar.model.NumberFunctions;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchViewFragment extends Fragment {

    CallMethod callMethod;
    APIInterface apiInterface;
    View view;
    RecyclerView rc_grp;
    RecyclerView rc_good;
    EditText ed_search;
    Handler handler = new Handler();
    String searchtarget = "", Where = "";
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Call<RetrofitResponse> call;
    GoodAdapter adapter;
    ArrayList<Good> Goods = new ArrayList<>();
    LottieAnimationView progressBar;
    LottieAnimationView img_lottiestatus;
    TextView tv_lottiestatus;
    Button Btn_GoodToOrder;
    DatabaseHelper dbh;
    String Parent_GourpCode;
    String good_GourpCode;

    public void setParent_GourpCode(String parent_GourpCode) {
        Parent_GourpCode = parent_GourpCode;
    }

    public void setGood_GourpCode(String good_GourpCode) {
        this.good_GourpCode = good_GourpCode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_goodview, container, false);

        rc_grp = view.findViewById(R.id.fragment_grp_recy);
        rc_good = view.findViewById(R.id.fragment_good_recy);
        ed_search = view.findViewById(R.id.fragment_good_search);
        Btn_GoodToOrder = view.findViewById(R.id.fragment_good_to_order);

        progressBar = view.findViewById(R.id.fragment_good_prog);
        img_lottiestatus = view.findViewById(R.id.fragment_good_lottie);
        tv_lottiestatus = view.findViewById(R.id.fragment_good_tvstatus);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        callMethod = new CallMethod(requireActivity());
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);
        dbh = new DatabaseHelper(requireActivity(), callMethod.ReadString("DatabaseName"));

        fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();


        ed_search.setText(searchtarget);
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(() -> {
                    searchtarget = NumberFunctions.EnglishNumber(ed_search.getText().toString());
                    Where = "GoodName Like N''%" + searchtarget.replaceAll(" ", "%") + "%'' ";
                    good_GourpCode=dbh.ReadConfig("GroupCodeDefult");
                    allgood();
                }, Integer.parseInt(callMethod.ReadString("Delay")));
            }
        });


        Btn_GoodToOrder.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), BasketActivity.class);
            startActivity(intent);
        });
        allgrp();
        allgood();


    }


    void allgrp() {
        Call<RetrofitResponse> call = apiInterface.Getgrp("GoodGroupInfo", Parent_GourpCode);
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    GrpAdapter adapter = new GrpAdapter(response.body().getGroups(), Parent_GourpCode, fragmentTransaction, requireActivity());
                    rc_grp.setLayoutManager(new LinearLayoutManager(requireActivity()));
                    rc_grp.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                rc_grp.setVisibility(View.GONE);
            }
        });
    }


    void allgood() {
        Goods.clear();
        progressBar.setVisibility(View.VISIBLE);
        img_lottiestatus.setVisibility(View.GONE);
        tv_lottiestatus.setVisibility(View.GONE);


        call = apiInterface.GetGoodFromGroup("GetOrderGoodList",
                Where,
                good_GourpCode,
                callMethod.ReadString("AppBasketInfoCode"));
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    Goods = response.body().getGoods();

                    callrecycler();

                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                Goods.clear();
                callrecycler();

            }
        });
    }


    private void callrecycler() {

        progressBar.setVisibility(View.GONE);

        adapter = new GoodAdapter(Goods, requireActivity());
        if (adapter.getItemCount() == 0) {
            tv_lottiestatus.setText(R.string.textvalue_notfound);
            img_lottiestatus.setVisibility(View.VISIBLE);
            tv_lottiestatus.setVisibility(View.VISIBLE);
        } else {
            img_lottiestatus.setVisibility(View.GONE);
            tv_lottiestatus.setVisibility(View.GONE);
        }
        rc_good.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
        rc_good.setAdapter(adapter);
        rc_good.setItemAnimator(new DefaultItemAnimator());

    }






    // In your fragment's onCreateView or where you initiate the Retrofit request


    // In your fragment's onDestroyView or onDestroy
    public void onDestroyView() {
        super.onDestroyView();

    }









}