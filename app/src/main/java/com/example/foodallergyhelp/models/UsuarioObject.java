package com.example.foodallergyhelp.models;

import java.util.Map;

public class UsuarioObject {

    private String nombre;
    private String password;
    private String correo;
    private Map<String, Boolean> alergenos;

    public UsuarioObject() {

    }



    public UsuarioObject(String nombre, String password, String correo, Map<String, Boolean> alergenos) {
        this.nombre = nombre;
        this.password = password;
        this.correo = correo;
        this.alergenos = alergenos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Map<String, Boolean> getAlergenos() {
        return alergenos;
    }

    public void setAlergenos(Map<String, Boolean> alergenos) {
        this.alergenos = alergenos;
    }

    @Override
    public String toString() {
        return nombre;
    }
}

