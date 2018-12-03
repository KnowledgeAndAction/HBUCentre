package cn.ian2018.hbu.centre.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;

import com.hicc.information.sensorsignin.R;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.ian2018.hbu.centre.db.MyDatabase;
import cn.ian2018.hbu.centre.fragment.ActivityFragment;
import cn.ian2018.hbu.centre.fragment.MeFragment;
import cn.ian2018.hbu.centre.fragment.QuantifyFragment;
import cn.ian2018.hbu.centre.model.Active;
import cn.ian2018.hbu.centre.model.ExitEvent;
import cn.ian2018.hbu.centre.model.Saying;
import cn.ian2018.hbu.centre.model.SignActive;
import cn.ian2018.hbu.centre.model.TabItem;
import cn.ian2018.hbu.centre.service.SensorService;
import cn.ian2018.hbu.centre.utils.Constant;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.SpUtil;
import cn.ian2018.hbu.centre.utils.StatusBarUtils;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import okhttp3.Call;

/**
 * 普通用户主界面
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ArrayList<TabItem> tabs;
    private static Boolean isExit = false;
    private ProgressDialog progressDialog;
    private BottomBar bottomBar;
    private Toolbar toolbar;
    private MyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化控件
        initWidget();

        // 注册监听退出登录的事件
        EventBus.getDefault().register(this);

        // 检测更新
        checkVersionCode();

        // 上传未上传成功的记录
        checkUnSign();

        // 检测是否有未签离的活动
        checkUnSignOutActive();

        // 初始化名言数据
        initSaying();
    }

    // 初始化名言数据
    private void initSaying() {
        OkHttpUtils
                .get()
                .url(URLs.GET_SAYING)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.e("获取名言失败"+e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                // 如果本地数据库和服务器数据不同
                                if (jsonArray.length() != db.getSaying().size()) {
                                    db.deleteSaying();
                                    for (int i=0; i<jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        Saying saying = new Saying();
                                        saying.setContent(object.getString("content"));
                                        db.saveSaying(saying);
                                    }
                                    Logs.d("数据库更新");
                                } else {
                                    Logs.e("数据库没更新");
                                }
                            } else {
                                Logs.e("获取名言失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Logs.e("获取名言失败"+e.toString());
                        }
                    }
                });
        if (db.getSaying().size() == 0) {

        }
    }

    private void checkUnSignOutActive() {
        // 如果sp里有数据，说明有未签离的活动，需要跳转到签离界面
        if (!SpUtil.getString(Constant.SIGN_OUT_ACTIVE_NAME,"").equals("")) {
            Active active = new Active();
            String activeName = SpUtil.getString(Constant.SIGN_OUT_ACTIVE_NAME, "");
            String location = SpUtil.getString(Constant.SIGN_OUT_LOCATION, "");
            int activeId = SpUtil.getInt(Constant.SIGN_OUT_ACTIVE_ID, 0);
            String endTime = SpUtil.getString(Constant.SIGN_OUT_ENDTIME, "");

            active.setActiveId(activeId);
            active.setActiveName(activeName);
            active.setActiveLocation(location);
            active.setEndTime(endTime);

            Intent intent = new Intent(this, MoveActivity.class);
            intent.putExtra("active",active);
            intent.putExtra("yunziId", SpUtil.getString(Constant.SIGN_OUT_YUNZIID,""));

            startActivity(intent);
        }
    }

    // 上传未上传成功的记录
    private void checkUnSign() {
        db = MyDatabase.getInstance();
        List<SignActive> unSaveActives = db.getUnSaveActives();
        for (SignActive unSaveActive : unSaveActives) {
            // 上传时间数据
            signForService(unSaveActive);
        }
    }

    // 上传时间数据
    private void signForService(final SignActive unSaveActive){
        OkHttpUtils
                .get()
                .url(URLs.SIGN_IN)
                .addParams("account", unSaveActive.getNumber())
                .addParams("activityid",unSaveActive.getActiveId()+"")
                .addParams("intime",unSaveActive.getInTime())
                .addParams("outtime", unSaveActive.getOutTime())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Logs.d("上传未成功签到的活动失败:"+e.toString());
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            if (jsonObject.getBoolean("sucessed")) {
                                db.updateSignActive(unSaveActive.getActiveId(),true);
                            } else {
                                Logs.d("上传未成功签到的活动失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Logs.d("上传未成功签到的活动失败:"+e.toString());
                        }
                    }
                });
    }

    // 检测更新
    private void checkVersionCode() {
        // 发送GET请求
        OkHttpUtils
                .get()
                .url(URLs.UPDATE)
                .addParams("appId", "1")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.i("获取最新app信息失败："+e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Logs.i(response);
                        Logs.i("获取最新app信息成功");
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
                if(version > getVersionCode()){
                    // 获取下载地址
                    String mAppUrl = data.getString("appUrl");
                    // 获取新版app描述
                    String appDescribe = data.getString("appDescribe");
                    // 如果sd卡可用
                    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        // 展示下载对话框
                        showUpDataDialog(appDescribe, mAppUrl);
                    }
                }
            } else {
                Logs.i("获取最新app信息失败："+jsonObject.getString("data"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Logs.i("解析最新app信息失败："+e.toString());
        }
    }

    // 显示更新对话框
    protected void showUpDataDialog(String description, final String appUrl) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        // 设置对话框左上角图标
        builder.setIcon(R.mipmap.ic_launcher);
        // 设置不能取消
        builder.setCancelable(false);
        // 设置对话框标题
        builder.setTitle("发现新版本");
        // 设置对话框内容
        builder.setMessage(description);
        // 设置积极的按钮
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 下载apk
                downLoadApk(appUrl);
                // 显示一个进度条对话框
                showProgressDialog();
            }
        });
        // 设置消极的按钮
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
                .execute(new FileCallBack(getExternalFilesDir("apk").getPath(),"中心宝.apk") {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.show("下载失败："+e.toString());
                        Logs.i("下载失败："+e.toString()+","+id);
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
        PackageManager pm = getPackageManager();
        // 获取包的基本信息
        try {
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            // 返回应用的版本号
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 初始化控件
    private void initWidget() {
        viewPager = (ViewPager) findViewById(R.id.viewPager_top);
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("签到");
        toolbar.setBackgroundColor(Color.parseColor("#03A9F4"));
        setSupportActionBar(toolbar);

        initLayout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                checkVersionCode();
        }
    }

    // 初始化布局
    private void initLayout() {
        tabs = new ArrayList<>();
        tabs.add(new TabItem(R.drawable.ic_assignment_turned_in, R.string.tab_sign, ActivityFragment.class));
        tabs.add(new TabItem(R.drawable.ic_assignment, R.string.tab_quantify, QuantifyFragment.class));
        tabs.add(new TabItem(R.drawable.ic_assignment_ind, R.string.tab_me, MeFragment.class));
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_active:
                        viewPager.setCurrentItem(0);
                        toolbar.setTitle("签到");
                        toolbar.setBackgroundColor(Color.parseColor("#03A9F4"));
                        StatusBarUtils.setWindowStatusBarColor(MainActivity.this, "#03A9F4");
                        break;
                    case R.id.tab_history:
                        viewPager.setCurrentItem(1);
                        toolbar.setTitle("量化");
                        toolbar.setBackgroundColor(Color.parseColor("#5D4037"));
                        StatusBarUtils.setWindowStatusBarColor(MainActivity.this, "#5D4037");
                        break;
                    case R.id.tab_setting:
                        viewPager.setCurrentItem(2);
                        toolbar.setTitle("我的");
                        toolbar.setBackgroundColor(Color.parseColor("#045563"));
                        StatusBarUtils.setWindowStatusBarColor(MainActivity.this, "#045563");
                        break;
                }
            }
        });

        final FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomBar.selectTabAtPosition(position,true);
                switch (position) {
                    case 0:
                        toolbar.setTitle("签到");
                        toolbar.setBackgroundColor(Color.parseColor("#03A9F4"));
                        StatusBarUtils.setWindowStatusBarColor(MainActivity.this, "#03A9F4");
                        break;
                    case 1:
                        toolbar.setTitle("量化");
                        toolbar.setBackgroundColor(Color.parseColor("#5D4037"));
                        StatusBarUtils.setWindowStatusBarColor(MainActivity.this, "#5D4037");
                        break;
                    case 2:
                        toolbar.setTitle("我的");
                        toolbar.setBackgroundColor(Color.parseColor("#045563"));
                        StatusBarUtils.setWindowStatusBarColor(MainActivity.this, "#045563");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    // viewpager适配器
    class FragmentAdapter extends FragmentPagerAdapter {
        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            try {
                return tabs.get(position).tagFragmentClz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabs.size();
        }
    }

    // 接收退出登录的消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ExitEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopService(new Intent(this,SensorService.class));
    }

    // 监听返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            exitBy2Click();
        }
        return false;
    }

    // 双击退出程序
    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            ToastUtil.show("再按一次退出程序");
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
            System.exit(0);
        }
    }
}
