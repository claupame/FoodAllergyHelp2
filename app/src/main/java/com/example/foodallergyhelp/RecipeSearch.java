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
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.foodallergyhelp.adapters.DietTypeAdapter;
import com.example.foodallergyhelp.adapters.DishTypeAdapter;
import com.example.foodallergyhelp.adapters.IntoleranciasAdapter;
import com.example.foodallergyhelp.adapters.OriginPlaceAdapter;
import com.example.foodallergyhelp.adapters.UsuarioAlergiasAdapter;
import com.example.foodallergyhelp.models.RecetaIngredienteObject;
import com.example.foodallergyhelp.models.UsuarioAlergiasObject;
import com.example.foodallergyhelp.models.UsuarioObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RecipeSearch extends AppCompatActivity {

    TextInputEditText etKeyWords;
    TextInputEditText etIngExclude;
    TextInputEditText etIntoleratedIng;
    TextInputEditText etOrigin;
    TextInputEditText etDietType;
    TextInputEditText etType;
    TextInputEditText etMaxResults;
    CheckBox chkUserData;
    String username;
    FloatingActionButton fbtSearch;

    Resources res;

    String[] lstIntolerancias;
    String[] lstDietas;
    String[] lstTipoPlato;
    ArrayList<Integer> intoleraciaItems = new ArrayList<>();
    boolean[] checkedIntoItems;
    ArrayList<Integer> mUserIntoItems = new ArrayList<>();
    String urlPeticion;
    String urlIngredientes;
    UsuarioObject usuarioObj = new UsuarioObject();
    String alergenosUserS = "";

    List<RecetaIngredienteObject> recetasIngredientes = new ArrayList<RecetaIngredienteObject>();
    TypedArray lstIcoNewAlergias;
    List<UsuarioAlergiasObject> lstUsuarioAlergias = new ArrayList<UsuarioAlergiasObject>();
    List<UsuarioAlergiasObject> lstOriginPlace = new ArrayList<UsuarioAlergiasObject>();
    List<UsuarioAlergiasObject> lstDietType = new ArrayList<UsuarioAlergiasObject>();
    List<UsuarioAlergiasObject> lstIntolerIng = new ArrayList<UsuarioAlergiasObject>();
    List<UsuarioAlergiasObject> lstTypeDish= new ArrayList<UsuarioAlergiasObject>();
    ArrayList selectedItems = new ArrayList();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_search);

        etKeyWords = (TextInputEditText) findViewById(R.id.etKeyWords);
        etIngExclude = (TextInputEditText) findViewById(R.id.etIngExclude);
        etIntoleratedIng = (TextInputEditText) findViewById(R.id.etIntoleratedIng);
        etOrigin = (TextInputEditText) findViewById(R.id.etOrigin);
        etDietType = (TextInputEditText) findViewById(R.id.etDietType);
        etType = (TextInputEditText) findViewById(R.id.etType);
        etMaxResults = (TextInputEditText) findViewById(R.id.etMaxResults);
        chkUserData = (CheckBox) findViewById(R.id.chkUserData);
        fbtSearch = (FloatingActionButton) findViewById(R.id.fbtSearch);

        etIngExclude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogIngredients();
            }
        });

        etIntoleratedIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogIntolerantIng();
            }
        });

        etOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogOriginPlace();
            }
        });

        etDietType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogDietType();
            }
        });

        etType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogType();
            }
        });

        chkUserData.setChecked(true);
        buscarAlergenos();

        chkUserData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(chkUserData.isChecked()){
                    buscarAlergenos();
                }
                else{
                    etIngExclude.setText("");
                }
            }
        });


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


        //////////
        String[] lstNombresLugares;
        res = getResources();
        lstNombresLugares = res.getStringArray(R.array.cocina_arrays);

        //// carga los ingredientes
        lstOriginPlace =  new ArrayList<UsuarioAlergiasObject>();
        for (int i = 0; i < lstNombresLugares.length; i++) {
            lstOriginPlace.add(new UsuarioAlergiasObject(lstNombresLugares[i], false));
        }

        //////////
        String[] listDietType;
        res = getResources();
        listDietType = res.getStringArray(R.array.dieta_arrays);

        //// carga los ingredientes
        lstDietType =  new ArrayList<UsuarioAlergiasObject>();
        for (int i = 0; i < listDietType.length; i++) {
            lstDietType.add(new UsuarioAlergiasObject(listDietType[i], false));
        }

        //////

        String[] lstType;
        res = getResources();
        lstType = res.getStringArray(R.array.tipo_plato_arrays);

        //// carga los ingredientes
        lstTypeDish =  new ArrayList<UsuarioAlergiasObject>();
        for (int i = 0; i < lstType.length; i++) {
            lstTypeDish.add(new UsuarioAlergiasObject(lstType[i], false));
        }


        ////////
        String[] lstInto;
        res = getResources();
        lstInto = res.getStringArray(R.array.intolerancias_arrays);

        //// carga los ingredientes
        lstIntolerIng =  new ArrayList<UsuarioAlergiasObject>();
        for (int i = 0; i < lstInto.length; i++) {
            lstIntolerIng.add(new UsuarioAlergiasObject(lstInto[i], false));
        }

        fbtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultarRecetas("recetas");
            }
        });
    }

    private void buscarAlergenos() {
        // obtener datos del usuario
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        final String username = settings.getString("Username", "").toString();

        // Read from the database
        DatabaseReference dbr  = FirebaseDatabase.getInstance().getReference("usuarios");


        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    UsuarioObject usuario = (UsuarioObject)postSnapshot.getValue(UsuarioObject.class);


                    if(usuario.getNombre().equals(username)) {
                        usuarioObj = usuario;
                        break;
                    }
                }

                if(usuarioObj != null){
                    alergenosUserS = "";
                    int i = 0;

                    for (Map.Entry<String, Boolean> entry : usuarioObj.getAlergenos().entrySet()) {
                        String alergeno = entry.getKey();
                        //Boolean es_agregado = entry.getValue();

                        alergenosUserS += alergeno;
                        i++;
                        if(i < usuarioObj.getAlergenos().size())
                            alergenosUserS += ", ";


                    }


                    etIngExclude.setText(alergenosUserS);
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

    private void openDialogType() {
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(RecipeSearch.this);
        alert.setTitle("Dish Type");
        View mView = getLayoutInflater().inflate(R.layout.dialog_ingredients,null);
        final ListView lstAlergias = (ListView)mView.findViewById(R.id.lstAlergias);
        Button btnOk = (Button)mView.findViewById(R.id.btnOk);
        Button btnCancel = (Button)mView.findViewById(R.id.btnCancel);

        DishTypeAdapter dishTypeAdapter = new DishTypeAdapter(this, lstTypeDish);
        lstAlergias.setAdapter(dishTypeAdapter);

        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valores = "";
                for (int i = 0; i < DishTypeAdapter.items.size(); i++){
                    //Insertamos un nuevo elemento en base de datos
                    if(DishTypeAdapter.items.get(i).isSeleccionado()) {
                        valores += DishTypeAdapter.items.get(i).getNombre();

                        if(i < DishTypeAdapter.items.size() - 1)
                            valores += ", ";
                    }
                }

                if(valores.length()>2)
                    etType.setText(valores.substring(0,valores.length()-2));

                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void openDialogDietType() {
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(RecipeSearch.this);
        alert.setTitle("Diet Type");
        View mView = getLayoutInflater().inflate(R.layout.dialog_ingredients,null);
        final ListView lstAlergias = (ListView)mView.findViewById(R.id.lstAlergias);
        Button btnOk = (Button)mView.findViewById(R.id.btnOk);
        Button btnCancel = (Button)mView.findViewById(R.id.btnCancel);



        DietTypeAdapter dietTypeAdapter = new DietTypeAdapter(this, lstDietType);
        lstAlergias.setAdapter(dietTypeAdapter);

        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valores = "";
                for (int i = 0; i < DietTypeAdapter.items.size(); i++){
                    //Insertamos un nuevo elemento en base de datos
                    if(DietTypeAdapter.items.get(i).isSeleccionado()) {
                        valores += DietTypeAdapter.items.get(i).getNombre();

                        if(i < DietTypeAdapter.items.size() - 1)
                            valores += ", ";
                    }
                }

                if(valores.length()>2)
                    etDietType.setText(valores.substring(0,valores.length()-2));

                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void openDialogIntolerantIng() {

        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(RecipeSearch.this);
        alert.setTitle("Intolerated Ingredients");
        View mView = getLayoutInflater().inflate(R.layout.dialog_ingredients,null);
        final ListView lstAlergias = (ListView)mView.findViewById(R.id.lstAlergias);
        Button btnOk = (Button)mView.findViewById(R.id.btnOk);
        Button btnCancel = (Button)mView.findViewById(R.id.btnCancel);



        IntoleranciasAdapter intoleranciasAdapter = new IntoleranciasAdapter(this, lstIntolerIng);
        lstAlergias.setAdapter(intoleranciasAdapter);

        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valores = "";
                for (int i = 0; i < IntoleranciasAdapter.items.size(); i++){
                    //Insertamos un nuevo elemento en base de datos
                    if(IntoleranciasAdapter.items.get(i).isSeleccionado()) {
                        valores += IntoleranciasAdapter.items.get(i).getNombre();

                        if(i < IntoleranciasAdapter.items.size() - 1)
                            valores += ", ";
                    }
                }

                if(valores.length()>2)
                    etIntoleratedIng.setText(valores.substring(0,valores.length()-2));

                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private void openDialogIngredients() {
        //lstUsuarioAlergias = new ArrayList<UsuarioAlergiasObject>();
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(RecipeSearch.this);
        alert.setTitle("Allergens Ingredients");
        View mView = getLayoutInflater().inflate(R.layout.dialog_ingredients,null);
        final ListView lstAlergias = (ListView)mView.findViewById(R.id.lstAlergias);
        Button btnOk = (Button)mView.findViewById(R.id.btnOk);
        Button btnCancel = (Button)mView.findViewById(R.id.btnCancel);

        UsuarioAlergiasAdapter usuarioAlergiasAdapter = new UsuarioAlergiasAdapter(this, lstUsuarioAlergias);
        lstAlergias.setAdapter(usuarioAlergiasAdapter);


        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String alergenos = "";
                for (int i = 0; i < UsuarioAlergiasAdapter.items.size(); i++){
                    //Insertamos un nuevo elemento en base de datos
                    if(UsuarioAlergiasAdapter.items.get(i).isSeleccionado()) {
                        alergenos += UsuarioAlergiasAdapter.items.get(i).getNombre();

                        if(i < UsuarioAlergiasAdapter.items.size() - 1)
                            alergenos += ", ";
                    }
                }

                if(alergenos.length()>2)
                    etIngExclude.setText(alergenos.substring(0,alergenos.length()-2));


                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }


    private void openDialogOriginPlace() {

        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(RecipeSearch.this);
        alert.setTitle("Origin Place");
        View mView = getLayoutInflater().inflate(R.layout.dialog_ingredients,null);
        final ListView lstAlergias = (ListView)mView.findViewById(R.id.lstAlergias);
        Button btnOk = (Button)mView.findViewById(R.id.btnOk);
        Button btnCancel = (Button)mView.findViewById(R.id.btnCancel);



        OriginPlaceAdapter originPlaceAdapter = new OriginPlaceAdapter(this, lstOriginPlace);
        lstAlergias.setAdapter(originPlaceAdapter);

        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valores = "";
                for (int i = 0; i < OriginPlaceAdapter.items.size(); i++){
                    //Insertamos un nuevo elemento en base de datos
                    if(OriginPlaceAdapter.items.get(i).isSeleccionado()) {
                        valores += OriginPlaceAdapter.items.get(i).getNombre();

                        if(i < OriginPlaceAdapter.items.size() - 1)
                            valores += ", ";
                    }
                }

                if(valores.length()>2)
                    etOrigin.setText(valores.substring(0,valores.length()-2));

                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.it_search:
                consultarRecetas("recetas");
                break;

            case R.id.it_analysis:
                consultarRecetas("ingredientes");
                break;

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
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(RecipeSearch.this);
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


    private void consultarRecetas(String tipoConsul){
        String cocina, dieta, ingEx, ingInt, tipo;
        int numero = 0;

        if(etKeyWords.getText().toString().trim().length() == 0)
            etKeyWords.setError("Required");

        else {

            /////////// validar valores ////////
            if (etOrigin.getText().toString().trim().length() == 0)
                cocina  =getString(R.string.val_s_defecto);
            else
                cocina = etOrigin.getText().toString().trim();

            if (etDietType.getText().toString().trim().length() == 0)
                dieta = getString(R.string.val_s_defecto);
            else
                dieta = etDietType.getText().toString().trim();


            if(tipoConsul.equals("recetas")){
                if (etIngExclude.getText().toString().trim().length() == 0)
                    ingEx =getString(R.string.val_s_defecto);
                else
                    ingEx = etIngExclude.getText().toString().trim();
            }
            else{
                ingEx = "none";
            }


            if (etIntoleratedIng.getText().toString().trim().length() == 0)
                ingInt = getString(R.string.val_s_defecto);
            else
                ingInt = etIntoleratedIng.getText().toString().trim();

            if(tipoConsul.equals("recetas")) {
                if (etMaxResults.getText().toString().trim().length() == 0)
                    numero = this.getResources().getInteger( R.integer.val_n_defecto);
                else
                    numero = Integer.parseInt(etMaxResults.getText().toString());
            }
            else{
                if (etMaxResults.getText().toString().trim().length() == 0)
                    numero = this.getResources().getInteger( R.integer.val_n_defecto_ing);
                else
                    numero = Integer.parseInt(etMaxResults.getText().toString());
            }


            if (etType.getText().toString().trim().length() == 0)
                tipo = getString(R.string.val_s_defecto);
            else
                tipo = etType.getText().toString().trim();




            ///////// construye la peticion ///////////
            urlPeticion = Constantes.urlBase + "/search?" +
                    "cuisine=" + cocina + "&" +
                    "diet=" + dieta + "&" +
                    "excludeIngredients=" + ingEx + "&" +
                    "intolerances=" + ingInt + "&" +
                    "number=" + numero + "&offset=0&" +
                    "type=" + tipo + "&" +
                    "query=" + etKeyWords.getText();


            Intent intent ;
            if(tipoConsul.equals("recetas")){
                intent = new Intent(getApplicationContext(), Recipes.class);
                intent.putExtra("urlPeticion", urlPeticion);
                startActivity(intent);
            }
            else{
                if(etIngExclude.getText().toString().trim().length()>0) {
                    intent = new Intent(getApplicationContext(), Analysis.class);
                    intent.putExtra("keyword", etKeyWords.getText().toString().trim());
                    intent.putExtra("alergenosUser", etIngExclude.getText().toString());
                    intent.putExtra("urlPeticion", urlPeticion);
                    startActivity(intent);
                }
                else{
                    etIngExclude.setError("Required");
                }

            }



        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipe_search,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
