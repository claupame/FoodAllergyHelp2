package com.example.foodallergyhelp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class Splash extends AppCompatActivity {

    private final int DURACION_SPLASH = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        // esconder la base de estado superior
        this.getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable(){
            public void run(){
                SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                String username = settings.getString("Username", "").toString();

                if(username.trim().length()>0) {
                    Intent intent = new Intent(getApplicationContext(),DashBoardPrincipal.class);
                    intent.putExtra("activity","splash");
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("activity","splash");
                    startActivity(intent);
                }
                finish();
            };
        }, DURACION_SPLASH);


    }
}
