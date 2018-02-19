package com.ecosystem.kin.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.view.View.OnClickListener;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.exception.TaskFailedException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.launch_marketplace).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Kin.launchMarketplace(MainActivity.this);
                } catch (TaskFailedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
