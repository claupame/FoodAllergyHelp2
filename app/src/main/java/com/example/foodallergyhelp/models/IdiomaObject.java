package com.example.foodallergyhelp.models;

/**
 * Created by Pamela on 3/10/2019.
 */

public class IdiomaObject {

    String codigo;
    String nombre;

    public IdiomaObject() {

    }

    public IdiomaObject(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
