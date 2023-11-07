package com.example.truyentranh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.truyentranh.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDiaLog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //khởi tạo xác thực firebase
        firebaseAuth = firebaseAuth.getInstance();

        //setup progress dialog

        progressDiaLog = new ProgressDialog(this);
        progressDiaLog.setTitle("Vui lòng đợi một chút");
        progressDiaLog.setCanceledOnTouchOutside(false);

        //sự kiện, đến màn hình đăng kí tài khoản


        binding.noAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        //xử lý sự kiện nhấn nút đăng nhập
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }


    private String email = "", password ="";
    private void validateData() {

        //lấy dữ liệu
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();

        //Kiem tra dữ liệu
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Emai không đúng...!", Toast.LENGTH_SHORT).show();
        }
        else  if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Nhập mật khẩu của bạn...!", Toast.LENGTH_SHORT).show();
        }
        else {
            //data is validated, begin login
            LoginUser();
        }
    }

    private void LoginUser() {

        //Hiện thị tiến trình đăng nhập
        progressDiaLog.setMessage("Đang đăng nhập...");
        progressDiaLog.show();

        //Đăng nhập vào người dùng
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //Đăng nhập thành công, kiểm tra xem người dùng có phải admin hay không
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Đăng nhập thất bại
                        progressDiaLog.dismiss();
                        Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        progressDiaLog.setMessage("Kiểm tra người dùng....");
        //Đăng nhập thành công, kiểm tra xem người dùng có phải admin từ db

        //lấy được người dùng
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        //kiểm tra trong db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        progressDiaLog.dismiss();
                        // lấy kiểu người dùng
                        String userType = (String) snapshot.child("userType").getValue();

                        //check user type
                        if(userType.equals("user")){
                            //Đây chỉ là người dùng bình thường, mở trang chủ người dùng
                            startActivity(new Intent(LoginActivity.this,DashboardUserActivity.class));
                            finish();
                        }
                        else if(userType.equals("admin")){
                            // Đây là tài khoản admin, mở trang chủ amdin
                            startActivity(new Intent(LoginActivity.this,DashboardAdminActivity.class));
                            finish();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}