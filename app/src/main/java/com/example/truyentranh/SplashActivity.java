package com.example.truyentranh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SplashActivity extends AppCompatActivity {

    //firebase auth

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //init  firebase auth
        firebaseAuth = FirebaseAuth.getInstance();


        //chay man hinh chuong trinh sau 2 giay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                checkUser();
            }
        }, 2000);


    }

    private void checkUser() {
        //lấy người dùng hiện tại, đăng nhập vào

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null){

            startActivity(new Intent(SplashActivity.this,MainActivity.class));
            finish();
        }
        else {
            //Đăng nhập thành công, kiểm tra kiểu người dùng admin hay người bình thường

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange( DataSnapshot snapshot) {


                            // lấy kiểu người dùng
                            String userType = ""+snapshot.child("userType").getValue();

                            //check user type
                            if(userType.equals("user")){
                                //Đây chỉ là người dùng bình thường, mở trang chủ người dùng
                                startActivity(new Intent(SplashActivity.this,DashboardUserActivity.class));
                                finish();
                            }
                            else if(userType.equals("admin")){
                                // Đây là tài khoản admin, mở trang chủ amdin
                                startActivity(new Intent(SplashActivity.this,DashboardAdminActivity.class));
                                finish();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }


}