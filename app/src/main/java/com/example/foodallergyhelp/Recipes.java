package com.example.foodallergyhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.foodallergyhelp.adapters.RecetasAdapter;
import com.example.foodallergyhelp.models.RecetaObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Recipes extends AppCompatActivity {

    String urlPeticion;
    List<RecetaObject> listRecetas = new ArrayList<RecetaObject>();
    TextView txtResultados;
    ListView lstRecetas;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        try{
            txtResultados = (TextView) findViewById(R.id.txtResultados);
            lstRecetas = (ListView) findViewById(R.id.lstRecetas);

            urlPeticion = getIntent().getStringExtra("urlPeticion");

            ConsultarRecetasTask consultarRecetasTask = new ConsultarRecetasTask();
            consultarRecetasTask.execute();

            lstRecetas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    RecetaObject item = (RecetaObject) lstRecetas.getAdapter().getItem(position);

                    Intent myIntent = new Intent(getApplicationContext(), RecipeDetail.class);
                    myIntent.putExtra("idReceta", item.getId());
                    startActivity(myIntent);
                }
            });
        }
            catch(Exception e){
            System.out.println("Se ha producido un error en Recetas oncreate " + e.getMessage());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipes,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.it_cerrar_sesion:
                cerrarSesion();
                break;

            case R.id.it_back:
                finish();
                break;

        }

        return true;
    }

    //// task que consulta las recetas que cumplan con los filtros dados
    public class ConsultarRecetasTask extends AsyncTask<String, Void, String> {

        String server_response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loader.setVisibility(View.VISIBLE);

            progressDialog = new ProgressDialog(Recipes.this);
            progressDialog.setMessage("Searching..."); // Setting Message
            progressDialog.setTitle("Recipies"); // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog.show(); // Display Progress Dialog
            progressDialog.setCancelable(false);


        }


        @Override
        protected String doInBackground(String... params) {

            OkHttpClient client = new OkHttpClient();


            String hh = getString (R.string.host_header);
            String h = getString (R.string.host);
            String kh = getString(R.string.key_header);
            String k = getString(R.string.key);
            Request request = new Request.Builder()
                    .url(urlPeticion)
                    .get()

                    .addHeader(hh, h)
                    .addHeader(kh, k)
                    .build();

            try {

                Response response = client.newCall(request).execute();
                return response.body().string();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;


        }


        @Override
        protected void onPostExecute(String resultado) {

            convertirStringJson(resultado);
            progressDialog.dismiss();
        }

    }


    public void convertirStringJson(String resultado){
        try{

            JSONObject json = new JSONObject(resultado);
            JSONArray resultsArray = json.getJSONArray("results");

            listRecetas = new ArrayList<RecetaObject>();

            String valor =  String.valueOf(resultsArray.length() + " results have been obtained");
            String id;
            String titulo;
            String imagen;
            String duracion;
            String porciones;

            for (int indice = 0; indice < resultsArray.length(); indice++) {
                // Obtener objeto a través del índice
                JSONObject receta = resultsArray.getJSONObject(indice);

                id = receta.getString("id");
                titulo = receta.getString("title");
                if(receta.has("image"))
                    imagen = receta.getString("image");
                else
                    imagen = "none";
                duracion = receta.getString("readyInMinutes");
                porciones = receta.getString("servings");

                listRecetas.add(new RecetaObject(id,titulo,imagen,duracion,porciones));
            }

            if(listRecetas.size()<= 0)
                txtResultados.setText("There are no results for this query");
            else{
                txtResultados.setText(listRecetas.size() + " results have been obtained");
                lstRecetas.setAdapter(new RecetasAdapter(this, listRecetas));
            }



        } catch (JSONException e) {
            System.out.println("***** Error JSONException en convertirStringJson " + e.getMessage());
        }
        catch (Exception e) {
            System.out.println("***** Error Exception en convertirStringJson " + e.getMessage());
        }
    }

    private void cerrarSesion() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Sign out");
        builder.setMessage(R.string.message_logout)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences preferences = getSharedPreferences("UserInfo", 0);
                        preferences.edit().remove("Username").commit();
                        preferences.edit().remove("Password").commit();
                        preferences.edit().remove("Alergenos").commit();


                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }
}
