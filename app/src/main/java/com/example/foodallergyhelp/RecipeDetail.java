package com.example.foodallergyhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.foodallergyhelp.models.IngredienteObject;
import com.example.foodallergyhelp.models.InstruccionesObject;
import com.example.foodallergyhelp.models.RecetaIngredienteObject;
import com.example.foodallergyhelp.models.RecetaObject;
import com.squareup.picasso.Picasso;

public class RecipeDetail extends AppCompatActivity {

    String idReceta;
    String urlIngredientes;
    List<RecetaIngredienteObject> recetasIngredientes = new ArrayList<RecetaIngredienteObject>();
    List<InstruccionesObject> recetasInstrucciones = new ArrayList<InstruccionesObject>();
    ImageView imgReceta;
    TextView txtIngredientes;
    TextView txtProceso;
    TextView txtTitulo;
    ProgressDialog progressDialog;

    RecetaObject receta = new RecetaObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        idReceta = getIntent().getStringExtra("idReceta");
        imgReceta = (ImageView)findViewById(R.id.imgReceta);
        txtIngredientes = (TextView) findViewById(R.id.txtIngredientes);
        txtProceso = (TextView) findViewById(R.id.txtProceso);
        txtTitulo = (TextView) findViewById(R.id.txtTitulo);

        ConsultarDetalleRecetasTask consultarDetalleRecetasTask = new ConsultarDetalleRecetasTask();
        consultarDetalleRecetasTask.execute();
    }


    public class ConsultarDetalleRecetasTask extends AsyncTask<Void, Void, String>
    {

        String server_response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RecipeDetail.this);
            progressDialog.setMessage("Searching..."); // Setting Message
            progressDialog.setTitle("Recipie"); // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog.show(); // Display Progress Dialog
            progressDialog.setCancelable(false);

        }


        @Override
        protected String doInBackground(Void... params) {

            OkHttpClient client = new OkHttpClient();

            urlIngredientes = Constantes.urlBase + "/"+ idReceta +"/information";

            Request request = new Request.Builder()
                    .url(urlIngredientes)
                    .get()
                    .addHeader(getString (R.string.host_header), getString (R.string.host))
                    .addHeader(getString(R.string.key_header), getString(R.string.key))
                    .build();

            try {
                Response response = client.newCall(request).execute();

                String resp = response.body().string();
                //resp[1] = params[1];    /// tiene el indice actual de recetas

                return resp;
            }
            catch (Exception e)
            {
                System.out.println("***** El error es en ConsultarRecetasIngTask " + e.getMessage());
            }
            return null;


        }


        @Override
        protected void onPostExecute(String resultado) {
            //txtString.setText(resultado);
            convertirStringJsonIng(resultado);
            progressDialog.dismiss();
        }

    }

    public void convertirStringJsonIng(String resultado){
        try{
            JSONObject json = new JSONObject(resultado);
            JSONArray resultsArray = json.getJSONArray("extendedIngredients");
            JSONArray resultsArray2 = json.getJSONArray("analyzedInstructions");
            String titulo = json.getString("title");
            //String image = json.getString("image");
            String image;

            if(json.has("image")){
                image = json.getString("image");


                new DownloadImageTask(imgReceta).execute(image);
            }
            else
                imgReceta.setImageResource(R.drawable.receta2);

            txtTitulo.setText(titulo);




            List<IngredienteObject> ingredientes = new ArrayList<IngredienteObject>();
            List<InstruccionesObject> instrucciones = new ArrayList<InstruccionesObject>();

            String valor = "Se ha obtenido " + String.valueOf(resultsArray.length() + " resultados");
            String id;
            String nombre;
            String cantidad;
            String todosIng = "";
            String ing = "";

            for (int indice = 0; indice < resultsArray.length(); indice++) {
                // Obtener objeto a través del índice
                JSONObject ingrediente = resultsArray.getJSONObject(indice);

                id = ingrediente.getString("id");
                nombre = ingrediente.getString("name");

                double cantDoub =  ingrediente.getDouble("amount");
                DecimalFormat decimalFormat = new DecimalFormat(".#");
                String cant = decimalFormat.format(cantDoub);
                cantidad = cant + " " + ingrediente.getString("unit");

                ingredientes.add(new IngredienteObject(id,nombre,cantidad));
                ing += "•  " + cantidad + " of " + nombre + Html.fromHtml("<br>");
                // todosIng = todosIng + nombre + ", " + cantidad + " - ";
            }
            txtIngredientes.setText(ing);
            recetasIngredientes.add(new RecetaIngredienteObject("1",ingredientes));


            //////////////////// instrucciones
            String numero;
            String descripcion;
            String pasos = "";
            for (int indice = 0; indice < resultsArray2.length(); indice++) {
                JSONObject obj = resultsArray2.getJSONObject(indice);
                JSONArray resultsArrayInstr = obj.getJSONArray("steps");

                for (int i = 0; i < resultsArrayInstr.length(); i++) {
                    // Obtener objeto a través del índice
                    JSONObject instruccion = resultsArrayInstr.getJSONObject(i);

                    numero = instruccion.getString("number");
                    descripcion = instruccion.getString("step");
                    recetasInstrucciones.add(new InstruccionesObject(numero,descripcion));
                    pasos += numero + ". " + descripcion + Html.fromHtml("<br><br>");
                }

            }
            txtProceso.setText(pasos);




        }
        catch (JSONException e) {
            System.out.println("***** Error en convertirStringJsonIng " + e.getMessage());
        }
        catch(Exception e){
            System.out.println("***** Error en convertirStringJsonIng " + e.getMessage());
        }

    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                BitmapFactory.Options bmOptions;
                bmOptions = new BitmapFactory.Options();
                bmOptions.inSampleSize = 1;
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in, null, bmOptions);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            Bitmap resized = Bitmap.createScaledBitmap(result, 150, 150, true);
            bmImage.setImageBitmap(resized);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipe_detail,menu);
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
