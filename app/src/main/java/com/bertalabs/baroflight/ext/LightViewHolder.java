package com.bertalabs.baroflight.ext;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bertalabs.baroflight.R;

import org.w3c.dom.Text;

public class LightViewHolder extends RecyclerView.ViewHolder {
    TextView ipAddress;
    TextView temperature;
    TextView health;
    ImageView image;

    public LightViewHolder(@NonNull View itemView) {
        super(itemView);
        temperature = (TextView) itemView.findViewById(R.id.temperatureValue);
        ipAddress = (TextView) itemView.findViewById(R.id.ipValue);
        image = (ImageView) itemView.findViewById(R.id.lightImage);
        health = (TextView) itemView.findViewById(R.id.healthText);
    }
}
