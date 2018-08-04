package com.coolweather.android.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.R;
import com.coolweather.android.activity.MainActivity;
import com.coolweather.android.activity.WeatherActivity;
import com.coolweather.android.base.BaseFragment;
import com.coolweather.android.db.City;
import com.coolweather.android.db.Country;
import com.coolweather.android.db.Province;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by FJQ on 2018/8/3.
 */

public class ChooseAreaFragment extends BaseFragment {
    private static final String TAG = "ChooseAreaFragment";
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.back_button)
    Button backButton;
    @BindView(R.id.list_view)
    ListView listView;
    private ProgressDialog progressDialog;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 区列表
     */
    private List<Country> countryList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;
    @Override
    public int getLayoutId() {
        return R.layout.fragment_choose_area;
    }

    @Override
    public void initUI() {
        Log.d(TAG, "initUI: ");
        adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
    }
    //可以进行与Activity交互的UI操作
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel==LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }
                else if (currentLevel==LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCountry();
                }
                else if (currentLevel==LEVEL_COUNTRY){
                    //解决progressDialog的窗体泄露
                    progressDialog = null;
                    String weatherId = countryList.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity){
                        Intent intent = new Intent(mContext, WeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }
                    else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.weatherId = weatherId;
                        activity.requestWeather(weatherId);
                    }

                }
            }
        });
        queryProvince();
    }

    /**
     * 查询所有的省 优先从数据库中查询 如果没有查询到再去服务器查询
     */
    private void queryProvince(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size()>0){
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }
        else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    /**
     * 查询所有的市 优先从数据库中查询 如果没有查询到再去服务器查询
     */
    private void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId=?",
                String.valueOf(selectedProvince.getProvinceCode())).find(City.class);
        if (cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }
        else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }

    /**
     * 查询所有的区 优先从数据库中查询 如果没有查询到再去服务器查询
     */
    private void queryCountry(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countryList = DataSupport.where("cityId=?",
                String.valueOf(selectedCity.getCityCode())).find(Country.class);
        if (countryList.size()>0){
            dataList.clear();
            for (Country country:countryList){
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTRY;
        }
        else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"
                    +cityCode;
            queryFromServer(address,"country");
        }
    }

    private void queryFromServer(String address, final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, final IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                        Toast.makeText(mContext,"加载失败",Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "加载失败"+e.toString() );
                    }
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvinceReponse(responseText);
                }
                else if ("city".equals(type)){
                    result = Utility.handleCityReponse(responseText,selectedProvince.getProvinceCode());
                }
                else if ("country".equals(type)){
                    result = Utility.handleCountryReponse(responseText,selectedCity.getCityCode());
                }

                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                            if ("province".equals(type)){
                                queryProvince();
                            }
                            else if ("city".equals(type)){
                                queryCities();
                            }
                            else if ("country".equals(type)){
                                queryCountry();
                            }
                        }
                    });
                }
            }
        });
    }
    //Activity has leaked window 窗体泄露 每个Activity都有窗体管理器 dialog需要依赖窗体管理器
    private void showProgressDialog(){
        if (progressDialog==null){
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("正在加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    //hide只是设置对话框不可见 当依赖的activity被销毁 会出现窗体泄露的问题 使用dismiss不会出现该问题
    private void hideProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @OnClick(R.id.back_button)
    public void onViewClicked() {
        if (currentLevel==LEVEL_CITY){
            queryProvince();
        }
        else if (currentLevel==LEVEL_COUNTRY){
            queryCities();
        }
    }
}
