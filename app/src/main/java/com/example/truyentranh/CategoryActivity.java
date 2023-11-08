package com.example.truyentranh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.truyentranh.databinding.ActivityCategoryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CategoryActivity extends AppCompatActivity {

    //  Ràng buộc
    private ActivityCategoryBinding binding;

    // xác thực tài khoản firebase
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Khởi tạo xác thực
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng chờ...");
        progressDialog.setCanceledOnTouchOutside(false);

        // Nút trở lại
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Tải lại danh mục
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
                    public  void onClick(View v) {
                validateData();
            }
        });
    }

    private  String category = "";
    private void validateData() {

        //Lấy dữ liệu
        category = binding.categoryEt.getText().toString().trim();
        if (TextUtils.isEmpty(category)){
            Toast.makeText(this, "Nhập danh mục...!", Toast.LENGTH_SHORT).show();
        }
        else {
            addCategoryFirebase();
        }
    }

    private void addCategoryFirebase() {
        progressDialog.setMessage("Đang thêm danh mục...");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();
        //Thêm thông tin vào Firebase
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", ""+timestamp);
        hashMap.put("category", ""+category);
        hashMap.put("timestamp", timestamp);
        hashMap.put("uid", ""+firebaseAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categorys");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        progressDialog.dismiss();
                        Toast.makeText(CategoryActivity.this,"Thêm danh mục thành công...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.dismiss();
                        Toast.makeText(CategoryActivity.this, "Lỗi thêm danh mục.."+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}