package com.bertalabs.baroflight.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bertalabs.baroflight.R;

public class LightViewHolder extends RecyclerView.ViewHolder {
    TextView ipAddress;
    TextView temperature;
    TextView health;
    ImageView image;

    public LightViewHolder(@NonNull View itemView) {
        super(itemView);
        temperature = itemView.findViewById(R.id.temperatureValue);
        ipAddress = itemView.findViewById(R.id.ipValue);
        image = itemView.findViewById(R.id.lightImage);
        health = itemView.findViewById(R.id.healthText);
    }
}
