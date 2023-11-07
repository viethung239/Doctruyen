package com.example.truyentranh;

import android.widget.Filter;

import java.io.File;
import java.util.ArrayList;

public class FilterCategory  extends Filter {
    //Danh sách mảng muốn tìm kiếm
    ArrayList<ModelCategory> filterList;
    //Lọc danh sách mảng
    AdapterCategory adapterCategory;

    public FilterCategory(ArrayList<ModelCategory> filterList, AdapterCategory adapterCategory){
        this.filterList = filterList;
        this.adapterCategory = adapterCategory;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if(constraint != null && constraint.length() >0){
            //thay đổi chữ hoa chữ thường
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelCategory> filteredModels = new ArrayList<>();
            for (int i=0; i<filterList.size(); i++){
                if(filterList.get(i).getCategory().toUpperCase().contains(constraint)){
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
        return null;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterCategory.categoryArrayList = (ArrayList<ModelCategory>)results.values;
        adapterCategory.notifyDataSetChanged();
    }
}
