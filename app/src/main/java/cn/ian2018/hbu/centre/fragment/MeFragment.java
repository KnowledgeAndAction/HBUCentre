package cn.ian2018.hbu.centre.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import cn.ian2018.hbu.centre.activity.AddBackDutyActivity;
import cn.ian2018.hbu.centre.activity.FeedBackActivity;
import cn.ian2018.hbu.centre.activity.GoodsLeaseActivity;
import cn.ian2018.hbu.centre.activity.SettingActivity;
import cn.ian2018.hbu.centre.utils.Constant;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.SpUtil;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import cn.ian2018.hbu.centre.utils.Utils;
import okhttp3.Call;

/**
 * 我的界面
 */

public class MeFragment extends BaseFragment implements View.OnClickListener {
    private LinearLayout ll_repair;
    private LinearLayout ll_feedback;
    private LinearLayout ll_setting;
    private ProgressDialog progressDialog;
    private LinearLayout ll_add_duty;
    private LinearLayout ll_lease;
    private TextView tv_name;
    private TextView tv_grade;
    private TextView tv_interest_group;
    private TextView tv_group;
    private ImageView iv_pic;


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

    @Override
    public void onResume() {
        super.onResume();
        tv_name.setText(SpUtil.getString(Constant.USER_NAME,""));
        tv_grade.setText("年级：" + SpUtil.getInt(Constant.USER_GRADE,17) + "级");
        tv_interest_group.setText("兴趣小组：" + Utils.getInterestGroup(SpUtil.getInt(Constant.USER_INTERESTGROUP,0)));
        tv_group.setText("组别：" + Utils.getGroup(SpUtil.getInt(Constant.USER_GROUP,0)));
        Glide.with(getContext()).load(SpUtil.getString(Constant.USER_IMAGE,"")).placeholder(R.drawable.icon_pic)
                .centerCrop()
                .error(R.drawable.icon_pic)
                .into(iv_pic);
    }

    // 初始化控件
    private void inItUI(View view) {
        ll_add_duty = (LinearLayout) view.findViewById(R.id.ll_add_duty);
        ll_lease = (LinearLayout) view.findViewById(R.id.ll_lease);
        ll_feedback = (LinearLayout) view.findViewById(R.id.ll_feedback);
        ll_repair = (LinearLayout) view.findViewById(R.id.ll_repair);
        ll_setting = (LinearLayout) view.findViewById(R.id.ll_setting);

        ll_add_duty.setOnClickListener(this);
        ll_lease.setOnClickListener(this);
        ll_feedback.setOnClickListener(this);
        ll_repair.setOnClickListener(this);
        ll_setting.setOnClickListener(this);

        iv_pic = (ImageView) view.findViewById(R.id.iv_pic);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_grade = (TextView) view.findViewById(R.id.tv_grade);
        tv_interest_group = (TextView) view.findViewById(R.id.tv_interest_group);
        tv_group = (TextView) view.findViewById(R.id.tv_group);

        Glide.with(getContext()).load(SpUtil.getString(Constant.USER_IMAGE,"")).placeholder(R.drawable.icon_pic)
                .centerCrop()
                .error(R.drawable.icon_pic)
                .into(iv_pic);

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

                dialog.show();
            }
        });
    }

    //设置按钮的点击事件。
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            // 添加补班
            case R.id.ll_add_duty:
                startActivity(new Intent(getContext(), AddBackDutyActivity.class));
                break;
            // 报修
            case R.id.ll_repair:
                // 获取维修人信息
                getRepairInfo();
                break;
            // 意见反馈
            case R.id.ll_feedback:
                startActivity(new Intent(getContext(), FeedBackActivity.class));
                break;
            // 租借
            case R.id.ll_lease:
                startActivity(new Intent(getContext(), GoodsLeaseActivity.class));
                break;
            // 设置
            case R.id.ll_setting:
                startActivity(new Intent(getContext(), SettingActivity.class));
                break;
        }
    }

    // 获取维修人信息
    private void getRepairInfo() {
        showProgressDialogs("获取维修人信息中...");
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
                                showRepairDialog(name,phone);
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

    // 显示报修对话框
    private void showRepairDialog(String name, final String phone) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
        //设置对话框左上角图标
        builder.setIcon(R.mipmap.ic_launcher);
        //设置对话框标题
        builder.setTitle("物品报修");
        //设置对话框内容
        builder.setMessage("如果遇到设备故障，请及时联系：" + name + "\n电话：" + phone);
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

    private void showProgressDialogs(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
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
