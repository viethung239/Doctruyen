package com.example.truyentranh.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyentranh.ComicListAdminActivity;
import com.example.truyentranh.filters.FilterCategory;
import com.example.truyentranh.model.ModelCategory;
import com.example.truyentranh.databinding.RowCatrgoryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.HolderCategory> implements Filterable {

    private Context context;
    public ArrayList<ModelCategory> categoryArrayList, filterList;

    //view binding
    private RowCatrgoryBinding binding;
    private FilterCategory filter;

    public AdapterCategory(Context context, ArrayList<ModelCategory> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
        this.filterList = categoryArrayList;
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //binding row_category
        binding = RowCatrgoryBinding.inflate(LayoutInflater.from(context), parent,false);
        return new HolderCategory(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {
        //lấy dữ liệu
        ModelCategory model = categoryArrayList.get(position);
        String id = model.getId();
        String category = model.getCategory();
        String uid = model.getUid();
        long timestamp = model.getTimestamp();


        holder.categoryTv.setText(category);

        //xóa danh mục
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //confirm delete dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xóa")
                        .setMessage("Bạn chắc chắn muốn xóa danh mục sản phẩm này?")
                        .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int Which) {
                                Toast.makeText(context, "Đang xóa...", Toast.LENGTH_SHORT).show();
                                deleteCategory(model, holder);
                            }
                        })
                        .setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int Which) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        // sự kiện nhấn nút,
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ComicListAdminActivity.class);
                intent.putExtra("categoryID", id);
                intent.putExtra("categoryTitle", category);
                context.startActivity(intent);
            }
        });
    }

    private void deleteCategory(ModelCategory model, HolderCategory holder) {
        //Xóa theo id của danh mục
        String id =model.getId();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categorys");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Xóa thành công...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterCategory(filterList, this);
        }
        return filter;
    }


    class HolderCategory extends RecyclerView.ViewHolder{


        TextView categoryTv;
        ImageButton deleteBtn;

        public HolderCategory(@NonNull View itemView) {
            super(itemView);


            categoryTv = binding.categoryTv;
            deleteBtn = binding.deleteBtn;
        }
    }
}
