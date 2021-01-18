package com.bertalabs.baroflight.ui.ditch;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bertalabs.baroflight.R;

public class DitchFragment extends Fragment {

    private DitchViewModel ditchViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ditchViewModel =
                ViewModelProviders.of(this).get(DitchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ditch, container, false);
        root.setBackgroundColor(Color.parseColor("grey"));
        root.setBackgroundTintMode(PorterDuff.Mode.DARKEN);
        final TextView textView = root.findViewById(R.id.text_notifications);
        ditchViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}