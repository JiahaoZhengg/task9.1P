package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.search.core.PoiInfo;
import com.example.myapplication.db.bean.LostFoundBean;

public class CreateActivity extends AppCompatActivity {

    private RadioButton rbLost;
    private RadioButton rbFound;
    private EditText etName;
    private EditText etPhone;
    private EditText etDesc;
    private TextView etDate;
    private EditText etLocation;
    private Button btnSave;
    private String date;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        initView();
    }

    private void initView() {
        rbLost = (RadioButton) findViewById(R.id.rb_lost);
        rbFound = (RadioButton) findViewById(R.id.rb_found);
        etName = (EditText) findViewById(R.id.et_name);
        etPhone = (EditText) findViewById(R.id.et_phone);
        etDesc = (EditText) findViewById(R.id.et_desc);
        etDate = (TextView) findViewById(R.id.et_date);
        etLocation = (EditText) findViewById(R.id.et_location);
        findViewById(R.id.btn_get_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(CreateActivity.this, SelectAddressActivity.class), 10001);
            }
        });
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CreateActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String realMonth = (month + 1) < 10 ? "0" + (month + 1) : (month + 1) + "";
                        String realDy = (dayOfMonth) < 10 ? "0" + (dayOfMonth) : (dayOfMonth) + "";
                        date = year + "-" + realMonth + "-" + realDy;
                        etDate.setText(date);
                    }
                }, 2022, 06, 12).show();
            }
        });
        btnSave = (Button) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etName.getText().toString())) {
                    Toast.makeText(CreateActivity.this, "Please input name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etPhone.getText().toString())) {
                    Toast.makeText(CreateActivity.this, "Please input phone", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etDesc.getText().toString())) {
                    Toast.makeText(CreateActivity.this, "Please input description", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etDate.getText().toString())) {
                    Toast.makeText(CreateActivity.this, "Please input date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etLocation.getText().toString())) {
                    Toast.makeText(CreateActivity.this, "Please input location", Toast.LENGTH_SHORT).show();
                    return;
                }
                LostFoundBean lostFoundBean = new LostFoundBean();
                lostFoundBean.setName(etName.getText().toString());
                lostFoundBean.setPhone(etPhone.getText().toString());
                lostFoundBean.setDesc(etDesc.getText().toString());
                lostFoundBean.setDate(etDate.getText().toString());
                lostFoundBean.setLocation(etLocation.getText().toString());
                lostFoundBean.setLat(lat);
                lostFoundBean.setLng(lng);
                lostFoundBean.setAddress(address);
                lostFoundBean.setType(rbLost.isChecked() ? "Lost" : "Found");
                MyApp.getInstance().getWordDatabase().getLostFoundDao().insert(lostFoundBean);
                finish();

            }
        });

    }
    private double lat,lng;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 10003) {
            PoiInfo info = data.getParcelableExtra("info");
            if (requestCode == 10001) {
                address = info.getAddress();
               lat = info.getLocation().latitude;
               lng = info.getLocation().longitude;
            }

        }
    }

}