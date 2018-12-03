package cn.ian2018.hbu.centre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hicc.information.sensorsignin.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.ian2018.hbu.centre.adapter.GoodsLeasePeopleAdapter;
import cn.ian2018.hbu.centre.model.Goods;
import cn.ian2018.hbu.centre.model.LeaseUser;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import cn.ian2018.hbu.centre.view.FullyLinearLayoutManager;
import okhttp3.Call;

/**
 * 管理物品详情页
 */
public class ManageGoodsInfoActivity extends AppCompatActivity {

    private Goods mGoods;
    private SwipeRefreshLayout refreshLayout;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private List<LeaseUser> leaseUserList = new ArrayList<>();
    private GoodsLeasePeopleAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_goods_info);

        Intent intent = getIntent();
        mGoods = (Goods) intent.getSerializableExtra("goods");

        initView();

        // 获取当前租借人信息
        initData();
    }

    // 获取当前租借人信息
    private void initData() {
        refreshLayout.setRefreshing(true);
        OkHttpUtils
                .get()
                .url(URLs.GET_LOAD_USER)
                .addParams("GoodsID",mGoods.getNid()+"")
                .addParams("Status","0")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.show("获取当前租借人信息失败，请稍后重试");
                        Logs.e("获取当前租借人信息失败:" + e.toString());
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                leaseUserList.clear();
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i=0; i < data.length(); i++) {
                                    JSONObject info = data.getJSONObject(i);
                                    String name = info.getString("name");
                                    int groupCode = info.getInt("groupCode");
                                    int grade = info.getInt("grade");
                                    String phone = info.getString("phone");

                                    LeaseUser leaseUser = new LeaseUser(name,grade,groupCode,phone);
                                    leaseUserList.add(leaseUser);
                                }
                                myAdapter.notifyDataSetChanged();
                            } else {
                                ToastUtil.show("该物品暂无人租借");
                                Logs.e("无人租借");
                            }
                            refreshLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.show("获取当前租借人信息失败，请稍后重试");
                            Logs.e("获取当前租借人信息失败:" + e.toString());
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
        toolbarLayout.setTitle(mGoods.getName());
        // 设置图片
        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        Glide.with(this).load(mGoods.getImageUrl()).into(imageView);

        TextView tv_price = (TextView) findViewById(R.id.tv_price);
        TextView tv_location = (TextView) findViewById(R.id.tv_location);
        TextView tv_number = (TextView) findViewById(R.id.tv_number);
        TextView tv_des = (TextView) findViewById(R.id.tv_des);

        tv_price.setText("价格：" + mGoods.getPrice() + "元");
        tv_location.setText("放置地点：" + mGoods.getLocation());
        tv_number.setText("数量：" + mGoods.getQuanutity());
        tv_des.setText("说明：" + mGoods.getDescription());

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new FullyLinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        // 设置适配器
        myAdapter = new GoodsLeasePeopleAdapter(leaseUserList);
        recyclerView.setAdapter(myAdapter);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setRippleColor(getResources().getColor(R.color.colorPrimaryDark));

        // 设置FAB点击事件
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到修改物品信息界面
                Intent intent = new Intent(getApplicationContext(),ChangeGoodsActivity.class);
                intent.putExtra("goods",mGoods);
                startActivity(intent);
            }
        });

        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.scrollView);
        // 设置滑动监听
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY - oldScrollY > 0) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
    }
}
