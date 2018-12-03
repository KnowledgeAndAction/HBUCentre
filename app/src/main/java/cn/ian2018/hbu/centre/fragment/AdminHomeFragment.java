package cn.ian2018.hbu.centre.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hicc.information.sensorsignin.R;

import cn.ian2018.hbu.centre.activity.ActiveManageActivity;
import cn.ian2018.hbu.centre.activity.MorningAndRunActivity;
import cn.ian2018.hbu.centre.activity.OnDutyManageActivity;
import cn.ian2018.hbu.centre.activity.RankManageActivity;
import cn.ian2018.hbu.centre.activity.TraningManageActivity;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 管理员首页
 */

public class AdminHomeFragment extends BaseFragment implements View.OnClickListener{

    @Override
    public void fetchData() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        initView(view);

        return view;
    }

    private void initView(View view) {
        FancyButton btn_active_manage = (FancyButton) view.findViewById(R.id.btn_active_manage);
        FancyButton btn_duty = (FancyButton) view.findViewById(R.id.btn_duty);
        FancyButton btn_training = (FancyButton) view.findViewById(R.id.btn_training);
        FancyButton btn_rank = (FancyButton) view.findViewById(R.id.btn_rank);
        FancyButton btn_morning_read = (FancyButton) view.findViewById(R.id.btn_morning_read);
        FancyButton btn_run = (FancyButton) view.findViewById(R.id.btn_run);

        btn_active_manage.setOnClickListener(this);
        btn_duty.setOnClickListener(this);
        btn_training.setOnClickListener(this);
        btn_rank.setOnClickListener(this);
        btn_morning_read.setOnClickListener(this);
        btn_run.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 管理活动
            case R.id.btn_active_manage:
                startActivity(new Intent(getContext(), ActiveManageActivity.class));
                break;
            // 值班情况
            case R.id.btn_duty:
                startActivity(new Intent(getContext(), OnDutyManageActivity.class));
                break;
            // 培训情况
            case R.id.btn_training:
                Intent intent = new Intent(getContext(), TraningManageActivity.class);
                intent.putExtra("type",1);
                startActivity(intent);
                break;
            // 排名情况
            case R.id.btn_rank:
                startActivity(new Intent(getContext(), RankManageActivity.class));
                break;
            // 晨读
            case R.id.btn_morning_read:
                Intent morningIntent = new Intent(getContext(), MorningAndRunActivity.class);
                morningIntent.putExtra("rule",5);
                startActivity(morningIntent);
                break;
            // 跑操
            case R.id.btn_run:
                Intent runIntent = new Intent(getContext(),MorningAndRunActivity.class);
                runIntent.putExtra("rule",4);
                startActivity(runIntent);
                break;
        }
    }
}
