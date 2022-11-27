package com.kits.orderkowsar.application;

import android.app.Dialog;
import android.content.Context;
import android.widget.Spinner;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.button.MaterialButton;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import java.util.ArrayList;

public class Search_box {

    private final Context mContext;

    private final DatabaseHelper dbh;

    APIInterface apiInterface;

    String Where;

    CallMethod callMethod;

    public Search_box(Context context) {
        this.mContext = context;
        this.Where = "";
        callMethod = new CallMethod(mContext);

        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));

        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

    }


}
