package cn.ian2018.hbu.centre.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.hicc.information.sensorsignin.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import cn.ian2018.hbu.centre.utils.Constant;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.SpUtil;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import okhttp3.Call;

// 添加补班信息
public class AddBackDutyActivity extends AppCompatActivity implements View.OnClickListener{

    private String account = "";
    private String activityName = "";
    private String time = "";
    private String endTime = "";
    private String date = "";
    private int week = 0;
    private Button bt_group;
    private Button bt_week;
    private Button bt_time;
    private Button bt_date;
    private ProgressDialog progressDialog;
    private int index_group = 0;
    private int index_week = 0;
    private int index_time = 0;
    private Button bt_leave_date;
    private Button bt_leave_time;
    private String BackActiveID = "";
    private String LeaveTime = "";
    private String LeaveActiveID = "";
    private final int BACK = 0;
    private final int LEAVE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_back_duty);

        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("添加补班");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bt_group = (Button) findViewById(R.id.bt_group);
        bt_week = (Button) findViewById(R.id.bt_week);
        bt_time = (Button) findViewById(R.id.bt_time);
        bt_date = (Button) findViewById(R.id.bt_date);
        bt_leave_date = (Button) findViewById(R.id.bt_leave_date);
        bt_leave_time = (Button) findViewById(R.id.bt_leave_time);
        Button bt_submit = (Button) findViewById(R.id.bt_submit);

        bt_group.setOnClickListener(this);
        bt_week.setOnClickListener(this);
        bt_time.setOnClickListener(this);
        bt_date.setOnClickListener(this);
        bt_leave_date.setOnClickListener(this);
        bt_leave_time.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_group:
                showSwitchGroupDialog();
                break;
            case R.id.bt_week:
                showSwitchWeekDialog();
                break;
            case R.id.bt_time:
                showSwitchTimeDialog(BACK);
                break;
            case R.id.bt_date:
                showSwitchDate(BACK);
                break;
            case R.id.bt_leave_time:
                showSwitchTimeDialog(LEAVE);
                break;
            case R.id.bt_leave_date:
                showSwitchDate(LEAVE);
                break;
            case R.id.bt_submit:
                if (week!=0 && !activityName.equals("") && !date.equals("") && !LeaveActiveID.equals("") && !LeaveTime.equals("")) {
                    showConfirmDialog();
                } else {
                    Logs.d("account:"+account+",week:"+week+",activityName:"+activityName+",date:"+date+",LeaveActiveID:"+LeaveActiveID +",LeaveTime:"+LeaveTime);
                    ToastUtil.show("请将信息填写完整");
                }
                break;
        }
    }

    // 选择组别
    public void showSwitchGroupDialog(){
        final String[] items = {"Android组","iOS组","Java组","PHP组","前端组","视频组",".NET组"};
        final String[] groups = {"android","ios","java","php","qianduan","shipin",".net"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择组别");
        builder.setSingleChoiceItems(items, index_group, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                index_group = which;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                account = groups[index_group];
                bt_group.setText(items[index_group]);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    // 选择星期
    public void showSwitchWeekDialog(){
        final String[] items = {"星期一","星期二","星期三","星期四","星期五","星期六","星期日"};
        final int[] weeks = {2,3,4,5,6,7,1};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择星期");
        builder.setSingleChoiceItems(items, index_week, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                index_week = which;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                week = weeks[index_week];
                bt_week.setText(items[index_week]);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    // 选择节次
    public void showSwitchTimeDialog(final int type){
        final String[] items = {"第一大节(08:00-09:40)","第二大节(10:10-11:50)",
                "第三大节(14:30-16:10)","第四大节(16:20-18:00)","第五大节(19:00-21:35)"};
        final String[] times = {"08:00","10:10","14:30","16:20","19:00"};
        final String[] endTimes = {"09:40","11:50","16:10","18:00","21:35"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择节次");
        builder.setSingleChoiceItems(items, index_time, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                index_time = which;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (type) {
                    case BACK:
                        activityName = items[index_time] + "补班";
                        BackActiveID = String.valueOf(index_time+1);
                        time = times[index_time];
                        endTime = endTimes[index_time];
                        bt_time.setText(items[index_time].substring(0,4));
                        break;
                    case LEAVE:
                        LeaveActiveID = String.valueOf(index_time+1);
                        bt_leave_time.setText(items[index_time].substring(0,4));
                        break;
                }

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    // 选择时间
    public void showSwitchDate(final int type) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
                        switch (type) {
                            case BACK:
                                date = i + "-" + (i1 + 1) + "-" + i2;
                                bt_date.setText(date);
                                break;
                            case LEAVE:
                                LeaveTime = i + "-" + (i1 + 1) + "-" + i2;
                                bt_leave_date.setText(LeaveTime);
                                break;
                        }
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

    // 显示确认对话框
    public void showConfirmDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        // 设置对话框左上角图标
        builder.setIcon(R.mipmap.ic_launcher);
        // 设置不能取消
        builder.setCancelable(false);
        // 设置对话框标题
        builder.setTitle("添加补班");
        // 设置对话框内容
        builder.setMessage("您确认补班信息填写正确，添加该补班？");
        // 设置积极的按钮
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 提交
                submitBackDutyToService();
                dialog.dismiss();
            }
        });
        // 设置消极的按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    // 提交信息
    private void submitBackDutyToService() {
        showProgressDialog();
        OkHttpUtils
                .get()
                .url(URLs.ADD_BACK_DUTY)
                .addParams("activityname",activityName)
                .addParams("time",date+" "+time)
                .addParams("endtime",date+" "+endTime)
                .addParams("account",-1+"")
                .addParams("week",week+"")
                .addParams("studentNum", SpUtil.getString(Constant.ACCOUNT,""))
                .addParams("BackActiveID", BackActiveID)
                .addParams("LeaveTime", LeaveTime)
                .addParams("LeaveActiveID", LeaveActiveID)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.e("添加补班失败:"+e.toString());
                        ToastUtil.show("添加补班失败:"+e.toString());
                        closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        closeProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                ToastUtil.show("添加补班成功，请记得来值班");
                                finish();
                            } else {
                                Logs.e("添加补班失败");
                                ToastUtil.show("添加补班失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Logs.e("添加补班失败:"+e.toString());
                            ToastUtil.show("添加补班失败:"+e.toString());
                        }
                    }
                });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage("提交中...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
