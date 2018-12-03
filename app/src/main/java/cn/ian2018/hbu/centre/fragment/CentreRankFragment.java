package cn.ian2018.hbu.centre.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hicc.information.sensorsignin.R;

/**
 * Created by 陈帅 on 2018/3/6/006.
 * 中心排名
 */

public class CentreRankFragment extends BaseFragment {
    @Override
    public void fetchData() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_centre_rank, container, false);

        return view;
    }
}
