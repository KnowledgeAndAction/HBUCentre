package cn.ian2018.hbu.centre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hicc.information.sensorsignin.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.ian2018.hbu.centre.adapter.WeekRecycleAdapter;
import cn.ian2018.hbu.centre.model.Week;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import okhttp3.Call;

/**
 * 周列表界面（查看历史祖内排名）
 */
public class WeekListActivity extends AppCompatActivity {

    private List<Week> mWeekList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeLayout;
    private WeekRecycleAdapter weekRecycleAdapter;
    private int groupCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_list);

        Intent intent = getIntent();
        groupCode = intent.getIntExtra("groupCode", 0);

        initView();

        getWeekList();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("选择周数");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 设置下拉刷新事件
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWeekList();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        weekRecycleAdapter = new WeekRecycleAdapter(mWeekList);
        recyclerView.setAdapter(weekRecycleAdapter);

        weekRecycleAdapter.setOnItemClickListener(new WeekRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 跳转到排名界面
                Intent intent = new Intent(getApplicationContext(), RankActivity.class);
                intent.putExtra("GroupID", groupCode);
                intent.putExtra("WeekID",mWeekList.get(position).getNid());
                startActivity(intent);
            }
        });
    }

    // 获取周列表数据
    private void getWeekList() {
        mSwipeLayout.setRefreshing(true);
        OkHttpUtils
                .get()
                .url(URLs.GET_WEEK_LIST)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.e("请求失败："+e.toString());
                        ToastUtil.show("获取周列表失败，请刷新重试");
                        mSwipeLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                mWeekList.clear();
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i=0; i<data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    int nid = object.getInt("nid");
                                    String startDate = object.getString("startDate");
                                    String endDate = object.getString("endDate");
                                    int weekCode = object.getInt("weekCode");

                                    Week week = new Week(nid,startDate,endDate,weekCode);
                                    mWeekList.add(week);
                                }
                                weekRecycleAdapter.notifyDataSetChanged();
                            } else {
                                Logs.e("服务器错误，获取周列表失败");
                                ToastUtil.show("获取周列表失败，请稍后重试");
                            }
                            mSwipeLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Logs.e("json解析错误：" + e.toString());
                            ToastUtil.show("获取周列表失败，请稍后重试");
                            mSwipeLayout.setRefreshing(false);
                        }
                    }
                });
    }
}
