package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.example.myapplication.db.bean.LostFoundBean;
import com.example.myapplication.db.dao.LostFoundDao;
import com.example.myapplication.utils.DefaultItemDecoration;
import com.example.myapplication.utils.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

public class ShowOnMapActivity extends AppCompatActivity {
    private MapView mBmapView;
    private BaiduMap mBaiduMap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_on_map);
        if (!PermissionUtil.isHasLocationPermission(this)) {
            PermissionUtil.requestLocationPermission(this);
        }
        initView();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBmapView != null) {
            mBmapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBmapView != null) {
            mBmapView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBmapView != null) {
            mBmapView.onDestroy();
        }
        mBaiduMap.setMyLocationEnabled(false);


    }

    private void initView() {

        mBmapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mBmapView.getMap();
        LostFoundDao lostFoundDao = MyApp.getInstance().getWordDatabase().getLostFoundDao();
        List<LostFoundBean> allData = lostFoundDao.getAllData();
        for (int i = 0; i < allData.size(); i++) {
            LostFoundBean lostFoundBean = allData.get(i);
            if (lostFoundBean.getLat() != 0) {
                addMark(lostFoundBean.getLat(), lostFoundBean.getLng());
            }
        }
    }

    public void addMark(double lat, double lng) {
        LatLng point = new LatLng(lat, lng);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_positioning);
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        mBaiduMap.addOverlay(option);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(new LatLng(lat,lng)).zoom(17.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus
                (builder.build()));
    }

}
