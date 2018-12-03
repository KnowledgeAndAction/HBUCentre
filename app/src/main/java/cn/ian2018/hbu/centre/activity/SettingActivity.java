package cn.ian2018.hbu.centre.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.hicc.information.sensorsignin.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cn.ian2018.hbu.centre.MyApplication;
import cn.ian2018.hbu.centre.model.ExitEvent;
import cn.ian2018.hbu.centre.utils.Constant;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.SpUtil;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import okhttp3.Call;

/**
 * 设置页面--陈帅
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout ll_change_info = (LinearLayout) findViewById(R.id.ll_change_info);
        LinearLayout ll_change_password = (LinearLayout) findViewById(R.id.ll_change_password);
        LinearLayout ll_update = (LinearLayout) findViewById(R.id.ll_update);
        LinearLayout ll_esc = (LinearLayout) findViewById(R.id.ll_esc);
        LinearLayout ll_interest_group = (LinearLayout) findViewById(R.id.ll_interest_group);

        ll_change_info.setOnClickListener(this);
        ll_change_password.setOnClickListener(this);
        ll_update.setOnClickListener(this);
        ll_esc.setOnClickListener(this);
        ll_interest_group.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 修改个人信息
            case R.id.ll_change_info:
                startActivity(new Intent(this,ChangeInformationActivity.class));
                break;
            // 修改密码
            case R.id.ll_change_password:
                startActivity(new Intent(this,ChangePswActivity.class));
                break;
            // 兴趣小组
            case R.id.ll_interest_group:
                startActivity(new Intent(this,InterestGroupListActivity.class));
                break;
            // 检测更新
            case R.id.ll_update:
                update();
                break;
            // 注销
            case R.id.ll_esc:
                showEscDialog();
                break;
        }
    }

    // 显示注销确认对话框
    private void showEscDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        //设置对话框左上角图标
        builder.setIcon(R.mipmap.ic_launcher);
        //设置对话框标题
        builder.setTitle("是否注销");
        //设置文本内容
        builder.setMessage("您将会注销应用");
        //设置积极的按钮
        builder.setPositiveButton("确认注销", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EventBus.getDefault().post(new ExitEvent());
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                SpUtil.putBoolean(Constant.IS_REMBER_PWD, false);
                finish();
            }
        });
        //设置消极的按钮
        builder.setNegativeButton("暂不注销", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    // 检测更新
    private void update() {
        showProgressDialogs("检测更新中...");
        // 发送GET请求
        OkHttpUtils
                .get()
                .url(URLs.UPDATE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        closeProgressDialog();
                        Logs.i("获取最新app信息失败：" + e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Logs.i(response);
                        Logs.i("获取最新app信息成功");
                        closeProgressDialog();
                        // 解析json
                        getAppInfoJson(response);
                    }
                });
    }

    // 解析服务器返回的app信息数据
    private void getAppInfoJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean falg = jsonObject.getBoolean("falg");
            if (falg) {
                JSONObject data = jsonObject.getJSONObject("data");
                double v = Double.valueOf(data.getString("appVersion"));
                int version = (int) v;
                // 如果服务器的版本号大于本地的  就更新
                if (version > getVersionCode()) {
                    // 获取下载地址
                    String mAppUrl = data.getString("appUrl");
                    // 获取新版app描述
                    String appDescribe = data.getString("appDescribe");
                    // 如果sd卡可用
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        // 展示下载对话框
                        showUpDataDialog(appDescribe, mAppUrl);
                    }
                } else {
                    ToastUtil.show("您的版本已经是最新的啦");
                }
            } else {
                Logs.i("获取最新app信息失败：" + jsonObject.getString("data"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Logs.i("解析最新app信息失败：" + e.toString());
        }
    }

    // 显示更新对话框
    protected void showUpDataDialog(String description, final String appUrl) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        //设置对话框左上角图标
        builder.setIcon(R.mipmap.ic_launcher);
        //设置对话框标题
        builder.setTitle("发现新版本");
        //设置对话框内容
        builder.setMessage(description);
        //设置积极的按钮
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载apk
                downLoadApk(appUrl);
                // 显示一个进度条对话框
                showProgressDialog();
            }
        });
        //设置消极的按钮
        builder.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    // 下载文件
    private void downLoadApk(String appUrl) {
        OkHttpUtils
                .get()
                .url(appUrl)
                .build()
                .execute(new FileCallBack(MyApplication.getContext().getExternalFilesDir("apk").getPath(), "中心宝.apk") {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.show("下载失败：" + e.toString());
                        Logs.i("下载失败：" + e.toString() + "," + id);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        ToastUtil.show("下载成功,保存路径:");
                        Logs.i("下载成功,保存路径:");
                        // 安装应用
                        installApk(response);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        // 设置进度
                        progressDialog.setProgress((int) (100 * progress));
                    }
                });
    }

    // 下载的进度条对话框
    protected void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setTitle("下载安装包中");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                return;
            }
        });
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
    }

    // 安装应用
    protected void installApk(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // 判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            // 参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(this, "cn.ian2018.hbu.centre.fileprovider", file);
            // 添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }

        startActivityForResult(intent,1);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    // 获取本应用版本号
    private int getVersionCode() {
        // 拿到包管理者
        PackageManager pm = MyApplication.getContext().getPackageManager();
        // 获取包的基本信息
        try {
            PackageInfo info = pm.getPackageInfo(MyApplication.getContext().getPackageName(), 0);
            // 返回应用的版本号
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
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
