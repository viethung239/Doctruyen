package com.example.truyentranh.Api;

import com.example.truyentranh.Interfaces.GetComic;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class ApiGetComic extends AsyncTask<Void, Void, String> {

    String data;
    GetComic getComic;

    public ApiGetComic(GetComic getComic) {
        this.getComic = getComic;
        this.getComic.start();


    }

    @Override
    protected String doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://www.myjsons.com/v/Comic").build();
        data = null;
        try {
            Response response =client.newCall(request).execute();
            ResponseBody body = response.body();
            data = body.string();
        }catch(IOException e){
        data = null;
        }
        return null;

    }

    @Override
    protected void onPostExecute(String s) {
        if(data==null){
            this.getComic.error();

        }else {
            this.getComic.end(data);
        }
    }




}







