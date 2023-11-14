package com.example.truyentranh;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.truyentranh.adapters.AdapterComicUser;
import com.example.truyentranh.databinding.FragmentComicUserBinding;
import com.example.truyentranh.model.ModelComic;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComicUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComicUserFragment extends Fragment {

    private String categoryId;
    private String category;
    private  String uid;

    private ArrayList<ModelComic> comicArrayList;

    private AdapterComicUser adapterComicUser;

    // view binding
    private FragmentComicUserBinding binding;
    public ComicUserFragment() {
        // Required empty public constructor
    }


    public static ComicUserFragment newInstance(String categoryId, String category, String uid) {
        ComicUserFragment fragment = new ComicUserFragment();
        Bundle args = new Bundle();
        args.putString("categoryId", categoryId);
        args.putString("category", category);
        args.putString("uid", uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
            category = getArguments().getString("category");
            uid = getArguments().getString("uid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = FragmentComicUserBinding.inflate(LayoutInflater.from(getContext()),container,false);


        if(category.equals("Tất Cả")){

            loadAllComics();
        }
        else if(category.equals("Xem Nhiều Nhất")){

            loadMostViewed("viewsCount");
        }
        else {

            loadCategorizedComics();
        }

        //tìm kiếm
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                try {
                    adapterComicUser.getFilter().filter(s);
                }
                catch (Exception e){

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return binding.getRoot();
    }

    private void loadAllComics() {
        comicArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comics");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                comicArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelComic model = ds.getValue(ModelComic.class);

                    comicArrayList.add(model);
                }
                adapterComicUser = new AdapterComicUser(getContext(), comicArrayList);

                binding.ComicRv.setAdapter(adapterComicUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadCategorizedComics() {

        comicArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comics");
        ref.orderByChild("categoryId").equalTo(categoryId).

                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        comicArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelComic model = ds.getValue(ModelComic.class);

                            comicArrayList.add(model);
                        }
                        adapterComicUser = new AdapterComicUser(getContext(), comicArrayList);

                        binding.ComicRv.setAdapter(adapterComicUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadMostViewed(String orderBy) {
        comicArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comics");
        ref.orderByChild(orderBy).limitToLast(10).

                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                comicArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelComic model = ds.getValue(ModelComic.class);

                    comicArrayList.add(model);
                }
                adapterComicUser = new AdapterComicUser(getContext(), comicArrayList);

                binding.ComicRv.setAdapter(adapterComicUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}