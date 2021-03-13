package com.bertalabs.baroflight.ui.front;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bertalabs.baroflight.R;
import com.bertalabs.baroflight.ext.LightIntensity;
import com.bertalabs.baroflight.ui.LightAdapter;

public class FrontFragment extends Fragment {

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 5000;
    private FrontViewModel frontViewModel;
    private LightAdapter adapter;

    ToggleButton powerToggle, linkToggle, masterToggle;

    private long lastTouchTime = 0;
    private long currentTouchTime = 0;

    private ToggleButton makePowerToggleButton(ToggleButton aToggle) {
        aToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    frontViewModel.getLightCache().modifyIntensity(LightIntensity.MEDIUM);
                    aToggle.setBackgroundDrawable(
                            ResourcesCompat.getDrawable(getResources(),
                                    R.drawable.ic_power_button_on,
                                    null));
                } else {
                    frontViewModel.getLightCache().modifyIntensity(LightIntensity.OFF);
                    aToggle.setBackgroundDrawable(
                            ResourcesCompat.getDrawable(getResources(),
                                    R.drawable.ic_power_button_off,
                                    null));
                }
            }
        });


        return aToggle;
    }


    private ToggleButton makeMasterToggle(ToggleButton aToggle, ToggleButton masterToggle) {
        aToggle.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean buttonState = aToggle.isChecked();
                lastTouchTime = currentTouchTime;
                currentTouchTime = System.currentTimeMillis();
                if (currentTouchTime - lastTouchTime < 400) {
                    aToggle.setChecked(buttonState);
                    masterToggle.setChecked(buttonState);
                    lastTouchTime = 0;
                    currentTouchTime = 0;
                } else {
                    aToggle.setChecked(!buttonState);
                }
            }
        });
        return aToggle;
    }

    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                frontViewModel.update();
                adapter.notifyDataSetChanged();
            }
        }, delay);
        super.onResume();
    }


    public void onPause() {
        //stop handler when activity is not visible
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    public void onStop() {
        frontViewModel.getLightCache().destroy();
        super.onStop();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        frontViewModel =
                ViewModelProviders.of(this).get(FrontViewModel.class);
        View root = inflater.inflate(R.layout.fragment_front, container, false);
        powerToggle = makePowerToggleButton(root.findViewById(R.id.powerToggle));
        masterToggle = makeMasterToggle(root.findViewById(R.id.masterToggle), root.findViewById(R.id.powerToggle));
        RecyclerView recyclerView = root.findViewById(R.id.lightView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new LightAdapter(root.getContext(), frontViewModel.getLights());
        recyclerView.setAdapter(adapter);
        return root;
    }

}