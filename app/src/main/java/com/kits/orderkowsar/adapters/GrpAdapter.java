package com.kits.orderkowsar.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kits.orderkowsar.Fragment.GrpFragment;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.model.GoodGroup;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GrpAdapter extends RecyclerView.Adapter<GrpAdapter.GoodGroupViewHolder> {

    ArrayList<GoodGroup> GoodGroups;
    Context mContext;
    FragmentTransaction fragmentTransaction;
    CallMethod callMethod;

    APIInterface apiInterface;
    Call<RetrofitResponse> call2;



    public GrpAdapter(ArrayList<GoodGroup> GoodGroups,FragmentTransaction fragmentTransaction, Context mContext) {
        this.GoodGroups = GoodGroups;
        this.mContext = mContext;
        this.fragmentTransaction = fragmentTransaction;
        this.callMethod = new CallMethod(mContext);
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

    }

    @NonNull
    @Override
    public GoodGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grp_list, parent, false);
        return new GoodGroupViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull GoodGroupViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.grpname.setText(GoodGroups.get(position).getGoodGroupFieldValue("Name"));

        holder.grpname.setOnClickListener(v -> {

            //todo
        });


        if (!GoodGroups.get(position).getGoodGroupFieldValue("GoodGroupImageName").equals("")) {

            Glide.with(holder.img)
                    .asBitmap()
                    .load(Base64.decode(GoodGroups.get(position).getGoodGroupFieldValue("GoodGroupImageName"), Base64.DEFAULT))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(holder.img);


        } else
        {

            call2 = apiInterface.GetImage(
                    "getImage",
                    GoodGroups.get(position).getGoodGroupFieldValue("GroupCode"),
                    "TGoodsGrp",
                    "0",
                    "200"
            );
            call2.enqueue(new Callback<RetrofitResponse>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {

                        assert response.body() != null;
                        if (!response.body().getText().equals("no_photo")) {
                            GoodGroups.get(position).setGoodGroupImageName(response.body().getText());
                        }
                        notifyItemChanged(position);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                    Log.e("test=",position+"=onFailure");

                }
            });
        }

        holder.rltv.setOnClickListener(v -> {
            if(Integer.parseInt(GoodGroups.get(position).getGoodGroupFieldValue("ChildNo"))>0) {
                GrpFragment grpFragment = new GrpFragment();
                grpFragment.setGroupCode(GoodGroups.get(position).getGoodGroupFieldValue("GroupCode"));
                fragmentTransaction.replace(R.id.searchactivity_framelayout, grpFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });



    }

    @Override
    public int getItemCount() {
        return GoodGroups.size();
    }

    class GoodGroupViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView grpname;
        LinearLayout rltv;

        GoodGroupViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.grp_list_image);
            grpname = itemView.findViewById(R.id.grp_list_name);
            rltv = itemView.findViewById(R.id.grp_list);
        }
    }
}
