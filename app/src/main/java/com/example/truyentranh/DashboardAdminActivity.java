package com.example.truyentranh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.truyentranh.databinding.ActivityDashboardAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardAdminActivity extends AppCompatActivity {


    private ActivityDashboardAdminBinding binding;
    //xác thực tài khoản firebase
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //khởi tạo xác thực firebase
        firebaseAuth = firebaseAuth.getInstance();
        checkUser();

        //sự kiện nhấn đăng xuất
        binding.LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });
    }

    private void checkUser() {
        //Lấy dữ liệu người dùng
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser== null){
            //Chưa đăng nhập, vào màn hình chính
            startActivity(new Intent(this,MainActivity.class));
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