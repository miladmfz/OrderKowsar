package com.kits.orderkowsar.model;

import android.annotation.SuppressLint;

import com.google.gson.annotations.SerializedName;
import com.kits.orderkowsar.application.App;

public class Activation {

    @SerializedName("AppBrokerCustomerCode")
    private String AppBrokerCustomerCode;
    @SerializedName("ActivationCode")
    private String ActivationCode;
    @SerializedName("PersianCompanyName")
    private String PersianCompanyName;
    @SerializedName("EnglishCompanyName")
    private String EnglishCompanyName;
    @SerializedName("ServerURL")
    private String ServerURL;
    @SerializedName("SQLiteURL")
    private String SQLiteURL;
    @SerializedName("MaxDevice")
    private String MaxDevice;

    @SuppressLint("SdCardPath")
    public String getDatabaseFolderPath() {
        return "/data/data/" + App.getContext().getPackageName() + "/databases/" + EnglishCompanyName;
    }


    @SuppressLint("SdCardPath")
    public String getDatabaseFilePath() {
        return "/data/data/" + App.getContext().getPackageName() + "/databases/" + EnglishCompanyName + "/KowsarDb.sqlite";
    }

    public String getAppBrokerCustomerCode() {
        return AppBrokerCustomerCode;
    }

    public void setAppBrokerCustomerCode(String appBrokerCustomerCode) {
        AppBrokerCustomerCode = appBrokerCustomerCode;
    }

    public String getActivationCode() {
        return ActivationCode;
    }

    public void setActivationCode(String activationCode) {
        ActivationCode = activationCode;
    }

    public String getPersianCompanyName() {
        return PersianCompanyName;
    }

    public void setPersianCompanyName(String persianCompanyName) {
        PersianCompanyName = persianCompanyName;
    }

    public String getEnglishCompanyName() {
        return EnglishCompanyName;
    }

    public void setEnglishCompanyName(String englishCompanyName) {
        EnglishCompanyName = englishCompanyName;
    }

    public String getServerURL() {
        return ServerURL;
    }

    public void setServerURL(String serverURL) {
        ServerURL = serverURL;
    }

    public String getSQLiteURL() {
        return SQLiteURL;
    }

    public void setSQLiteURL(String SQLiteURL) {
        this.SQLiteURL = SQLiteURL;
    }

    public String getMaxDevice() {
        return MaxDevice;
    }

    public void setMaxDevice(String maxDevice) {
        MaxDevice = maxDevice;
    }
}
