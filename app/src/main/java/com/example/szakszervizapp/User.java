package com.example.szakszervizapp;

public class User {
    User(String email, String postaCim, Boolean szakember){
        this.email = email;
        this.postaCim = postaCim;
        this.szakember = szakember;
    }

    public String email;
    public String postaCim;
    public Boolean szakember;
}
