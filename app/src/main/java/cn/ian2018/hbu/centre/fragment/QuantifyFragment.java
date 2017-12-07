package cn.ian2018.hbu.centre.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.ian2018.hbu.centre.activity.HistoryActivity;
import cn.ian2018.hbu.centre.db.MyDatabase;
import cn.ian2018.hbu.centre.model.AnalyzeSign;
import cn.ian2018.hbu.centre.model.HistoryActive;
import cn.ian2018.hbu.centre.utils.Constant;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.SpUtil;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import cn.ian2018.hbu.centre.utils.Utils;
import okhttp3.Call;
import rorbin.q.radarview.RadarData;
import rorbin.q.radarview.RadarView;


/**
 * 量化界面
 */

public class QuantifyFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private List<HistoryActive> mActiveList = new ArrayList<>();
    private boolean respond;
    private SwipeRefreshLayout mSwipeLayout;
    private View view;
    private RadarView mRadarView;
    private MyDatabase db;
    private TextView tv_des;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);

        db = MyDatabase.getInstance();

        // 判断网络是否可用
        respond = Utils.isNetworkAvalible(getContext());

        initView(view);

        // 获取活动信息
        getActive();

        getAllDutyTime();

        return view;
    }

    private void getAllDutyTime() {
        OkHttpUtils
                .get()
                .url(URLs.GET_ALL_DUTY_TIME)
                .addParams("studentNum", SpUtil.getString(Constant.ACCOUNT, ""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.e("获取总时间失败:" + e.toString());
                        tv_des.setText("抱歉，数据出了点问题，请刷新重试");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                String time = jsonObject.getString("data");
                                tv_des.setText("同学，你已经累计值班" + time + "，继续加油啊");
                            } else {
                                tv_des.setText("同学，目前还没有值班记录，赶快走一波学习吧");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Logs.e("获取总时间失败:" + e.toString());
                            tv_des.setText("抱歉，数据出了点问题，请刷新重试");
                        }
                    }
                });
    }

    private void initData() {
        List<String> vertexText = new ArrayList<>();
        Collections.addAll(vertexText, "毅力", "通识", "守时", "自律", "精神");
        mRadarView.setVertexText(vertexText);

        // 获取日常签到次数
        int dailyFrequency = db.getDailyFrequency(SpUtil.getString(Constant.ACCOUNT, ""));
        // 获取讲座签到次数
        int lectureFrequency = db.getLectureFrequency(SpUtil.getString(Constant.ACCOUNT, ""));
        // 获取准时签到的次数
        int earlyFrequency = db.getEarlyFrequency(SpUtil.getString(Constant.ACCOUNT, ""));

        List<Float> values = new ArrayList<>();
        Collections.addAll(values, (float) dailyFrequency, (float) lectureFrequency, (float) earlyFrequency, 7f, 5f);
        RadarData data = new RadarData(values);

        mRadarView.addData(data);

        mRadarView.animeValue(2000);
    }

    // 获取历史活动
    private void getActive() {
        db.deleteAnalyzeSign();
        OkHttpUtils
                .get()
                .url(URLs.GET_SING)
                .addParams("userId", SpUtil.getString(Constant.ACCOUNT, ""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        mSwipeLayout.setRefreshing(false);
                        if (respond) {
                            ToastUtil.show("历史记录响应失败:" + e.toString());
                        } else {
                            ToastUtil.show("当前网络不可用，请检查网络连接");
                        }
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            boolean sucessed = jsonObject.getBoolean("sucessed");
                            mActiveList.clear();
                            if (sucessed) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int j = 0; j < data.length(); j++) {
                                    JSONObject hActivity = data.getJSONObject(j);

                                    String hActivityId = hActivity.getString("id");
                                    String hStudnetNum = hActivity.getString("studentNum");
                                    String hInTime = hActivity.getString("inTime");
                                    String hOutTime = hActivity.getString("outTime");
                                    String hActivityDescription = hActivity.getString("activityDes");
                                    String hTime = hActivity.getString("time");
                                    String hLocation = hActivity.getString("location");
                                    String hActivityName = hActivity.getString("activityName");

                                    // 如果是完成签离的活动才展示
                                    if (!hInTime.equals(hOutTime)) {
                                        HistoryActive historyActive = new HistoryActive();
                                        historyActive.sethActivityId(hActivityId);
                                        historyActive.sethStudnetNum(hStudnetNum);
                                        historyActive.sethInTime(hInTime);
                                        historyActive.sethOutTime(hOutTime);
                                        historyActive.setActivityDescription(hActivityDescription);
                                        historyActive.sethActivityName(hActivityName);
                                        historyActive.sethLocation(hLocation);
                                        historyActive.sethTime(hTime);
                                        historyActive.setEndTime(hActivity.getString("endTime"));
                                        mActiveList.add(historyActive);

                                        AnalyzeSign analyzeSign = new AnalyzeSign();
                                        analyzeSign.setNumber(hStudnetNum);
                                        analyzeSign.setActiveName(hActivityName);
                                        analyzeSign.setRule(Integer.valueOf(hActivity.getString("rule")));
                                        analyzeSign.setInTime(hInTime);
                                        analyzeSign.setOutTime(hOutTime);
                                        analyzeSign.setTime(hTime);
                                        analyzeSign.setEndTime(hActivity.getString("endTime"));
                                        db.saveAnalyzeSign(analyzeSign);
                                    }
                                    initData();
                                }
                            } else {
                                mRadarView.setEmptyHint("暂无数据");
                            }
                            mSwipeLayout.setRefreshing(false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mSwipeLayout.setRefreshing(false);
                            ToastUtil.show("获取历史记录失败：" + e.toString());
                        }
                    }
                });
    }

    @Override
    public void fetchData() {
    }

    private void initView(View view) {
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark);
        mSwipeLayout.setOnRefreshListener(this);

        tv_des = (TextView) view.findViewById(R.id.tv_des);

        mRadarView = (RadarView) view.findViewById(R.id.radarView);
        mRadarView.setEmptyHint("暂无数据");

        // 设置线条颜色
        List<Integer> layerColor = new ArrayList<>();
        Collections.addAll(layerColor, 0x3300bcd4, 0x3303a9f4, 0x335677fc, 0x333f51b5, 0x33673ab7);
        mRadarView.setLayerColor(layerColor);

        TextView tv_detail = (TextView) view.findViewById(R.id.tv_detail);
        tv_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), HistoryActivity.class));
            }
        });
        TextView tv_quantify = (TextView) view.findViewById(R.id.tv_quantify);
        tv_quantify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onRefresh() {
        getActive();

        getAllDutyTime();
    }
}
