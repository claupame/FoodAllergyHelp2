package com.example.foodallergyhelp.models;

/**
 * Created by Pamela on 15/1/2020.
 */

public class InstruccionesObject {
    String numero;
    String descripcion;

    public InstruccionesObject() {

    }

    public InstruccionesObject(String numero, String descripcion) {
        this.numero = numero;
        this.descripcion = descripcion;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
