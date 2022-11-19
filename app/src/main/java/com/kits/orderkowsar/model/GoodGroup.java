package com.kits.orderkowsar.model;

import com.google.gson.annotations.SerializedName;

public class GoodGroup {

    @SerializedName("GoodGroupCode")
    private String GoodGroupCode;
    @SerializedName("GroupCode")
    private String GroupCode;
    @SerializedName("Name")
    private String Name;
    @SerializedName("L1")
    private String L1;
    @SerializedName("L2")
    private String L2;
    @SerializedName("L3")
    private String L3;
    @SerializedName("L4")
    private String L4;
    @SerializedName("L5")
    private String L5;

    @SerializedName("ChildNo")
    private String ChildNo;

    @SerializedName("GoodGroupImageName")
    private String GoodGroupImageName;

    public String getGoodGroupImageName() {
        return GoodGroupImageName;
    }

    public void setGoodGroupImageName(String goodGroupImageName) {
        GoodGroupImageName = goodGroupImageName;
    }

    public String getGoodGroupFieldValue(String AKey) {

        String iKey = AKey.toLowerCase();
        String Res = "";

        switch (iKey) {
            case "goodgroupcode":
                if (GoodGroupCode == null) Res = "";
                else Res = GoodGroupCode.toString();
                break;
            case "groupcode":
                if (GroupCode == null) Res = "";
                else Res = GroupCode.toString();
                break;
            case "name":
                if (Name == null) Res = "";
                else Res = Name;
                break;
            case "goodgroupimagename":
                if (GoodGroupImageName == null) Res = "";
                else Res = GoodGroupImageName.toString();
                break;
            case "l1":
                if (L1 == null) Res = "";
                else Res = L1.toString();
                break;
            case "l2":
                if (L2 == null) Res = "";
                else Res = L2.toString();
                break;
            case "l3":
                if (L3 == null) Res = "";
                else Res = L3.toString();
                break;
            case "l4":
                if (L4 == null) Res = "";
                else Res = L4.toString();
                break;
            case "l5":
                if (L5 == null) Res = "";
                else Res = L5.toString();
                break;
            case "childno":
                if (ChildNo == null) Res = "0";
                else Res = ChildNo.toString();
                break;
        }

        return Res;

    }

    public void setName(String name) {
        Name = name;
    }


}
