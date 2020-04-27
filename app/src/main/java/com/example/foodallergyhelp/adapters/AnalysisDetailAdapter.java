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
import com.example.foodallergyhelp.models.BasicObject;
import com.example.foodallergyhelp.models.UsuarioAlergiasObject;

import java.util.List;

/**
 * Created by Pamela on 15/1/2020.
 */

public class AnalysisDetailAdapter extends BaseAdapter {
    private Context context;
    public static List<BasicObject> items;
   // UsuarioAlergiasObject item;


    public AnalysisDetailAdapter(Context context, List<BasicObject> items) {
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
            convertView = inflater.inflate(R.layout.item_analysisdet, null, true);

            holder.tvNombre = (TextView) convertView.findViewById(R.id.tvNombre);


            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvNombre.setText(items.get(position).getNombre()  + " : " + items.get(position).getValor());




        return convertView;

    }


    private class ViewHolder {
        private TextView tvNombre;



    }


}
