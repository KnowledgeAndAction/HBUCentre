package cn.ian2018.hbu.centre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.ian2018.hbu.centre.model.Active;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import okhttp3.Call;

/**
 * 培训活动列表
 */

public class TrainingListActivity extends AppCompatActivity {

    private String groupName;
    private int groupCode;
    private SwipeRefreshLayout swipe_refresh;
    private List<Active> mActiveList = new ArrayList<>();
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_list);

        Intent intent = getIntent();
        groupName = intent.getStringExtra("groupName");
        groupCode = intent.getIntExtra("group",0);
        Logs.d("组号："+groupCode);

        initView();

        initData();
    }

    private void initData() {
        swipe_refresh.setRefreshing(true);
        OkHttpUtils
                .get()
                .url(URLs.GET_ACTIVE_BY_ACCOUNT)
                .addParams("account",groupCode+"")
                .addParams("rule",3+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.show("获取培训信息失败，请稍后重试");
                        Logs.e("获取培训信息失败:" + e.toString());
                        swipe_refresh.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Logs.d(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                mActiveList.clear();
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i=0; i < data.length(); i++) {
                                    JSONObject info = data.getJSONObject(i);
                                    String name = info.getString("activityName");
                                    String des = info.getString("activityDes");
                                    String time = info.getString("time");
                                    String location = info.getString("location");
                                    long activeId = info.getLong("id");
                                    String endTime = info.getString("endTime");
                                    int show = info.getInt("display");

                                    Active active = new Active();
                                    active.setActiveId(activeId);
                                    active.setActiveName(name);
                                    active.setActiveLocation(location);
                                    active.setActiveDes(des);
                                    active.setActiveTime(time);
                                    active.setEndTime(endTime);
                                    active.setRule(3);

                                    mActiveList.add(active);
                                }
                                myAdapter.notifyDataSetChanged();
                            } else {
                                ToastUtil.show("暂无培训记录");
                                Logs.e("无培训记录失败");
                            }
                            swipe_refresh.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.show("获取培训记录失败，请稍后重试");
                            Logs.e("获取培训记录失败:" + e.toString());
                            swipe_refresh.setRefreshing(false);
                        }
                    }
                });
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(groupName + "培训");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        // 设置下拉刷新
        swipe_refresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });

        ListView lv_training_list = (ListView) findViewById(R.id.lv_training_list);
        myAdapter = new MyAdapter();
        lv_training_list.setAdapter(myAdapter);
        lv_training_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Active active = mActiveList.get(position);
                // 跳转到培训详情签到页面
                Intent intent = new Intent(TrainingListActivity.this,TrainingInfoActivity.class);
                intent.putExtra("active",active);
                startActivity(intent);
            }
        });
    }

    // listview适配器
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mActiveList.size();
        }

        @Override
        public Active getItem(int position) {
            return mActiveList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_list, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tv_name.setText(getItem(position).getActiveName());
            viewHolder.tv_location.setText("地点: "+getItem(position).getActiveLocation());
            viewHolder.tv_time.setText(getItem(position).getActiveTime().replace("T", " ").substring(0, 16));


            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_name;
        TextView tv_location;
        TextView tv_time;
    }
}
