package com.example.foodallergyhelp.models;

/**
 * Created by Pamela on 16/1/2020.
 */

public class UsuarioAlergiasObject {
    String nombre;
    int imagen;
    boolean seleccionado;
    boolean agregado;

    public UsuarioAlergiasObject() {

    }

    public UsuarioAlergiasObject(String nombre, boolean seleccionado, boolean agregado) {
        this.nombre = nombre;
        this.seleccionado = seleccionado;
        this.agregado = agregado;
    }

    public UsuarioAlergiasObject(String nombre, boolean seleccionado) {
        this.nombre = nombre;
        this.seleccionado = seleccionado;
        this.agregado = agregado;
    }

    public UsuarioAlergiasObject(String nombre, int imagen, boolean seleccionado, boolean agregado) {
        this.nombre = nombre;
        this.imagen = imagen;
        this.seleccionado = seleccionado;
        this.agregado = agregado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public boolean isAgregado() {
        return agregado;
    }

    public void setAgregado(boolean agregado) {
        this.agregado = agregado;
    }

    //Represetacion del objeto como cadena de texto
    @Override
    public String toString() {
        return nombre;
    }
}
