package com.example.truyentranh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.truyentranh.R;
import com.example.truyentranh.object.Comic;

import java.util.ArrayList;
import java.util.List;


public class ComicAdapter extends ArrayAdapter<Comic> {
    private Context ct;
    private ArrayList<Comic> arr;

    public ComicAdapter( Context context, int resource,  List<Comic> objects) {
        super(context, resource, objects);
        this.ct = context;
        this.arr = new ArrayList<>(objects);
    }
    /* search name comic */
    public void sortComic(String s){
        s = s.toUpperCase();
        int k =0;
        for(int i = 0 ; i < arr.size();i++){
            Comic c = arr.get(i);
            String name  = c.getNameComic().toUpperCase();
            if(name.indexOf(s)>=0){
                arr.set(i,arr.get(k));
                arr.set(k,c);
                k++;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, @Nullable View convertView,  ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater)ct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_comic,null);
        }
        if(arr.size()>0){
            Comic comic = this.arr.get(position);
            TextView nameNameComic = convertView.findViewById(R.id.nameComic);
            TextView nameNameChap = convertView.findViewById(R.id.txtNameChap);
            ImageView imgImgComic = convertView.findViewById(R.id.imgComic);

            nameNameComic.setText(comic.getNameComic());
            nameNameChap.setText(comic.getNameChap());
            Glide.with(this.ct).load(comic.getLinkImg()).into(imgImgComic);
        }
        return convertView;
    }
}

