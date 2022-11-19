package com.kits.orderkowsar.application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.model.GroupLayerOne;
import com.kits.orderkowsar.model.GroupLayerTwo;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;


public class GroupLayerAdapter extends ExpandableRecyclerViewAdapter<GroupLayerOneViewHolder, GroupLayerTwoViewHolder> {

    Context mContext;

    public GroupLayerAdapter(List<? extends ExpandableGroup> groups, Context mContext) {
        super(groups);
        this.mContext=mContext;
    }

    @Override
    public GroupLayerOneViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new GroupLayerOneViewHolder(v);
    }

    @Override
    public GroupLayerTwoViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item2,parent,false);
        return new GroupLayerTwoViewHolder(v);
    }

    @Override
    public void onBindChildViewHolder(GroupLayerTwoViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {

        final GroupLayerTwo product = (GroupLayerTwo) group.getItems().get(childIndex);
        holder.bind(product);
        holder.intent(product, App.getContext());


    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindGroupViewHolder(GroupLayerOneViewHolder holder, int flatPosition, ExpandableGroup group) {
        final GroupLayerOne company = (GroupLayerOne) group;
        holder.bind(company);
        holder.intent(company, App.getContext());
        holder.hide(company);
    }
}
