package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.mapapi.search.core.PoiInfo;

import java.util.ArrayList;
import java.util.List;

public class AddressSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Context mContext;
    private List<PoiInfo> infoList;
    private OnAddressSelectItemClickListener mItemClickListener;
    private int selectedPosition = 0;// 选中的位置

    public AddressSelectAdapter(Context context) {
        mContext = context;
        infoList = new ArrayList<>();
    }

    public void changeSelected(int positon) {
        if (positon != selectedPosition) {
            selectedPosition = positon;
            notifyDataSetChanged();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.main_item_poi_address, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        MyViewHolder hodler = (MyViewHolder) viewHolder;
        PoiInfo info = infoList.get(i);
        hodler.nameTv.setText(info.getName());
        hodler.addressTv.setText(info.getAddress());

        if (selectedPosition == i) {
            hodler.checkImageIv.setImageResource(R.mipmap.location_gps_green);
        } else {
            hodler.checkImageIv.setImageResource(0);
        }
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv;
        TextView addressTv;
        ImageView checkImageIv;
        LinearLayout addressSelectItemLl;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = (TextView) itemView.findViewById(R.id.name_tv);
            addressTv = (TextView) itemView.findViewById(R.id.address_tv);
            checkImageIv = (ImageView) itemView.findViewById(R.id.check_image_iv);
            addressSelectItemLl = (LinearLayout) itemView.findViewById(R.id.address_select_item_ll);
            addressSelectItemLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        int posotion = getAdapterPosition();
                        mItemClickListener.onAddressSelectItemClick(infoList.get(posotion), posotion);
                    }
                }
            });
        }
    }

    public void setPoiAddrList(List<PoiInfo> infos) {
        infoList = infos;
    }

    public void clearPoiAddrList() {
        infoList.clear();
    }


    public interface OnAddressSelectItemClickListener {
        void onAddressSelectItemClick(PoiInfo info, int position);
    }

    public void setOnAddressSelectItemClickListener(OnAddressSelectItemClickListener listener) {
        mItemClickListener = listener;
    }
}

