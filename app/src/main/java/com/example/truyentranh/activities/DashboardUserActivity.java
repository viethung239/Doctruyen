package com.example.truyentranh.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.truyentranh.ComicUserFragment;
import com.example.truyentranh.databinding.ActivityDashboardUserBinding;
import com.example.truyentranh.model.ModelCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardUserActivity extends AppCompatActivity {


    //hieenr thi danh muc tren tabs
    public ArrayList<ModelCategory> categoryArrayList;

    public ViewPagerAdapter viewPagerAdapter;

    private ActivityDashboardUserBinding binding;
    //xác thực tài khoản firebase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //khởi tạo xác thực firebase
        firebaseAuth = firebaseAuth.getInstance();
        checkUser();

        setupViewPagerAdapter(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        //sự kiện nhấn đăng xuất
        binding.LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(DashboardUserActivity.this,MainActivity.class));
                finish();


            }
        });

        // mo profile
        binding.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardUserActivity.this, ProfileActivity.class));
            }
        });
    }


    private  void  setupViewPagerAdapter(ViewPager viewPager){

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,this);


        categoryArrayList = new ArrayList<>();

        // load danh muc tu firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categorys");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                categoryArrayList.clear();

                //Thêm dữ liệu vào model
                ModelCategory modelAll = new ModelCategory("01","Tất Cả","",1);
                ModelCategory modelMostView = new ModelCategory("02","Xem Nhiều Nhất","",1);

                //thêm model vào dnah sách
                categoryArrayList.add(modelAll);
                categoryArrayList.add(modelMostView);


                viewPagerAdapter.addFragment(ComicUserFragment.newInstance(
                        ""+modelAll.getId(),
                        ""+modelAll.getCategory(),
                        ""+modelAll.getUid()
                ), modelAll.getCategory());

                viewPagerAdapter.addFragment(ComicUserFragment.newInstance(
                        ""+modelMostView.getId(),
                        ""+modelMostView.getCategory(),
                        ""+modelMostView.getUid()
                ), modelMostView.getCategory());


                viewPagerAdapter.notifyDataSetChanged();

                //Load from firebase

                for( DataSnapshot ds: snapshot.getChildren()){
                    //lấy dữ liệu
                    ModelCategory model = ds.getValue(ModelCategory.class);
                    // thêm dữ liệu vảo mảng
                    categoryArrayList.add(model);
                    // thêm dữ liệu vào pager viewPageradpater

                    viewPagerAdapter.addFragment(ComicUserFragment.newInstance(
                            ""+model.getId(),
                            ""+model.getCategory(),
                            ""+model.getUid()), model.getCategory());

                    //refresh list
                    viewPagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        viewPager.setAdapter(viewPagerAdapter);

    }

    public  class  ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<ComicUserFragment> fragmentsList = new ArrayList<>();

        private  ArrayList<String> fragmentTitleList = new ArrayList<>();
        private Context context ;
        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior, Context context) {
            super(fm, behavior);
            this.context = context;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentsList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentsList.size();
        }

        private void addFragment (ComicUserFragment fragment, String title){

            fragmentsList.add(fragment);

            fragmentTitleList.add(title);
        }
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }


    private void checkUser() {
        //Lấy dữ liệu người dùng
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser== null){
            //Chưa đăng nhập, vào màn hình chính
            binding.subtitleTv.setText("Không tài khoản");
        }
        else {
            //đã đăng nhập, lấy thông tin người dùng

            String email = firebaseUser.getEmail();
            //set in textview of toolbar
            binding.subtitleTv.setText(email);
        }
    }
}