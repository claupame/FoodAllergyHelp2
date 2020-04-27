package com.example.foodallergyhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.foodallergyhelp.adapters.AnalysisAdapter;
import com.example.foodallergyhelp.adapters.AnalysisAdapter2;
import com.example.foodallergyhelp.adapters.AnalysisDetailAdapter;
import com.example.foodallergyhelp.adapters.RecetasAdapter;
import com.example.foodallergyhelp.models.AlergenosEncObject;
import com.example.foodallergyhelp.models.BasicObject;
import com.example.foodallergyhelp.models.IngredienteObject;
import com.example.foodallergyhelp.models.RecetaIngredienteObject;
import com.example.foodallergyhelp.models.RecetaObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Analysis extends AppCompatActivity {
    TextView tvResult;
    TextView tvTotal;
    TextView tvSearching;
    ImageView imvResult;
    ProgressDialog progressDialog;
    ProgressDialog progressDialogIng;
    String urlPeticion;
    List<RecetaObject> recetas = new ArrayList<RecetaObject>();

    String urlIngredientes;
    List<RecetaIngredienteObject> recetasIngredientes = new ArrayList<RecetaIngredienteObject>();
    String alergenosUser = "";
    List<AlergenosEncObject> alergenosEncList;

    ListView lsvAnalysis;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        tvResult = (TextView) findViewById(R.id.tvResult);
        tvTotal = (TextView) findViewById(R.id.tvTotal);
        tvSearching = (TextView) findViewById(R.id.tvSearching);
        imvResult = (ImageView) findViewById(R.id.imvResult);
        lsvAnalysis = (ListView) findViewById(R.id.lsvAnalysis);

        String keyword = getIntent().getStringExtra("keyword");
        urlPeticion = getIntent().getStringExtra("urlPeticion");
        alergenosUser = getIntent().getStringExtra("alergenosUser");

        progressDialogIng = new ProgressDialog(Analysis.this);
        tvSearching.setText("You are looking for: " + keyword);

        alergenosEncList = new ArrayList<AlergenosEncObject>();

        lsvAnalysis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlergenosEncObject item = (AlergenosEncObject)lsvAnalysis.getItemAtPosition(position);
                openDialogDetail(item.getEncontrados());
            }
        });



        ConsultarRecetasTask consultarRecetasTask = new ConsultarRecetasTask();
        consultarRecetasTask.execute();


    }


    //// task que consulta las recetas que cumplan con los filtros dados
    public class ConsultarRecetasTask extends AsyncTask<String, Void, String> {

        String server_response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Analysis.this);
            progressDialog.setMessage("Searching..."); // Setting Message
            progressDialog.setTitle("Recipies"); // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog.show(); // Display Progress Dialog
            progressDialog.setCancelable(false);
        }


        @Override
        protected String doInBackground(String... params) {

            OkHttpClient client = new OkHttpClient();



            Request request = new Request.Builder()
                    .url(urlPeticion)
                    .get()
                    .addHeader(getString (R.string.host_header), getString (R.string.host))
                    .addHeader(getString(R.string.key_header), getString(R.string.key))
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

            recetas = new ArrayList<RecetaObject>();

            String valor = "Se ha obtenido " + String.valueOf(resultsArray.length() + " resultados");
            String id;
            String titulo;
            String imagen;
            String duracion;
            String porciones;

            for (int indice = 0; indice < resultsArray.length(); indice++) {
                // Obtener objeto a través del índice
                JSONObject receta = resultsArray.getJSONObject(indice);
                valor = valor + "\n" + receta.getString("title");

                id = receta.getString("id");
                titulo = receta.getString("title");
                if(receta.has("image"))
                    imagen = receta.getString("image");
                else
                    imagen = "none";
                duracion = receta.getString("readyInMinutes");
                porciones = receta.getString("servings");


                recetas.add(new RecetaObject(id,titulo,imagen,duracion,porciones));
                //recetas.add(new RecetaObject(id));
            }


            ///// busqueda de ingredientes de las recetas encontradas
            RecetaObject r = new RecetaObject();
            if(recetas.size()>0){
                r = ((RecetaObject) recetas.get(0));   // obtiene la primera receta
                /// busca los ingredientes de la primera receta
                ConsultarRecetasIngTask consultarRecetasIngTask = new ConsultarRecetasIngTask();
                consultarRecetasIngTask.execute(r.getId(), String.valueOf(0));
            }
            else{
                tvResult.setText("There are no results for this query");

            }




        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public class ConsultarRecetasIngTask extends AsyncTask <String, Void, String[]>
    {

        String server_response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            if(!progressDialogIng.isShowing()) {
                progressDialogIng.setMessage("Searching..."); // Setting Message
                progressDialogIng.setTitle("Ingredients Recipies"); // Setting Title
                progressDialogIng.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialogIng.show(); // Display Progress Dialog
                progressDialogIng.setCancelable(false);
            }

        }


        @Override
        protected String[] doInBackground(String... params) {

            OkHttpClient client = new OkHttpClient();

            urlIngredientes = Constantes.urlBase + "/"+ params[0] +"/information";
            System.out.println("**********  receta " + params[1] );
            //System.out.println("********** la url es  " + urlIngredientes );

            Request request = new Request.Builder()
                    .url(urlIngredientes)
                    .get()
                    .addHeader(getString (R.string.host_header), getString (R.string.host))
                    .addHeader(getString(R.string.key_header), getString(R.string.key))
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String[] resp = {"","",""};
                resp[0] = response.body().string();
                resp[1] = params[1];    /// tiene el indice actual de recetas
                resp[2] = params[0];

                return resp;
            }
            catch (Exception e)
            {
                System.out.println("***** El error es en ConsultarRecetasIngTask " + e.getMessage());
            }
            return null;


        }


        @Override
        protected void onPostExecute(String[] resultado) {
            //txtString.setText(resultado);
            try{
                String[] resp = {"","",""};
                resp[0] = resultado[0];  // respuesta de la consulta
                resp[1] = resultado[1];  // indice actual del array
                resp[2] = resultado[2];  // indice de la receta
                //

                convertirStringJsonIng(resp);
                if(Integer.valueOf(resultado[1]) == recetas.size()-1)
                    progressDialogIng.dismiss();
            }
            catch(Exception e){
                System.out.println("***** Error en onPostExecute " + e.getMessage());
            }
        }

    }

    public void convertirStringJsonIng(String[] resultado){
        try{
            JSONObject json = new JSONObject(resultado[0]);
            JSONArray resultsArray = json.getJSONArray("extendedIngredients");

            List<IngredienteObject> ingredientes = new ArrayList<IngredienteObject>();

            String valor = "Se ha obtenido " + String.valueOf(resultsArray.length() + " resultados");
            String id;
            String nombre;
            String cantidad;
            String todosIng = "";

            for (int indice = 0; indice < resultsArray.length(); indice++) {
                // Obtener objeto a través del índice
                JSONObject ingrediente = resultsArray.getJSONObject(indice);

                id = ingrediente.getString("id");
                nombre = ingrediente.getString("name");
                cantidad = ingrediente.getString("amount") + " " + ingrediente.getString("unit");
                ingredientes.add(new IngredienteObject(id,nombre,cantidad));
                // todosIng = todosIng + nombre + ", " + cantidad + " - ";
            }

            recetasIngredientes.add(new RecetaIngredienteObject(resultado[2],ingredientes));

            int indReceta = Integer.valueOf(resultado[1]);
            RecetaObject r = new RecetaObject();

            if(indReceta<recetas.size()-1){   /// si aun quedan recetas por consultar
                r = ((RecetaObject) recetas.get(indReceta+1));
                String idRec = String.valueOf(indReceta+1);
                ConsultarRecetasIngTask consultarRecetasIngTask = new ConsultarRecetasIngTask();
                consultarRecetasIngTask.execute(r.getId(), idRec);
            }
            else{
               /* todosIng = "";
                for (int i = 0; i<= indReceta;i++ ){   //bucle que recorre todas las recetas con sus ingredientes
                    List<IngredienteObject> listaIng = new ArrayList<IngredienteObject>();
                    listaIng = ((RecetaIngredienteObject) recetasIngredientes.get(i)).getIngredientes();

                    todosIng = todosIng + "***** receta " + String.valueOf(i+1);
                    for (int j = 0; j< listaIng.size();j++ ){
                        IngredienteObject ing = ((IngredienteObject)listaIng.get(j));
                        todosIng = todosIng + "  " + ing.getNombre() + ", ";
                    }

                }
                System.out.println("**** el valor de todos ing " + todosIng);

                */
                buscarAlergenosRec();
            }




        }
        catch (JSONException e) {
            System.out.println("***** Error en convertirStringJsonIng JsonException " + e.getMessage());
        }
        catch(Exception e){
            System.out.println("***** Error en convertirStringJsonIng " + e.getMessage());
        }

    }




    private void buscarAlergenosRec() {
        ArrayList<String> encontrados = new ArrayList<String>();
        String[] alergenos = alergenosUser.split(",");
        for (String alerg : alergenos) {   // recorre todos los alergenos del usuario
            encontrados = new ArrayList<String>();

            for (int i = 0; i< recetasIngredientes.size();i++ ){   //bucle que recorre todas las recetas con sus ingredientes
                List<IngredienteObject> listaIng = new ArrayList<IngredienteObject>();
                listaIng = ((RecetaIngredienteObject) recetasIngredientes.get(i)).getIngredientes();

                for (int j = 0; j< listaIng.size();j++ ){   // bucle que recorre todos los ingredientes de una receta
                    IngredienteObject ing = ((IngredienteObject)listaIng.get(j));

                    if (ing.getNombre().trim().contains(alerg.trim())) {
                        encontrados.add(ing.getNombre());
                    }
                }


            }
            alergenosEncList.add(new AlergenosEncObject(alerg,encontrados, recetasIngredientes.size()));


        }

        lsvAnalysis.setAdapter(new AnalysisAdapter2(this, alergenosEncList));

        resultadoGeneral();

    }

    private void resultadoGeneral() {
        boolean recomendable = true;
        for(AlergenosEncObject obj: alergenosEncList){
            if(obj.getEncontrados().size() > 0) {
                recomendable = false;
                break;
            }
        }

        if(recomendable){
            imvResult.setImageResource(R.drawable.eat);
            String color = getString(Integer.parseInt(String.valueOf(R.color.green2)));
            tvResult.setText(getString (R.string.no_aller));
            tvResult.setTextColor(Color.parseColor(color));
        }
        else{
            String color = getString(Integer.parseInt(String.valueOf(R.color.red)));
            imvResult.setImageResource(R.drawable.noteat);
            tvResult.setText(getString (R.string.aller_det));
            tvResult.setTextColor(Color.parseColor(color));
        }
        tvTotal.setText("Analyzed recipes: " + recetas.size());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_analysis,menu);
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

    private void openDialogDetail(ArrayList<String> encontrados) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(encontrados.size()>0)
            builder.setTitle(getString (R.string.aller_det_title));
        else
            builder.setTitle(getString (R.string.no_aller_title));
        View mView = getLayoutInflater().inflate(R.layout.dialog_allergy_detail,null);

        ListView lvDetail = (ListView) mView.findViewById(R.id.lvDetail);
        ImageView imvResult = (ImageView) mView.findViewById(R.id.imvResult);
        Button btnCancel = (Button)mView.findViewById(R.id.btn_okay);

        String resultado = "";
        ArrayList<BasicObject> resulEnc = new ArrayList<BasicObject>();
        for(String ec: encontrados){
            boolean existe = false;
            for(int i = 0; i<resulEnc.size(); i++){
                if(ec.trim().equals(resulEnc.get(i).getNombre())){
                    resulEnc.get(i).setValor(resulEnc.get(i).getValor()+1);
                    existe =true;
                }
            }
            if(!existe)
                resulEnc.add(new BasicObject(ec,1));

        }

        lvDetail.setAdapter(new AnalysisDetailAdapter(this, resulEnc));
        if(encontrados.size()>0)
            imvResult.setImageResource(R.drawable.noteat);

        /*for(BasicObject ob:resulEnc){
            resultado += ob.getNombre() + ":" +  ob.getValor() + "  ";
        }
        */


        //tvDetail.setText(resultado);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(mView)
                // Add action buttons
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        builder.create().show();





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
