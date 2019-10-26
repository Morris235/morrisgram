package com.example.morrisgram.DTOclass.Firebase;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Users_Signup {
    private String Email;
    private String NickName;
    private String Password;
    private String Phone;
    private String Sex;

    public Users_Signup(String email, String NickName, String Password, String Phone, String Sex) {
        this.Email = email;
        this.NickName = NickName;
        this.Password = Password;
        this.Phone = Phone;
        this.Sex = Sex;
    }

    @Exclude
   public Map<String,Object> toMap() {
        HashMap<String,Object> result = new HashMap<>();
        result.put("Email_ID",Email);
        result.put("Password",Password);
        result.put("NickName",NickName);
        result.put("Phone",Phone);
        result.put("Sex",Sex);
        return result;
    }
}
