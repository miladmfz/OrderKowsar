package com.kits.orderkowsar.model;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class GroupLayerOne extends ExpandableGroup<GroupLayerTwo> {

    public int id ;
    public int childno ;
    public String name ;


    public GroupLayerOne(String title, List<GroupLayerTwo> items, int id, int childno) {
        super(title, items);
        this.id=id;
        this.name=title;
        this.childno=childno;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
