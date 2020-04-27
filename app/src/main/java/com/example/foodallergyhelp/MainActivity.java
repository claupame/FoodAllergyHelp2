package com.example.foodallergyhelp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;

import com.example.foodallergyhelp.adapters.RecetasAdapter;
import com.example.foodallergyhelp.models.RecetaObject;
import com.example.foodallergyhelp.models.UsuarioObject;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    Button btnLogin;
    Button btnRegister;
    TextInputEditText etUser;
    TextInputEditText etPassword;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // esconder la base de estado superior
        this.getSupportActionBar().hide();

        // iniciacion de elementos
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        etUser = (TextInputEditText) findViewById(R.id.etUser);
        etPassword = (TextInputEditText) findViewById(R.id.etPassword);



            //////////////////////////
            btnLogin.setFocusable(true);
            btnLogin.requestFocus();
            btnLogin.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String u = etUser.getText().toString();
                    if (etUser.getText().toString().length() > 0 && etPassword.getText().toString().length() > 0)
                        buscarUsuario(u);
                    else {
                        if (etUser.getText().toString().length() == 0)
                            etUser.setError("Requerido");

                        if (etPassword.getText().toString().length() == 0)
                            etPassword.setError("Requerido");
                    }

                }
            });

            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Register.class);
                    startActivity(intent);

                }
            });


            ConsultarRecetasTask consultarRecetasTask = new ConsultarRecetasTask();
            //consultarRecetasTask.execute();
    }

    private void buscarUsuario(final String nombre) {

        if(Constantes.checkInternetConnection(getApplicationContext())) {

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Login..."); // Setting Message
            progressDialog.setTitle("Food Allergy Help"); // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog.show(); // Display Progress Dialog
            progressDialog.setCancelable(false);

            // Read from the database
            DatabaseReference dbr = FirebaseDatabase.getInstance().getReference("usuarios");
            //UsuarioObject usuario;

            dbr.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Login");
                    builder.setMessage("Incorrect username/password")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });

                    UsuarioObject usuario = new UsuarioObject();
                    String pw = "";
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        usuario = (UsuarioObject) postSnapshot.getValue(UsuarioObject.class);

                        if (usuario.getNombre().equals(nombre)) {
                            pw = usuario.getPassword();
                            break;
                        }

                    }

                    if (usuario.getNombre() != null) {
                        String psw = etPassword.getText().toString();
                        String alergenos = "";
                        if (psw.equals(pw)) {
                            /// guarda los datos del usuario

                            int contador = 1;

                            for (Map.Entry<String, Boolean> entry : usuario.getAlergenos().entrySet()) {
                                String alergeno = entry.getKey();


                                if (contador < usuario.getAlergenos().size()) {
                                    alergenos += alergeno + ", ";
                                } else
                                    alergenos += alergeno;
                                contador++;
                            }


                            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("Username", usuario.getNombre());
                            editor.putString("Password", usuario.getPassword());
                            editor.putString("Alergenos", alergenos);
                            editor.commit();

                            AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                            builder2.setTitle("Attention");
                            builder2.setMessage("This application is based on public recipe data, which serve as a guide but do not offer absolute guarantee")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {


                                            Intent intent = new Intent(getApplicationContext(), DashBoardPrincipal.class);
                                            intent.putExtra("activity","main");
                                            startActivity(intent);
                                        }
                                    });
                            builder2.show();

                        } else {
                            builder.show();
                        }
                        //Toast.makeText(getApplicationContext(), "Incorrect username/password", Toast.LENGTH_SHORT).show();
                    } else {
                        builder.show();
                    }

                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    //Log.w(TAG, "Failed to read value.", error.toException());
                    progressDialog.dismiss();
                }
            });
        }

    }




    //////////////////////////////
    //// task que consulta las recetas que cumplan con los filtros dados
    public class ConsultarRecetasTask extends AsyncTask<String, Void, String> {

        String server_response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loader.setVisibility(View.VISIBLE);

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Searching..."); // Setting Message
            progressDialog.setTitle("Synonims"); // Setting Title
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
                    .url("https://wordsapiv1.p.rapidapi.com/words/shrimp/synonyms")
                    .get()
                    .addHeader(hh,"wordsapiv1.p.rapidapi.com")
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
            JSONArray resultsArray = json.getJSONArray("synonyms");

            ArrayList<String> ingSinonimos = new ArrayList<String>();


            String ingredientes = "";
            for (int indice = 0; indice < resultsArray.length(); indice++) {
                // Obtener objeto a través del índice
                ingredientes +=  resultsArray.get(indice).toString() + ",";

            }

            System.out.println("***** resultados " + resultado);



        }
        catch (Exception e) {
            System.out.println("***** Error Exception en convertirStringJson " + e.getMessage());
        }
    }




    public class LoginTask extends AsyncTask<String, Void, String> {

        String server_response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loader.setVisibility(View.VISIBLE);

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Login..."); // Setting Message
            progressDialog.setTitle("Food Allergy Help"); // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog.show(); // Display Progress Dialog
            progressDialog.setCancelable(false);


        }


        @Override
        protected String doInBackground(String... params) {


            return null;


        }


        @Override
        protected void onPostExecute(String resultado) {


            progressDialog.dismiss();
        }

    }
}
