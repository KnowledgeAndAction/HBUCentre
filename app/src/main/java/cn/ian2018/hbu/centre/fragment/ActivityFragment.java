package cn.ian2018.hbu.centre.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothAdapter;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;
import com.sensoro.cloud.SensoroManager;
import com.skyfishjy.library.RippleBackground;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.ian2018.hbu.centre.activity.DetailActivity;
import cn.ian2018.hbu.centre.activity.ScanActivity;
import cn.ian2018.hbu.centre.db.MyDatabase;
import cn.ian2018.hbu.centre.model.Active;
import cn.ian2018.hbu.centre.model.Saying;
import cn.ian2018.hbu.centre.service.SensorService;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import cn.ian2018.hbu.centre.utils.Utils;
import okhttp3.Call;

/**
 * 活动对应界面
 */

public class ActivityFragment extends BaseFragment {

    private static final int BLUETOOTH_CODE = 0;
    private static final int SCAN_CODE = 1;

    private static final int TYPE_ORDINARY = 1; // 普通活动
    private static final int TYPE_DUTY = 2; // 值班
    private static final int TYPE_TRAINING = 3; // 培训
    private static final int TYPE_RUN = 4; // 跑步
    private static final int TYPE_READ = 5; // 晨读
    private List<Active> mActiveList = new ArrayList<>();
    private ListView listView;
    private MyBroadcast myBroadcast;
    private String yunziId;
    private MyAdapter adapter;
    private SensorGoneBroadcast sensorGoneBroadcast;
    private MyDatabase db;
    private List<Saying> sayingList;
    private long startTime;
    private SensoroManager sensoroManager;
    private TextView tv_scan;
    private RippleBackground rippleBackground;
    private ImageView foundDevice;
    private TextView tv_saying;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                // 获取活动成功
                case 1:
                    foundDevice.setImageResource(R.drawable.ic_phone2);
                    closeDialog();
                    break;
                // 获取活动失败
                case 2:
                    foundDevice.setImageResource(R.drawable.ic_error);
                    closeDialog();
                    // 延迟2秒再显示textview  为的是等动画结束
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tv_scan.setVisibility(View.VISIBLE);
                        }
                    },2000);
                    break;
            }

        }
    };

    @Override
    public void fetchData() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active, container, false);

        sensoroManager = SensoroManager.getInstance(getContext());
        db = MyDatabase.getInstance();
        sayingList = db.getSaying();

        initView(view);

        // 注册广播接收者
        initBroadcast();

        // 检查蓝牙是否可用
        checkBluetooth();

        return view;
    }

    // 开启子线程一直检测获取云子的时间
    private void checkTimeThread() {
        // 获取开始时间
        startTime = System.currentTimeMillis();

        new Thread() {
            @Override
            public void run() {
                super.run();
                boolean looper = true;
                while (looper) {
                    long currentTimeMillis = System.currentTimeMillis();
                    // 如果过了60秒 并且还没有活动   就发送消息 通知显示TextView
                    if (currentTimeMillis - startTime > 60000 && mActiveList.size() == 0) {
                        mHandler.sendEmptyMessage(2);
                        looper = false;
                    }
                }
            }
        }.start();
    }

    // 检查蓝牙是否可用
    private void checkBluetooth() {
        if (!sensoroManager.isBluetoothEnabled()) {
            Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bluetoothIntent, BLUETOOTH_CODE);
        } else {
            showDialog();
            // 开启子线程一直检测获取云子的时间
            checkTimeThread();
            // 如果服务没有运行，开启服务
            if (!Utils.ServiceIsWorked(SensorService.class.getName())) {
                getContext().startService(new Intent(getContext(), SensorService.class));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BLUETOOTH_CODE:
                // 蓝牙可用
                if (sensoroManager.isBluetoothEnabled()) {
                    showDialog();
                    // 开启子线程一直检测获取云子的时间
                    checkTimeThread();
                    // 如果服务没有运行，开启服务
                    if (!Utils.ServiceIsWorked(SensorService.class.getName())) {
                        getContext().startService(new Intent(getContext(), SensorService.class));
                    }
                } else {
                    ToastUtil.show("请打开蓝牙");
                }
                break;
            case SCAN_CODE:
                //处理扫描结果（在界面上显示）
                if (null != data) {
                    Bundle bundle = data.getExtras();
                    if (bundle == null) {
                        return;
                    }
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        try {
                            String result = bundle.getString(CodeUtils.RESULT_STRING);
                            // 解析后操作
                            if (result.substring(0,4).equals("http")) {
                                result = result.substring(13,25);
                            } else {
                                result = result.substring(0,12);
                            }
                            // 获取活动
                            showDialog();
                            getActive(result,true);
                        } catch (StringIndexOutOfBoundsException e) {
                            ToastUtil.showLong("请确保您扫描的是云子上的二维码");
                        }
                    } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                        ToastUtil.show("解析二维码失败");
                    }
                }
        }
    }

    // 初始化控件
    private void initView(View view) {
        rippleBackground = (RippleBackground) view.findViewById(R.id.content);
        foundDevice = (ImageView) view.findViewById(R.id.foundDevice);
        tv_saying = (TextView) view.findViewById(R.id.tv_saying);

        tv_scan = (TextView) view.findViewById(R.id.tv_scan);
        tv_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), ScanActivity.class),SCAN_CODE);
            }
        });

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
    private void getActive(String yunziId, final boolean isScan) {
        OkHttpUtils
                .get()
                // TODO 无值班表方式
                .url(URLs.GET_ACTIVE_BY_YUNZI_NO_DUTY)
                .addParams("sensoroId", yunziId)
                //.addParams("studentNum", SpUtil.getString(Constant.ACCOUNT,""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        ToastUtil.show("获取活动失败，请稍后重试");
                        Logs.e("获取活动失败:" + e.toString());
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        System.out.println(s);
                        // 解析
                        parseJson(s,isScan);
                    }
                });
    }

    // 解析json
    private void parseJson(String s, boolean isScan) {
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
                    // TODO 如果显示 并且 是普通活动或值班是当前星期：&& (week==0 || week == Utils.getWeek())
                    // 暂时先把当前星期判断去掉
                    if (show == 1) {
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
                                active.setScan(isScan); // 是否通过扫描二维码获取的
                                mActiveList.add(active);
                                Logs.d("添加一个活动:" + name);

                                // 如果有活动，就把TextView隐藏
                                tv_scan.setVisibility(View.GONE);
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
                // 如果扫到云子的时间在4秒之内，则让对话框保持停留4秒
                /*if ((endTime - startTime) < 4000) {
                    mHandler.sendEmptyMessageDelayed(0, 4000 - (endTime - startTime));
                } else {
                    mHandler.sendEmptyMessage(1);
                }*/
                mHandler.sendEmptyMessage(1);
            }
        }
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
            getActive(yunziId,false);
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
            switch (getItem(position).getRule()) {
                case TYPE_ORDINARY:
                case TYPE_TRAINING:
                    viewHolder.tv_time.setText(getItem(position).getActiveTime().replace("T", " ").substring(0, 16));
                    break;
                case TYPE_DUTY:
                case TYPE_READ:
                case TYPE_RUN:
                    viewHolder.tv_time.setText("时间: "+getItem(position).getActiveTime().replace("T", " ").substring(11, 16)
                            + " - " + getItem(position).getEndTime().replace("T", " ").substring(11, 16));
                    break;
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

    // 找到云子的动画
    private void foundDevice() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList = new ArrayList<>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        foundDevice.setVisibility(View.VISIBLE);
        animatorSet.start();
    }

    private void showDialog() {
        // 产生一个随机数
        int number = (int) (Math.random() * 100) + 1;
        if (sayingList.size() > 0) {
            int position = number % sayingList.size();
            tv_saying.setText(sayingList.get(position).getContent());
        }
        rippleBackground.setVisibility(View.VISIBLE);
        rippleBackground.startRippleAnimation();
    }

    // 关闭动画
    private void closeDialog() {
        foundDevice();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rippleBackground.stopRippleAnimation();
                foundDevice.setVisibility(View.GONE);
                rippleBackground.setVisibility(View.GONE);
            }
        }, 2000);
    }
}
