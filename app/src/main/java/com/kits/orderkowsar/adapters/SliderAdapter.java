package com.kits.orderkowsar.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.kits.orderkowsar.R;
import com.kits.orderkowsar.application.CallMethod;
import com.kits.orderkowsar.application.ImageInfo;
import com.kits.orderkowsar.model.Good;
import com.kits.orderkowsar.model.RetrofitResponse;
import com.kits.orderkowsar.webService.APIClient;
import com.kits.orderkowsar.webService.APIInterface;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SliderAdapter extends SliderViewAdapter<SliderAdapter.GoodViewHolder> {
    APIInterface apiInterface;
    private final ArrayList<Good> goods;
    private final Context mcontext;
    private final Boolean image_zoom;
    private final ImageInfo image_info;
    CallMethod callMethod;

    String url;

    public SliderAdapter(ArrayList<Good> Goods, boolean zoom, Context context) {

        this.mcontext = context;
        this.goods = Goods;
        this.image_zoom = zoom;
        this.callMethod = new CallMethod(mcontext);
        image_info = new ImageInfo(mcontext);
        url = callMethod.ReadString("ServerURLUse");
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

    }


    @Override
    public GoodViewHolder onCreateViewHolder(ViewGroup parent) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new GoodViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final GoodViewHolder holder, final int position) {



        if (image_info.Image_exist(goods.get(position).getGoodFieldValue("KsrImageCode"))) {

            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            File imagefile = new File(
                    root +
                            "/Kowsar/" +
                            callMethod.ReadString("EnglishCompanyNameUse") +
                            "/" +
                            goods.get(position).getGoodFieldValue("KsrImageCode") +
                            ".jpg");
            Bitmap myBitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
            holder.imageViewBackground.setImageBitmap(myBitmap);



        }
        else
        {

            byte[] imageByteArray1;
            imageByteArray1 = Base64.decode(mcontext.getString(R.string.no_photo), Base64.DEFAULT);
            holder.imageViewBackground.setImageBitmap(
                    Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length),
                            BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2,
                            BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2,
                            false)
            );

            Call<RetrofitResponse> call2 = apiInterface.GetImageFromKsr("GetImageFromKsr", goods.get(position).getGoodFieldValue("KsrImageCode")
            );
            call2.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call2, @NonNull Response<RetrofitResponse> response) {

                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (response.body().getText().equals("no_photo")) {
                            byte[] imageByteArray1;
                            imageByteArray1 = Base64.decode(mcontext.getString(R.string.no_photo), Base64.DEFAULT);
                            holder.imageViewBackground.setImageBitmap(
                                    Bitmap.createScaledBitmap(
                                            BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length),
                                            BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2,
                                            BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2,
                                            false)
                            );

                        } else {
                            byte[] imageByteArray1;
                            imageByteArray1 = Base64.decode(response.body().getText(), Base64.DEFAULT);
                            holder.imageViewBackground.setImageBitmap(Bitmap.createScaledBitmap(
                                    BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length),
                                    BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getWidth() * 2,
                                    BitmapFactory.decodeByteArray(imageByteArray1, 0, imageByteArray1.length).getHeight() * 2,
                                    false));
                            image_info.SaveImage(
                                    BitmapFactory.decodeByteArray(
                                            Base64.decode(response.body().getText(), Base64.DEFAULT),
                                            0,
                                            Base64.decode(response.body().getText(), Base64.DEFAULT).length),
                                    goods.get(position).getGoodFieldValue("KsrImageCode"));
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call2, @NonNull Throwable t) {
                    callMethod.ErrorLog(t.getMessage());
                }
            });

        }

        holder.imageViewBackground.setOnClickListener(v -> {
            if (image_zoom) {
                image_zome_view();
            }
        });

    }

    @Override
    public int getCount() {
        return goods.size();
    }


    static class GoodViewHolder extends ViewHolder {

        ImageView imageViewBackground;

        public GoodViewHolder(View itemView) {

            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
        }
    }

    public void image_zome_view() {



        final Dialog dialog = new Dialog(mcontext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//title laye nadashte bashim
        dialog.setContentView(R.layout.image_zoom);
        SliderView sliderView = dialog.findViewById(R.id.imageSlider_zoom_view);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);




        SliderAdapter adapter = new SliderAdapter(goods, false, mcontext);
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimations.SCALE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        dialog.show();
    }

}
