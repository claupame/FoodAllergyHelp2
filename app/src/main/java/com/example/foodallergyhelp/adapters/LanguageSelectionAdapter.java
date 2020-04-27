package com.example.foodallergyhelp.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.foodallergyhelp.R;
import com.example.foodallergyhelp.models.IdiomaObject;

/**
 * Created by Pamela on 3/10/2019.
 */

public  class LanguageSelectionAdapter extends BaseAdapter {

    private IdiomaObject[] mLangs;


    public LanguageSelectionAdapter(Context context) {

        Resources resources = context.getResources();

        String[] codes = resources.getStringArray(R.array.idioma_cod_arrays);
        String[] names = resources.getStringArray(R.array.idioma_nombre_arrays);

        mLangs = new IdiomaObject[names.length];
        for (int i = 0; i < names.length; i++) {
            String code = codes[i];
            String name = names[i];
            mLangs[i] = new IdiomaObject(code, name);
        }
    }

    @Override
    public int getCount() {
        return mLangs == null ? 0 : mLangs.length;
    }

    @Override
    public Object getItem(int position) {
        return mLangs == null ? null : mLangs[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
        text1.setText(mLangs[position].getNombre());

        return convertView;
    }
}
