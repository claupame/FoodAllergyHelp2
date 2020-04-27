package com.example.foodallergyhelp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Pamela on 14/1/2020.
 */

public class Constantes {

    //public final static String host = "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com";
   // public final static String key = "03bdaa5a01msh5bebaf85a079fa2p19cc2fjsn7dbf44bba05a";
    //public final static String key = "55eaaaee55msh9696918a99b2151p16808fjsn112e1b0dba20";
    //public  final static String hostHeader= "x-rapidapi-host";
    //public  final  static String keyHeader = "x-rapidapi-key";
    public final static String urlImages = "https://spoonacular.com/recipeImages/";
    public final static String urlBase = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes";


    public static boolean checkInternetConnection(Context cont) {

        boolean connected = false;

        //Check internet connection:
        ConnectivityManager connectivityManager = (ConnectivityManager) cont.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                connected = true;
        }


        return connected;
    }


}
