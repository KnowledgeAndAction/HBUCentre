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

import cn.ian2018.hbu.centre.adapter.SignInfoAdapter;
import cn.ian2018.hbu.centre.model.Active;
import cn.ian2018.hbu.centre.model.SignInfo;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import okhttp3.Call;

/**
 * 管理员查看签到记录界面
 */
public class SignRecordActivity extends AppCompatActivity {

    private long activeId;
    private Toolbar toolbar;
    private List<SignInfo> signInfoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipe_refresh;
    private SignInfoAdapter myAdapter;
    private Active active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_record);

        // 从上一个界面接收这个活动的id
        active = (Active) getIntent().getSerializableExtra("active");
        activeId = active.getActiveId();
        Logs.d("活动ID："+activeId);

        initView();
    }

    // 从网络获取数据
    private void initData() {
        signInfoList.clear();
        OkHttpUtils
                .get()
                .url(URLs.GET_SIGN_INFO)
                .addParams("activityId",activeId+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        swipe_refresh.setRefreshing(false);
                        ToastUtil.show("加载数据失败，请稍后重试:" +e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        getJson(response);
                    }
                });
    }

    // 解析json数据
    private void getJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("sucessed")) {
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i=0; i<data.length(); i++) {
                    JSONObject object = data.getJSONObject(i);
                    String in = object.getString("inTime");
                    String out = object.getString("outTime");
                    String name = object.getString("name");
                    int group = object.getInt("groupCode");

                    SignInfo signInfo = new SignInfo();
                    signInfo.setInTime(in);
                    signInfo.setOutTime(out);
                    signInfo.setName(name);
                    signInfo.setGroupCode(group);
                    signInfoList.add(signInfo);
                }
                swipe_refresh.setRefreshing(false);
                myAdapter.notifyDataSetChanged();
            } else {
                swipe_refresh.setRefreshing(false);
                ToastUtil.show("暂无签到记录");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            swipe_refresh.setRefreshing(false);
            ToastUtil.show("加载数据失败，请稍后重试:" + e.toString());
        }
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(active.getActiveName()+"签到记录");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                        // 跳转到修改活动界面
                        Intent intent = new Intent(SignRecordActivity.this,ChangeActiveActivity.class);
                        // 把活动传过去
                        intent.putExtra("active",active);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new SignInfoAdapter(signInfoList);
        recyclerView.setAdapter(myAdapter);

        // 配置swipeRefresh
        swipe_refresh.setColorSchemeResources(R.color.colorPrimary , R.color.colorAccent, R.color.colorPrimaryDark);
        // 设置刷新事件
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });

        // 设置开始就刷新
        swipe_refresh.setRefreshing(true);
        initData();
    }

    // 显示菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin_change_active, menu);
        return true;
    }
}
