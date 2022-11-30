package com.kits.orderkowsar.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Good implements Serializable {


    @SerializedName("GoodCode")
    private String GoodCode;
    @SerializedName("GoodMainCode")
    private String GoodMainCode;
    @SerializedName("GoodSubCode")
    private String GoodSubCode;
    @SerializedName("GoodName")
    private String GoodName;
    @SerializedName("MaxSellPrice")
    private String MaxSellPrice;
    @SerializedName("GoodImageName")
    private String GoodImageName;

    @SerializedName("DefaultUnitValue")
    private String DefaultUnitValue;
    @SerializedName("GoodUnitRef")
    private String GoodUnitRef;

    @SerializedName("Extera1")
    private String Extera1;
    @SerializedName("Extera2")
    private String Extera2;
    @SerializedName("Extera3")
    private String Extera3;
    @SerializedName("Extera4")
    private String Extera4;
    @SerializedName("Extera5")
    private String Extera5;
    @SerializedName("Extera6")
    private String Extera6;
    @SerializedName("Extera7")
    private String Extera7;
    @SerializedName("Extera8")
    private String Extera8;
    @SerializedName("Extera9")
    private String Extera9;
    @SerializedName("Extera10")
    private String Extera10;


    @SerializedName("Amount")
    private String Amount;
    @SerializedName("Price")
    private String Price;
    @SerializedName("RowCode")
    private String RowCode;
    @SerializedName("Explain")
    private String Explain;

    @SerializedName("ErrCode")
    private String ErrCode;
    @SerializedName("ErrDesc")
    private String ErrDesc;

    @SerializedName("SumFacAmount")
    private String SumFacAmount;
    @SerializedName("SumPrice")
    private String SumPrice;
    @SerializedName("CountGood")
    private String CountGood;

    @SerializedName("AppBasketInfoRef")
    private String AppBasketInfoRef;

    public String getAppBasketInfoRef() {
        return AppBasketInfoRef;
    }

    public void setAppBasketInfoRef(String appBasketInfoRef) {
        AppBasketInfoRef = appBasketInfoRef;
    }

    public String getDefaultUnitValue() {
        return DefaultUnitValue;
    }

    public void setDefaultUnitValue(String defaultUnitValue) {
        DefaultUnitValue = defaultUnitValue;
    }

    public String getGoodUnitRef() {
        return GoodUnitRef;
    }

    public void setGoodUnitRef(String goodUnitRef) {
        GoodUnitRef = goodUnitRef;
    }

    public String getSumFacAmount() {
        return SumFacAmount;
    }

    public void setSumFacAmount(String sumFacAmount) {
        SumFacAmount = sumFacAmount;
    }

    public String getSumPrice() {
        return SumPrice;
    }

    public void setSumPrice(String sumPrice) {
        SumPrice = sumPrice;
    }

    public String getCountGood() {
        return CountGood;
    }

    public void setCountGood(String countGood) {
        CountGood = countGood;
    }

    public String getGoodCode() {
        return GoodCode;
    }

    public void setGoodCode(String goodCode) {
        GoodCode = goodCode;
    }

    public String getGoodMainCode() {
        return GoodMainCode;
    }

    public void setGoodMainCode(String goodMainCode) {
        GoodMainCode = goodMainCode;
    }

    public String getGoodSubCode() {
        return GoodSubCode;
    }

    public void setGoodSubCode(String goodSubCode) {
        GoodSubCode = goodSubCode;
    }

    public String getGoodName() {
        return GoodName;
    }

    public void setGoodName(String goodName) {
        GoodName = goodName;
    }

    public String getMaxSellPrice() {
        if (MaxSellPrice.indexOf(".")>0){
            MaxSellPrice=MaxSellPrice.substring(0,MaxSellPrice.indexOf("."));
        }
        return MaxSellPrice;
    }

    public void setMaxSellPrice(String maxSellPrice) {
        MaxSellPrice = maxSellPrice;
    }

    public String getGoodImageName() {
        if (GoodImageName==null){
            GoodImageName="";
        }
        return GoodImageName;
    }

    public void setGoodImageName(String goodImageName) {
        GoodImageName = goodImageName;
    }

    public String getExtera1() {
        return Extera1;
    }

    public void setExtera1(String extera1) {
        Extera1 = extera1;
    }

    public String getExtera2() {
        return Extera2;
    }

    public void setExtera2(String extera2) {
        Extera2 = extera2;
    }

    public String getExtera3() {
        return Extera3;
    }

    public void setExtera3(String extera3) {
        Extera3 = extera3;
    }

    public String getExtera4() {
        return Extera4;
    }

    public void setExtera4(String extera4) {
        Extera4 = extera4;
    }

    public String getExtera5() {
        return Extera5;
    }

    public void setExtera5(String extera5) {
        Extera5 = extera5;
    }

    public String getExtera6() {
        return Extera6;
    }

    public void setExtera6(String extera6) {
        Extera6 = extera6;
    }

    public String getExtera7() {
        return Extera7;
    }

    public void setExtera7(String extera7) {
        Extera7 = extera7;
    }

    public String getExtera8() {
        return Extera8;
    }

    public void setExtera8(String extera8) {
        Extera8 = extera8;
    }

    public String getExtera9() {
        return Extera9;
    }

    public void setExtera9(String extera9) {
        Extera9 = extera9;
    }

    public String getExtera10() {
        return Extera10;
    }

    public void setExtera10(String extera10) {
        Extera10 = extera10;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getRowCode() {
        return RowCode;
    }

    public void setRowCode(String rowCode) {
        RowCode = rowCode;
    }

    public String getExplain() {
        return Explain;
    }

    public void setExplain(String explain) {
        Explain = explain;
    }

    public String getErrCode() {
        return ErrCode;
    }

    public void setErrCode(String errCode) {
        ErrCode = errCode;
    }

    public String getErrDesc() {
        return ErrDesc;
    }

    public void setErrDesc(String errDesc) {
        ErrDesc = errDesc;
    }
}
