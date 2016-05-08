package com.aditya.ctrl.thedailypromo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aditya.ctrl.thedailypromo.R;
import com.aditya.ctrl.thedailypromo.activity.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterArea extends RecyclerView.Adapter<AdapterArea.ViewHolder> {

    ArrayList<HashMap<String, String>> list_area;
    HashMap<String, String> item_data = new HashMap<>();

    public AdapterArea(ArrayList<HashMap<String, String>> list_area) {
        this.list_area = list_area;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView holder_text;
        public ViewHolder(View view) {
            super(view);
            holder_text = (TextView) view.findViewById(R.id.txt_promo);
        }
    }

    @Override
    public int getItemCount() {
        return list_area.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View viewItem = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (list_area != null) {
            item_data = list_area.get(position);
            viewHolder.holder_text.setText(item_data.get(MainActivity.PROMOLIST));
        }
    }
}
