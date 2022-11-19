package com.kits.orderkowsar.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupLayerTwo implements Parcelable {
    public final String name;
    public int id;
    public int ChildNo;



    public GroupLayerTwo(String name, Integer id, Integer ChildNo) {
        this.name = name;
        this.id = id;
        this.ChildNo = ChildNo;
    }

    protected GroupLayerTwo(Parcel in) {
        name = in.readString();
    }

    public static final Creator<GroupLayerTwo> CREATOR = new Creator<GroupLayerTwo>() {
        @Override
        public GroupLayerTwo createFromParcel(Parcel in) {
            return new GroupLayerTwo(in);
        }

        @Override
        public GroupLayerTwo[] newArray(int size) {
            return new GroupLayerTwo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

}
