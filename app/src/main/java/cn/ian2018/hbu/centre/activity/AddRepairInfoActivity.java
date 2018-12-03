package cn.ian2018.hbu.centre.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.hicc.information.sensorsignin.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import okhttp3.Call;

/**
 * 管理员添加保修单界面
 */
public class AddRepairInfoActivity extends AppCompatActivity {

    private Button bt_submit;
    private ProgressDialog progressDialog;
    private EditText et_name;
    private EditText et_account;
    private EditText et_handle;
    private EditText et_date;
    private ImageView iv_date;
    private EditText et_repair_des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_repair);

        // 初始化控件
        initView();

        // 控制层
        control();
    }

    private void control() {
        // 提交按钮点击事件
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString().trim();
                String account = et_account.getText().toString().trim();
                String handle = et_handle.getText().toString().trim();
                String date = et_date.getText().toString().trim();
                String des = et_repair_des.getText().toString().trim();

                // 如果都不为空，提交报修单
                if (!name.equals("") && !account.equals("") && !handle.equals("")
                        && !date.equals("") && !des.equals("")) {
                    // 弹出确认对话框
                    showConfirmDialog(name, account, handle, date, des);
                } else {
                    ToastUtil.show("请将信息填写完整");
                }
            }
        });

        // 选择日期
        iv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });
    }

    private void showConfirmDialog(final String name, final String account, final String handle, final String date, final String des) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        // 设置对话框左上角图标
        builder.setIcon(R.mipmap.ic_launcher);
        // 设置不能取消
        builder.setCancelable(false);
        // 设置对话框标题
        builder.setTitle("添加报修单");
        // 设置对话框内容
        builder.setMessage("您确认保修单填写正确，添加该保修单？");
        // 设置积极的按钮
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 提交活动
                showProgressDialog();
                submitService(name, account, handle, date, des);
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

    // 设置日期选择按钮点击事件
    private void setDate() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
                        String date = i + "-" + (i1 + 1) + "-" + i2;
                        et_date.setText(date);
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

    // 提交活动信息到数据库
    private void submitService(String name, String account, String handle, String date, String des) {
        // GET方法提交
        OkHttpUtils
                .get()
                .url(URLs.ADD_REPAIR_INFO)
                .addParams("Account", account)
                .addParams("Article", name)
                .addParams("Handle", handle)
                .addParams("Time", date)
                .addParams("Description", des)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.show("提交失败：" + e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        AnalysisJson(response);
                    }
                });
    }

    // 解析json数据
    private void AnalysisJson(String response) {
        try {
            closeProgressDialog();

            JSONObject jsonObject = new JSONObject(response);
            boolean sucessed = jsonObject.getBoolean("sucessed");
            if (sucessed) {
                et_name.setText("");
                et_account.setText("");
                et_handle.setText("");
                et_date.setText("");
                et_repair_des.setText("");

                ToastUtil.showLong("提交成功");
            } else {
                ToastUtil.show("提交失败，请稍后重试");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            closeProgressDialog();
            ToastUtil.show("提交失败：" + e.toString());
        }
    }


    // 初始化控件
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("添加报修单");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et_name = (EditText) findViewById(R.id.et_name);
        et_account = (EditText) findViewById(R.id.et_acount);
        et_handle = (EditText) findViewById(R.id.et_handle);
        et_date = (EditText) findViewById(R.id.et_date);
        et_repair_des = (EditText) findViewById(R.id.et_repair_des);

        bt_submit = (Button) findViewById(R.id.bt_submit);

        iv_date = (ImageView) findViewById(R.id.iv_date);
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
