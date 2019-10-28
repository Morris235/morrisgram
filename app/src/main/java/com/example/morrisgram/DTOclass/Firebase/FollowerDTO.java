package com.example.morrisgram.DTOclass.Firebase;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class FollowerDTO {


    public FollowerDTO(String UID) {
        this.UID = UID;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    private String UID;

    //파베에 업드로할 해쉬맵 틀
    @Exclude
    public Map<String,Object> toMap() {
        HashMap<String,Object> result = new HashMap<>();
        result.put("UID",UID);
        return result;
    }

}
