package com.example.authenticator;

import java.util.HashMap;
import java.util.Map;


public class EditUser {
    String fullName;
    String email;
    String hobbies;

    public EditUser(){

    }

    public EditUser(String fullName, String email, String hobbies) {
        this.fullName = fullName;
        this.email = email;
        this.hobbies = hobbies;
    }

    public Map<String,Object> toMap(){
        HashMap<String,Object> result=new HashMap<>();
        result.put("fullName",fullName);
        result.put("email",email);
        result.put("hobbies",hobbies);

        return result;
    }
}
