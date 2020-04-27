package com.example.foodallergyhelp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pamela on 23/10/2019.
 */

public class RecetaObject {
    String id;
    String titulo;
    String imagen;
    String duracion;
    String porciones;
    List<IngredienteObject> ingredientes = new ArrayList<IngredienteObject>();
    String barato;
    String puntaje;
    List<InstruccionesObject> instrucciones = new ArrayList<InstruccionesObject>();

    public RecetaObject() {
    }

    public RecetaObject(List<IngredienteObject> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public RecetaObject(String id) {
        this.id = id;
    }

    public RecetaObject(String id, String titulo, String imagen, String duracion, String porciones) {
        this.id = id;
        this.titulo = titulo;
        this.imagen = imagen;
        this.duracion = duracion;
        this.porciones = porciones;
    }

    public RecetaObject(String id, String titulo, String imagen, String duracion, String porciones, List<IngredienteObject> ingredientes,
                        String barato, String puntaje, List<InstruccionesObject> instrucciones) {
        this.id = id;
        this.titulo = titulo;
        this.imagen = imagen;
        this.duracion = duracion;
        this.porciones = porciones;
        this.ingredientes = ingredientes;
        this.barato = barato;
        this.puntaje = puntaje;
        this.instrucciones = instrucciones;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public List<IngredienteObject> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<IngredienteObject> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getPorciones() {
        return porciones;
    }

    public void setPorciones(String porciones) {
        this.porciones = porciones;
    }

    public String getBarato() {
        return barato;
    }

    public void setBarato(String barato) {
        this.barato = barato;
    }

    public String getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(String puntaje) {
        this.puntaje = puntaje;
    }

    public List<InstruccionesObject> getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(List<InstruccionesObject> instrucciones) {
        this.instrucciones = instrucciones;
    }
}
