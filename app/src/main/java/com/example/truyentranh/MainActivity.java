package com.example.truyentranh;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.GridView;

import com.example.truyentranh.adapter.ComicAdapter;
import com.example.truyentranh.object.Comic;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
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
    }
    private void init(){
        ComicArrList = new ArrayList<>();
        ComicArrList.add(new Comic("Đại Phản Diện","Chương 78","https://nettruyenco.vn/public/images/comics/dai-phan-dien.jpg"));
        ComicArrList.add(new Comic("Bất Lộ Thanh Sắc","Chap 70","https://nettruyenco.vn/public/images/comics/bat-lo-thanh-sac.jpg"));
        ComicArrList.add(new Comic("Linh Khư","Chapter 76","https://nettruyenco.vn/public/images/comics/linh-khu.jpg"));
        ComicArrList.add(new Comic("Long Hưởng Thiên Hạ","Chương 5","https://nettruyenco.vn/public/images/comics/long-huong-thien-ha.jpg"));
        ComicArrList.add(new Comic("Đại Phản Diện","Chương 78","https://nettruyenco.vn/public/images/comics/dai-phan-dien.jpg"));
        ComicArrList.add(new Comic("Bất Lộ Thanh Sắc","Chap 70","https://nettruyenco.vn/public/images/comics/bat-lo-thanh-sac.jpg"));
        ComicArrList.add(new Comic("Linh Khư","Chapter 76","https://nettruyenco.vn/public/images/comics/linh-khu.jpg"));
        ComicArrList.add(new Comic("Long Hưởng Thiên Hạ","Chương 5","https://nettruyenco.vn/public/images/comics/long-huong-thien-ha.jpg"));
        ComicArrList.add(new Comic("Đại Phản Diện","Chương 78","https://nettruyenco.vn/public/images/comics/dai-phan-dien.jpg"));
        ComicArrList.add(new Comic("Bất Lộ Thanh Sắc","Chap 70","https://nettruyenco.vn/public/images/comics/bat-lo-thanh-sac.jpg"));
        ComicArrList.add(new Comic("Linh Khư","Chapter 76","https://nettruyenco.vn/public/images/comics/linh-khu.jpg"));
        ComicArrList.add(new Comic("Long Hưởng Thiên Hạ","Chương 5","https://nettruyenco.vn/public/images/comics/long-huong-thien-ha.jpg"));

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
}