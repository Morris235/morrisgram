package com.example.morrisgram.DTO_Classes.Firebase;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Users_ProfileModify {
    private String Website;
    private String Introduce;

    public Users_ProfileModify(String website, String introduce) {
        this.Website = website;
        this.Introduce = introduce;
    }

    @Exclude
    public Map<String,Object> toMap() {
        HashMap<String,Object> result = new HashMap<>();
        result.put("Website",Website);
        result.put("Introduce",Introduce);
        return result;
    }
}
