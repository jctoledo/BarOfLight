package com.bertalabs.baroflight.ui.front;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bertalabs.baroflight.MainActivity;
import com.bertalabs.baroflight.R;
import com.bertalabs.baroflight.ext.LightAdapter;

public class FrontFragment extends Fragment {

    private FrontViewModel frontViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        frontViewModel =
                ViewModelProviders.of(this).get(FrontViewModel.class);


        View root = inflater.inflate(R.layout.fragment_front, container, false);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.lightView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        LightAdapter adapter = new LightAdapter(root.getContext(), ((MainActivity) getActivity()).getCachedLights());
        recyclerView.setAdapter(adapter);

//        final TextView textView = root.findViewById(R.id.text_home);
//        frontViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }
}