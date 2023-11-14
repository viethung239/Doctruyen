package com.example.truyentranh.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.truyentranh.databinding.ActivityComicEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ComicEditActivity extends AppCompatActivity {

    private ActivityComicEditBinding binding;


    private ProgressDialog progressDialog;
    private ArrayList<String> categoryTitleArrayList, categoryIdArrayList;
    private String comicId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComicEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        comicId = getIntent().getStringExtra("comicId");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng chờ một chút");
        progressDialog.setCanceledOnTouchOutside(false);

        loadCategories();
        loadComicInfo();

        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDiaLog();
            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Nhấn nút Cập nhật
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 validateData();
            }
        });
    }




    private void loadComicInfo() {


        DatabaseReference refComics = FirebaseDatabase.getInstance().getReference("Comics");
        refComics.child(comicId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // Lấy thông tin truyện
                        selectedCategoryId = ""+snapshot.child("categoryId").getValue();
                        String description = ""+snapshot.child("description").getValue();
                        String title = ""+snapshot.child("title").getValue();
                        // hiện thị với giao diện

                        binding.titleEt.setText(title);
                        binding.descriptionEt.setText(description);


                        DatabaseReference  refComicCategory = FirebaseDatabase.getInstance().getReference("Categorys");
                        refComicCategory.child(selectedCategoryId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        // Lấy danh mục
                                        String category = ""+snapshot.child("category").getValue();

                                        // hiện thị ra giao diện
                                        binding.categoryTv.setText(category);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String title="", description ="";
    private void validateData() {

        //lấy dữ liệu

        title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();


        if(TextUtils.isEmpty(title)){
            Toast.makeText(this, "Nhập tên...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Nhập mô tả truyện...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(selectedCategoryId)){
            Toast.makeText(this, "Chọn danh mục ...",Toast.LENGTH_SHORT).show();
        }
        else {
            updatePdf();
        }
    }

    private void updatePdf() {

        progressDialog.setMessage("Đang cập nhật thông tin truyện");
        progressDialog.show();

        //chuẩn bị dữ liệu
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("title",""+title);
        hashMap.put("description",""+description);
        hashMap.put("categoryId",""+selectedCategoryId);

        //bắt đầu cập nhaajt

        DatabaseReference ref  = FirebaseDatabase.getInstance().getReference("Comics");
        ref.child(comicId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(ComicEditActivity.this,"Đã cập nhật",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ComicEditActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private String selectedCategoryId ="", selectedCategoryTitle="";
    private  void categoryDiaLog(){
        //make string array from arraylist of string

        String[] categoriesArray = new  String[categoryTitleArrayList.size()];
        for (int i=0; i<categoryTitleArrayList.size();i++){
            categoriesArray[i] = categoryTitleArrayList.get(i);

        }
        //Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn Danh Mục")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedCategoryId = categoryIdArrayList.get(which);
                        selectedCategoryTitle = categoryTitleArrayList.get(which);

                        binding.categoryTv.setText(selectedCategoryTitle);
                    }
                })
                .show();
    }
    private void loadCategories() {


        categoryIdArrayList = new ArrayList<>();
        categoryTitleArrayList = new ArrayList<>();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categorys");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryIdArrayList.clear();
                categoryTitleArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){

                    String id ="" + ds.child("id").getValue();
                    String category ="" +ds.child("category").getValue();
                    categoryIdArrayList.add(id);
                    categoryTitleArrayList.add(category);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}