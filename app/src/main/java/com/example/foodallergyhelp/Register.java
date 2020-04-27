package com.example.foodallergyhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

import okhttp3.OkHttpClient;

public class Register extends AppCompatActivity {

    TextInputEditText etUser;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /// inicializacion de elementos
        etUser = (TextInputEditText) findViewById(R.id.etUser);
        etPassword = (TextInputEditText) findViewById(R.id.etPassword);
        etPasswordConf = (TextInputEditText) findViewById(R.id.etPasswordConf);
        etEmail = (TextInputEditText) findViewById(R.id.etEmail);
        lstAlergias = (ListView) findViewById(R.id.lstAlergias);


        try {
            inicializarFirebase();

            String[] lstNombresAlergias;
            TypedArray lstIcoAlergias;
            res = getResources();
            lstNombresAlergias = res.getStringArray(R.array.alergias_arrays);
            lstIcoAlergias = res.obtainTypedArray(R.array.alergias_ico_arrays);
            lstIcoNewAlergias = res.obtainTypedArray(R.array.ingred_ico_arrays);

            //// carga los ingredientes
            for (int i = 0; i < lstNombresAlergias.length; i++) {
                lstUsuarioAlergias.add(new UsuarioAlergiasObject(lstNombresAlergias[i], lstIcoAlergias.getResourceId(i,0), false,false));
            }


            lstAlergias.setAdapter(new UsuarioAlergiasAdapter(this, lstUsuarioAlergias));
        }
        catch(Exception e){
            System.out.println("***** error  " + e.getMessage());
        }


    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        //firebaseDatabase.setAndroidContext(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean validarTextos() {
        boolean valido = true;
        String user = etUser.getText().toString();
        String password = etPassword.getText().toString();
        String email = etEmail.getText().toString();
        String passwordConf = etPasswordConf.getText().toString();


        if(user.equals("")){
            etUser.setError("Required");
            valido = false;
        }


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
                buscarUsuario(etUser.getText().toString());
                break;

            case R.id.it_add_ing:
                addIngredient();
                break;

            case R.id.it_back:
                finish();
                break;

        }

        return true;
    }

    public void addIngredient(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(Register.this);
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

                String val = txt_inputText.getText().toString();
                int img = lstIcoNewAlergias.getResourceId(0,0);
                lstUsuarioAlergias.add(new UsuarioAlergiasObject(val, img, true, true));

                //lstAlergias.setAdapter(new UsuarioAlergiasAdapter(getApplicationContext(), lstUsuarioAlergias));
                //System.out.println("**** el nuevo es " + val);

                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }



    private void guardarDatos() {
        try{
            UsuarioObject user = new UsuarioObject();
            user.setNombre(etUser.getText().toString());
            user.setPassword(etPassword.getText().toString());
            user.setCorreo(etEmail.getText().toString());

            // ingredientes
            Map<String,Boolean> alergenos = new HashMap<>();
            for (int i = 0; i < UsuarioAlergiasAdapter.items.size(); i++){
                //Insertamos un nuevo elemento en base de datos
                if(UsuarioAlergiasAdapter.items.get(i).isSeleccionado())
                    alergenos.put(UsuarioAlergiasAdapter.items.get(i).getNombre(),
                            UsuarioAlergiasAdapter.items.get(i).isAgregado());
            }
            user.setAlergenos(alergenos);

            databaseReference.child("usuarios").child(user.getNombre()).setValue(user) ;

            //limpiarDatos();
            new AlertDialog.Builder(Register.this)
                    .setTitle("Results")
                    .setMessage("Added user")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    }).show();

        }
        catch (Exception e){
            System.out.println("***** Error " + e.getMessage());
        }
    }

    private void limpiarDatos() {
        etUser.setText("");
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
                    etUser.setError("User already exists ");
                }
                else{
                    if(validarTextos())
                        guardarDatos();

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
}
