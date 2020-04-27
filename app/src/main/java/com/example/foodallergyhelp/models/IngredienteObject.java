package com.example.foodallergyhelp.models;

/**
 * Created by Pamela on 23/10/2019.
 */

public class IngredienteObject {
    String id;
    String nombre;
    String cantidad;


    public IngredienteObject() {

    }

    public IngredienteObject(String id, String nombre, String cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }
}
