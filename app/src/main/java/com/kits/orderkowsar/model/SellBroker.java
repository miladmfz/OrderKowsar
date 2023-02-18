package com.kits.orderkowsar.model;

import com.google.gson.annotations.SerializedName;

public class SellBroker {

    @SerializedName("brokerCode") private String brokerCode;
    @SerializedName("BrokerNameWithoutType") private String BrokerNameWithoutType;

    public String getBrokerCode() {
        return brokerCode;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public String getBrokerNameWithoutType() {
        return BrokerNameWithoutType;
    }

    public void setBrokerNameWithoutType(String brokerNameWithoutType) {
        BrokerNameWithoutType = brokerNameWithoutType;
    }
}
