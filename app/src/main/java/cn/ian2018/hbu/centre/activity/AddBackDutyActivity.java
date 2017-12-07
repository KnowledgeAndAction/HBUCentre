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
        Button bt_submit = (Button) findViewById(R.id.bt_submit);

        bt_group.setOnClickListener(this);
        bt_week.setOnClickListener(this);
        bt_time.setOnClickListener(this);
        bt_date.setOnClickListener(this);
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
                showSwitchTimeDialog();
                break;
            case R.id.bt_date:
                showSwitchDate();
                break;
            case R.id.bt_submit:
                if (!account.equals("") && week!=0 && !activityName.equals("") && !date.equals("")) {
                    showConfirmDialog();
                } else {
                    ToastUtil.show("请将信息填写完整");
                }
                break;
        }
    }

    // 选择组别
    public void showSwitchGroupDialog(){
        final String[] items = {"Android组","iOS组","Java组","PHP组","前端组","视频组",".NET组"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择组别");
        builder.setSingleChoiceItems(items, index_group, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                index_group = which;
                switch (which) {
                    case 0:
                        account = "android";
                        break;
                    case 1:
                        account = "ios";
                        break;
                    case 2:
                        account = "java";
                        break;
                    case 3:
                        account = "php";
                        break;
                    case 4:
                        account = "qianduan";
                        break;
                    case 5:
                        account = "shipin";
                        break;
                    case 6:
                        account = ".net";
                        break;
                }
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择星期");
        builder.setSingleChoiceItems(items, index_week, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                index_week = which;
                switch (which) {
                    case 0:
                        week = 2;
                        break;
                    case 1:
                        week = 3;
                        break;
                    case 2:
                        week = 4;
                        break;
                    case 3:
                        week = 5;
                        break;
                    case 4:
                        week = 6;
                        break;
                    case 5:
                        week = 7;
                        break;
                    case 6:
                        week = 1;
                        break;
                }
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    // 选择时间
    public void showSwitchTimeDialog(){
        final String[] items = {"第一大节(08:00-09:40)","第二大节(10:10-11:50)",
                "第三大节(14:30-16:10)","第四大节(16:20-18:00)","第五大节(19:00-21:35)"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择时间");
        builder.setSingleChoiceItems(items, index_time, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activityName = items[which] + "值班";
                index_time = which;
                switch (which) {
                    case 0:
                        time = "08:00";
                        endTime = "09:40";
                        break;
                    case 1:
                        time = "10:10";
                        endTime = "11:50";
                        break;
                    case 2:
                        time = "14:30";
                        endTime = "16:10";
                        break;
                    case 3:
                        time = "16:20";
                        endTime = "18:00";
                        break;
                    case 4:
                        time = "19:00";
                        endTime = "21:35";
                        break;
                }
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bt_time.setText(items[index_time].substring(0,4));
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

    public void showSwitchDate() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
                        date = i + "-" + (i1 + 1) + "-" + i2;
                        bt_date.setText(date);
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
        builder.setIcon(R.mipmap.logo2);
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
                .addParams("account",account)
                .addParams("week",week+"")
                .addParams("studentNum", SpUtil.getString(Constant.ACCOUNT,""))
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
