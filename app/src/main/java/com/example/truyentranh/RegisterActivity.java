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

import com.example.truyentranh.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {


    private ActivityRegisterBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress dialog

    private ProgressDialog progressDiaLog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //khởi tạo người dùng
        firebaseAuth = FirebaseAuth.getInstance();


        //setup progress dialog

        progressDiaLog = new ProgressDialog(this);
        progressDiaLog.setTitle("Vui lòng đợi một chút");
        progressDiaLog.setCanceledOnTouchOutside(false);
        // sự kiện trờ lại khi nhấn

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //xử lý sự kiện nhấn nút đăng kí

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });




    }

    private  String name ="", email ="", password ="";
    private void validateData() {

        //lấy dữ liệu
        name = binding.nameEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();

        String cPassword = binding.cPasswordEt.getText().toString().trim();

        //
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Tên của bạn...", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Emai không đúng...!", Toast.LENGTH_SHORT).show();
        }
        else  if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Nhập mật khẩu của bạn...!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(cPassword)){
           Toast.makeText(this, "Nhập lại mật khẩu...! ", Toast.LENGTH_SHORT).show(); 
        }
        else  if (!password.equals(cPassword)){
            Toast.makeText(this, "Mật khẩu không giống nhau...!", Toast.LENGTH_SHORT).show();
        }
        else {
            // dữ liệu đã đúng, chuẩn bị tạo tài khoản
            createUserAccount();
        }
    }

    private void createUserAccount() {
        //hiển thị tiến trình

        progressDiaLog.setMessage("Đang tạo tài khoản..");
        progressDiaLog.show();


        //tạo tài người dùng trong database

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //tạo tài khoản thành công,  thêm cơ sở dữ liệu vào firebase
                       updateUserInfo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //tạo tài khoản không thành công
                        progressDiaLog.dismiss();

                        Toast.makeText(RegisterActivity.this, "" +e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserInfo() {

        progressDiaLog.setTitle("Lưu thông tin người dùng...");

        //Timestamp
        long timestamp = System.currentTimeMillis();
        //lấy  uid người dùng
        String uid = firebaseAuth.getUid();

        //thiết lập dữ liệu để thêm vào firebase

        HashMap< String, String>  hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("name", name);
        hashMap.put("profileImage",""); //add emtty, will do later
        hashMap.put("userType", "user");
        String timestampString = Long.toString(timestamp);
        hashMap.put("timestamp", timestampString);

        //set data to db

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //thêm dữ liệu thành công vào db

                        progressDiaLog.dismiss();
                        Toast.makeText(RegisterActivity.this,"Tài Khoản đã được tạo...", Toast.LENGTH_SHORT).show();
                        //since user account is created so start dashboard of user
                        startActivity(new Intent(RegisterActivity.this,DashboardUserActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure( Exception e) {
                        //Thêm dữ liệu thất bại
                        progressDiaLog.dismiss();
                        Toast.makeText(RegisterActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}