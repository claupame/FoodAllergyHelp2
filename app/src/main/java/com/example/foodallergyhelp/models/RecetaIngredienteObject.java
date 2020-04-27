package com.example.foodallergyhelp.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pamela on 18/11/2019.
 */

public class RecetaIngredienteObject {
    String id;
    List<IngredienteObject> ingredientes = new ArrayList<IngredienteObject>();


    public RecetaIngredienteObject() {

    }


    public RecetaIngredienteObject(String id, List<IngredienteObject> ingredientes) {
        this.id = id;
        this.ingredientes = ingredientes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<IngredienteObject> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<IngredienteObject> ingredientes) {
        this.ingredientes = ingredientes;
    }
}
