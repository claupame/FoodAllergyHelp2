package com.example.foodallergyhelp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.foodallergyhelp.adapters.AnalysisDetailAdapter;
import com.example.foodallergyhelp.adapters.AnalysisUploadAdapter;
import com.example.foodallergyhelp.adapters.LanguageSelectionAdapter;
import com.example.foodallergyhelp.adapters.LanguageSelectionAdapter2;
import com.example.foodallergyhelp.models.BasicObject;
import com.example.foodallergyhelp.models.IdiomaObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UploadImage extends AppCompatActivity {

    ImageView imageUP;
    private static final int RESULT_LOAD_IMAGE = 1;
    ImageButton btnTraduce;
    ImageButton btnUpload;
    ImageButton btnProcess;
    ImageButton btnCamera;
    TextView tvTraduccion;
    Bitmap bitmap;
    final int RequestCameraPermissionID = 1001;
    Bitmap scaled;
    CardView cdvTexto;

    Spinner spnIdiomaDestino;
    Spinner spnIdiomaOrigen;
    Translate translate;
    private boolean connected;
    //String textoTraducir = "";
    String textoTraducido = "";
    ProgressDialog progressDialog;
    String[] alergenos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);


        imageUP = (ImageView) findViewById(R.id.imageUP);
        btnTraduce = (ImageButton) findViewById(R.id.btnTraduce);
        btnUpload = (ImageButton) findViewById(R.id.btnUpload);
        btnProcess = (ImageButton) findViewById(R.id.btnProcess);
        btnCamera = (ImageButton) findViewById(R.id.btnCamera);
        tvTraduccion = (TextView) findViewById(R.id.tvTraduccion);
        cdvTexto = (CardView) findViewById(R.id.cdvTexto);

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


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryInt = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(galleryInt,RESULT_LOAD_IMAGE);

            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(intent);

            }
        });

        btnTraduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bm=((BitmapDrawable)imageUP.getDrawable()).getBitmap();
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bm);

                FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();


                textRecognizer.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText result) {

                                // Task completed successfully
                                String resultText = result.getText();

/*
                                StringBuilder resultText = new StringBuilder();

                                int contador = 0;
                                 for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
                                    String blockText = block.getText();
                                    Float blockConfidence = block.getConfidence();
                                    List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
                                    Point[] blockCornerPoints = block.getCornerPoints();
                                    Rect blockFrame = block.getBoundingBox();
                                    for (FirebaseVisionText.Line line: block.getLines()) {
                                        if(contador > 0)
                                            resultText.append("*!+");
                                        String lineText = line.getText();
                                        Float lineConfidence = line.getConfidence();
                                        List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                                        Point[] lineCornerPoints = line.getCornerPoints();
                                        Rect lineFrame = line.getBoundingBox();
                                        for (FirebaseVisionText.Element element: line.getElements()) {
                                            String elementText = element.getText();
                                            Float elementConfidence = element.getConfidence();
                                            List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                                            Point[] elementCornerPoints = element.getCornerPoints();
                                            Rect elementFrame = element.getBoundingBox();

                                            resultText.append(elementText);
                                        }
                                        contador ++;
                                    }
                                }
                                */


                                tvTraduccion.setText(resultText);

                                if (Constantes.checkInternetConnection(getApplicationContext())) {


                                    getTranslateService();

                                    String texto = resultText.replace(System.lineSeparator()," # ");


                                    translate(texto,  ((IdiomaObject)spnIdiomaOrigen.getSelectedItem()).getCodigo(),
                                            ((IdiomaObject)spnIdiomaDestino.getSelectedItem()).getCodigo(),"traduce");


                                } else {

                                    //If not, display "no connection" warning:
                                    //translatedTv.setText(getResources().getString(R.string.no_connection));
                                    tvTraduccion.setText("No hay conexion a internet");
                                }

                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<FirebaseVisionText>() {
                            @Override
                            public void onComplete(@NonNull Task<FirebaseVisionText> task) {
                                setDialog(false);
                            }
                        })


                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...

                                    }
                                });


            }
        });


        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bm=((BitmapDrawable)imageUP.getDrawable()).getBitmap();
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bm);

                FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();


                textRecognizer.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText result) {

                                // Task completed successfully
                                String resultText = result.getText();
                                tvTraduccion.setText(resultText);


                                if (Constantes.checkInternetConnection(getApplicationContext())) {


                                    getTranslateService();
                                    String texto = resultText.toString().replace(System.lineSeparator()," # ");
                                    translate(texto, ((IdiomaObject)spnIdiomaOrigen.getSelectedItem()).getCodigo(),
                                            "en","process");


                                } else {

                                    //If not, display "no connection" warning:
                                    //translatedTv.setText(getResources().getString(R.string.no_connection));
                                    tvTraduccion.setText("There are not internet connection");
                                }

                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<FirebaseVisionText>() {
                            @Override
                            public void onComplete(@NonNull Task<FirebaseVisionText> task) {
                                setDialog(false);
                            }
                        })


                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...

                                    }
                                });


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
        tvTraduccion.setText(textoTraducido2);

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
        builder.setTitle("Possible allergens found");
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





    private void useImage(Uri uri)
    {
        try {

            Glide.with(getApplicationContext())
                    .load(uri)
                    .into(imageUP);

        }
        catch (Exception e){
            System.out.println("************* Error " + e.getMessage());
        }
    }


    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getRotationCompensation(String cameraId, Activity activity, Context context)
            throws CameraAccessException {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);
        // On most devices, the sensor orientation is 90 degrees, but for some
        // devices it is 270 degrees. For devices with a sensor orientation of
        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
        CameraManager cameraManager = (CameraManager) context.getSystemService(CAMERA_SERVICE);
        int sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION);
        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360;
        // Return the corresponding FirebaseVisionImageMetadata rotation value.
        int result;
        switch (rotationCompensation) {
            case 0:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                break;
            case 90:
                result = FirebaseVisionImageMetadata.ROTATION_90;
                break;
            case 180:
                result = FirebaseVisionImageMetadata.ROTATION_180;
                break;
            case 270:
                result = FirebaseVisionImageMetadata.ROTATION_270;
                break;
            default:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                //Log.e(TAG, "Bad rotation value: " + rotationCompensation);
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            final Uri uri = data.getData();

            useImage(uri);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_upload_image,menu);
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
