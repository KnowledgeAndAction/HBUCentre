package cn.ian2018.hbu.centre.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hicc.information.sensorsignin.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.MD5Util;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import okhttp3.Call;

// 注册
public class SignUpActivity extends AppCompatActivity {

    private EditText et_account;
    private EditText et_password;
    private EditText et_password_two;
    private EditText et_name;
    private EditText et_grade;
    private EditText et_class;
    private Button bt_submit;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initView();

        signUp();
    }

    private void signUp() {
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = et_account.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                String password2 = et_password_two.getText().toString().trim();
                String name = et_name.getText().toString().trim();
                String grade = et_grade.getText().toString().trim();
                String clas = et_class.getText().toString().trim();

                // 如果都不为空
                if (!account.equals("") && !password.equals("") && !password2.equals("")
                        && !name.equals("") && !grade.equals("") && !clas.equals("")) {
                    // 两次密码一致
                    if (password.equals(password2)) {
                        // 注册
                        SignUpToService(account,password,name,grade,clas);
                    } else {
                        ToastUtil.show("两次密码不一致");
                    }
                } else {
                    ToastUtil.show("请将信息填写完整");
                }
            }
        });
    }

    private void SignUpToService(final String account, final String password, String name, String grade, String clas) {
        showProgressDialog();
        // 对密码加密
        String md5Pass = MD5Util.strToMD5(password);
        // 发送请求
        OkHttpUtils
                .get()
                .url(URLs.SIGN_UP)
                .addParams("studentNum",account)
                .addParams("password",md5Pass)
                .addParams("name",name)
                .addParams("grade",grade)
                .addParams("class",clas)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.e("注册失败："+e.toString());
                        ToastUtil.show("注册失败："+e.toString());
                        closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        closeProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                ToastUtil.show("注册成功");
                                Intent intent = new Intent();
                                intent.putExtra("account",account);
                                intent.putExtra("password",password);
                                SignUpActivity.this.setResult(2,intent);
                                SignUpActivity.this.finish();
                            } else {
                                ToastUtil.show("注册失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Logs.e("注册失败："+e.toString());
                            ToastUtil.show("注册失败："+e.toString());
                        }
                    }
                });
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("注册");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et_account = (EditText) findViewById(R.id.et_account);
        et_password = (EditText) findViewById(R.id.et_password);
        et_password_two = (EditText) findViewById(R.id.et_password_two);
        et_name = (EditText) findViewById(R.id.et_name);
        et_grade = (EditText) findViewById(R.id.et_grade);
        et_class = (EditText) findViewById(R.id.et_class);

        bt_submit = (Button) findViewById(R.id.bt_submit);
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage("注册中...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
