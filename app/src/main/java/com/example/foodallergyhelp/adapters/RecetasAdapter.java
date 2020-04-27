package com.example.foodallergyhelp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodallergyhelp.Constantes;
import com.example.foodallergyhelp.R;
import com.example.foodallergyhelp.models.RecetaObject;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Pamela on 15/1/2020.
 */

public class RecetasAdapter extends BaseAdapter {
    private Context context;
    private List<RecetaObject> items;


    public RecetasAdapter(Context context, List<RecetaObject> items) {
        this.context = context;
        this.items = items;


    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (convertView == null) {
            // Create a new view into the list.
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.item_recipe, parent, false);
        }         // Set data into the view.

        ImageView imgReceta = (ImageView)rowView.findViewById(R.id.imgReceta);
        TextView txtTitulo = (TextView) rowView.findViewById(R.id.txtTitulo);
        TextView txtDuracion = (TextView) rowView.findViewById(R.id.txtDuracion);
        TextView txtPorciones = (TextView) rowView.findViewById(R.id.txtPorciones);

        RecetaObject item = this.items.get(position);
        txtTitulo.setText(item.getTitulo());
        txtDuracion.setText("Duracion: " + item.getDuracion() + " minutos");
        txtPorciones.setText("Porciones: " + item.getPorciones());

        if(item.getImagen().equals("none"))
            imgReceta.setImageResource(R.drawable.receta2);
        else
            new DownloadImageTask(imgReceta).execute(Constantes.urlImages + item.getImagen());


        return rowView;
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
            Bitmap resized = Bitmap.createScaledBitmap(result, 80, 80, true);
            bmImage.setImageBitmap(resized);
        }
    }


}
