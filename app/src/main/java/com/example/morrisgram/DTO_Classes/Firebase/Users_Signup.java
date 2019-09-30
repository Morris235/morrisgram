package com.example.morrisgram.DTO_Classes.Firebase;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Users_Signup {
    private String Email;
    private String PersonName;
    private String Password;
    private String Phone;
    private String Sex;

    public Users_Signup(String email, String personName, String Password, String Phone, String Sex) {
        this.Email = email;
        this.PersonName = personName;
        this.Password = Password;
        this.Phone = Phone;
        this.Sex = Sex;
    }

    @Exclude
   public Map<String,Object> toMap() {
        HashMap<String,Object> result = new HashMap<>();
        result.put("Email_ID",Email);
        result.put("PersonName",Password);
        result.put("Password",PersonName);
        result.put("Phone",Phone);
        result.put("Sex",Sex);
        return result;
    }
}
