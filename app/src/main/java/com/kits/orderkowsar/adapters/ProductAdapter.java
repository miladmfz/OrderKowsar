package com.kits.orderkowsar.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.application.App;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.application.ImageInfo;
import com.kits.orderkowsar.model.Category;
import com.kits.orderkowsar.model.Product;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.viewholder.CategoryViewHolder;
import com.kits.orderkowsar.viewholder.ProductViewHolder;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductAdapter extends ExpandableRecyclerViewAdapter<CategoryViewHolder, ProductViewHolder> {

    Context mContext;
    CallMethod callMethod;
    APIInterface apiInterface;
    ImageInfo image_info;
    Call<RetrofitResponse> call;


    public ProductAdapter(List<? extends ExpandableGroup> groups, Context mContext) {
        super(groups);
        this.mContext = mContext;
        this.callMethod = new CallMethod(mContext);
        this.image_info = new ImageInfo(mContext);
        this.apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);


    }

    @Override
    public CategoryViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new CategoryViewHolder(v);
    }

    @Override
    public ProductViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item2, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindChildViewHolder(ProductViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {

        final Product product = (Product) group.getItems().get(childIndex);
        holder.bind(product);
        holder.intent(product, App.getContext());


        if (image_info.Image_exist("TGoodsGrp" + product.getId())) {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File imagefile = new File(root + "/Kowsar/" +
                    callMethod.ReadString("EnglishCompanyNameUse") + "/" +
                    "TGoodsGrp" + product.getId() + ".jpg");
            Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
            holder.bindimage(myBitmap);

        } else {

            call = apiInterface.GetImageCustom("GetImageCustom"
                    , "TGoodsGrp"
                    , String.valueOf(product.id)
                    , "500"
            );
            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {

                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (!response.body().getText().equals("no_photo")) {
                            image_info.SaveImage(
                                    BitmapFactory.decodeByteArray(
                                            Base64.decode(response.body().getText(), Base64.DEFAULT),
                                            0,
                                            Base64.decode(response.body().getText(), Base64.DEFAULT).length
                                    ),
                                    "TGoodsGrp" + product.getId()
                            );

                            notifyItemChanged(flatPosition);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                    callMethod.ErrorLog(t.getMessage());
                }
            });

        }


    }

    @Override
    public void onBindGroupViewHolder(CategoryViewHolder holder, int flatPosition, ExpandableGroup group) {
        final Category company = (Category) group;


        holder.bind(company);
        holder.intent(company, App.getContext());
        holder.hide(company);


        if (image_info.Image_exist("TGoodsGrp" + company.getId())) {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File imagefile = new File(root + "/Kowsar/" +
                    callMethod.ReadString("EnglishCompanyNameUse") + "/" +
                    "TGoodsGrp" + company.getId() + ".jpg");
            Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
            holder.bindimage(myBitmap);

        } else {

            call = apiInterface.GetImageCustom("GetImageCustom"
                    , "TGoodsGrp"
                    , String.valueOf(company.id)
                    , "500"
            );
            call.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {

                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (!response.body().getText().equals("no_photo")) {
                            image_info.SaveImage(
                                    BitmapFactory.decodeByteArray(
                                            Base64.decode(response.body().getText(), Base64.DEFAULT),
                                            0,
                                            Base64.decode(response.body().getText(), Base64.DEFAULT).length
                                    ),
                                    "TGoodsGrp" + company.getId()
                            );

                            notifyItemChanged(flatPosition);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                    callMethod.ErrorLog(t.getMessage());
                }
            });

        }

    }
}
