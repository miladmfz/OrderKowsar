package com.kits.orderkowsar.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class RetrofitResponse {

    @SerializedName("Good")
    private Good good;
    @SerializedName("Activation")
    private Activation activation;
    @SerializedName("RstMiz")
    private RstMiz rstMiz;
    @SerializedName("Group")
    private GoodGroup group;
    @SerializedName("BasketInfo")
    private BasketInfo basketInfo;
    @SerializedName("ObjectType")
    private ObjectType objectType;

    @SerializedName("Goods")
    private ArrayList<Good> Goods;
    @SerializedName("Activations")
    private ArrayList<Activation> Activations;
    @SerializedName("RstMizs")
    private ArrayList<RstMiz> RstMizs;
    @SerializedName("BasketInfos")
    private ArrayList<BasketInfo> BasketInfos;

    @SerializedName("Values")
    private ArrayList<DistinctValue> Values;



    @SerializedName("Groups")
    private ArrayList<GoodGroup> Groups;

    @SerializedName("ObjectTypes")
    private ArrayList<ObjectType> ObjectTypes;


    @SerializedName("Factors")
    private ArrayList<Factor> Factors;


    @SerializedName("AppPrinters")
    private ArrayList<AppPrinter> AppPrinters;


    @SerializedName("Text")
    private String Text;
    @SerializedName("ErrCode")
    private String ErrCode;
    @SerializedName("ErrDesc")
    private String ErrDesc;

    public ArrayList<AppPrinter> getAppPrinters() {
        return AppPrinters;
    }

    public void setAppPrinters(ArrayList<AppPrinter> appPrinters) {
        AppPrinters = appPrinters;
    }

    public ArrayList<Factor> getFactors() {
        return Factors;
    }

    public void setFactors(ArrayList<Factor> factors) {
        Factors = factors;
    }

    public ArrayList<BasketInfo> getBasketInfos() {
        return BasketInfos;
    }

    public void setBasketInfos(ArrayList<BasketInfo> basketInfos) {
        BasketInfos = basketInfos;
    }

    public BasketInfo getBasketInfo() {
        return basketInfo;
    }

    public void setBasketInfo(BasketInfo basketInfo) {
        this.basketInfo = basketInfo;
    }

    public ArrayList<Good> getGoods() {
        return Goods;
    }

    public void setGoods(ArrayList<Good> goods) {
        Goods = goods;
    }
    public ArrayList<Activation> getActivations() {
        return Activations;
    }

    public void setActivations(ArrayList<Activation> activations) {
        Activations = activations;
    }

    public ArrayList<RstMiz> getRstMizs() {
        return RstMizs;
    }

    public void setRstMizs(ArrayList<RstMiz> rstMizs) {
        RstMizs = rstMizs;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public Activation getActivation() {
        return activation;
    }

    public void setActivation(Activation activation) {
        this.activation = activation;
    }

    public RstMiz getRstMiz() {
        return rstMiz;
    }

    public void setRstMiz(RstMiz rstMiz) {
        this.rstMiz = rstMiz;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
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

    public GoodGroup getGroup() {
        return group;
    }

    public void setGroup(GoodGroup group) {
        this.group = group;
    }

    public ArrayList<GoodGroup> getGroups() {
        return Groups;
    }

    public void setGroups(ArrayList<GoodGroup> groups) {
        Groups = groups;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public ArrayList<ObjectType> getObjectTypes() {
        return ObjectTypes;
    }

    public void setObjectTypes(ArrayList<ObjectType> objectTypes) {
        ObjectTypes = objectTypes;
    }

    public ArrayList<DistinctValue> getValues() {
        return Values;
    }

    public void setValues(ArrayList<DistinctValue> values) {
        Values = values;
    }
}
