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

import cn.ian2018.hbu.centre.adapter.MyLeaseAdapter;
import cn.ian2018.hbu.centre.model.LeaseInfo;
import cn.ian2018.hbu.centre.utils.Constant;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.SpUtil;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import okhttp3.Call;

/**
 * Created by fzgh on 2017/12/10.
 * 我的租赁
 */

public class MyLeaseActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipe_refresh;
    private List<LeaseInfo> unBackList = new ArrayList<>();
    private List<LeaseInfo> backList = new ArrayList<>();
    private MyLeaseAdapter myLeaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lease);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyLeaseList();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("我的租借");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        swipe_refresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMyLeaseList();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myLeaseAdapter = new MyLeaseAdapter(unBackList,backList,this);
        recyclerView.setAdapter(myLeaseAdapter);
    }

    // 获取数据
    private void getMyLeaseList() {
        swipe_refresh.setRefreshing(true);
        OkHttpUtils
                .get()
                .url(URLs.GET_LOANS)
                .addParams("Account", SpUtil.getString(Constant.ACCOUNT,""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.e("获取租借信息失败："+e.toString());
                        ToastUtil.show("获取租借信息失败，请稍后重试");
                        swipe_refresh.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                unBackList.clear();
                                backList.clear();
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i=0; i<data.length(); i++) {
                                    JSONObject object = data.getJSONObject(i);
                                    String actualBackTime = object.getString("actualBackTime");
                                    int articleID = object.getInt("articleID");
                                    String name = object.getString("name");
                                    int nid = object.getInt("nid");
                                    String handle = object.getString("handle");
                                    String time = object.getString("time");
                                    String backtime = object.getString("backtime");
                                    String account = object.getString("account");
                                    int status = object.getInt("status");

                                    LeaseInfo leaseInfo = new LeaseInfo(name,actualBackTime,articleID,nid,handle,time,backtime,account,status);

                                    // （0未归还，1归还）
                                    if (status == 1) {
                                        backList.add(leaseInfo);
                                    } else {
                                        unBackList.add(leaseInfo);
                                    }
                                }
                                // 刷新数据
                                myLeaseAdapter.notifyDataSetChanged();
                            } else {
                                ToastUtil.show("获取租借信息失败");
                                Logs.e("获取租借信息失败：服务器错误");
                            }
                            swipe_refresh.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Logs.e("获取租借信息失败："+e.toString());
                            ToastUtil.show("获取租借信息失败，请稍后重试");
                            swipe_refresh.setRefreshing(false);
                        }
                    }
                });
    }
}
