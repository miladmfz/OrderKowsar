package com.kits.orderkowsar.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class RetrofitResponse {

    @SerializedName("Good")
    private Good good;
    @SerializedName("PreFactor")
    private PreFactor preFactor;
    @SerializedName("Activation")
    private Activation activation;
    @SerializedName("RstMiz")
    private RstMiz rstMiz;
    @SerializedName("Group")
    private GoodGroup group;

    @SerializedName("Goods")
    private ArrayList<Good> Goods;
    @SerializedName("PreFactors")
    private ArrayList<PreFactor> PreFactors;
    @SerializedName("Activations")
    private ArrayList<Activation> Activations;
    @SerializedName("RstMizs")
    private ArrayList<RstMiz> RstMizs;



    @SerializedName("Groups")
    private ArrayList<GoodGroup> Groups;


    @SerializedName("Text")
    private String Text;
    @SerializedName("ErrCode")
    private String ErrCode;
    @SerializedName("ErrDesc")
    private String ErrDesc;




    public ArrayList<Good> getGoods() {
        return Goods;
    }

    public void setGoods(ArrayList<Good> goods) {
        Goods = goods;
    }

    public ArrayList<PreFactor> getPreFactors() {
        return PreFactors;
    }

    public void setPreFactors(ArrayList<PreFactor> preFactors) {
        PreFactors = preFactors;
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

    public PreFactor getPreFactor() {
        return preFactor;
    }

    public void setPreFactor(PreFactor preFactor) {
        this.preFactor = preFactor;
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
}
