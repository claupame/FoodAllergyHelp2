package com.example.foodallergyhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.foodallergyhelp.adapters.UsuarioAlergiasAdapter;
import com.example.foodallergyhelp.models.UsuarioAlergiasObject;
import com.example.foodallergyhelp.models.UsuarioObject;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserProfile extends AppCompatActivity {
    TextView tvUser;
    TextInputEditText etPassword;
    TextInputEditText etPasswordConf;
    TextInputEditText etEmail;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    List<UsuarioObject> usuarios = new ArrayList<>();

    ListView lstAlergias;
    List<UsuarioAlergiasObject> lstUsuarioAlergias = new ArrayList<UsuarioAlergiasObject>();
    List<UsuarioAlergiasObject> lstUsuarioTemp = new ArrayList<UsuarioAlergiasObject>();
    TypedArray lstIcoNewAlergias;
    Resources res;
    String username ="";
    String[] lstNombresAlergias;
    TypedArray lstIcoAlergias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);



        /// inicializacion de elementos
        tvUser = (TextView) findViewById(R.id.tvUser);
        etPassword = (TextInputEditText) findViewById(R.id.etPassword);
        etPasswordConf = (TextInputEditText) findViewById(R.id.etPasswordConf);
        etEmail = (TextInputEditText) findViewById(R.id.etEmail);
        lstAlergias = (ListView) findViewById(R.id.lstAlergias);

        // obtener datos del usuario
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        username = settings.getString("Username", "").toString();
        String pass = settings.getString("Password", "").toString();

        tvUser.setText("USER: " + username);
        inicializarFirebase();


        res = getResources();
        lstNombresAlergias = res.getStringArray(R.array.alergias_arrays);
        lstIcoAlergias = res.obtainTypedArray(R.array.alergias_ico_arrays);
        lstIcoNewAlergias = res.obtainTypedArray(R.array.ingred_ico_arrays);



        buscarUsuario(username);

    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        //firebaseDatabase.setAndroidContext(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_profile,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean validarTextos() {
        boolean valido = true;
        String password = etPassword.getText().toString();
        String email = etEmail.getText().toString();
        String passwordConf = etPasswordConf.getText().toString();


        if(password.equals("")){
            etPassword.setError("Required");
            valido = false;
        }

        if(email.equals("")){
            etEmail.setError("Required");
            valido = false;
        }

        if(passwordConf.equals("")){
            etPasswordConf.setError("Required");
            valido = false;
        }

        if(!password.equals(passwordConf)){
            etPassword.setError("Do not match");
            etPasswordConf.setError("Do not match");
            valido = false;
        }

        return valido;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.it_save:
                usuarios.clear();
                //buscarUsuario(usuario);
                if(validarTextos())
                    guardarDatos();
                break;

            case R.id.it_add_ing:
                addIngredient();
                break;

            case R.id.it_back:
                finish();
                break;

            case R.id.it_cerrar_sesion:
                cerrarSesion();
                break;

        }

        return true;
    }

    public void addIngredient(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(UserProfile.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_item,null);
        final EditText txt_inputText = (EditText)mView.findViewById(R.id.tvIngrediente);
        Button btn_cancel = (Button)mView.findViewById(R.id.btn_cancel);
        Button btn_okay = (Button)mView.findViewById(R.id.btn_okay);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String val = txt_inputText.getText().toString();
                    int img = lstIcoNewAlergias.getResourceId(0, 0);
                    lstUsuarioAlergias.add(new UsuarioAlergiasObject(val, img, true, true));

                    //UsuarioAlergiasAdapter adapter = new UsuarioAlergiasAdapter(getApplicationContext(), lstUsuarioAlergias);
                    //lstAlergias.setAdapter(adapter);
                    //adapter.notifyDataSetChanged();
                    System.out.println("**** el nuevo es " + val);

                    alertDialog.dismiss();
                }
                catch(Exception e){
                    System.out.println("**** error es " + e.getMessage());
                }
            }
        });
        alertDialog.show();
    }



    private void guardarDatos() {
       // try{
            UsuarioObject user = new UsuarioObject();
            user.setNombre(username);
            user.setPassword(etPassword.getText().toString());
            user.setCorreo(etEmail.getText().toString());

            // ingredientes
            Map<String,Boolean> alergenos = new HashMap<>();
            String alerg = "";
            for (int i = 0; i < UsuarioAlergiasAdapter.items.size(); i++){
                //Insertamos un nuevo elemento en base de datos
                if(UsuarioAlergiasAdapter.items.get(i).isSeleccionado()) {
                    alergenos.put(UsuarioAlergiasAdapter.items.get(i).getNombre(), UsuarioAlergiasAdapter.items.get(i).isAgregado());

                    if (i == UsuarioAlergiasAdapter.items.size() - 1) {
                        alerg += UsuarioAlergiasAdapter.items.get(i).getNombre();
                    } else
                        alerg += UsuarioAlergiasAdapter.items.get(i).getNombre() + ", ";
                }
            }
            user.setAlergenos(alergenos);
            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("Alergenos",alerg);
             editor.apply();


            databaseReference.child("usuarios").child(user.getNombre()).setValue(user) ;

            AlertDialog alertDialog = new AlertDialog.Builder(UserProfile.this).create();
            alertDialog.setTitle("Results");
            alertDialog.setMessage("User Modified");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("no cierra");
                        finish();
                    }
                });
            alertDialog.show();

        /*}
        catch (Exception e){
            System.out.println("***** Error " + e.getMessage());
        }*/
    }

    private void limpiarDatos() {
        //etUser.setText("");
        etPassword.setText("");
        etEmail.setText("");
        etPasswordConf.setText("");
    }

    private void buscarUsuario(final String nombre) {
        // Read from the database
        DatabaseReference dbr  = FirebaseDatabase.getInstance().getReference("usuarios");


        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UsuarioObject usuario = (UsuarioObject)postSnapshot.getValue(UsuarioObject.class);

                    if(usuario.getNombre().equals(nombre))
                        usuarios.add(usuario);
                }

                if(usuarios.size() > 0){
                    //Toast.makeText(getApplicationContext(), "User already exists ", Toast.LENGTH_SHORT).show();
                   // etUser.setError("User already exists ");

                    etPassword.setText(((UsuarioObject)usuarios.get(0)).getPassword());
                    etPasswordConf.setText(((UsuarioObject)usuarios.get(0)).getPassword());
                    etEmail.setText(((UsuarioObject)usuarios.get(0)).getCorreo());

                    cargarAlergenos(((UsuarioObject)usuarios.get(0)).getAlergenos());
                }
                /*else{
                    if(validarTextos())
                        guardarDatos();

                }*/
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    /// carga los alergenos del usuario
    private void cargarAlergenos(Map<String, Boolean> alergenos) {

        // lee cada uno de los ingredientes del listview
        for (int i = 0; i < lstNombresAlergias.length; i++) {
            // consulta si el alergeno actual hay que seleccionar y si es agregado
            UsuarioAlergiasObject obj  = existeAler(alergenos,lstNombresAlergias[i]);

            if(obj.getNombre() != null)  // si esta seleccionado
                lstUsuarioAlergias.add(new UsuarioAlergiasObject(lstNombresAlergias[i],
                    lstIcoAlergias.getResourceId(i,0), true,obj.isAgregado()));
            else
                lstUsuarioAlergias.add(new UsuarioAlergiasObject(lstNombresAlergias[i],
                        lstIcoAlergias.getResourceId(i,0), false,false));
        }

        /// añadir agregados
        ArrayList<String> agregados = new ArrayList<String>();
        agregados = addAgregados(alergenos);
        for(String ag: agregados){
            lstUsuarioAlergias.add(new UsuarioAlergiasObject(ag,
                    R.drawable.ingrediente, true,true));
        }

        UsuarioAlergiasAdapter adapter = new UsuarioAlergiasAdapter(this, lstUsuarioAlergias);
        lstAlergias.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private UsuarioAlergiasObject existeAler(Map<String, Boolean> alergenos, String nombre){
        UsuarioAlergiasObject obj = new UsuarioAlergiasObject();

        for (Map.Entry<String, Boolean> entry : alergenos.entrySet()) {
            String alergeno = entry.getKey();
            Boolean es_agregado = entry.getValue();

            if(nombre.equals(alergeno)) {
                obj = new UsuarioAlergiasObject("EXISTE",true,es_agregado);
                break;
            }
        }
        return obj;
    }

    private ArrayList<String> addAgregados(Map<String, Boolean> alergenos){
        ArrayList<String> agregados = new ArrayList<String>();

        for (Map.Entry<String, Boolean> entry : alergenos.entrySet()) {
            String alergeno = entry.getKey();
            Boolean es_agregado = entry.getValue();

            if(es_agregado) {
                agregados.add(alergeno);
            }
        }
        return agregados;
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
