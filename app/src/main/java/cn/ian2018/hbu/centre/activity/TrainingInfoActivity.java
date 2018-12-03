package cn.ian2018.hbu.centre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ian2018.hbu.centre.adapter.TrainingSignRecyclerAdapter;
import cn.ian2018.hbu.centre.model.Active;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import cn.ian2018.hbu.centre.view.FullyLinearLayoutManager;
import okhttp3.Call;


public class TrainingInfoActivity extends AppCompatActivity {

    private Active mActive;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private List<Map<String,String>> mapList = new ArrayList<>();
    private TrainingSignRecyclerAdapter trainingSignRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_info);

        Intent intent = getIntent();
        mActive = (Active) intent.getSerializableExtra("active");

        initView();

        // 获取签到数据
        initData();
    }

    private void initData() {
        refreshLayout.setRefreshing(true);
        OkHttpUtils
                .get()
                .url(URLs.GET_SIGN_INFO)
                .addParams("activityId",mActive.getActiveId()+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.show("获取签到记录失败，请稍后重试");
                        Logs.e("获取签到记录失败:" + e.toString());
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                mapList.clear();
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i=0; i < data.length(); i++) {
                                    JSONObject info = data.getJSONObject(i);
                                    String name = info.getString("name");
                                    String inTime = info.getString("inTime");
                                    String outTime = info.getString("outTime");
                                    int groupCode = info.getInt("groupCode");

                                    Map<String,String> map = new HashMap<String, String>();
                                    map.put("name",name);
                                    map.put("time",inTime);
                                    mapList.add(map);
                                }
                                trainingSignRecyclerAdapter.notifyDataSetChanged();
                            } else {
                                ToastUtil.show("暂无签到记录");
                                Logs.e("签到记录");
                            }
                            refreshLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.show("获取签到记录失败，请稍后重试");
                            Logs.e("获取签到记录失败:" + e.toString());
                            refreshLayout.setRefreshing(false);
                        }
                    }
                });

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        //refreshLayout.setEnabled(false);
        // 设置刷新监听事件
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        // 设置标题
        toolbarLayout.setTitle(mActive.getActiveName());

        TextView activityLocation = (TextView) findViewById(R.id.tv_location);
        TextView tv_info = (TextView) findViewById(R.id.tv_info);
        TextView tv_time = (TextView) findViewById(R.id.tv_time);

        activityLocation.setText("地点：" + mActive.getActiveLocation());
        tv_info.setText("内容：" + mActive.getActiveDes());
        tv_time.setText("时间：" + mActive.getActiveTime().replace("T", " ").substring(0, 16));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new FullyLinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        // 设置适配器
        trainingSignRecyclerAdapter = new TrainingSignRecyclerAdapter(mapList);
        recyclerView.setAdapter(trainingSignRecyclerAdapter);
    }
}
