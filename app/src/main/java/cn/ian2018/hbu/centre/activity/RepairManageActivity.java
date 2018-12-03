package cn.ian2018.hbu.centre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hicc.information.sensorsignin.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.ian2018.hbu.centre.adapter.RepairManageAdapter;
import cn.ian2018.hbu.centre.model.RepairInfo;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import okhttp3.Call;

/**
 * 报修情况
 */
public class RepairManageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipe_refresh;
    private List<RepairInfo> repairInfoList = new ArrayList<>();
    private RepairManageAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_manage);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        swipe_refresh.setRefreshing(true);
        // 获取报修表
        getRepairList();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("报修情况");
        setSupportActionBar(toolbar);
        // 显示返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 设置返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 添加菜单点击事件
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add:
                        // 跳转到添加报修单界面
                        startActivity(new Intent(RepairManageActivity.this, AddRepairInfoActivity.class));
                        break;
                }
                return true;
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        // 初始化recyclerview
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new RepairManageAdapter(repairInfoList);
        recyclerView.setAdapter(myAdapter);

        // 配置swipeRefresh
        swipe_refresh.setColorSchemeResources(R.color.colorPrimary , R.color.colorAccent, R.color.colorPrimaryDark);

        // 设置刷新事件
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRepairList();
            }
        });

        // 设置开始就刷新
        swipe_refresh.setRefreshing(true);

        // 获取活动
        getRepairList();
    }

    // 从网络获取报修信息
    private void getRepairList() {
        OkHttpUtils
                .get()
                .url(URLs.GET_REPAIR_LIST)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        ToastUtil.show("获取维修信息失败，请稍后重试:" + e.toString());
                        swipe_refresh.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            repairInfoList.clear();
                            JSONObject jsonObject = new JSONObject(s);
                            boolean sucessed = jsonObject.getBoolean("sucessed");
                            if (sucessed) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int j = 0; j < data.length(); j++) {
                                    JSONObject activity = data.getJSONObject(j);
                                    String accountName = activity.getString("accountName");
                                    String handleName = activity.getString("handleName");
                                    String time = activity.getString("time");
                                    String description = activity.getString("description");
                                    int nid = activity.getInt("nid");
                                    String handle = activity.getString("handle");
                                    String account = activity.getString("account");
                                    String article = activity.getString("article");

                                    RepairInfo repairInfo = new RepairInfo(article,account,handle,time,description);
                                    repairInfo.setId(nid);
                                    repairInfo.setAccountName(accountName);
                                    repairInfo.setHandlerName(handleName);
                                    repairInfoList.add(repairInfo);
                                }
                            } else {
                                ToastUtil.show("暂无数据");
                            }
                            myAdapter.notifyDataSetChanged();
                            swipe_refresh.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.show("获取维修信息失败，请稍后重试:" + e.toString());
                            swipe_refresh.setRefreshing(false);
                        }
                    }
                });
    }


    // 显示菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tool_bar, menu);
        return true;
    }
}
