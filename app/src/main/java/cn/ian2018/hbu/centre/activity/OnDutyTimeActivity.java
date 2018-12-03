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

import cn.ian2018.hbu.centre.adapter.OnDutyTimeRecyclerAdapter;
import cn.ian2018.hbu.centre.model.OnDutyTime;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import cn.ian2018.hbu.centre.utils.Utils;
import okhttp3.Call;

/**
 * 各组值班时间排名
 */
public class OnDutyTimeActivity extends AppCompatActivity {

    private List<OnDutyTime> mList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeLayout;
    private OnDutyTimeRecyclerAdapter rankRecycleAdapter;
    private int groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onduty_time);

        Intent intent = getIntent();
        groupID = intent.getIntExtra("groupCode", 0);

        initView();

        getRankList();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(Utils.getGroup(groupID) + "值班时长");
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
                getRankList();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankRecycleAdapter = new OnDutyTimeRecyclerAdapter(mList);
        recyclerView.setAdapter(rankRecycleAdapter);
    }

    private void getRankList() {
        mSwipeLayout.setRefreshing(true);
        OkHttpUtils
                .get()
                .url(URLs.GET_DUTY_TIME_LIST_FOR_WEEK_BY_GROUP)
                .addParams("GroupCode",groupID+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.e("请求失败："+e.toString());
                        ToastUtil.show("获取值班时间失败，请刷新重试");
                        mSwipeLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                mList.clear();
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i=0; i<data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    String name = object.getString("studentName");
                                    String studentNum = object.getString("studentNum");
                                    String allTime = object.getString("allTime");
                                    long allTimes = object.getLong("allTimes");
                                    int groupCode = object.getInt("groupCode");
                                    int gradeCode = object.getInt("gradeCode");

                                    OnDutyTime onDutyTime = new OnDutyTime(name,allTimes,allTime,groupCode,gradeCode,studentNum);

                                    mList.add(onDutyTime);
                                }
                                rankRecycleAdapter.notifyDataSetChanged();
                            } else {
                                Logs.e("服务器错误，获取值班时间失败");
                                ToastUtil.show("本周暂无人值班");
                            }
                            mSwipeLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Logs.e("json解析错误：" + e.toString());
                            ToastUtil.show("获取值班时间失败");
                            mSwipeLayout.setRefreshing(false);
                        }
                    }
                });
    }
}
