package com.example.truyentranh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;

import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.truyentranh.Api.ApiGetComic;
import com.example.truyentranh.Interfaces.GetComic;
import com.example.truyentranh.adapter.ComicAdapter;
import com.example.truyentranh.object.Comic;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //chay man hinh chuong trinh sau 2 giay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //chay man hinh
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 2000);


    }








}