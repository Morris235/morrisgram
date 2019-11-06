package com.example.morrisgram.DTOclass.PosterDTO;

public class PreView {
    private String PosterKey;

    public PreView(){
    }

    public PreView(String posterKey) {
        this.PosterKey = posterKey;
    }

    public String getPosterKey() {
        return PosterKey;
    }

    public void setPosterKey(String posterKey) {
        this.PosterKey = posterKey;
    }


}
