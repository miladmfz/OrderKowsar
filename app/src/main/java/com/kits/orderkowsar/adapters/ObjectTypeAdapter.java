package com.kits.orderkowsar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.activity.TableActivity;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.model.ObjectType;
import com.kits.orderkowsar.viewholder.ObjectTypeViewHolder;

import java.util.ArrayList;

public class ObjectTypeAdapter extends RecyclerView.Adapter<ObjectTypeViewHolder> {

    Context mContext;
    CallMethod callMethod;
    ArrayList<ObjectType> objectTypes;


    public ObjectTypeAdapter(ArrayList<ObjectType> objectTypes, Context mContext) {
        this.mContext = mContext;
        this.objectTypes = objectTypes;
        this.callMethod = new CallMethod(mContext);

    }

    @NonNull
    @Override
    public ObjectTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.objecttype_item_cardview, parent, false);
        if (callMethod.ReadString("LANG").equals("fa")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else if (callMethod.ReadString("LANG").equals("ar")) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        return new ObjectTypeViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ObjectTypeViewHolder holder, int position) {


        if(objectTypes.get(position).getaType().equals("")){
            holder.tv_name.setText(R.string.textvalue_tagall);
        }else {
            holder.tv_name.setText(callMethod.NumberRegion(objectTypes.get(position).getaType()));
        }
        holder.rltv.setOnClickListener(v -> {
            TableActivity activity = (TableActivity) mContext;
            callMethod.EditString("ObjectType", objectTypes.get(position).getaType());

            activity.State = "0";
            activity.CallSpinner();
        });

    }

    @Override
    public int getItemCount() {
        return objectTypes.size();
    }


}
