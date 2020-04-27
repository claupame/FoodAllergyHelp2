package com.example.foodallergyhelp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.IntentCompat;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class DashBoardPrincipal extends AppCompatActivity {

    CardView cdvUserProfile;
    CardView cdvRecipies;
    CardView cdvScan;
    CardView cdvUpload;
    ImageView imbSignout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board_principal);

        // esconder la base de estado superior
        this.getSupportActionBar().hide();

        /// inicializaciones
        cdvUserProfile = (CardView) findViewById(R.id.cdvUserProfile);
        cdvRecipies = (CardView) findViewById(R.id.cdvRecipies);
        cdvScan = (CardView) findViewById(R.id.cdvScan);
        cdvUpload = (CardView) findViewById(R.id.cdvUpload);
        imbSignout = (ImageView) findViewById(R.id.imbSignout);


        cdvUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),UserProfile.class);
                startActivity(intent);
            }
        });

        cdvRecipies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RecipeSearch.class);
                startActivity(intent);
            }
        });

        cdvScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Scan.class);
                startActivity(intent);
            }
        });


        cdvUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),UploadImage.class);
                startActivity(intent);
            }
        });

        imbSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DashBoardPrincipal.this);
                builder.setTitle("Sign out");
                builder.setMessage(R.string.message_logout)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences preferences = getSharedPreferences("UserInfo", 0);
                                preferences.edit().remove("Username").commit();
                                preferences.edit().remove("Password").commit();

                                String actividadPrevia = getIntent().getStringExtra("activity");

                                if(actividadPrevia.equals("main"))
                                    finish();
                                else {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("activity", "dashboard");
                                    startActivity(intent);
                                }
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
        });

    }
}
