package cn.ian2018.hbu.centre.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hicc.information.sensorsignin.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cn.ian2018.hbu.centre.MyApplication;
import cn.ian2018.hbu.centre.activity.AddBackDutyActivity;
import cn.ian2018.hbu.centre.activity.FeedBackActivity;
import cn.ian2018.hbu.centre.activity.LoginActivity;
import cn.ian2018.hbu.centre.activity.LookActiveForIdActivity;
import cn.ian2018.hbu.centre.activity.LookFeedbackActivity;
import cn.ian2018.hbu.centre.model.ExitEvent;
import cn.ian2018.hbu.centre.utils.Constant;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.SpUtil;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import okhttp3.Call;

/**
 * 我的界面
 */

public class MeFragment extends BaseFragment implements View.OnClickListener {
    private LinearLayout ll_checkToUpdate;
    private LinearLayout ll_feedback;
    private LinearLayout ll_esc;
    private ProgressDialog progressDialog;
    private long[] mHit = new long[6];
    private LinearLayout ll_look_feedback;
    private LinearLayout ll_add_duty;


    @Override
    public void fetchData() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        inItUI(view);

        return view;
    }

    // 初始化控件
    private void inItUI(View view) {
        ll_checkToUpdate = (LinearLayout) view.findViewById(R.id.ll_checkToUpdate);
        ll_feedback = (LinearLayout) view.findViewById(R.id.ll_feedback);
        ll_look_feedback = (LinearLayout) view.findViewById(R.id.ll_look_feedback);
        ll_esc = (LinearLayout) view.findViewById(R.id.ll_esc);
        ll_add_duty = (LinearLayout) view.findViewById(R.id.ll_add_duty);

        ll_checkToUpdate.setOnClickListener(this);
        ll_feedback.setOnClickListener(this);
        ll_look_feedback.setOnClickListener(this);
        ll_esc.setOnClickListener(this);
        ll_add_duty.setOnClickListener(this);

        ImageView iv_pic = (ImageView) view.findViewById(R.id.iv_pic);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        TextView tv_grade = (TextView) view.findViewById(R.id.tv_grade);
        TextView tv_class = (TextView) view.findViewById(R.id.tv_class);

        Glide.with(getContext()).load(SpUtil.getString(Constant.USER_IMAGE,"")).placeholder(R.drawable.icon_pic)
                .centerCrop()
                .error(R.drawable.icon_pic)
                .into(iv_pic);
        tv_name.setText(SpUtil.getString(Constant.USER_NAME,""));
        tv_grade.setText("年级：20" + SpUtil.getInt(Constant.USER_GRADE,17) + "级");
        tv_class.setText("班级：" + SpUtil.getString(Constant.USER_CLASS,""));

        // 放大图片
        iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final AlertDialog dialog = builder.create();
                View view = View.inflate(getContext(), R.layout.dialog_image_avatar, null);
                dialog.setView(view, 0, 0, 0, 0);

                ImageView iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
                Glide.with(getContext()).load(SpUtil.getString(Constant.USER_IMAGE,"")).placeholder(R.drawable.icon_pic)
                        .centerCrop()
                        .error(R.drawable.icon_pic)
                        .into(iv_avatar);

                // 6击打开查看云子活动功能
                iv_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 如果是管理员，才有这个功能
                        if (SpUtil.getString(Constant.ACCOUNT,"").equals("admin")) {
                            System.arraycopy(mHit, 1, mHit, 0, mHit.length-1);
                            mHit[mHit.length-1] = SystemClock.uptimeMillis();
                            if(mHit[mHit.length-1]-mHit[0] < 1000){
                                startActivity(new Intent(getContext(),LookActiveForIdActivity.class));
                            }
                        }
                    }
                });

                dialog.show();
            }
        });

        // 如果是开发者
        if (SpUtil.getString(Constant.PASS_WORD,"").equals("ccce27a4d2a2cb38de55e3d207b03a47")) {
            ll_look_feedback.setVisibility(View.VISIBLE);
            ll_feedback.setVisibility(View.GONE);
        } else {
            ll_look_feedback.setVisibility(View.GONE);
            ll_feedback.setVisibility(View.VISIBLE);
        }

        if (SpUtil.getInt(Constant.USER_TYPE,0) == 1) {
            ll_add_duty.setVisibility(View.GONE);
        }
    }

    //设置按钮的点击事件。
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            /*case R.id.ll_change:
                Intent intent = new Intent(getContext(), ChangePswActivity.class);
                startActivity(intent);
                break;*/
            case R.id.ll_add_duty:
                startActivity(new Intent(getContext(), AddBackDutyActivity.class));
                break;
            case R.id.ll_checkToUpdate:
                showProgressDialogs();
                // 发送GET请求
                OkHttpUtils
                        .get()
                        .url(URLs.UPDATE)
                        .addParams("appId", "1")
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
                break;
            case R.id.ll_feedback:
                startActivity(new Intent(getContext(), FeedBackActivity.class));
                break;
            case R.id.ll_look_feedback:
                startActivity(new Intent(getContext(), LookFeedbackActivity.class));
                break;
            case R.id.ll_esc:
                showConfirmDialog();
                break;
        }
    }

    // 显示确认对话框
    private void showConfirmDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
        //设置对话框左上角图标
        builder.setIcon(R.mipmap.logo2);
        //设置对话框标题
        builder.setTitle("是否注销");
        //设置文本内容
        builder.setMessage("您将会注销应用");
        //设置积极的按钮
        builder.setPositiveButton("确认注销", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EventBus.getDefault().post(new ExitEvent());
                startActivity(new Intent(getContext(), LoginActivity.class));
                SpUtil.putBoolean(Constant.IS_REMBER_PWD, false);
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
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
        //设置对话框左上角图标
        builder.setIcon(R.mipmap.logo2);
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
                .execute(new FileCallBack(MyApplication.getContext().getExternalFilesDir("apk").getPath(), "小蜜蜂.apk") {
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
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIcon(R.mipmap.logo2);
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
        intent.addCategory("android.intent.category.DEFAULT");
        //文件作为数据源
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, 1);
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

    private void showProgressDialogs() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
        }
        progressDialog.setMessage("检测更新中...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
