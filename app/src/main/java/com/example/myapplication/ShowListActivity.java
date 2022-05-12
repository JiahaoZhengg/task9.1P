package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.myapplication.db.bean.LostFoundBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShowListActivity extends AppCompatActivity {

    private RecyclerView rvLostFound;
    private BaseQuickAdapter<LostFoundBean, BaseViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        initView();
    }

    private void initView() {
        rvLostFound = (RecyclerView) findViewById(R.id.rv_lost_found);
        rvLostFound.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickAdapter<LostFoundBean, BaseViewHolder>(R.layout.item_lost_found) {
            @Override
            protected void convert(@NotNull BaseViewHolder baseViewHolder, LostFoundBean lostFoundBean) {
                baseViewHolder.setText(R.id.tv_name, "Name：" + lostFoundBean.getName());
                baseViewHolder.setText(R.id.tv_desc, "Desc：" + lostFoundBean.getDesc());
                baseViewHolder.setText(R.id.tv_phone, "Phone：" + lostFoundBean.getPhone());
                baseViewHolder.setText(R.id.tv_type, "Type：" + lostFoundBean.getType());

            }


        };
        rvLostFound.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                LostFoundBean o = (LostFoundBean) adapter.getData().get(position);
                Intent intent = new Intent(ShowListActivity.this, DetailActivity.class);
                intent.putExtra("info",o);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<LostFoundBean> allData = MyApp.getInstance().getWordDatabase().getLostFoundDao().getAllData();
        adapter.setNewInstance(allData);
    }
}