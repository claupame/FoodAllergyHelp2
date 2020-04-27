package com.example.foodallergyhelp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodallergyhelp.R;
import com.example.foodallergyhelp.models.UsuarioAlergiasObject;

import java.util.List;

/**
 * Created by Pamela on 15/1/2020.
 */

public class UsuarioAlergiasAdapter extends BaseAdapter {
    private Context context;
    public static List<UsuarioAlergiasObject> items;
   // UsuarioAlergiasObject item;


    public UsuarioAlergiasAdapter(Context context, List<UsuarioAlergiasObject> items) {
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
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder(); LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_ingredient, null, true);

            holder.chkSelecciona = (CheckBox) convertView.findViewById(R.id.chkSelecciona);
            holder.txtNombre = (TextView) convertView.findViewById(R.id.txtNombre);
            holder.imgIngrediente = (ImageView)convertView.findViewById(R.id.imgIngrediente);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        holder.txtNombre.setText(items.get(position).getNombre());
        holder.imgIngrediente.setImageResource(items.get(position).getImagen());

        holder.chkSelecciona.setChecked(items.get(position).isSeleccionado());

        holder.chkSelecciona.setTag( position);
        holder.chkSelecciona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*View tempview = (View) holder.chkSelecciona.getTag(R.integer.btnplusview);
                TextView tv = (TextView) tempview.findViewById(R.id.animal);*/
                Integer pos = (Integer)  holder.chkSelecciona.getTag();
                //Toast.makeText(context, "Checkbox "+pos+" clicked!", Toast.LENGTH_SHORT).show();

                if(items.get(pos).isSeleccionado()){
                    items.get(pos).setSeleccionado(false);
                }else {
                    items.get(pos).setSeleccionado(true);
                }

            }
        });

        return convertView;

    }


    private class ViewHolder {

        private ImageView imgIngrediente;
        private TextView txtNombre;
        protected CheckBox chkSelecciona;

    }


}
