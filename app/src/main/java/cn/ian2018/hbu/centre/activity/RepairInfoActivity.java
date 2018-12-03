package cn.ian2018.hbu.centre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;

import cn.ian2018.hbu.centre.model.RepairInfo;

/**
 * 报修详情页
 */
public class RepairInfoActivity extends AppCompatActivity {

    private RepairInfo mRepairInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_info);

        initData();

        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        mRepairInfo = (RepairInfo) intent.getSerializableExtra("repair");
    }

    private void initView() {
        TextView tv_account = (TextView) findViewById(R.id.tv_account);
        TextView tv_handle = (TextView) findViewById(R.id.tv_handle);
        TextView tv_date = (TextView) findViewById(R.id.tv_date);
        TextView tv_des = (TextView) findViewById(R.id.tv_des);

        tv_account.setText("报修人：" + mRepairInfo.getAccountName());
        tv_handle.setText("处理人："+ mRepairInfo.getHandlerName());
        tv_date.setText("日期："+ mRepairInfo.getTime().substring(0,10));
        tv_des.setText("    "+ mRepairInfo.getRepairDes());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setEnabled(false);

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        // 设置标题
        toolbarLayout.setTitle(mRepairInfo.getName());
    }
}
