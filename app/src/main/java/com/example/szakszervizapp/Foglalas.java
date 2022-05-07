package com.example.szakszervizapp;

import java.sql.Timestamp;

public class Foglalas {
    Foglalas(String foglaloEmail, Timestamp ido, String szakemberEmail){
        this.foglaloEmail = foglaloEmail;
        this.ido = ido;
        this.szakemberEmail = szakemberEmail;
    }
    public String foglaloEmail;
    public Timestamp ido;
    public String szakemberEmail;
}
