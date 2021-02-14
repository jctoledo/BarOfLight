package com.bertalabs.baroflight.ext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bertalabs.baroflight.R;

import java.util.ArrayList;
import java.util.List;

public class LightAdapter extends RecyclerView.Adapter {
    List<Light> lights = new ArrayList<Light>();
    Context context;

    public LightAdapter(Context context, List<Light> lights){
        this.context = context;
        this.lights = lights;
    }

    @NonNull
    @Override
    public LightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);;
        return new LightViewHolder(v);
    }

    public void onBindViewHolder(@NonNull LightViewHolder holder, int position){
        holder.ipAddress.setText(lights.get(position).ip_address);
        holder.temperature.setText(Double.toString(lights.get(position).temperature));
        if (!lights.get(position).isConnected){
            holder.image.setImageResource(R.drawable.wifi_disconected);
            holder.health.setText(R.string.disconnected);
        } else if(!lights.get(position).health){
            holder.health.setText(R.string.not_healthy);
            holder.image.setImageResource(R.drawable.light_bulb_red);
        } else {
            holder.health.setText(R.string.healthy);
            holder.image.setImageResource(R.drawable.light_bulb_green);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        onBindViewHolder((LightViewHolder) holder, position);
    }

    @Override
    public int getItemCount() {
        return lights.size();
    }
}
