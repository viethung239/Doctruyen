package com.example.truyentranh;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements GetComic {
GridView gdvListComic;
ComicAdapter adapter;
ArrayList<Comic> ComicArrList;
EditText SearchComic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        anhXa();
        setUp();
        setClick();
        new ApiGetComic(this).execute();

    }
    private void init(){
        ComicArrList = new ArrayList<>();


        adapter = new ComicAdapter(this,0,ComicArrList);
    }
    private void anhXa(){
        gdvListComic = findViewById(R.id.gdvListComic);
        SearchComic = findViewById(R.id.SearchComic);
    }
    private void setUp(){
        gdvListComic.setAdapter(adapter);
    }
    private void setClick(){
        SearchComic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = SearchComic .getText().toString();
                adapter.sortComic(s);

            }
        });
    }

    @Override
    public void start() {
        Toast.makeText(this,"Đang Lấy Về",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void end(String data) {
        try {
            ComicArrList.clear();
            JSONArray arr = new JSONArray(data);
            for (int i=0;i<arr.length();i++){
                JSONObject o =arr.getJSONObject(i);
                ComicArrList.add(new Comic(o));
            }
            adapter = new ComicAdapter(this,0,ComicArrList);
            gdvListComic.setAdapter(adapter);
        }catch (JSONException e){

        }
    }

    @Override
    public void error() {
        Toast.makeText(this,"Không Lấy Về Được",Toast.LENGTH_SHORT).show();
    }
}