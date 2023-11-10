package com.example.truyentranh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.truyentranh.adapters.AdapterComicAdmin;
import com.example.truyentranh.databinding.ActivityComicListAdminBinding;
import com.example.truyentranh.model.ModelComic;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ComicListAdminActivity extends AppCompatActivity {


    private ActivityComicListAdminBinding binding;
    private ArrayList<ModelComic> comicArrayList;

    private AdapterComicAdmin adapterComicAdmin;
    private String categoryId, categoryTitle ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComicListAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // lấy dữ liệu từ intent
        Intent intent = getIntent();
        categoryId   = intent.getStringExtra("categoryId");
        categoryTitle = intent.getStringExtra("categoryTitle");

        //set pdf category
        binding.subtitleTv.setText(categoryTitle);

        loadPdfList();
        // Tìm kiếm
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterComicAdmin.getFilter().filter(s);
                }catch (Exception e)
                    {

                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // sự kiện nhấn nút
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadPdfList() {

        comicArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comics");

        ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        comicArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            //lấy dữ liệu
                            ModelComic model = ds.getValue(ModelComic.class);
                            comicArrayList.add(model);
                        }

                        adapterComicAdmin = new AdapterComicAdmin(ComicListAdminActivity.this,comicArrayList);
                        binding.ComicRv.setAdapter(adapterComicAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}