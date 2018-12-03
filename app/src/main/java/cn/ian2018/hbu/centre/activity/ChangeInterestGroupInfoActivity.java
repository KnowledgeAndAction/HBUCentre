package cn.ian2018.hbu.centre.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import cn.ian2018.hbu.centre.model.InterestGroup;
import cn.ian2018.hbu.centre.utils.Constant;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.SpUtil;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import okhttp3.Call;


public class ChangeInterestGroupInfoActivity extends AppCompatActivity {

    private InterestGroup mInterestGroup;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_interest_group_info);

        // 从上一个activity接收数据
        Intent intent = getIntent();
        mInterestGroup = (InterestGroup) intent.getSerializableExtra("interestGroup");

        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mInterestGroup.getInterestGroup());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView tv_des = (TextView) findViewById(R.id.tv_des);
        Button bt_submit = (Button) findViewById(R.id.bt_submit);

        tv_des.setText(mInterestGroup.getDes());
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignConfirmDialog();
            }
        });
    }

    // 修改兴趣小组
    private void changeInterestGroup() {
        showDialog();
        OkHttpUtils
                .get()
                .url(URLs.CHANGE_INTEREST_GROUP)
                .addParams("Account", SpUtil.getString(Constant.ACCOUNT,""))
                .addParams("InterestGroup", mInterestGroup.getNid()+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        closeDialog();
                        ToastUtil.show("修改兴趣小组失败，请稍后重试");
                        Logs.e("修改兴趣小组失败:"+e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                ToastUtil.show("修改兴趣小组成功");
                                SpUtil.putInt(Constant.USER_INTERESTGROUP,mInterestGroup.getNid());
                            } else {
                                ToastUtil.show("修改兴趣小组失败");
                            }
                            closeDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            closeDialog();
                            ToastUtil.show("修改兴趣小组失败");
                            Logs.e("修改兴趣小组失败:"+e.toString());
                        }
                    }
                });
    }

    // 显示确认签到对话框
    private void showSignConfirmDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        //设置对话框左上角图标
        builder.setIcon(R.mipmap.ic_launcher);
        //设置对话框标题
        builder.setTitle("加入"+mInterestGroup.getInterestGroup());
        //设置文本内容
        builder.setMessage("您确定要加入" + mInterestGroup.getInterestGroup() + "兴趣小组吗？");
        //设置积极的按钮
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                changeInterestGroup();
            }
        });
        //设置消极的按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void showDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("修改兴趣小组中...");
        progressDialog.show();
    }

    private void closeDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
