package com.example.foodallergyhelp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.foodallergyhelp.R;
import com.example.foodallergyhelp.models.AlergenosEncObject;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by Pamela on 22/2/2020.
 */

public class AnalysisAdapter2 extends BaseAdapter {
    private Context context;
    public static List<AlergenosEncObject> items;


    public AnalysisAdapter2(Context context, List<AlergenosEncObject> items) {
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
            convertView = inflater.inflate(R.layout.item_analysis2, null, true);

            holder.tvNombreAlerg = (TextView) convertView.findViewById(R.id.tvNombreAlerg);
            holder.tvCantidad = (TextView) convertView.findViewById(R.id.tvCantidad);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvNombreAlerg.setText(items.get(position).getAlergeno());

        List<SliceValue> pieData = new ArrayList<>();

        int contains = items.get(position).getEncontrados().size();

        String color;
        if(contains >0){
            color = context.getString(Integer.parseInt(String.valueOf(R.color.red)));
        }
        else{
            color = context.getString(Integer.parseInt(String.valueOf(R.color.colorPrimaryDark)));

        }

        holder.tvNombreAlerg.setTextColor(Color.parseColor(color));
        holder.tvCantidad.setTextColor(Color.parseColor(color));


        float pc = (contains * 100) / items.get(position).getTotal();

        if(pc > 100) {
            contains = items.get(position).getTotal();
            pc = 100;
        }

        int notContains = items.get(position).getTotal()-contains;
        float pnc = 100-pc;

        holder.tvCantidad.setText(pc +"%");


        return convertView;

    }


    private class ViewHolder {
        private TextView tvNombreAlerg;
        private TextView tvCantidad;

    }




}
