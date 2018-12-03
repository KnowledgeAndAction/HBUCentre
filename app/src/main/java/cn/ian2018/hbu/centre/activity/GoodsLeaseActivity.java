package cn.ian2018.hbu.centre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.hicc.information.sensorsignin.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.ian2018.hbu.centre.adapter.GoodsLeaseAdapter;
import cn.ian2018.hbu.centre.model.Goods;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import okhttp3.Call;

/**
 * Created by 崔国钊 on 2017/12/10.
 * 物品租赁
 */

public class GoodsLeaseActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipe_refresh;
    private List<Goods> mGoodsList = new ArrayList<>();
    private GoodsLeaseAdapter goodsLeaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_lease);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGoodsList();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("物品租借");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RelativeLayout rl_my_lease = (RelativeLayout) findViewById(R.id.rl_my_lease);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // 设置我的租借点击事件
        rl_my_lease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MyLeaseActivity.class));
            }
        });

        // 设置下拉刷新
        swipe_refresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getGoodsList();
            }
        });

        // 设置recycle
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        goodsLeaseAdapter = new GoodsLeaseAdapter(mGoodsList);
        recyclerView.setAdapter(goodsLeaseAdapter);
    }

    // 获取数据
    private void getGoodsList() {
        swipe_refresh.setRefreshing(true);
        OkHttpUtils
                .get()
                .url(URLs.GET_GOODS)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.show("获取物品信息失败，请稍后重试");
                        Logs.e("获取物品失败:" + e.toString());
                        swipe_refresh.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                mGoodsList.clear();
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i=0; i < data.length(); i++) {
                                    JSONObject info = data.getJSONObject(i);
                                    double price = info.getDouble("price");
                                    String name = info.getString("name");
                                    int nid = info.getInt("nid");
                                    String description = info.getString("description");
                                    int quanutity = info.getInt("quanutity");
                                    int type = info.getInt("type");
                                    int status = info.getInt("status");
                                    String location = info.getString("location");

                                    Goods goods = new Goods(price,name,nid,description,quanutity,type,status,location);
                                    mGoodsList.add(goods);
                                }
                                goodsLeaseAdapter.notifyDataSetChanged();
                            } else {
                                ToastUtil.show("获取物品信息失败，请稍后重试");
                                Logs.e("获取物品信息失败：服务器错误");
                            }
                            swipe_refresh.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.show("获取物品信息失败，请稍后重试");
                            Logs.e("获取物品失败:" + e.toString());
                            swipe_refresh.setRefreshing(false);
                        }
                    }
                });
    }
}
