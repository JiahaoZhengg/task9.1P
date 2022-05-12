package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.utils.PermissionUtil;

public class MainActivity extends AppCompatActivity {

    private Button btnCreate;
    private Button btnShow;
    private Button btnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!PermissionUtil.isHasLocationPermission(this)) {
            PermissionUtil.requestLocationPermission(this);
        }
        initView();

    }

    private void initView() {
        btnMap = (Button) findViewById(R.id.btn_map);
        btnCreate = (Button) findViewById(R.id.btn_create);
        btnShow = (Button) findViewById(R.id.btn_show);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateActivity.class));

            }
        });
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ShowListActivity.class));

            }
        });
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ShowOnMapActivity.class));

            }
        });
    }
}