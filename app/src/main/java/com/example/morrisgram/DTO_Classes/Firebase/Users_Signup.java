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

    public Users_Signup(String email, String personName, String Password) {
        this.Email = email;
        this.PersonName = personName;
        this.Password = Password;
    }

    @Exclude
   public Map<String,Object> toMap() {
        HashMap<String,Object> result = new HashMap<>();
        result.put("Email",Email);
        result.put("PersonName",PersonName);
        result.put("Password",Password);

        return result;
    }
}
