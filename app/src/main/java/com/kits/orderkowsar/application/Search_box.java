package com.kits.orderkowsar.application;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.button.MaterialButton;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.activity.SearchActivity;
import com.kits.orderkowsar.model.Column;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.NumberFunctions;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import java.util.ArrayList;

public class Search_box {

    private final Context mContext;

    private final DatabaseHelper dbh;

    APIInterface apiInterface;

    String Where;
    Spinner spinner;
    ArrayList<Column> Goodtype;
    ArrayList<String> Goodtype_array = new ArrayList<>();
    ArrayList<Column> Columns;
    LinearLayoutCompat layout_view;
    MaterialButton btn_search;
    Dialog dialog;
    CallMethod callMethod;

    public Search_box(Context context) {
        this.mContext = context;
        this.Where = "";
        callMethod = new CallMethod(mContext);

        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));

        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

    }


}
