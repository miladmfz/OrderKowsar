package com.kits.orderkowsar.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.kits.orderkowsar.databinding.ActivityAboutusBinding;
import com.kits.orderkowsar.model.NumberFunctions;


public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAboutusBinding binding = ActivityAboutusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.tv1.setText(NumberFunctions.PerisanNumber(binding.tv1.getText().toString()));
        binding.tv2.setText(NumberFunctions.PerisanNumber(binding.tv2.getText().toString()));
        binding.tv3.setText(NumberFunctions.PerisanNumber(binding.tv3.getText().toString()));



    }


}
