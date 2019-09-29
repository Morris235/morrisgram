package com.example.morrisgram.DTO_Classes.Firebase;

import android.util.Patterns;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Users_Signup {
    private String Email;
    private String PersonName;
    private String Password;

    public Users_Signup(String email, String personName, String Password) {
        Email = email;
        PersonName = personName;
        Password = Password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPersonName() {
        return PersonName;
    }

    public void setPersonName(String personName) {
        PersonName = personName;
    }
    @Exclude
   public Map<String,Object> toMap() {
        HashMap<String,Object> result = new HashMap<>();
        result.put("Email",Email);
        result.put("PersonName",PersonName);

        return result;
    }
}
