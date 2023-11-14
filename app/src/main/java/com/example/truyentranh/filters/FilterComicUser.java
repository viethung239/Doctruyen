package com.example.truyentranh.filters;

import android.widget.Filter;

import com.example.truyentranh.adapters.AdapterComicUser;
import com.example.truyentranh.model.ModelComic;

import java.util.ArrayList;

public class FilterComicUser extends Filter {

    //arrayList in wich we want to serch
    ArrayList<ModelComic> filterList;
    //adapter in which filter need to be implementd
    AdapterComicUser adapterComicUser;


    public FilterComicUser(ArrayList<ModelComic> filterList, AdapterComicUser adapterComicUser) {
        this.filterList = filterList;
        this.adapterComicUser = adapterComicUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //value to be searched should not be null/emty

        if(constraint!= null || constraint.length()>0){
            //not null or empty
            //change to uppercase or lower case to avoid case sensitivity

            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelComic> filterModels = new ArrayList<>();

            for (int i = 0; i <filterList.size(); i++){
                //validate
                if( filterList.get(i).getTitle().toUpperCase().contains(constraint)){
                    //search matches add to list
                    filterModels.add(filterList.get(i));
                }
            }
            results.count = filterModels.size();
            results.values = filterModels;

        }
        else {
            // empty or null, makeoriginal list/ result
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        //aply filter changes

        adapterComicUser.comicArrayList = (ArrayList<ModelComic>)results.values;

        //notify changes
        adapterComicUser.notifyDataSetChanged();


    }
}
