package com.ecosystem.kin.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.kin.ecosystem.Kin;
import com.kin.ecosystem.exception.TaskFailedException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.launch_marketplace).setOnClickListener(v -> {
            try {
                Kin.launchMarketplace(v.getContext());
            } catch (TaskFailedException e) {
                e.printStackTrace();
            }
        });
    }
}
