package com.example.truyentranh.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.truyentranh.adapters.AdapterCategory;
import com.example.truyentranh.databinding.ActivityDashboardAdminBinding;
import com.example.truyentranh.model.ModelCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardAdminActivity extends AppCompatActivity {


    private ActivityDashboardAdminBinding binding;
    //xác thực tài khoản firebase
    private FirebaseAuth firebaseAuth;
    //Danh sách mảng để lưu trữ danh mục
    private ArrayList<ModelCategory> categoryArrayList;
    //adapter
    private AdapterCategory adapterCategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //khởi tạo xác thực firebase
        firebaseAuth = firebaseAuth.getInstance();
        checkUser();
        loadCategories();

        //Chỉnh sửa và tìm kiếm
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterCategory.getFilter().filter(s);
                }
                catch (Exception e){

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //sự kiện nhấn đăng xuất
        binding.LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        // Nút thêm danh mục
        binding.addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardAdminActivity.this, CategoryActivity.class));
            }
        });

        //Sự kiến nhấn nút thêm truyện file pdf
        binding.addPdfFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardAdminActivity.this, PdfAddActivity.class));
            }
        });

    }

    private void loadCategories() {
        //Khởi tạo mảng
        categoryArrayList = new ArrayList<>();
        // lấy tất cả danh mục từ firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categorys");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //xóa danh sách mảng trước khi thêm dữ liệu vào
                categoryArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    //Lấy dữu liệu
                    ModelCategory model = ds.getValue(ModelCategory.class);

                    //Thêm vào mảng
                    categoryArrayList.add(model);
                }

                adapterCategory = new AdapterCategory(DashboardAdminActivity.this, categoryArrayList);
                binding.categoriesRv.setAdapter(adapterCategory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkUser() {
        //Lấy dữ liệu người dùng
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser== null){
            //Chưa đăng nhập, vào màn hình chính
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else {
            //đã đăng nhập, lấy thông tin người dùng

            String email = firebaseUser.getEmail();
            //set in textview of toolbar
            binding.subtitleTv.setText(email);
        }
    }
}