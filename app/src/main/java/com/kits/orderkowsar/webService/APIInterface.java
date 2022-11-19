package com.kits.orderkowsar.webService;

import com.kits.orderkowsar.model.RetrofitResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIInterface {


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetImageFromKsr(@Field("tag") String tag,
                                           @Field("KsrImageCode") String KsrImageCode
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetImageCustom(@Field("tag") String tag,
                                          @Field("ClassName") String ClassName,
                                          @Field("ObjectRef") String ObjectRef,
                                          @Field("Scale") String Scale
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> Activation(@Field("tag") String tag,
                                      @Field("ActivationCode") String ActivationCode
    );


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetBasketFromTable(@Field("tag") String tag,
                                              @Field("AppBasketInfoCode") String AppBasketInfoCode
    );

    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> info(@Field("tag") String tag,
                                @Field("Where") String Where);


    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> Getgrp(@Field("tag") String tag,
                                  @Field("GroupCode") String GroupCode);



    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> VersionInfo(@Field("tag") String tag);



    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetRstMiz(@Field("tag") String tag);



    @POST("index.php")
    @FormUrlEncoded
    Call<RetrofitResponse> GetGoodFromGroup(@Field("tag") String tag,
                                            @Field("GroupCode") String GroupCode);







    @POST("index.php")
    @FormUrlEncoded
    Call <RetrofitResponse> GetImage(@Field("tag") String tag,
                                     @Field("ObjectRef") String ObjectRef,
                                     @Field("ClassName") String ClassName,
                                     @Field("IX") String IX,
                                     @Field("Scale") String Scale);












}

