package com.example.truyentranh;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.truyentranh.databinding.ActivityPdfAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class PdfAddActivity extends AppCompatActivity {


    private ActivityPdfAddBinding binding;
    private FirebaseAuth firebaseAuth;

    private ArrayList<String> categoryTitleArrayList, categoryIdArrayList;
    private  static  final int PDF_PICK_CODE = 1000;
    private Uri pdfUri = null;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfAddBinding.inflate(getLayoutInflater());


        setContentView(binding.getRoot());

        firebaseAuth = firebaseAuth.getInstance();
        loadPdfCategories();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vùi lòng chờ một chút");
        progressDialog.setCanceledOnTouchOutside(false);


        // Nhan nut tro lai
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // Nhaans nust them truyen
        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfPickIntent();
            }
        });
        //nhan nust chon danh muc

        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryPickDiaLog();
            }
        });
    }

    private void loadPdfCategories() {


        categoryTitleArrayList = new ArrayList<>();

        categoryIdArrayList = new ArrayList<>();

        //load du lieu tu firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categorys");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear();
                categoryIdArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){

                    // lấy id và tên của danh mục
                    String categoryId = ""+ds.child("id").getValue();
                    String categoryTitle = ""+ds.child("category").getValue();

                    // add to respective arraylist
                    categoryTitleArrayList.add(categoryTitle);
                    categoryIdArrayList.add(categoryId);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // day truyen
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate data
                validateData();
            }
        });
    }

    private String title="", description ="";

    private void validateData() {



        //lay du lieu
        title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();


        //kiem tra du lieu
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Vui lòng ghi tên Truyện...", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Vui lòng ghi mô tả....", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedCategoryTitle)) {
            Toast.makeText(this, "Vui lòng chọn danh mục....", Toast.LENGTH_SHORT).show();
        } else if (pdfUri == null) {
            Toast.makeText(this, "Chọn PDF..", Toast.LENGTH_SHORT).show();
        } else {
            //du lieu ko co van de, day len
            uploadPdfToStorage();
        }
    }
    private void uploadPdfToStorage() {



        //show progress

        progressDialog.setMessage("Đang tải file pdf");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();
        //Duong dan file pdf
        String filePathAndName = "Comics/" +timestamp;

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                        while (!uriTask.isSuccessful());
                        String uploadedPdfUrl = ""+uriTask.getResult();
                        //day len firebase database

                        uploadPdfInfoDb(uploadedPdfUrl, timestamp);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();

                        Toast.makeText(PdfAddActivity.this, "Tải tệp không thành công vì"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void uploadPdfInfoDb(String uploadedPdfUrl, long timestamp) {


        progressDialog.setMessage("Đang tải tệp pdf");

        String uid = firebaseAuth.getUid();

        //chuan bi du lieu de tai len
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+uid);
        hashMap.put("id",""+timestamp);
        hashMap.put("title",""+title);
        hashMap.put("description",""+description);
        hashMap.put("categoryId",""+selectedCategoryId);
        hashMap.put("url",""+uploadedPdfUrl);
        hashMap.put("timestamp",+timestamp);

        //tai len bang co ten Comics

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Comics");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();

                        Toast.makeText(PdfAddActivity.this,"Tải lên thành công...",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();

                        Toast.makeText(PdfAddActivity.this,"Tải lên Database thất bại"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });


    }


    private  String selectedCategoryId, selectedCategoryTitle;
    private  void categoryPickDiaLog(){



        //mang chuoi danh muc tu danh sach
        String[] catergoriesArray = new String[categoryTitleArrayList.size()];
        for (int i = 0; i< categoryTitleArrayList.size(); i++){
            catergoriesArray[i] = categoryTitleArrayList.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn Danh Mục")
                .setItems(catergoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nhấn chọn danh mục
                        selectedCategoryTitle = categoryTitleArrayList.get(which);
                        selectedCategoryId = categoryIdArrayList.get(which);


                        binding.categoryTv.setText(selectedCategoryTitle);



                    }
                })
                .show();
    }

    private void pdfPickIntent() {


        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");

        try {
            startActivityForResult(Intent.createChooser(intent, "Chọn File PDF"), PDF_PICK_CODE);
        } catch (ActivityNotFoundException e) {

            Toast.makeText(this, "Không tìm thấy ứng dụng mở file PDF", Toast.LENGTH_SHORT).show();
        }
    }

    @Override





    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)

    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PDF_PICK_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                pdfUri = data.getData();



            } else {

                Toast.makeText(this, "Bạn chưa chọn file PDF", Toast.LENGTH_SHORT).show();
            }
        } else {

            Toast.makeText(this, "Chọn file PDF bị hủy hoặc thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}

