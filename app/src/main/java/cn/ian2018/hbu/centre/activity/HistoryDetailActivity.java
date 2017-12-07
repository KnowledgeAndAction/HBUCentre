package cn.ian2018.hbu.centre.activity;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;
import cn.ian2018.hbu.centre.model.HistoryActive;

/**
 * 历史活动详情
 */
public class HistoryDetailActivity extends AppCompatActivity {
    private HistoryActive historyActive;
    private TextView tv_details;
    private TextView tv_hintime;
    private TextView tv_houttime;
    private TextView tv_hlocation;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        initData();

        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        historyActive = (HistoryActive) intent.getSerializableExtra("ActivityId");
    }

    private void initView() {
        tv_details = (TextView) findViewById(R.id.tv_details);
        tv_hintime = (TextView) findViewById(R.id.tv_hintime);
        tv_houttime = (TextView) findViewById(R.id.tv_houttime);
        tv_hlocation = (TextView) findViewById(R.id.tv_hlocation);

        TextView tv_start_time = (TextView) findViewById(R.id.tv_start_time);
        TextView tv_end_time = (TextView) findViewById(R.id.tv_end_time);
        tv_start_time.setText("活动开始时间：" + historyActive.gethTime().replace("T", " ").substring(0, 16));
        tv_end_time.setText("活动结束时间：" + historyActive.getEndTime().replace("T", " ").substring(0, 16));

        tv_details.setText("    " + historyActive.getActivityDescription());
        tv_hintime.setText("签到时间："+ historyActive.gethInTime().replace("T", " "));
        tv_houttime.setText("签离时间："+ historyActive.gethOutTime().replace("T", " "));
        tv_hlocation.setText("地点："+ historyActive.getLocation());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        toolbarLayout.setTitle(historyActive.gethActivityName());
    }
}
