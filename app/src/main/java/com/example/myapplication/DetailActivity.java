package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.db.bean.LostFoundBean;

import java.io.Serializable;

public class DetailActivity extends AppCompatActivity {

    private TextView tvType;
    private TextView tvName;
    private TextView tvPhone;
    private TextView tvDesc;
    private TextView tvDate;
    private TextView tvLocation;
    private Button btnRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initView();
    }

    private void initView() {
        tvType = (TextView) findViewById(R.id.tv_type);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvLocation = (TextView) findViewById(R.id.tv_location);
        btnRemove = (Button) findViewById(R.id.btn_remove);
        LostFoundBean info = (LostFoundBean) getIntent().getSerializableExtra("info");
        tvType.setText(info.getType());
        tvName.setText(info.getName());
        tvPhone.setText(info.getPhone());
        tvDesc.setText(info.getDesc());
        tvLocation.setText(info.getLocation());
        tvDate.setText(info.getDate());
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.getInstance().getWordDatabase().getLostFoundDao().delete(info);
                finish();
            }
        });
    }
}