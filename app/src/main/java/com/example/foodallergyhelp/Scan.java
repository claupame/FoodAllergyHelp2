package com.example.foodallergyhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
//import androidx.multidex.MultiDex;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.foodallergyhelp.adapters.AnalysisUploadAdapter;
import com.example.foodallergyhelp.adapters.LanguageSelectionAdapter;
import com.example.foodallergyhelp.adapters.LanguageSelectionAdapter2;
import com.example.foodallergyhelp.models.BasicObject;
import com.example.foodallergyhelp.models.IdiomaObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Scan extends AppCompatActivity {
    SurfaceView sfvCamara;
    TextView tvCamara;

    CameraSource cameraSource;

    ImageButton btnEscanear;
    final int RequestCameraPermissionID = 1001;
    ImageButton btnProcesar;
    ImageButton btnTraducir;
    //ImageButton btnFlash;
    //Spinner spnIdiomaOrigen;
    Spinner spnIdiomaDestino;
    Spinner spnIdiomaOrigen;
    Translate translate;
    private boolean connected;
    String textoTraducir = "";
    String textoTraducido = "";
    boolean encender = false;
    ProgressDialog progressDialog;
    String[] alergenos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        sfvCamara = (SurfaceView) findViewById(R.id.sfvCamara);
        tvCamara = (TextView) findViewById(R.id.tvCamara);
        btnEscanear = (ImageButton)findViewById(R.id.btnEscanear);
       // btnFlash = (ImageButton)findViewById(R.id.btnFlash);
        btnProcesar = (ImageButton)findViewById(R.id.btnProcesar);
        btnTraducir = (ImageButton)findViewById(R.id.btnTraducir);
        spnIdiomaDestino = (Spinner) findViewById(R.id.spnIdiomaDestino);
        spnIdiomaOrigen = (Spinner) findViewById(R.id.spnIdiomaOrigen);

        LanguageSelectionAdapter2 adaptadorIdiomaOrig = new LanguageSelectionAdapter2(getApplicationContext());
        LanguageSelectionAdapter adaptadorIdioma = new LanguageSelectionAdapter(getApplicationContext());
        //spnIdiomaOrigen.setAdapter(adaptadorIdioma);
        spnIdiomaDestino.setAdapter(adaptadorIdioma);
        spnIdiomaOrigen.setAdapter(adaptadorIdiomaOrig);

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        String alerg = settings.getString("Alergenos", "").toString();

        alergenos = alerg.split(",");

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (textRecognizer.isOperational()) {

            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(640, 480)
                    .setAutoFocusEnabled(true)
                    .build();

            sfvCamara.getHolder().addCallback(new SurfaceHolder.Callback() {

                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2){
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder){
                    cameraSource.stop();
                }

            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if(items.size() != 0){
                        tvCamara.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i = 0; i<items.size();i++){
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                tvCamara.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }


        btnEscanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Scan.this,
                                new String[]{Manifest.permission.CAMERA},
                                RequestCameraPermissionID );

                        return;
                    }
                    cameraSource.start(sfvCamara.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });



        btnTraducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraSource.stop();
                /*Toast.makeText(getActivity().getApplicationContext(), "el valor es" + tvCamara.getText(),
                        Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity().getApplicationContext(), "el valor es " + ((Idioma)spnIdiomaOrigen.getSelectedItem()).getCodigo(),
                        Toast.LENGTH_LONG).show();*/
                textoTraducir = tvCamara.getText().toString();

                if (Constantes.checkInternetConnection(getApplicationContext())) {

                    //If there is internet connection, get translate service and start translation:
                    getTranslateService();
                    String texto = textoTraducir.replace(System.lineSeparator()," # ");
                    translate(texto, ((IdiomaObject)spnIdiomaOrigen.getSelectedItem()).getCodigo(),
                            ((IdiomaObject)spnIdiomaDestino.getSelectedItem()).getCodigo(),"traduce");


                } else {

                    //If not, display "no connection" warning:
                    //translatedTv.setText(getResources().getString(R.string.no_connection));
                    tvCamara.setText("No hay conexion a internet");
                }


            }
        });

       /* btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    encender = !encender;
                    CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                    String cameraId = camManager.getCameraIdList()[0]; // usualmente la camara delantera esta en la posicion 0
                    camManager.setTorchMode(cameraId, encender);
                }
                catch (Exception e){
                    System.out.println("******** error" + e.getMessage());
                }
            }
        });*/

        btnProcesar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cameraSource.stop();
                /*Toast.makeText(getActivity().getApplicationContext(), "el valor es" + tvCamara.getText(),
                        Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity().getApplicationContext(), "el valor es " + ((Idioma)spnIdiomaOrigen.getSelectedItem()).getCodigo(),
                        Toast.LENGTH_LONG).show();*/
                textoTraducir = tvCamara.getText().toString();

                if (Constantes.checkInternetConnection(getApplicationContext())) {

                    //If there is internet connection, get translate service and start translation:
                    getTranslateService();
                    String texto = textoTraducir.toString().replace(System.lineSeparator()," # ");
                    translate(texto,  ((IdiomaObject)spnIdiomaOrigen.getSelectedItem()).getCodigo(),
                            "en","process");


                } else {

                    //If not, display "no connection" warning:
                    //translatedTv.setText(getResources().getString(R.string.no_connection));
                    tvCamara.setText("There are not internet connection");
                }



            }
        });
    }


    private void setDialog(boolean show){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //View view = getLayoutInflater().inflate(R.layout.progress);
        builder.setView(R.layout.dialog_progress);
        Dialog dialog = builder.create();
        if (show)dialog.show();
        else dialog.dismiss();
    }

    public void getTranslateService() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (InputStream is = getResources().openRawResource(R.raw.credentialsgoogle)) {

            //Get credentials:
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //Set credentials and get translate service:
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();

        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }

    public void translate(String originalText, String idiomaOrigen, String idiomaDestino, String tipo) {


        ///////////// IDIOMA AL QUE SE QUIERE TRADUCIR ////////////////
        Translation translation;
        if(idiomaOrigen.equals("n"))
            translation = translate.translate(originalText,
                Translate.TranslateOption.targetLanguage(idiomaDestino),
                Translate.TranslateOption.model("base"));
        else
            translation = translate.translate(originalText,
                    // Translate.TranslateOption.targetLanguage(idioma),
                    Translate.TranslateOption.sourceLanguage(idiomaOrigen),
                    Translate.TranslateOption.targetLanguage(idiomaDestino),
                    Translate.TranslateOption.model("base"));


        textoTraducido = translation.getTranslatedText();
        String textoTraducido2 = textoTraducido.replace(" # ",System.lineSeparator()).replace("#"," ");
        //Translated text and original text are set to TextViews:
        tvCamara.setText(textoTraducido2);

        if(tipo.equals("process")){
            buscaIngredientes(textoTraducido2);
        }


    }

    private void buscaIngredientes(String textoTraducido) {
        String[] alergTrad = textoTraducido.split("[,.:]");
        List<BasicObject> encontrados = new ArrayList<BasicObject>();

        String enc = "";
        for (String ingredAlerg : alergenos) {  // ingredientes a los que tiene alergia
            for (String ingredAlergT : alergTrad) {  // ingredientes cargados de la imagen
                if (ingredAlergT.toLowerCase().trim().contains(ingredAlerg.toLowerCase().trim())) {
                    encontrados.add(new BasicObject(ingredAlergT,0));
                    enc += ingredAlergT + "";
                }
            }
        }


            openDialogDetail(encontrados);
    }

    private void openDialogDetail(List<BasicObject> encontrados ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        View mView = getLayoutInflater().inflate(R.layout.dialog_allergy_detail,null);

        ListView lvDetail = (ListView) mView.findViewById(R.id.lvDetail);
        ImageView imvResult   = (ImageView) mView.findViewById(R.id.imvResult);
        TextView tvResult   = (TextView) mView.findViewById(R.id.tvResult);

        if(encontrados.size()==0){
            builder.setTitle(getString (R.string.no_aller_title));
            imvResult.setImageResource(R.drawable.eat);
            tvResult.setText(getString (R.string.no_aller));
            tvResult.setTextColor(Color.parseColor("#00D300"));
        }
        else{
            builder.setTitle(getString (R.string.aller_det_title));
            imvResult.setImageResource(R.drawable.noteat);
            tvResult.setText(getString (R.string.aller_det));
            tvResult.setTextColor(Color.RED);
        }


        lvDetail.setAdapter(new AnalysisUploadAdapter(this, encontrados));

        builder.setView(mView)
                // Add action buttons
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        builder.create().show();


    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case RequestCameraPermissionID:
            {
                System.out.println("entra a onRequestPermissionsResult ****************** ");
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(sfvCamara.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("hay un error  onRequestPermissionsResult ****************** "+ e.getMessage());
                    }
                }
            }
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan,menu);
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
