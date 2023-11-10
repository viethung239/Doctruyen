package com.example.truyentranh.filters;

import android.widget.Filter;

import com.example.truyentranh.adapters.AdapterCategory;
import com.example.truyentranh.adapters.AdapterComicAdmin;
import com.example.truyentranh.model.ModelCategory;
import com.example.truyentranh.model.ModelComic;

import java.util.ArrayList;

public class FilterComicAdmin extends Filter {
    //Danh sách mảng muốn tìm kiếm
    ArrayList<ModelComic> filterList;
    //Lọc danh sách mảng
    AdapterComicAdmin adapterComicAdmin;

    public FilterComicAdmin(ArrayList<ModelComic> filterList, AdapterComicAdmin adapterComicAdmin){
        this.filterList = filterList;
        this.adapterComicAdmin = adapterComicAdmin;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if(constraint != null && constraint.length() >0){
            //thay đổi chữ hoa chữ thường
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelComic> filteredModels = new ArrayList<>();
            for (int i=0; i<filterList.size(); i++){
                if(filterList.get(i).getTitle().toUpperCase().contains(constraint)){
                    // Thêm vào danh sách đã lọc
                    filteredModels.add(filterList.get(i));
                }
            }

            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterComicAdmin.comicArrayList = (ArrayList<ModelComic>)results.values;
        adapterComicAdmin.notifyDataSetChanged();
    }
}
