package com.example.truyentranh.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.truyentranh.MyApplication;
import com.example.truyentranh.R;
import com.example.truyentranh.adapters.AdapterPdfFavorite;
import com.example.truyentranh.databinding.ActivityProfileBinding;
import com.example.truyentranh.model.ModelComic;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    //Rang buoc
    private ActivityProfileBinding binding;

    //tai len du lieu nguoi dung
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelComic> comicArrayList;
    private AdapterPdfFavorite adapterPdfFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        firebaseAuth = FirebaseAuth.getInstance();
        loadUserInfo();
        loadFavoriteComics();

        //chinh sua trang ho so
        binding.profileEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class));
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private  void loadUserInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //lay tat ca thong tin cua user
                        String email = ""+snapshot.child("email").getValue();
                        String name = ""+snapshot.child("name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String uid = ""+snapshot.child("uid").getValue();
                        String userType = ""+snapshot.child("userType").getValue();

                        //format date to dd/MM/yyyy
                        String formattedDate = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        //dat du lieu thanh uid
                        binding.emailTv.setText(email);
                        binding.nameTv.setText(name);
                        binding.memberDateTv.setText(formattedDate);
                        binding.accountTypeTv.setText(userType);

                        //set image, using glide
                        Glide.with(ProfileActivity.this)
                                .load(profileImage)
                                .placeholder(R.drawable.baseline_person_2_24)
                                .into(binding.prodileIv);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private  void loadFavoriteComics(){
        comicArrayList = new ArrayList<>();

        //tai truyen tranh yeu thich len tu db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //xóa danh sách trước khi bắt đầu thêm dữ liệu
                        comicArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){

                            String comicId = ""+ds.child("id").getValue();

                            ModelComic modelComic = new ModelComic();
                            modelComic.setId(comicId);

                            comicArrayList.add(modelComic);
                        }

                        binding.favoriteComicCountTv.setText(""+comicArrayList.size());
                        adapterPdfFavorite = new AdapterPdfFavorite(ProfileActivity.this, comicArrayList);
                        binding.ComicsRv.setAdapter(adapterPdfFavorite);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}