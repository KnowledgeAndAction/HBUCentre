package cn.ian2018.hbu.centre.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hicc.information.sensorsignin.R;

import java.util.ArrayList;
import java.util.List;

import cn.ian2018.hbu.centre.adapter.OnDutyManageAdapter;
import cn.ian2018.hbu.centre.model.Active;

/**
 * Created by 陈帅 on 2018/4/4/006.
 * 按节次查看值班签到记录
 */

public class OnDutySignInfoFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private OnDutyManageAdapter myAdapter;
    private List<Active> activeList = new ArrayList<>();

    @Override
    public void fetchData() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onduty_sign, container, false);

        initView(view);

        initData();

        return view;
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myAdapter = new OnDutyManageAdapter(activeList);
        recyclerView.setAdapter(myAdapter);
    }

    // 初始化数据
    private void initData() {
        activeList.clear();

        Active active1 = new Active();
        active1.setActiveName("第一大节值班");
        active1.setActiveId(1);
        activeList.add(active1);

        Active active2 = new Active();
        active2.setActiveName("第二大节值班");
        active2.setActiveId(2);
        activeList.add(active2);

        Active active3 = new Active();
        active3.setActiveName("第三大节值班");
        active3.setActiveId(3);
        activeList.add(active3);

        Active active4 = new Active();
        active4.setActiveName("第四大节值班");
        active4.setActiveId(4);
        activeList.add(active4);

        Active active5 = new Active();
        active5.setActiveName("第五大节值班");
        active5.setActiveId(5);
        activeList.add(active5);

        myAdapter.notifyDataSetChanged();
    }
}
