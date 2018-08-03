package com.coolweather.android.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by FJQ on 2018/7/30.
 */

public abstract class BaseFragment extends Fragment {
    protected View rootView;
    protected Context mContext;
    Unbinder unbinder;
    //加载布局和findViewById的操作 不建议执行耗时的操作
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView==null){
            rootView=inflater.inflate(getLayoutId(), container, false);
            unbinder = ButterKnife.bind(this, rootView);
            initUI();
        }
        return rootView;
    }

    public abstract int getLayoutId();

    public abstract void initUI();
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
