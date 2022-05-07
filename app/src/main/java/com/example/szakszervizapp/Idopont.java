package com.example.szakszervizapp;

import java.sql.Timestamp;

public class Idopont {
    Idopont(String email, String cim, Timestamp ido){
        this.email = email;
        this.cim = cim;
        this.ido = ido;
    }
    Idopont(){
        email = "";
        cim = "";
        ido = Timestamp.valueOf("2023-09-01 09:01:15");
    }

    String getEmail(){
        return email;
    }
    String getCim(){
        return cim;
    }
    Timestamp getIdo(){
        return ido;
    }

    public String email;
    public String cim;
    public Timestamp ido;
}
