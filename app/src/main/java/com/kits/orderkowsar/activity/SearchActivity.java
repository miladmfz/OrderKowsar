package com.kits.orderkowsar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kits.orderkowsar.Fragment.GrpFragment;
import com.kits.orderkowsar.R;
import com.kits.orderkowsar.adapters.GoodAdapter;
import com.kits.orderkowsar.adapters.InternetConnection;
import com.kits.orderkowsar.application.App;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.databinding.ActivitySearchBinding;
import com.kits.orderkowsar.model.Good;
import com.kits.orderkowsar.model.GoodGroup;
import com.kits.orderkowsar.model.GroupLayerOne;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchActivity extends AppCompatActivity {


    CallMethod callMethod;
    APIInterface apiInterface;
    String groupCode = "";
    int counter=0;
    Intent intent;
    GroupLayerOne cm ;
    ArrayList<GroupLayerOne> companies=new ArrayList<>();

    FragmentManager fragmentManager ;
    FragmentTransaction fragmentTransaction;
    GrpFragment grp_Fragment;
    FrameLayout grp_framelayout;
    FrameLayout good_framelayout;

    int width=1;


    Integer id = 0, conter = 0;

    String title = "گروه ها";
    MaterialCardView line_pro, line;
    Button change_search, filter_active;
    EditText edtsearch;
    Handler handler = new Handler();
    RecyclerView rc_grp, rc_good;
    Toolbar toolbar;
    ArrayList<Good> goods;
    ArrayList<GoodGroup> Groups;
    LottieAnimationView prog;
    TextView textCartItemCount;
    TextView tvstatus;
    ArrayList<Good> Goods, Goods_setupBadge;
    GridLayoutManager gridLayoutManager;
    public String srch = "", sq = "";
    private boolean loading = true;
    int pastVisiblesItems = 0, visibleItemCount, totalItemCount;
    public int PageNo = 0;
    GoodAdapter adapter;
    ArrayList<Good> Multi_Good = new ArrayList<>();
    FloatingActionButton fab;

    ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        InternetConnection ic = new InternetConnection(this);
        if (ic.has()) {
            try {
                init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            finish();
        }


    }

    //*************************************************

    public void Config() {
        callMethod = new CallMethod(App.getContext());
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width =metrics.widthPixels;


        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        grp_Fragment = new GrpFragment();


        grp_framelayout = findViewById(R.id.searchactivity_framelayout);


    }

    public void intent() {

    }

    public void init() {
        Config();
        GetFirstData();

        Button testbtn= findViewById(R.id.testbtn);

        testbtn.setOnClickListener(v -> {
            Log.e("test__0",fragmentManager.getBackStackEntryCount()+"");
            Log.e("test__1",fragmentManager.getFragments().size()+"");

        });

    }



    private void callGrpfragment (String GroupCode){

        grp_Fragment.setGroupCode(GroupCode);
        fragmentTransaction.replace(R.id.searchactivity_framelayout, grp_Fragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();

    }


    public void GetFirstData() {

        Call<RetrofitResponse> call1 = apiInterface.info("kowsar_info", "AppOrder_DefaultGroupCode");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    callGrpfragment(response.body().getText());
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.bag_shop) {


            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void RefreshState() {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        RefreshState();
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onBackPressed() {

        int count = fragmentManager.getBackStackEntryCount();
        if (count == 0 ) {
            super.onBackPressed();
            //additional code
        } else {
            fragmentManager.popBackStack();

        }

    }
}




