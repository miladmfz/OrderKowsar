package com.kits.orderkowsar.application;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.activity.NavActivity;
import com.kits.orderkowsar.model.Column;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.NumberFunctions;
import com.kits.orderkowsar.model.ReplicationModel;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.model.TableDetail;
import com.kits.orderkowsar.model.UserInfo;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Replication {

    private final Context mContext;
    CallMethod callMethod;
    APIInterface apiInterface;
    Intent intent;
    ImageInfo image_info;

    private final SQLiteDatabase database;
    private final Integer RepRowCount = 100;
    private Integer FinalStep = 0;
    String LastRepCode = "0";
    Dialog dialog;
    private final DatabaseHelper dbh;
    ArrayList<TableDetail> tableDetails = new ArrayList<>();
    ArrayList<ReplicationModel> replicationModels = new ArrayList<>();

    String url;
    Integer replicatelevel;
    Cursor cursor;

    TextView tv_rep;
    TextView tv_step;


    public Replication(Context context) {
        this.mContext = context;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        this.image_info = new ImageInfo(mContext);
        url = callMethod.ReadString("ServerURLUse");
        database = mContext.openOrCreateDatabase(callMethod.ReadString("DatabaseName"), Context.MODE_PRIVATE, null);

        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

    }


    public void DoingReplicate() {

        dialog = new Dialog(mContext);
        dialog();

        if (dbh.GetColumnscount().equals("0")) {
            tv_rep.setText(NumberFunctions.PerisanNumber("در حال بروز رسانی تنظیم جدول"));
            GoodTypeReplication();
        } else {
            Call<RetrofitResponse> call1 = apiInterface.MaxRepLogCode("MaxRepLogCode");
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                    assert response.body() != null;
                    dbh.SaveConfig("MaxRepLogCode", response.body().getText());
                    RetrofitReplicate(0);
                }

                @Override
                public void onFailure(Call<RetrofitResponse> call, Throwable t) {

                }
            });
        }


    }

    public void DoingReplicateAuto() {

        Call<RetrofitResponse> call1 = apiInterface.MaxRepLogCode("MaxRepLogCode");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(Call<RetrofitResponse> call, Response<RetrofitResponse> response) {
                assert response.body() != null;
                dbh.SaveConfig("MaxRepLogCode", response.body().getText());
                RetrofitReplicateAuto(0);
            }

            @Override
            public void onFailure(Call<RetrofitResponse> call, Throwable t) {

            }
        });

    }

    public void dialog() {
        dialog.setContentView(R.layout.rep_prog);
        tv_rep = dialog.findViewById(R.id.rep_prog_text);
        tv_step = dialog.findViewById(R.id.rep_prog_step);
        dialog.show();
    }

    public void RetrofitReplicate(Integer replevel) {
        dbh.closedb();
        replicatelevel = replevel;

        replicationModels = dbh.GetReplicationTable();

        if (replicatelevel < replicationModels.size()) {

            ReplicationModel replicatedetail = replicationModels.get(replicatelevel);
            tv_rep.setText(NumberFunctions.PerisanNumber(replicationModels.size() + "/" + replicatedetail.getReplicationCode() + "در حال بروز رسانی"));
            tableDetails = dbh.GetTableDetail(replicatedetail.getClientTable());
            FinalStep = 0;
            LastRepCode = String.valueOf(replicatedetail.getLastRepLogCode());

            UserInfo userInfo = dbh.LoadPersonalInfo();

            String where = replicatedetail.getCondition().replace("BrokerCondition", userInfo.getBrokerCode());


            Call<RetrofitResponse> call1 = apiInterface.RetrofitReplicate(
                    "repinfo",
                    LastRepCode,
                    replicatedetail.getServerTable(),
                    where,
                    "1",
                    String.valueOf(RepRowCount)
            );

            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {

                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        try {

                            JSONArray arrayobject = new JSONArray(response.body().getText());
                            int ObjectSize = arrayobject.length();
                            JSONObject singleobject = arrayobject.getJSONObject(0);
                            String state = singleobject.getString("RLOpType");
                            FinalStep = Integer.parseInt(singleobject.getString("RowsCount"));
                            tv_step.setText(NumberFunctions.PerisanNumber(singleobject.getString("RowsCount") + "تعداد"));
                            tv_step.setVisibility(View.VISIBLE);

                            switch (state) {
                                case "n":
                                case "N":
                                    break;
                                default:

                                    for (int i = 0; i < ObjectSize; i++) {

                                        singleobject = arrayobject.getJSONObject(i);
                                        String reptype = singleobject.getString("RLOpType");
                                        String repcode = singleobject.getString("RepLogDataCode");
                                        String repObjectCode = singleobject.getString("RLObjectRef");
                                        String code = singleobject.getString(replicatedetail.getServerPrimaryKey());
                                        int columnDetail = tableDetails.size();
                                        StringBuilder qCol = new StringBuilder();

                                        switch (reptype) {
                                            case "U":
                                            case "u":
                                            case "I":
                                            case "i":

                                                for (TableDetail singletabale : tableDetails) {

                                                    if (singleobject.has(singletabale.getName())) {
                                                        singletabale.setText(singleobject.getString(singletabale.getName()));
                                                        if (singletabale.getText() != null)
                                                            singletabale.setText(singletabale.getText().replace("'", " "));
                                                    }
                                                }

                                                @SuppressLint("Recycle") Cursor d = database.rawQuery("Select Count(*) AS cntRec From " + replicatedetail.getClientTable() + " Where " + replicatedetail.getClientPrimaryKey() + " = " + code, null);
                                                d.moveToFirst();
                                                @SuppressLint("Range") int nc = d.getInt(d.getColumnIndex("cntRec"));
                                                if (nc == 0) {


                                                    qCol = new StringBuilder("INSERT INTO " + replicatedetail.getClientTable() + " ( ");
                                                    int QueryConditionCount = 0;
                                                    for (int z = 0; z < columnDetail; z++) {
                                                        if (tableDetails.get(z).getText() != null) {
                                                            if (QueryConditionCount > 0)
                                                                qCol.append(" , ");
                                                            qCol.append(" ").append(tableDetails.get(z).getName());
                                                            QueryConditionCount++;
                                                        }
                                                    }
                                                    qCol.append(" ) Select  ");
                                                    QueryConditionCount = 0;

                                                    for (int z = 0; z < columnDetail; z++) {
                                                        if (tableDetails.get(z).getText() != null) {
                                                            if (QueryConditionCount > 0)
                                                                qCol.append(" , ");
                                                            String valuetype = tableDetails.get(z).getType().substring(0, 2);
                                                            if (!tableDetails.get(z).getText().equals("null")) {
                                                                if (valuetype.equals("CH")) {
                                                                    qCol.append(" '").append(tableDetails.get(z).getText()).append("' ");
                                                                } else {
                                                                    qCol.append(" ").append(tableDetails.get(z).getText());
                                                                }
                                                            } else {
                                                                qCol.append(" ").append(tableDetails.get(z).getText());
                                                            }
                                                            QueryConditionCount++;
                                                        }

                                                    }


                                                } else {

                                                    qCol = new StringBuilder("Update " + replicatedetail.getClientTable() + "  Set ");
                                                    int QueryConditionCount = 0;
                                                    for (int z = 1; z < columnDetail; z++) {
                                                        if (tableDetails.get(z).getText() != null) {
                                                            if (QueryConditionCount > 0)
                                                                qCol.append(" , ");
                                                            if (!tableDetails.get(z).getText().equals("null")) {
                                                                String valuetype = tableDetails.get(z).getType().substring(0, 2);
                                                                if (valuetype.equals("CH")) {
                                                                    qCol.append(" ").append(tableDetails.get(z).getName()).append(" = '").append(tableDetails.get(z).getText()).append("' ");
                                                                } else {
                                                                    qCol.append(" ").append(tableDetails.get(z).getName()).append(" = ").append(tableDetails.get(z).getText()).append(" ");
                                                                }
                                                            } else {
                                                                qCol.append(" ").append(tableDetails.get(z).getName()).append(" = ").append(tableDetails.get(z).getText()).append(" ");
                                                            }
                                                            QueryConditionCount++;
                                                        }
                                                    }
                                                    qCol.append(" Where ").append(replicatedetail.getClientPrimaryKey()).append(" = ").append(code);

                                                }

                                                try {
                                                    Log.e("test_qCol=", repcode + " = " + qCol.toString());
                                                    database.execSQL(qCol.toString());
                                                    LastRepCode = repcode;
                                                } catch (Exception e) {
                                                    Log.e("test_qCol=", e.getMessage());
                                                }


                                                d.close();
                                                break;
                                            case "D":
                                            case "d":
                                                qCol = new StringBuilder("Delete from " + replicatedetail.getClientTable() + "  Where ").append(replicatedetail.getClientPrimaryKey()).append(" = ").append(repObjectCode);
                                                try {
                                                    Log.e("test_qCol=", repcode + " = " + qCol);
                                                    database.execSQL(qCol.toString());
                                                    LastRepCode = repcode;
                                                } catch (Exception e) {
                                                    Log.e("test_qCol=", e.getMessage());
                                                }

                                                break;
                                        }
                                    }
                                    Log.e("test_","3");

                                    database.execSQL("Update ReplicationTable Set LastRepLogCode = " + LastRepCode + " Where ServerTable = '" + replicatedetail.getServerTable() + "' ");
                                    break;
                            }
                            Log.e("test_","4");

                            if (arrayobject.length() >= RepRowCount) {
                                RetrofitReplicate(replicatelevel);
                            } else {
                                Log.e("test_","5");

                                if (Integer.parseInt(LastRepCode) < 0) {
                                    Log.e("test_","6");

                                    database.execSQL("Update ReplicationTable Set LastRepLogCode = " + dbh.ReadConfig("MaxRepLogCode") + " Where ServerTable = '" + replicatedetail.getServerTable() + "' ");

                                    RetrofitReplicate(replicatelevel);
                                } else {
                                    Log.e("test_","7");

                                    tv_step.setVisibility(View.GONE);
                                    RetrofitReplicate(replicatelevel + 1);
                                }
                            }
                        } catch (JSONException ignored) {
                            Log.e("test_","8");
                            Log.e("test_",ignored.getMessage());

                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    Log.e("test_",t.getMessage());

                    RetrofitReplicate(replicatelevel);
                }
            });

        } else {
            replicateGoodImageChange();
        }
    }


    public void RetrofitReplicateAuto(Integer replevel) {
        dbh.closedb();
        replicationModels = dbh.GetReplicationTable();
        if (replevel < replicationModels.size()) {
            ReplicationModel replicatedetail = replicationModels.get(replevel);
            tableDetails = dbh.GetTableDetail(replicatedetail.getClientTable());

            FinalStep = 0;
            LastRepCode = String.valueOf(replicatedetail.getLastRepLogCode());

            UserInfo userInfo = dbh.LoadPersonalInfo();

            String where = replicatedetail.getCondition().replace("BrokerCondition", userInfo.getBrokerCode());

            Log.e("test_qCol",LastRepCode);
            Call<RetrofitResponse> call1 = apiInterface.RetrofitReplicate(
                    "repinfo",
                    LastRepCode,
                    replicatedetail.getServerTable(),
                    where,
                    "1",
                    "100"
            );

            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {

                    if (response.isSuccessful()) {
                        new Thread(() -> {

                            assert response.body() != null;
                            try {
                                JSONArray arrayobject = new JSONArray(response.body().getText());
                                int ObjectSize = arrayobject.length();
                                JSONObject singleobject = arrayobject.getJSONObject(0);
                                String state = singleobject.getString("RLOpType");

                                switch (state) {
                                    case "n":
                                    case "N":
                                        break;
                                    default:

                                        for (int i = 0; i < ObjectSize; i++) {

                                            singleobject = arrayobject.getJSONObject(i);
                                            String reptype = singleobject.getString("RLOpType");
                                            String repcode = singleobject.getString("RepLogDataCode");
                                            String repObjectCode = singleobject.getString("RLObjectRef");
                                            String code = singleobject.getString(replicatedetail.getServerPrimaryKey());

                                            int columnDetail = tableDetails.size();
                                            StringBuilder qCol = new StringBuilder();

                                            switch (reptype) {
                                                case "U":
                                                case "u":
                                                case "I":
                                                case "i":

                                                    for (TableDetail singletabale : tableDetails) {

                                                        if (singleobject.has(singletabale.getName())) {
                                                            singletabale.setText(singleobject.getString(singletabale.getName()));
                                                            if (singletabale.getText() != null)
                                                                singletabale.setText(singletabale.getText().replace("'", " "));
                                                        }
                                                    }

                                                    @SuppressLint("Recycle") Cursor d = database.rawQuery("Select Count(*) AS cntRec From " + replicatedetail.getClientTable() + " Where " + replicatedetail.getClientPrimaryKey() + " = " + code, null);
                                                    d.moveToFirst();
                                                    @SuppressLint("Range") int nc = d.getInt(d.getColumnIndex("cntRec"));
                                                    if (nc == 0) {


                                                        qCol = new StringBuilder("INSERT INTO " + replicatedetail.getClientTable() + " ( ");
                                                        int QueryConditionCount = 0;
                                                        for (int z = 0; z < columnDetail; z++) {
                                                            if (tableDetails.get(z).getText() != null) {
                                                                if (QueryConditionCount > 0)
                                                                    qCol.append(" , ");
                                                                qCol.append(" ").append(tableDetails.get(z).getName());
                                                                QueryConditionCount++;
                                                            }
                                                        }
                                                        qCol.append(" ) Select  ");
                                                        QueryConditionCount = 0;

                                                        for (int z = 0; z < columnDetail; z++) {
                                                            if (tableDetails.get(z).getText() != null) {
                                                                if (QueryConditionCount > 0)
                                                                    qCol.append(" , ");
                                                                String valuetype = tableDetails.get(z).getType().substring(0, 2);
                                                                if (!tableDetails.get(z).getText().equals("null")) {
                                                                    if (valuetype.equals("CH")) {
                                                                        qCol.append(" '").append(tableDetails.get(z).getText()).append("' ");
                                                                    } else {
                                                                        qCol.append(" ").append(tableDetails.get(z).getText());
                                                                    }
                                                                } else {
                                                                    qCol.append(" ").append(tableDetails.get(z).getText());
                                                                }
                                                                QueryConditionCount++;
                                                            }

                                                        }


                                                    } else
                                                    {

                                                        qCol = new StringBuilder("Update " + replicatedetail.getClientTable() + "  Set ");
                                                        int QueryConditionCount = 0;
                                                        for (int z = 1; z < columnDetail; z++) {
                                                            if (tableDetails.get(z).getText() != null) {
                                                                if (QueryConditionCount > 0)
                                                                    qCol.append(" , ");
                                                                if (!tableDetails.get(z).getText().equals("null")) {
                                                                    String valuetype = tableDetails.get(z).getType().substring(0, 2);
                                                                    if (valuetype.equals("CH")) {
                                                                        qCol.append(" ").append(tableDetails.get(z).getName()).append(" = '").append(tableDetails.get(z).getText()).append("' ");
                                                                    } else {
                                                                        qCol.append(" ").append(tableDetails.get(z).getName()).append(" = ").append(tableDetails.get(z).getText()).append(" ");
                                                                    }
                                                                } else {
                                                                    qCol.append(" ").append(tableDetails.get(z).getName()).append(" = ").append(tableDetails.get(z).getText()).append(" ");
                                                                }
                                                                QueryConditionCount++;
                                                            }
                                                        }
                                                        qCol.append(" Where ").append(replicatedetail.getClientPrimaryKey()).append(" = ").append(code);

                                                    }

                                                    try {
                                                        Log.e("test_qCol=", repcode + " = " + qCol.toString());
                                                        database.execSQL(qCol.toString());
                                                        LastRepCode = repcode;

                                                    } catch (Exception e) {
                                                        Log.e("test_qCol=", e.getMessage());
                                                    }

                                                    d.close();
                                                    break;

                                                case "D":
                                                case "d":

                                                    qCol = new StringBuilder("Delete from " + replicatedetail.getClientTable() + "  Where ").append(replicatedetail.getClientPrimaryKey()).append(" = ").append(repObjectCode);
                                                    try {
                                                        Log.e("test_qCol=", repcode + " = " + qCol);
                                                        database.execSQL(qCol.toString());
                                                        LastRepCode = repcode;
                                                    } catch (Exception e) {
                                                        Log.e("test_qCol=", e.getMessage());
                                                    }

                                                    break;
                                            }
                                        }
                                        database.execSQL("Update ReplicationTable Set LastRepLogCode = " + LastRepCode + " Where ServerTable = '" + replicatedetail.getServerTable() + "' ");
                                        break;
                                }
                                if (arrayobject.length() >= RepRowCount) {
                                    RetrofitReplicateAuto(replevel);
                                } else {
                                    if (Integer.parseInt(LastRepCode) < 0) {

                                        database.execSQL("Update ReplicationTable Set LastRepLogCode = " + dbh.ReadConfig("MaxRepLogCode") + " Where ServerTable = '" + replicatedetail.getServerTable() + "' ");

                                        RetrofitReplicateAuto(replevel);
                                    } else {
                                        RetrofitReplicateAuto(replevel + 1);
                                    }
                                }
                            } catch (JSONException ignored) {
                            }

                        }).start();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    Log.e("test_object.length", t.getMessage());
                }
            });

        }


    }

    public void replicateGoodImageChange() {
        tv_rep.setText(NumberFunctions.PerisanNumber("در حال بروز رسانی عکس"));
        FinalStep = 0;
        String RepTable = "KsrImage";
        cursor = database.rawQuery("Select DataValue From Config Where KeyValue ='KsrImage_LastRepCode'", null);
        cursor.moveToFirst();
        LastRepCode = cursor.getString(0);
        cursor.close();

        Call<RetrofitResponse> call1 = apiInterface.RetrofitReplicate(
                "repinfo"
                , LastRepCode
                , RepTable
                , ""
                , "1"
                , String.valueOf(400)
        );
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    try {
                        JSONArray arrayobject = new JSONArray(response.body().getText());
                        int ObjectSize = arrayobject.length();
                        JSONObject singleobject = arrayobject.getJSONObject(0);
                        String state = singleobject.getString("RLOpType");

                        switch (state) {
                            case "n":
                            case "N":

                                break;
                            default:
                                tv_step.setVisibility(View.VISIBLE);
                                FinalStep = Integer.parseInt(arrayobject.getJSONObject(0).getString("RowsCount"));
                                for (int i = 0; i < ObjectSize; i++) {
                                    tv_step.setText(NumberFunctions.PerisanNumber(FinalStep + "تعداد"));
                                    singleobject = arrayobject.getJSONObject(i);
                                    String optype = singleobject.getString("RLOpType");
                                    String repcode = singleobject.getString("RepLogDataCode");
                                    String code = singleobject.getString("KsrImageCode");
                                    String qCol = "";
                                    String ObjectRef = singleobject.getString("ObjectRef");
                                    Cursor d = database.rawQuery("Select Count(*) AS cntRec From KsrImage Where KsrImageCode =" + code, null);

                                    d.moveToFirst();
                                    @SuppressLint("Range") int nc = d.getInt(d.getColumnIndex("cntRec"));

                                    switch (optype) {
                                        case "U":
                                        case "u":
                                        case "I":
                                        case "i":
                                            if (nc != 0) {
                                                qCol = "Delete from KsrImage Where KsrImageCode= " + code;
                                                try {
                                                    database.execSQL(qCol);
                                                    Log.e("test_qCol=", qCol);
                                                } catch (Exception e) {
                                                    Log.e("test_qCol=", e.getMessage());
                                                }
                                                image_info.DeleteImage(code);
                                            }

                                            qCol = "INSERT INTO KsrImage(KsrImageCode, ObjectRef,IsDefaultImage) Select " + code + "," + ObjectRef + ",'false'";

                                            try {
                                                database.execSQL(qCol);
                                                Log.e("test_qCol=", qCol);
                                            } catch (Exception e) {
                                                Log.e("test_qCol=", e.getMessage());
                                            }
                                            d.close();
                                            break;

                                        case "D":
                                        case "d":

                                            qCol = "Delete from KsrImage Where KsrImageCode= " + code;
                                            image_info.DeleteImage(code);


                                            try {
                                                database.execSQL(qCol);
                                                Log.e("test_qCol=", qCol);
                                            } catch (Exception e) {
                                                Log.e("test_qCol=", e.getMessage());
                                            }
                                            d.close();
                                            break;
                                    }
                                    LastRepCode = repcode;
                                }
                                database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'KsrImage_LastRepCode'");
                                break;
                        }

                        if (arrayobject.length() >= 400) {
                            replicateGoodImageChange();
                        } else {
                            tv_step.setVisibility(View.GONE);
                            intent = new Intent(mContext, NavActivity.class);
                            mContext.startActivity(intent);
                            ((Activity) mContext).finish();
                            callMethod.showToast("بروز رسانی انجام شد");
                        }
                    } catch (JSONException ignored) {

                    }

                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                replicateGoodImageChange();
            }
        });
    }

    public void BrokerStack() {

        UserInfo userInfo = dbh.LoadPersonalInfo();
        dbh.DatabaseCreate();
        Call<RetrofitResponse> call1 = apiInterface.BrokerStack("BrokerStack", userInfo.getBrokerCode());
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Log.e("test_BrokerStack", response.body().getText());
                    if (!response.body().getText().equals(dbh.ReadConfig("BrokerStack"))) {
                        dbh.SaveConfig("BrokerStack", response.body().getText());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                Log.e("test_Retrofitbroker", t.getMessage());
            }
        });
        MenuBroker();
    }

    public void GroupCodeDefult() {

        Call<RetrofitResponse> call1 = apiInterface.info("kowsar_info", "AppBroker_DefaultGroupCode");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (!response.body().getText().equals("")) {
                        if (!response.body().getText().equals(dbh.ReadConfig("GroupCodeDefult"))) {
                            dbh.SaveConfig("GroupCodeDefult", response.body().getText());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });
    }

    public void MenuBroker() {
        Call<RetrofitResponse> call1 = apiInterface.MenuBroker("GetMenuBroker");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (!response.body().getText().equals(dbh.ReadConfig("MenuBroker"))) {
                        dbh.SaveConfig("MenuBroker", response.body().getText());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
            }
        });
        GroupCodeDefult();
    }

    public void GoodTypeReplication() {

        Call<RetrofitResponse> call1 = apiInterface.GetGoodType("GetGoodType");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    ArrayList<Column> columns = response.body().getColumns();
                    for (Column column : columns) {
                        dbh.ReplicateGoodtype(column);
                    }
                    columnReplication(0);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                Log.e("onFailure_t", t.getMessage());
            }
        });


    }

    public void columnReplication(Integer i) {
        if (i < 4) {
            Call<RetrofitResponse> call2 = apiInterface.GetColumnList("GetColumnList", "" + i, "1", "1");
            call2.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        ArrayList<Column> columns = response.body().getColumns();
                        Log.e("columns_size", columns.size() + "");
                        int j = 0;
                        for (Column column : columns) {
                            dbh.ReplicateColumn(column, i);
                            j++;
                        }
                        if (j == columns.size()) {
                            columnReplication(i + 1);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    Log.e("onFailure_t2", t.getMessage());
                    Log.e("onFailure", t.toString());
                }
            });
        } else {
            DoingReplicate();

        }
    }


}
