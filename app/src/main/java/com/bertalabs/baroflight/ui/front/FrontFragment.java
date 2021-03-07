package com.bertalabs.baroflight.ui.front;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bertalabs.baroflight.R;
import com.bertalabs.baroflight.ext.LightAdapter;

public class FrontFragment extends Fragment {

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 5000;
    private FrontViewModel frontViewModel;
    private LightAdapter adapter;

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
        RecyclerView recyclerView = root.findViewById(R.id.lightView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new LightAdapter(root.getContext(), frontViewModel.getLights());
        recyclerView.setAdapter(adapter);
        return root;
    }

}