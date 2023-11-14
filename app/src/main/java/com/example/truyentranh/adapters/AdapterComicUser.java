package com.example.truyentranh.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyentranh.MyApplication;
import com.example.truyentranh.activities.ComicDetailActivity;
import com.example.truyentranh.databinding.RowComicUserBinding;
import com.example.truyentranh.filters.FilterComicUser;
import com.example.truyentranh.model.ModelComic;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterComicUser extends RecyclerView.Adapter<AdapterComicUser.HolderComicUser>  implements Filterable {

    private Context context;
    public ArrayList<ModelComic> comicArrayList, filterList;

    private FilterComicUser filter;
    private RowComicUserBinding binding;


    public AdapterComicUser(Context context, ArrayList<ModelComic> comicArrayList) {
        this.context = context;
        this.comicArrayList = comicArrayList;
        this.filterList = comicArrayList;
    }

    @NonNull
    @Override
    public HolderComicUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        binding = RowComicUserBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderComicUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderComicUser holder, int position) {
        // get data, set data, handle click

        //lấy dữ liệu
        ModelComic model = comicArrayList.get(position);
        String comicId = model.getId();
        String title = model.getTitle();
        String desciption = model.getDescription();
        String pdfUrl = model.getUrl();
        String  categoryId = model.getCategoryId();
        long timestamp = model.getTimestamp();

        // convert time
        String date = MyApplication.formatTimestamp(timestamp);

        //set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(desciption);
        holder.dateTv.setText(date);

        MyApplication.loadPdfFromUrlSinglePage(""+pdfUrl,""+title,holder.pdfView,holder.progressBar);

        MyApplication.loadCategory(""+categoryId, holder.categoryTv);
        MyApplication.loadPdfSize(""+pdfUrl,""+title,holder.sizeTv);


        // sự kiện nhất nút hiện chi tiết truyện

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ComicDetailActivity.class);
                intent.putExtra("comicId",comicId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comicArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter== null){
            filter = new FilterComicUser(filterList, this);
        }
        return filter;
    }

    class HolderComicUser extends RecyclerView.ViewHolder{

        TextView titleTv, descriptionTv, categoryTv, sizeTv, dateTv;
        PDFView pdfView;
        ProgressBar progressBar;

        public HolderComicUser(@NonNull View itemView) {
            super(itemView);
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
            categoryTv = binding.categoryTv;
            sizeTv = binding.sizeTv;
            dateTv = binding.dateTv;
            pdfView = binding.comicView;
            progressBar = binding.progressBar;


        }
    }
}
