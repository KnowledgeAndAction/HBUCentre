package cn.ian2018.hbu.centre.activity;

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

import cn.ian2018.hbu.centre.adapter.InterestGroupAdapter;
import cn.ian2018.hbu.centre.model.InterestGroup;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import okhttp3.Call;

/**
 * 兴趣小组页面 -- 陈帅
 */
public class InterestGroupListActivity extends AppCompatActivity {

    private SwipeRefreshLayout mSwipeLayout;
    private List<InterestGroup> mList = new ArrayList<>();
    private InterestGroupAdapter interestGroupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_group_list);

        initView();

        getInterestGroupList();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("兴趣小组");
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
                getInterestGroupList();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        interestGroupAdapter = new InterestGroupAdapter(mList);
        recyclerView.setAdapter(interestGroupAdapter);
    }

    // 获取
    private void getInterestGroupList() {
        mSwipeLayout.setRefreshing(true);
        OkHttpUtils
                .get()
                .url(URLs.GET_INTEREST_GROUP_LIST)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mSwipeLayout.setRefreshing(false);
                        ToastUtil.show("获取兴趣小组列表失败，请刷新重试");
                        Logs.e("获取兴趣小组失败:" + e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        parseJson(response);
                    }
                });
    }

    // 解析json
    private void parseJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("sucessed")) {
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i=0; i<data.length(); i++) {
                    JSONObject info = data.getJSONObject(i);
                    int nid = info.getInt("nid");
                    String interestGroup = info.getString("interestGroup");
                    String description = info.getString("description");

                    InterestGroup interestGroup1 = new InterestGroup(nid,interestGroup,description);
                    mList.add(interestGroup1);
                }
                interestGroupAdapter.notifyDataSetChanged();
            } else {
                ToastUtil.show("获取兴趣小组列表失败");
            }
            mSwipeLayout.setRefreshing(false);
        } catch (JSONException e) {
            e.printStackTrace();
            mSwipeLayout.setRefreshing(false);
            ToastUtil.show("获取兴趣小组失败");
            Logs.e("获取兴趣小组失败:" + e.toString());
        }
    }
}
