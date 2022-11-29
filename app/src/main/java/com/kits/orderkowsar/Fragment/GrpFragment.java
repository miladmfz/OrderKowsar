package com.kits.orderkowsar.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.adapters.GoodAdapter;
import com.kits.orderkowsar.adapters.GrpAdapter;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.model.GoodGroup;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GrpFragment extends Fragment {

    CallMethod callMethod;
    APIInterface apiInterface;
    View view;
    ArrayList<GoodGroup> goodGroups=new ArrayList<>();
    RecyclerView rc_grp;
    RecyclerView rc_good;
    String groupCode;
    FragmentManager fragmentManager ;
    FragmentTransaction fragmentTransaction;
    Call<RetrofitResponse> call;
    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public ArrayList<GoodGroup> getGoodGroups() {
        return goodGroups;
    }

    public void setGoodGroups(ArrayList<GoodGroup> goodGroups) {
        this.goodGroups = goodGroups;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.fragment_grp, container, false);

        rc_grp=view.findViewById(R.id.fragment_grp_recy);
        rc_good=view.findViewById(R.id.fragment_good_recy);



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        callMethod = new CallMethod(requireActivity());
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

        fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();


        allgrp();


    }






    void allgrp() {
        Call<RetrofitResponse> call = apiInterface.Getgrp(
                "GoodGroupInfo",
                groupCode
        );
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    allgood();
                    GrpAdapter adapter = new GrpAdapter(response.body().getGroups(),fragmentTransaction, requireActivity());
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

        call = apiInterface.GetGoodFromGroup(
                "GetOrderGoodList",
                groupCode
        );
        call.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    GoodAdapter adapter = new GoodAdapter(response.body().getGoods(), requireActivity());
                    rc_good.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
                    rc_good.setAdapter(adapter);
                    rc_good.setItemAnimator(new DefaultItemAnimator());
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call.isExecuted()){
            call.cancel();
        }

    }
}