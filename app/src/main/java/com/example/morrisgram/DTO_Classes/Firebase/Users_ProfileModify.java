package com.example.morrisgram.DTO_Classes.Firebase;

import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Users_ProfileModify {
    private String Website;
    private String Introduce;
    private String ProfileIMG;

    public Users_ProfileModify(String website, String introduce, String ProfileIMG) {
        this.Website = website;
        this.Introduce = introduce;
        this.ProfileIMG = ProfileIMG;
    }

    @Exclude
    public Map<String,Object> toMap() {
        HashMap<String,Object> result = new HashMap<>();
        result.put("Website",Website);
        result.put("Introduce",Introduce);
        result.put("ProfileIMG",ProfileIMG);
        return result;
    }
}
