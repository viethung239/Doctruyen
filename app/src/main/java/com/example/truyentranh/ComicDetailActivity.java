package com.example.truyentranh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.truyentranh.databinding.ActivityComicDetailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ComicDetailActivity extends AppCompatActivity {




    private ActivityComicDetailBinding binding;

    String comicId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComicDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        comicId = intent.getStringExtra("comicId");

        loadComicDetail();
        MyApplication.incrementComicViewCount(comicId);
        // su kien nhan nut tro lai
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //sự kiện nhấn nút đọc
        binding.readComicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ComicDetailActivity.this,ComicViewActivity.class );

                intent1.putExtra("comicId",comicId);
                startActivity(intent1);
            }
        });

    }

    private void loadComicDetail() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comics");
        ref.child(comicId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String title = ""+snapshot.child("title").getValue();
                        String description = ""+snapshot.child("description").getValue();
                        String categoryId = ""+snapshot.child("categoryId").getValue();
                        String viewsCount = ""+snapshot.child("viewsCount").getValue();
                        String downloadsCount = ""+snapshot.child("downloadsCount").getValue();
                        String url =""+snapshot.child("url").getValue();
                        String timestamp =""+snapshot.child("timestamp").getValue();

                        String date = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        MyApplication.loadCategory(""+categoryId,binding.categoryTv);
                        MyApplication.loadPdfFromUrlSinglePage(""+url,""+title,binding.pdfView,binding.progressBar);
                        MyApplication.loadPdfSize(""+url,""+title,binding.sizeTv);


                        //set data
                        binding.titleTv.setText(title);
                        binding.descriptionTv.setText(description);
                        binding.viewTv.setText(viewsCount.replace("null","N/A"));
                        binding.downloadsTv.setText(downloadsCount.replace("null","N/A"));
                        binding.dateTv.setText(date);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}