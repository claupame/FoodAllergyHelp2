package com.example.foodallergyhelp.models;

import java.util.ArrayList;

public class AlergenosEncObject {
    String alergeno;
    ArrayList<String> encontrados = new ArrayList<String>();
    int total;

    public AlergenosEncObject() {

    }


    public AlergenosEncObject(String alergeno, ArrayList<String> encontrados, int total) {
        this.alergeno = alergeno;
        this.encontrados = encontrados;
        this.total = total;
    }

    public String getAlergeno() {
        return alergeno;
    }

    public void setAlergeno(String alergeno) {
        this.alergeno = alergeno;
    }

    public ArrayList<String> getEncontrados() {
        return encontrados;
    }

    public void setEncontrados(ArrayList<String> encontrados) {
        this.encontrados = encontrados;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
