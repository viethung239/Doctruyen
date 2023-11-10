package com.example.truyentranh.adapters;

import static com.example.truyentranh.Contrants.MAX_BYTES_PDF;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.truyentranh.MyApplication;
import com.example.truyentranh.databinding.RowComicAdminBinding;
import com.example.truyentranh.filters.FilterComicAdmin;
import com.example.truyentranh.model.ModelComic;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterComicAdmin extends RecyclerView.Adapter<AdapterComicAdmin.HolderComicAdmin> implements Filterable {

    //context
    private Context context;


    public ArrayList<ModelComic> comicArrayList, fillterList;



    private RowComicAdminBinding binding;

    private FilterComicAdmin filter;

    //
    public AdapterComicAdmin(Context context, ArrayList<ModelComic> comicArrayList) {
        this.context = context;
        this.comicArrayList = comicArrayList;
        this.fillterList = comicArrayList;
    }

    @NonNull
    @Override
    public HolderComicAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        binding = RowComicAdminBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderComicAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderComicAdmin holder, int position) {



        //Lấy Dữ Liệu
        ModelComic model = comicArrayList.get(position);
        String title = model.getTitle();
        String description = model.getDescription();
        Long timestamp = model.getTimestamp();

        String formatedDate = MyApplication.formatTimestamp(timestamp);

        //set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formatedDate);


        loadCategory(model,holder);
        loadPdfFromUrl(model,holder);
        loadPdfSize(model,holder);

    }

    private void loadPdfSize(ModelComic model, HolderComicAdmin holder) {



        String pdfUrl = model.getUrl();
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);

        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {

                        double bytes = storageMetadata.getSizeBytes();


                        double kb = bytes/1024;
                        double mb = kb/1024;

                        if (mb > 1){
                            holder.sizeTv.setText(String.format("%.2f",mb)+ "MB");

                        }
                        else  if (kb >= 1){
                            holder.sizeTv.setText(String.format("%.2f",kb)+ "KB");

                        }
                        else {
                            holder.sizeTv.setText(String.format("%.2f",bytes)+ "bytes");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadPdfFromUrl(ModelComic model, HolderComicAdmin holder) {
        //using url we can get file and its metadata from firebase storage

        String pdfUrl = model.getUrl();
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {


                        holder.pdfView.fromBytes(bytes)
                                .pages(0)//show only first páge
                                .spacing(0)
                                .swipeHorizontal(false)
                                .enableSwipe(false)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                            holder.progressBar.setVisibility(View.INVISIBLE);
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        holder.progressBar.setVisibility(View.INVISIBLE);
                                    }
                                })
                                .onLoad(new OnLoadCompleteListener() {
                                    @Override
                                    public void loadComplete(int nbPages) {
                                        holder.progressBar.setVisibility(View.INVISIBLE);
                                    }
                                })
                                .load();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.progressBar.setVisibility(View.INVISIBLE);
                    }
                });


    }

    private void loadCategory(ModelComic model, HolderComicAdmin holder) {

        //lấy category sử dụng categoryId
        String categoryId = model.getCategoryId();
        if (categoryId != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categorys");
            ref.child(categoryId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            //lấy category
                            String category = ""+snapshot.child("category").getValue();

                            holder.categoryTv.setText(category);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }
        else {

        }




    }

    @Override
    public int getItemCount() {
        return comicArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null)
        {
            filter = new FilterComicAdmin(fillterList,this);
        }
        return filter;
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
}
