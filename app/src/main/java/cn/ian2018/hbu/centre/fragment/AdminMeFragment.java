package cn.ian2018.hbu.centre.fragment;

import android.content.Intent;
import android.os.Bundle;
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

import cn.ian2018.hbu.centre.activity.GoodsManageActivity;
import cn.ian2018.hbu.centre.activity.LookActiveForIdActivity;
import cn.ian2018.hbu.centre.activity.LookFeedbackActivity;
import cn.ian2018.hbu.centre.activity.RepairManageActivity;
import cn.ian2018.hbu.centre.activity.SettingActivity;
import cn.ian2018.hbu.centre.activity.TraningManageActivity;
import cn.ian2018.hbu.centre.utils.Constant;
import cn.ian2018.hbu.centre.utils.SpUtil;

/**
 * 管理员 我的界面
 */

public class AdminMeFragment extends BaseFragment implements View.OnClickListener {
    private LinearLayout ll_permissions_setting;
    private LinearLayout ll_check_feedback;
    private LinearLayout ll_setting;
    private long[] mHit = new long[6];
    private LinearLayout ll_manage_article;
    private LinearLayout ll_repair_info;
    private TextView tv_name;
    private TextView tv_grade;
    private TextView tv_class;
    private TextView tv_group;
    private ImageView iv_pic;


    @Override
    public void fetchData() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_me, container, false);

        inItUI(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        tv_name.setText(SpUtil.getString(Constant.USER_NAME, ""));
        tv_grade.setText("年级：20" + SpUtil.getInt(Constant.USER_GRADE, 17) + "级");
        tv_class.setText("班级：" + SpUtil.getString(Constant.USER_CLASS, ""));
        tv_group.setText("超级管理员");
        Glide.with(getContext()).load(SpUtil.getString(Constant.USER_IMAGE, "")).placeholder(R.drawable.icon_pic)
                .centerCrop()
                .error(R.drawable.icon_pic)
                .into(iv_pic);
    }

    // 初始化控件
    private void inItUI(View view) {
        ll_manage_article = (LinearLayout) view.findViewById(R.id.ll_manage_article);
        ll_repair_info = (LinearLayout) view.findViewById(R.id.ll_repair_info);
        ll_check_feedback = (LinearLayout) view.findViewById(R.id.ll_check_feedback);
        ll_permissions_setting = (LinearLayout) view.findViewById(R.id.ll_permissions_setting);
        ll_setting = (LinearLayout) view.findViewById(R.id.ll_setting);

        ll_manage_article.setOnClickListener(this);
        ll_repair_info.setOnClickListener(this);
        ll_check_feedback.setOnClickListener(this);
        ll_permissions_setting.setOnClickListener(this);
        ll_setting.setOnClickListener(this);

        iv_pic = (ImageView) view.findViewById(R.id.iv_pic);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_grade = (TextView) view.findViewById(R.id.tv_grade);
        tv_class = (TextView) view.findViewById(R.id.tv_class);
        tv_group = (TextView) view.findViewById(R.id.tv_group);

        Glide.with(getContext()).load(SpUtil.getString(Constant.USER_IMAGE, "")).placeholder(R.drawable.icon_pic)
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
                Glide.with(getContext()).load(SpUtil.getString(Constant.USER_IMAGE, "")).placeholder(R.drawable.icon_pic)
                        .centerCrop()
                        .error(R.drawable.icon_pic)
                        .into(iv_avatar);

                // 6击打开查看云子活动功能
                iv_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 如果是管理员，才有这个功能
                        System.arraycopy(mHit, 1, mHit, 0, mHit.length - 1);
                        mHit[mHit.length - 1] = SystemClock.uptimeMillis();
                        if (mHit[mHit.length - 1] - mHit[0] < 1000) {
                            startActivity(new Intent(getContext(), LookActiveForIdActivity.class));
                        }
                    }
                });

                dialog.show();
            }
        });
    }

    //设置按钮的点击事件。
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            // 物品管理
            case R.id.ll_manage_article:
                startActivity(new Intent(getContext(), GoodsManageActivity.class));
                break;
            // 报修情况
            case R.id.ll_repair_info:
                startActivity(new Intent(getContext(), RepairManageActivity.class));
                break;
            // 查看反馈
            case R.id.ll_check_feedback:
                startActivity(new Intent(getContext(), LookFeedbackActivity.class));
                break;
            // 权限设置
            case R.id.ll_permissions_setting:
                Intent intent = new Intent(getContext(), TraningManageActivity.class);
                intent.putExtra("type",2);
                startActivity(intent);
                break;
            // 设置
            case R.id.ll_setting:
                startActivity(new Intent(getContext(), SettingActivity.class));
                break;
        }
    }

}
