package com.kits.orderkowsar.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.adapters.ProductAdapter;
import com.kits.orderkowsar.application.App;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.model.Category;
import com.kits.orderkowsar.model.DatabaseHelper;
import com.kits.orderkowsar.model.GoodGroup;
import com.kits.orderkowsar.model.Product;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import java.util.ArrayList;


public class AllViewActivity extends AppCompatActivity {


    APIInterface apiInterface;
    CallMethod callMethod;
    DatabaseHelper dbh;
    Toolbar toolbar;
    ArrayList<Category> categories = new ArrayList<>();
    RecyclerView rc;
    Category category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_view);


        Config();
        try {
            Handler handler = new Handler();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                handler.postDelayed(this::init, 100);
            }
        } catch (Exception e) {
            callMethod.ErrorLog(e.getMessage());
        }


    }


    public void Config() {
        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        dbh.ClearSearchColumn();

        toolbar = findViewById(R.id.allview_toolbar);
        rc = findViewById(R.id.allview_rc);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

        setSupportActionBar(toolbar);
    }


    public void init() {


        categories = new ArrayList<>();


        ArrayList<GoodGroup> groupsHeader = dbh.getAllGroups(dbh.ReadConfig("GroupCodeDefult"));

        for (GoodGroup groupHeader : groupsHeader) {
            ArrayList<Product> Product_child = new ArrayList<>();
            ArrayList<GoodGroup> groupsRow = dbh.getAllGroups(groupHeader.getGoodGroupFieldValue("groupcode"));
            for (GoodGroup groupRow : groupsRow) {
                Product_child.add(new Product(
                        groupRow.getGoodGroupFieldValue("Name"),
                        Integer.parseInt(groupRow.getGoodGroupFieldValue("groupcode")),
                        Integer.parseInt(groupRow.getGoodGroupFieldValue("ChildNo"))));
            }

            category = new Category(
                    groupHeader.getGoodGroupFieldValue("Name"),
                    Product_child,
                    Integer.parseInt(groupHeader.getGoodGroupFieldValue("groupcode")),
                    Integer.parseInt(groupHeader.getGoodGroupFieldValue("ChildNo")));

            categories.add(category);
        }


        ProductAdapter adapter = new ProductAdapter(categories, App.getContext());
        rc.setAdapter(adapter);
        rc.setLayoutManager(new LinearLayoutManager(this));


    }


}