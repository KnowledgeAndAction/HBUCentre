package cn.ian2018.hbu.centre.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.ian2018.hbu.centre.activity.DetailActivity;
import cn.ian2018.hbu.centre.db.MyDatabase;
import cn.ian2018.hbu.centre.model.Active;
import cn.ian2018.hbu.centre.model.Saying;
import cn.ian2018.hbu.centre.utils.Constant;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.SpUtil;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import cn.ian2018.hbu.centre.utils.Utils;
import okhttp3.Call;

/**
 * 活动对应界面
 */

public class ActivityFragment extends BaseFragment {
    private List<Active> mActiveList = new ArrayList<>();
    private ListView listView;
    private MyBroadcast myBroadcast;
    private String yunziId;
    private MyAdapter adapter;
    private SensorGoneBroadcast sensorGoneBroadcast;
    private ProgressDialog progressDialog;
    private MyDatabase db;
    private List<Saying> sayingList;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    closeDialog();
                    break;
                case 1:
                    closeDialog();
                    break;
            }

        }
    };
    private long startTime;

    @Override
    public void fetchData() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active, container, false);

        db = MyDatabase.getInstance();
        sayingList = db.getSaying();

        // 注册广播接收者
        initBroadcast();

        initView(view);

        startTime = System.currentTimeMillis();
        showDialog();

        Logs.d("saying" + sayingList.size());

        return view;
    }

    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.lv_active);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Active active = mActiveList.get(position);
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("active", active);
                intent.putExtra("yunziId", yunziId);
                startActivity(intent);
            }
        });
    }

    // 注册广播接收者
    private void initBroadcast() {
        // 注册发现云子广播接收者
        myBroadcast = new MyBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("GET_YUNZI_ID");
        getContext().registerReceiver(myBroadcast, intentFilter);

        // 注册云子消失广播接收者
        sensorGoneBroadcast = new SensorGoneBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("SENSOR_GONE");
        getContext().registerReceiver(sensorGoneBroadcast, filter);
    }

    // 从网络获取活动信息
    private void getActive(final String yunziId) {
        OkHttpUtils
                .get()
                .url(URLs.GET_ACTIVE_BY_YUNZI)
                .addParams("sensoroId", yunziId)
                .addParams("studentNum", SpUtil.getString(Constant.ACCOUNT,""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        ToastUtil.show("获取活动失败，请稍后重试" + e.toString());
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        System.out.println(s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            boolean sucessed = jsonObject.getBoolean("sucessed");
                            if (sucessed && jsonObject.getJSONArray("data").length()>0) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int j = 0; j < data.length(); j++) {
                                    JSONObject activity = data.getJSONObject(j);
                                    String name = activity.getString("activityName");
                                    String des = activity.getString("activityDes");
                                    String time = activity.getString("time");
                                    String location = activity.getString("location");
                                    long activeId = activity.getLong("id");
                                    String endTime = activity.getString("endTime");
                                    int rule = activity.getInt("rule");
                                    int show = activity.getInt("display");
                                    int backTo = activity.getInt("backTo");
                                    int week = activity.getInt("week");
                                    Logs.d("week:"+week+"       当前："+Utils.getWeek());
                                    if (show == 1 && (week==0 || week== Utils.getWeek())) {
                                        // 获取当前时间，判断该活动是否已经失效，不失效时才添加到集合中
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        String presentTime = sdf.format(new java.util.Date());
                                        if (sdf.parse(presentTime).getTime() <= sdf.parse(endTime.replace("T", " ").substring(0, 19)).getTime()) {
                                            if (getActiveForNid(activeId) != null) {
                                                Active activeForId = getActiveForNid(activeId);
                                                activeForId.setActiveId(activeId);
                                                activeForId.setSersorID(yunziId);
                                                activeForId.setActiveName(name);
                                                activeForId.setActiveTime(time);
                                                activeForId.setActiveDes(des);
                                                activeForId.setActiveLocation(location);
                                                activeForId.setEndTime(endTime);
                                                activeForId.setRule(rule);
                                                activeForId.setBackTo(backTo);
                                                Logs.d("活动信息更新:" + name);
                                            } else {
                                                Active active = new Active();
                                                active.setActiveId(activeId);
                                                active.setSersorID(yunziId);
                                                active.setActiveName(name);
                                                active.setActiveTime(time);
                                                active.setActiveDes(des);
                                                active.setActiveLocation(location);
                                                active.setEndTime(endTime);
                                                active.setRule(rule);
                                                active.setBackTo(backTo);
                                                mActiveList.add(active);
                                                Logs.d("添加一个活动:" + name);
                                            }
                                        } else {
                                            Active activeForId = getActiveForNid(activeId);
                                            mActiveList.remove(activeForId);
                                            Logs.d("这个活动过期了:" + name);
                                        }
                                    } else {
                                        Logs.e("该活动被删除：" + name);
                                    }
                                }

                                adapter.notifyDataSetChanged();

                            } else {
                                Logs.d("这个云子上没有活动：" + yunziId);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Logs.d("异常");
                        } finally {
                            if (mActiveList.size() > 0) {
                                long endTime = System.currentTimeMillis();
                                if ((endTime - startTime) < 4000) {
                                    mHandler.sendEmptyMessageDelayed(0, 4000 - (endTime - startTime));
                                } else {
                                    mHandler.sendEmptyMessage(1);
                                }
                            }
                        }
                    }
                });
    }

    // 云子消失广播接收者
    public class SensorGoneBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logs.d("接收到了云子消失的广播");
            String sensorNumber = intent.getStringExtra("sensorNumber");
            List<Active> list = getActiveForSensor(sensorNumber);
            for (Active active : list) {
                // TODO 暂时不动态更新
                //mActiveList.remove(active);
                //adapter.notifyDataSetChanged();
            }
        }
    }

    // 发现云子
    public class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logs.d("发现云子:" + yunziId);
            yunziId = intent.getStringExtra("yunzi");
            // 根据云子id从网络获取具体活动信息
            getActive(yunziId);
        }
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
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_activity_list, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (getItem(position).getBackTo() == 1) {
                viewHolder.tv_name.setText(getItem(position).getActiveName() + " 补班");
            } else {
                viewHolder.tv_name.setText(getItem(position).getActiveName());
            }
            viewHolder.tv_location.setText("地点: "+getItem(position).getActiveLocation());
            if(getItem(position).getRule() == 3) {
                viewHolder.tv_time.setText("时间: "+getItem(position).getActiveTime().replace("T", " ").substring(11, 16)
                                            + " - " + getItem(position).getEndTime().replace("T", " ").substring(11, 16));
            } else {
                viewHolder.tv_time.setText(getItem(position).getActiveTime().replace("T", " ").substring(0, 16));
            }

            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_name;
        TextView tv_location;
        TextView tv_time;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 取消注册广播
        if (myBroadcast != null) {
            getContext().unregisterReceiver(myBroadcast);
        }
        if (sensorGoneBroadcast != null) {
            getContext().unregisterReceiver(sensorGoneBroadcast);
        }
        mActiveList.clear();
    }

    // 根据云子id获取对应的活动对象集合
    private List<Active> getActiveForSensor(String number) {
        List<Active> list = new ArrayList<>();
        for (Active active : mActiveList) {
            if (number.equals(active.getSersorID())) {
                list.add(active);
            }
        }
        return list;
    }
    // 根据Nid获取对应的活动对象
    private Active getActiveForNid(long nid) {
        for (Active active : mActiveList) {
            if (active.getActiveId() == nid) {
                return active;
            }
        }
        return null;
    }

    private void showDialog() {
        // 产生一个随机数
        int number = (int) (Math.random() * 100) + 1;
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
        }
        if (sayingList.size() > 0) {
            int position = number % sayingList.size();
            progressDialog.setMessage(sayingList.get(position).getContent());
        } else {
            progressDialog.setMessage("加载中...");
        }
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void closeDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
