package cn.ian2018.hbu.centre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hicc.information.sensorsignin.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.ian2018.hbu.centre.adapter.OnDutySignInfoAdapter;
import cn.ian2018.hbu.centre.model.SignInfo;
import cn.ian2018.hbu.centre.utils.Constant;
import cn.ian2018.hbu.centre.utils.SpUtil;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import cn.ian2018.hbu.centre.utils.Utils;
import okhttp3.Call;

/**
 * 值班签到情况
 */
public class OnDutySignInfoActivity extends AppCompatActivity {

    private String title;
    private long activeId;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipe_refresh;
    private List<SignInfo> signInfoList = new ArrayList<>();
    private OnDutySignInfoAdapter myAdapter;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private String mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_duty_sign_info);

        // 从上一个activity接受信息
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        activeId = intent.getLongExtra("activeId",0);
        mDate = Utils.getDate();

        initView();

        initData();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title + "  " + mDate);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setRippleColor(getResources().getColor(R.color.colorPrimaryDark));

        // 设置FAB点击事件
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出选择日期对话框
                showDateDialog();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new OnDutySignInfoAdapter(signInfoList);
        recyclerView.setAdapter(myAdapter);

        // 设置recycleview滑动监听事件
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 隐藏或者显示fab
                if (dy > 0) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });

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
    }

    // 选择日期对话框
    private void showDateDialog() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
                        String date = i + "-" + (i1 + 1) + "-" + i2;
                        mDate = date;
                        toolbar.setTitle(title + "  "+ date);
                        // 重新刷新信息
                        swipe_refresh.setRefreshing(true);
                        initData();
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        //dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.setAccentColor("#154db4");
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    // 根据用户类型获取是否查看所有组
    private int getGroupCode() {
        int groupCode;
        int type = SpUtil.getInt(Constant.USER_TYPE, 0);
        if (type == 1 || type == 2){
            groupCode = 0;
        } else {
            groupCode = SpUtil.getInt(Constant.USER_GROUP,0);
        }

        return groupCode;
    }

    // 从网络获取数据
    private void initData() {
        OkHttpUtils
                .get()
                // 根据活动id和时间  原来是：GET_ONDUTY_SIGN
                .url(URLs.GET_SIGN_BY_DATE)
                .addParams("ActiveID",activeId+"")
                //.addParams("GroupCode",getGroupCode()+"")
                .addParams("Date",mDate)
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
            signInfoList.clear();
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("sucessed")) {
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i=0; i<data.length(); i++) {
                    JSONObject object = data.getJSONObject(i);
                    String name = object.getString("name");
                    String intime = object.getString("inTime");
                    String outtime = object.getString("outTime");
                    int groupCode = object.getInt("groupCode");
                    //int backTo = object.getInt("backTo");

                    SignInfo signInfo = new SignInfo();
                    signInfo.setName(name);
                    signInfo.setGroupCode(groupCode);
                    signInfo.setBackTo(0);
                    signInfo.setInTime(intime);
                    signInfo.setOutTime(outtime);

                    signInfoList.add(signInfo);
                }

                swipe_refresh.setRefreshing(false);
                myAdapter.notifyDataSetChanged();
            } else {
                swipe_refresh.setRefreshing(false);
                ToastUtil.show("暂无签到信息");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            swipe_refresh.setRefreshing(false);
            ToastUtil.show("加载数据失败，请稍后重试:" + e.toString());
        }
    }
}
