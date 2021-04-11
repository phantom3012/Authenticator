package com.example.authenticator;

public class User {
    public String fullName,email,password,hobbies;

    public User(){

    }
    public User(String fullName, String email, String password, String hobbies){
        this.fullName=fullName;
        this.email=email;
        this.password=password;
        this.hobbies=hobbies;
    }
}

