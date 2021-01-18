package com.bertalabs.baroflight.ui.fill;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bertalabs.baroflight.R;

public class FillFragment extends Fragment {

    private FillViewModel fillViewModel;
    private ProgressBar progress;
    private TextView text;
    private Button button;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fillViewModel =
                ViewModelProviders.of(this).get(FillViewModel.class);
        View root = inflater.inflate(R.layout.fragment_fill, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        fillViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        progress = (ProgressBar) root.findViewById(R.id.progressBar1);
        text = (TextView) root.findViewById(R.id.textView1);
        button = (Button) root.findViewById(R.id.button1);
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                startProgress(view);
            }
        });
        return root;
    }

    public void startProgress(View view) {
        // do something long
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 10; i++) {
                    final int value = i;
                    doFakeWork();
                    progress.post(new Runnable() {
                        @Override
                        public void run() {
                            text.setText("Updating");
                            progress.setProgress(value);
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }

    // Simulating something timeconsuming
    private void doFakeWork() {
        SystemClock.sleep(500);
    }
}