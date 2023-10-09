package com.example.truyentranh.object;

import org.json.JSONException;
import org.json.JSONObject;

public class Comic {
    private String nameComic,nameChap,linkImg;
    /*{
        "nameComic":"",
        nameChap:"",
        linkImg:""
       }
    */
    public Comic(){

    }
    public Comic(JSONObject o) throws JSONException {
        nameComic = o.getString("nameComic");
        nameChap = o.getString("nameChap");
        linkImg = o.getString("linkImg");

    }
    public Comic(String nameComic, String nameChap, String linkImg) {
        this.nameComic = nameComic;
        this.nameChap = nameChap;
        this.linkImg = linkImg;
    }

    public String getNameComic() {
        return nameComic;
    }

    public void setNameComic(String nameComic) {
        this.nameComic = nameComic;
    }

    public String getNameChap() {
        return nameChap;
    }

    public void setNameChap(String nameChap) {
        this.nameChap = nameChap;
    }

    public String getLinkImg() {
        return linkImg;
    }

    public void setLinkImg(String linkImg) {
        this.linkImg = linkImg;
    }
}
