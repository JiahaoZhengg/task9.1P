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
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
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
import com.example.myapplication.utils.DefaultItemDecoration;
import com.example.myapplication.utils.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

public class SelectAddressActivity extends AppCompatActivity implements AddressSelectAdapter.OnAddressSelectItemClickListener {
    private RecyclerView mJARecyclerView;
    private TextView cancleTv, searchAddressTv, confirmTv, searchNoDataTv;
    private MapView mBmapView;
    private EditText mSearchAddressEt;
    private LinearLayout searchNoDataLayout, searchNonmalLayout, searchEditLayout;
    private ImageView centerImage, locationIv, clearInputIv;

    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    boolean isFirstLoc = true;
    private AddressSelectAdapter adapter;
    private List<PoiInfo> mFilterList = new ArrayList<>();
    private PoiInfo selPoiDetailInfo;
    double mLantitude;
    double mLongtitude;
    LatLng mLoactionLatLng;
    Point mCenterPoint = null;
    GeoCoder mGeoCoder = null;
    List<PoiInfo> mInfoList;
    PoiInfo mCurentInfo;
    MyBDLocationListner mListner = null;
    private Animation centerAnimation;
    private boolean isUseGeoCoder = true;
    private boolean isNeedAnimation = true;
    private InputMethodManager inputMethodManager;
    private SuggestionSearch mSuggestionSearch;
    private String changeContent;
    private MKOfflineMap mOffline;
    private String selectCityCode;
    private ArrayList<MKOLSearchRecord> mkolSearchRecords;
    private RotateAnimation mAnimation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_address_select_layout);
        if (!PermissionUtil.isHasLocationPermission(this)) {
            PermissionUtil.requestLocationPermission(this);
        }
        initView();
        initEvent();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mOffline = new MKOfflineMap();
        mOffline.init(new MKOfflineMapListener() {
            @Override
            public void onGetOfflineMapState(int i, int i1) {

            }
        });
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
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
        if (mGeoCoder != null) {
            mGeoCoder.destroy();
        }
        if (mSuggestionSearch != null) {
            mSuggestionSearch.destroy();
        }
        if (mOffline != null) {
            mOffline.destroy();
        }
    }

    private void initView() {
        mJARecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        cancleTv = (TextView) findViewById(R.id.cancle_tv);
        searchAddressTv = (TextView) findViewById(R.id.search_address_tv);
        confirmTv = (TextView) findViewById(R.id.confirm_tv);
        mBmapView = (MapView) findViewById(R.id.bmapView);
        mSearchAddressEt = (EditText) findViewById(R.id.search_address_et);
        searchNoDataTv = (TextView) findViewById(R.id.search_no_data_tv);
        searchNoDataLayout = (LinearLayout) findViewById(R.id.search_no_data_layout);
        searchNonmalLayout = (LinearLayout) findViewById(R.id.search_nonmal_layout);
        searchEditLayout = (LinearLayout) findViewById(R.id.search_edit_layout);
        centerImage = (ImageView) findViewById(R.id.center_image);
        locationIv = (ImageView) findViewById(R.id.location_iv);
        clearInputIv = (ImageView) findViewById(R.id.clear_input_iv);
    }

    private void initEvent() {
        mBaiduMap = mBmapView.getMap();
        adapter = new AddressSelectAdapter(this);
        adapter.setOnAddressSelectItemClickListener(this);
        mJARecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mJARecyclerView.addItemDecoration(new DefaultItemDecoration(this, R.dimen.common_utils_divider_height, R.color.line));
        mJARecyclerView.setAdapter(adapter);
        mSearchAddressEt.setHint("Search Address");
        searchAddressTv.setText("Search Address");
        mSearchAddressEt.addTextChangedListener(new SearchDevicesTextWatcher());
        cancleTv.setText("Cancel");
        confirmTv.setText("Sure");
        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("info", selPoiDetailInfo);
                setResult(10003, data);
                finish();
            }
        });

        mAnimation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setDuration(1000);
        mAnimation.setRepeatCount(-1);
        mAnimation.setInterpolator(new LinearInterpolator());
        // mCheckTipIv.setColorFilter(getResources().getColor(R.color.src_c1));
        // mCheckTipIv.setImageResource(R.mipmap.icon_login_loading);
        centerAnimation = AnimationUtils.loadAnimation(this, R.anim.center_anim);

        initMyLocation();
        mBaiduMap.setCompassEnable(false);


        searchNonmalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchNonmalLayout.setVisibility(View.GONE);
                searchEditLayout.setVisibility(View.VISIBLE);
                showInput(mSearchAddressEt);
            }
        });

        clearInputIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchNonmalLayout.setVisibility(View.VISIBLE);
                searchEditLayout.setVisibility(View.GONE);
                mSearchAddressEt.setText("");
                mSearchAddressEt.clearFocus();
                hideSoftInputWindow(v);
                mFilterList.clear();
                if (mLoactionLatLng != null) {
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(mLoactionLatLng).zoom(17.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus
                            (builder.build()));
                    isUseGeoCoder = true;
                    isNeedAnimation = false;
                } else {
                }
            }
        });

        locationIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoactionLatLng != null) {
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(mLoactionLatLng).zoom(17.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus
                            (builder.build()));
                    isUseGeoCoder = true;
                    isNeedAnimation = true;
                } else {
                }
            }
        });
    }


    private void hideSoftInputWindow(View view) {
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public void showInput(final EditText et) {
        et.requestFocus();
        if (inputMethodManager.isActive()) {
            inputMethodManager.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void initMyLocation() {
        mBmapView.showZoomControls(false);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);
        mBaiduMap.setMapStatus(msu);
        mInfoList = new ArrayList<PoiInfo>();
        mCenterPoint = mBaiduMap.getMapStatus().targetScreen;
        mLoactionLatLng = mBaiduMap.getMapStatus().target;

        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(this);
        mListner = new MyBDLocationListner();
        mLocationClient.registerLocationListener(mListner);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        mLocationClient.setLocOption(option);
        mLocationClient.start();

        mGeoCoder = GeoCoder.newInstance();
        mGeoCoder.setOnGetGeoCodeResultListener(mGeoListener);


        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaiduMap.clear();
                adapter.changeSelected(0);
                mJARecyclerView.smoothScrollToPosition(0);
                MapStatusUpdate u = MapStatusUpdateFactory
                        .newLatLng(latLng);
                mBaiduMap.animateMapStatus(u);
                isUseGeoCoder = true;
            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {
                LatLng position = mapPoi.getPosition();
                mBaiduMap.clear();
                MapStatusUpdate u = MapStatusUpdateFactory
                        .newLatLng(position);
                mBaiduMap.animateMapStatus(u);
                isUseGeoCoder = true;

            }
        });


        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    isUseGeoCoder = true;
                    isNeedAnimation = true;
                }
            }
        });

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                mBaiduMap.clear();
                if (isNeedAnimation) {
                    centerImage.startAnimation(centerAnimation);
                }
                LatLng target = mapStatus.target;
                if (isUseGeoCoder) {
                    mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption())
                            .location(target));
                    adapter.changeSelected(0);
                    mJARecyclerView.smoothScrollToPosition(0);
                }
            }
        });
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(listener);
    }


    OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {
        @Override
        public void onGetSuggestionResult(SuggestionResult suggestionResult) {
            List<SuggestionResult.SuggestionInfo> allSuggestions = suggestionResult.getAllSuggestions();
            mInfoList.clear();
            if (allSuggestions == null) {

            } else {
                for (SuggestionResult.SuggestionInfo suggestionInfo : allSuggestions) {
                    if (suggestionInfo == null) {
                        continue;
                    }
                    PoiInfo info = new PoiInfo();
                    info.address = suggestionInfo.getKey();
                    LatLng pt = suggestionInfo.pt;
                    info.location = pt;
                    info.city = suggestionInfo.city;
                    info.name = suggestionInfo.getKey();
                    mInfoList.add(info);
                }
                selPoiDetailInfo = mInfoList.get(0);
                mkolSearchRecords = mOffline.searchCity(selPoiDetailInfo.getCity());
                selectCityCode = String.valueOf(mkolSearchRecords.get(0).cityID);
            }
            handleRelativeAddressResult(mInfoList, changeContent);
        }
    };

    @Override
    public void onAddressSelectItemClick(PoiInfo info, int position) {
        isUseGeoCoder = false;
        isNeedAnimation = true;
        mBaiduMap.clear();
        LatLng la = info.location;
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(la);
        mBaiduMap.animateMapStatus(u);
        selPoiDetailInfo = info;
        adapter.changeSelected(position);
        if (position != 0) {
            mkolSearchRecords = mOffline.searchCity(selPoiDetailInfo.getCity());
            selectCityCode = String.valueOf(mkolSearchRecords.get(0).cityID);
        }
        if (BuildConfig.DEBUG) {
            Log.d("lyw", "onAddressSelectItemClick-cityCode->" + info.city + selectCityCode);
        }
    }


    private class MyBDLocationListner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mBmapView == null) {
                return;
            }
            if (isFirstLoc) {

                MyLocationData data = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        .latitude(location.getLatitude())
                        .longitude(location.getLongitude())
                        .build();
                mBaiduMap.setMyLocationData(data);
                MyLocationConfiguration config = new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.NORMAL, false, null);
                mBaiduMap.setMyLocationConfigeration(config);
                mLantitude = location.getLatitude();
                mLongtitude = location.getLongitude();
                LatLng currentLatLng = new LatLng(mLantitude, mLongtitude);
                mLoactionLatLng = new LatLng(mLantitude, mLongtitude);
                isFirstLoc = false;
                MapStatusUpdate u = MapStatusUpdateFactory
                        .newLatLng(currentLatLng);
                mBaiduMap.animateMapStatus(u);
                mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption())
                        .location(currentLatLng));
            }
        }
    }


    OnGetGeoCoderResultListener mGeoListener = new OnGetGeoCoderResultListener() {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult result) {
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (BuildConfig.DEBUG) {
                Log.d("lyw", "result.error-->" + result.error);
            }
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                if (result.error == SearchResult.ERRORNO.NETWORK_ERROR) {
                    Toast.makeText(SelectAddressActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            } else {
                mCurentInfo = new PoiInfo();
                mCurentInfo.address = result.getAddress();
                mCurentInfo.location = result.getLocation();
                mCurentInfo.name = result.getAddress();
                mCurentInfo.city = String.valueOf(result.getCityCode());
                selectCityCode = String.valueOf(result.getCityCode());
                mInfoList.clear();
                mInfoList.add(mCurentInfo);
                if (result.getPoiList() != null) {
                    mInfoList.addAll(result.getPoiList());
                }
                adapter.setPoiAddrList(mInfoList);
                adapter.notifyDataSetChanged();
                selPoiDetailInfo = mInfoList.get(0);
            }
        }
    };


    class SearchDevicesTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int before) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            changeContent = charSequence.toString();
            if (changeContent != null && !changeContent.isEmpty() || !"".equals(changeContent)) {
                mSuggestionSearch.requestSuggestion(new SuggestionSearchOption().city("美国").keyword(changeContent));
            } else {
                if (mLoactionLatLng != null) {
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(mLoactionLatLng).zoom(17.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus
                            (builder.build()));
                    isUseGeoCoder = true;
                    isNeedAnimation = false;
                } else {
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }


    private void handleRelativeAddressResult(List<PoiInfo> mInfoList, String content) {
        mJARecyclerView.setAdapter(adapter);
        adapter.setPoiAddrList(mInfoList);
        if (mInfoList.size() == 0) {
            searchNoDataLayout.setVisibility(View.VISIBLE);
            String formatContent = String.format("没有找到“%1$s”相关内容", content);
            SpannableStringBuilder builder = new SpannableStringBuilder(formatContent);
            ForegroundColorSpan blueSpan = new ForegroundColorSpan(getResources().getColor(R.color.black));
            int start = formatContent.indexOf(content);
            int end = start + content.length();
            builder.setSpan(blueSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            searchNoDataTv.setText(builder);
        } else {
            searchNoDataLayout.setVisibility(View.GONE);
        }
    }


}
