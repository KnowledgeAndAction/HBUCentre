package cn.ian2018.hbu.centre.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import cn.ian2018.hbu.centre.model.Goods;
import cn.ian2018.hbu.centre.utils.Constant;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.SpUtil;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import cn.ian2018.hbu.centre.utils.Utils;
import okhttp3.Call;


public class GoodsInfoActivity extends AppCompatActivity {

    private Goods mGoods;
    private final int PRECIOUS = 1; // 贵重
    private final int UN_PRECIOUS = 0;  // 不贵重
    private ProgressDialog progressDialog;
    private String mDate = "";
    private String mTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_info);

        Intent intent = getIntent();
        mGoods = (Goods) intent.getSerializableExtra("goods");

        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("物品详情");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView tv_name = (TextView) findViewById(R.id.tv_name);
        TextView tv_num = (TextView) findViewById(R.id.tv_num);
        TextView tv_des = (TextView) findViewById(R.id.tv_des);
        TextView tv_location = (TextView) findViewById(R.id.tv_location);
        Button bt_lease = (Button) findViewById(R.id.bt_lease);

        tv_name.setText(mGoods.getName());
        tv_num.setText("剩余：" + mGoods.getQuanutity());
        tv_location.setText("地点：" + mGoods.getLocation());
        tv_des.setText("    " + mGoods.getDescription());

        // 设置按钮点击事件
        bt_lease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoods.getType() == UN_PRECIOUS) {
                    // 可以直接租借
                    showLeaseDialog();
                } else {
                    // 需要打电话确认
                    getRepairInfo();
                }
            }
        });
    }

    // 获取维修人信息
    private void getRepairInfo() {
        showProgressDialogs("获取信息中...");
        OkHttpUtils
                .get()
                .url(URLs.GET_REPAIR_USER)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.show("获取维修人信息失败，请稍后重试");
                        Logs.e("获取维修人信息失败:" + e.toString());
                        closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                String name = data.getString("name");
                                String phone = data.getString("phone");
                                closeProgressDialog();
                                // 弹出对话框
                                showCallPhoneDialog(name, phone);
                            } else {
                                ToastUtil.show("获取维修人信息失败，请稍后重试");
                                Logs.e("获取维修人信息失败:服务器错误");
                                closeProgressDialog();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.show("获取维修人信息失败，请稍后重试");
                            Logs.e("获取维修人信息失败:" + e.toString());
                            closeProgressDialog();
                        }
                    }
                });
    }

    // 弹出打电话对话框
    private void showCallPhoneDialog(String name, final String phone) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        //设置对话框标题
        builder.setTitle(mGoods.getName());
        //设置对话框内容
        builder.setMessage("该物品为贵重物品，如果想租借使用，请联系：" + name + "\n电话：" + phone);
        //设置积极的按钮
        builder.setPositiveButton("打电话", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 跳转到拨号界面
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });
        //设置消极的按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    // 弹出租借对话框
    private void showLeaseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_lease, null);
        dialog.setView(view, 0, 0, 0, 0);

        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        final Button bt_date = (Button) view.findViewById(R.id.bt_date);
        final Button bt_time = (Button) view.findViewById(R.id.bt_time);
        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);

        tv_name.setText(mGoods.getName());
        // 弹出选择日期对话框
        bt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
                                mDate = i + "-" + (i1 + 1) + "-" + i2;
                                bt_date.setText(mDate);
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAccentColor("#154db4");
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        // 弹出选择时间对话框
        bt_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePickerDialog timePickerDialog, int i, int i1, int i2) {
                                mTime = i + ":" + i1;
                                bt_time.setText(mTime);
                            }
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                );
                tpd.setAccentColor("#154db4");
                tpd.show(getFragmentManager(), "Timepickerdialog");
            }
        });
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mDate.equals("") && !mTime.equals("")) {
                    sendLeaseInfo();
                    dialog.cancel();
                } else {
                    ToastUtil.show("请选择归还日期");
                }
            }
        });

        dialog.show();
    }

    private void sendLeaseInfo() {
        showProgressDialogs("租借中...");
        OkHttpUtils
                .get()
                .url(URLs.LEASE_GOODS)
                .addParams("Account", SpUtil.getString(Constant.ACCOUNT, ""))
                .addParams("ArticleId", mGoods.getNid() + "")
                .addParams("Time", Utils.getTime())
                .addParams("BackTime", mDate + " " + mTime)
                .addParams("Handle", SpUtil.getString(Constant.ACCOUNT, ""))
                .addParams("Quantity", (mGoods.getQuanutity() - 1) + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        closeProgressDialog();
                        Logs.e("租借失败：" + e.toString());
                        ToastUtil.show("租借失败，请稍后重试");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                ToastUtil.show("租借成功");
                                finish();
                            } else {
                                ToastUtil.show("租借失败，请稍后重试");
                            }
                            closeProgressDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            closeProgressDialog();
                            Logs.e("租借失败：" + e.toString());
                            ToastUtil.show("租借失败，请稍后重试");
                        }
                    }
                });
    }

    private void showProgressDialogs(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
