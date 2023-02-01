package com.kits.orderkowsar.model;

import com.google.gson.annotations.SerializedName;

public class Factor {

    @SerializedName("FactorCode")
    private String FactorCode;
    @SerializedName("FactorDate")
    private String FactorDate;
    @SerializedName("DailyCode")
    private String DailyCode;
    @SerializedName("RstMizName")
    private String RstMizName;
    @SerializedName("FactorExplain")
    private String FactorExplain;
    @SerializedName("FactorRowCode")
    private String FactorRowCode;
    @SerializedName("GoodRef")
    private String GoodRef;
    @SerializedName("FacAmount")
    private String FacAmount;
    @SerializedName("CanPrint")
    private String CanPrint;
    @SerializedName("RowExplain")
    private String RowExplain;

    @SerializedName("GoodName")
    private String GoodName;

    @SerializedName("IsExtra")
    private String IsExtra;

    @SerializedName("TimeStart")
    private String TimeStart;

    @SerializedName("ErrCode")
    private String ErrCode;

    @SerializedName("ErrDesc")
    private String ErrDesc;

    @SerializedName("MizType")
    private String MizType;


    @SerializedName("InfoState")
    private String InfoState;


    @SerializedName("ReserveStart")
    private String ReserveStart;


    @SerializedName("InfoPrintCount")
    private String InfoPrintCount;

    @SerializedName("AppBasketInfoDate")
    private String AppBasketInfoDate;


    @SerializedName("InfoExplain")
    private String InfoExplain;

    public String getMizType() {
        return MizType;
    }

    public void setMizType(String mizType) {
        MizType = mizType;
    }

    public String getInfoExplain() {
        return InfoExplain;
    }

    public void setInfoExplain(String infoExplain) {
        InfoExplain = infoExplain;
    }

    public String getAppBasketInfoDate() {
        return AppBasketInfoDate;
    }

    public void setAppBasketInfoDate(String appBasketInfoDate) {
        AppBasketInfoDate = appBasketInfoDate;
    }

    public String getInfoState() {
        return InfoState;
    }

    public void setInfoState(String infoState) {
        InfoState = infoState;
    }

    public String getReserveStart() {
        return ReserveStart;
    }

    public void setReserveStart(String reserveStart) {
        ReserveStart = reserveStart;
    }

    public String getInfoPrintCount() {
        return InfoPrintCount;
    }

    public void setInfoPrintCount(String infoPrintCount) {
        InfoPrintCount = infoPrintCount;
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

    public String getTimeStart() {
        return TimeStart;
    }

    public void setTimeStart(String timeStart) {
        TimeStart = timeStart;
    }

    public String getIsExtra() {
        return IsExtra;
    }

    public void setIsExtra(String isExtra) {
        IsExtra = isExtra;
    }

    public String getGoodName() {
        return GoodName;
    }

    public void setGoodName(String goodName) {
        GoodName = goodName;
    }

    public String getFactorCode() {
        return FactorCode;
    }

    public void setFactorCode(String factorCode) {
        FactorCode = factorCode;
    }

    public String getFactorDate() {
        return FactorDate;
    }

    public void setFactorDate(String factorDate) {
        FactorDate = factorDate;
    }

    public String getDailyCode() {
        return DailyCode;
    }

    public void setDailyCode(String dailyCode) {
        DailyCode = dailyCode;
    }

    public String getRstMizName() {
        return RstMizName;
    }

    public void setRstMizName(String rstMizName) {
        RstMizName = rstMizName;
    }

    public String getFactorExplain() {
        return FactorExplain;
    }

    public void setFactorExplain(String factorExplain) {
        FactorExplain = factorExplain;
    }

    public String getFactorRowCode() {
        return FactorRowCode;
    }

    public void setFactorRowCode(String factorRowCode) {
        FactorRowCode = factorRowCode;
    }

    public String getGoodRef() {
        return GoodRef;
    }

    public void setGoodRef(String goodRef) {
        GoodRef = goodRef;
    }

    public String getFacAmount() {
        if (FacAmount.indexOf(".") > 0) {
            FacAmount = FacAmount.substring(0, FacAmount.indexOf("."));
        }
        return FacAmount;
    }

    public void setFacAmount(String facAmount) {
        FacAmount = facAmount;
    }

    public String getCanPrint() {
        return CanPrint;
    }

    public void setCanPrint(String canPrint) {
        CanPrint = canPrint;
    }

    public String getRowExplain() {
        return RowExplain;
    }

    public void setRowExplain(String rowExplain) {
        RowExplain = rowExplain;
    }
}
