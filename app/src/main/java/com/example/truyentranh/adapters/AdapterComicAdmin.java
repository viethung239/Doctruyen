package com.example.truyentranh.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyentranh.activities.ComicDetailActivity;
import com.example.truyentranh.activities.ComicEditActivity;
import com.example.truyentranh.MyApplication;
import com.example.truyentranh.databinding.RowComicAdminBinding;
import com.example.truyentranh.filters.FilterComicAdmin;
import com.example.truyentranh.model.ModelComic;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterComicAdmin extends RecyclerView.Adapter<AdapterComicAdmin.HolderComicAdmin> implements Filterable {

    //context
    private Context context;


    public ArrayList<ModelComic> comicArrayList, fillterList;



    private RowComicAdminBinding binding;

    private FilterComicAdmin filter;

    //progress
    private ProgressDialog progressDialog;


    //
    public AdapterComicAdmin(Context context, ArrayList<ModelComic> comicArrayList) {
        this.context = context;
        this.comicArrayList = comicArrayList;
        this.fillterList = comicArrayList;


        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Vui lòng đợi");
        progressDialog.setCanceledOnTouchOutside(false);
    }




    private void moreOptionsDiaLog(ModelComic model, HolderComicAdmin holder) {

        String comicId = model.getId();
        String comicUrl = model.getUrl();
        String comicTitle = model.getTitle();


        //Lựa chọn hiển thị xoá hoặc sửa
        String[] options = {"Sửa", "Xoá"};

        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        builder.setTitle("Chọn")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            // hiển thị lựa chọn
                        if(which ==0)
                        {
                            //Chọn sửa

                            Intent intent = new Intent(context, ComicEditActivity.class);
                            intent.putExtra("comicId", comicId);
                            context.startActivity(intent);

                        }
                        else if(which==1)
                        {
                            //Chọn xoá
                            MyApplication.deleteComic(context, ""+comicId,""+comicUrl,""+comicTitle);

                            //delteComic(model, holder);
                        }
                    }
                })
                .show();


    }




    @NonNull
    @Override
    public HolderComicAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = RowComicAdminBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderComicAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull  AdapterComicAdmin.HolderComicAdmin holder, int position) {



        //Lấy Dữ Liệu
        ModelComic model = comicArrayList.get(position);
        String pdfId = model.getId();
        String categoryId = model.getCategoryId();
        String title = model.getTitle();
        String description = model.getDescription();
        Long timestamp = model.getTimestamp();

        String pdfUrl = model.getUrl();
        String formatedDate = MyApplication.formatTimestamp(timestamp);

        //set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formatedDate);


        MyApplication.loadCategory(""+categoryId,holder.categoryTv);

        MyApplication.loadPdfFromUrlSinglePage(""+pdfUrl,""+title,holder.pdfView,holder.progressBar,null);
        MyApplication.loadPdfSize(""+pdfUrl,""+title,holder.sizeTv);


        //sự kiện nhấn nút, hiển thị lựa chọn xoá hoặc chỉnh sửa
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreOptionsDiaLog(model, holder);
            }
        });

        // su kien nhan nut mo chi tiet truyen
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ComicDetailActivity.class);
                intent.putExtra("comicId",pdfId);
                context.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return comicArrayList.size();
    }
    class HolderComicAdmin extends RecyclerView.ViewHolder{


        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv, descriptionTv,categoryTv,sizeTv,dateTv;
        ImageButton moreBtn;
        public HolderComicAdmin(@NonNull View itemView) {
            super(itemView);
            //init ui views

            pdfView = binding.comicView;
            progressBar = binding.progressBar;
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
            categoryTv = binding.categoryTv;
            sizeTv = binding.sizeTv;
            dateTv = binding.dateTv;
            moreBtn = binding.moreBtn;


        }
    }

    @Override
    public Filter getFilter() {
        if(filter == null)
        {
            filter = new FilterComicAdmin(fillterList,this);
        }
        return filter;
    }


}
