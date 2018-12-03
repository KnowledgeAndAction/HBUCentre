package cn.ian2018.hbu.centre.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.hicc.information.sensorsignin.R;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cn.ian2018.hbu.centre.utils.Constant;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.SpUtil;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import cn.ian2018.hbu.centre.view.CircleImageView;
import okhttp3.Call;

/**
 * Created by 崔国钊 on 2017/12/10.
 * 更改信息
 */

public class ChangeInformationActivity extends TakePhotoActivity {
    private Button bt_informationChange;
    private EditText et_changeName;
    private EditText et_changeGrade;
    private EditText et_changeClass;
    private ProgressDialog progressDialog;
    private Button bt_change_group;
    private int index_group = 0;
    private String oldName;
    private String oldClass;
    private int oldGrade;
    private String[] items = new String[]{"Android组","iOS组","Java组","PHP组","行政组","前端组","视频组",".NET组"};
    private int oldGroup;
    private String changeName;
    private int changeGrade;
    private String changeClass;
    private EditText et_change_phone;
    private String changePhone;
    private String oldPhone;
    private String oldImage;
    private CircleImageView circleImageView;
    private UploadManager uploadManager;
    private TakePhoto mTakePhoto;
    private String imagePath = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_information);

        // 初始化七牛云配置
        Configuration config = new Configuration.Builder().build();
        uploadManager = new UploadManager(config);

        // 获取图片选择对象
        mTakePhoto = getTakePhoto();

        inItView();

        inItData();
    }

    private void inItData() {
        //实现更改信息按钮功能
        bt_informationChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeName = et_changeName.getText().toString().trim();
                changeGrade = Integer.valueOf(et_changeGrade.getText().toString().trim());
                changeClass = et_changeClass.getText().toString().trim();
                changePhone = et_change_phone.getText().toString().trim();
                if (!changeClass.equals(oldClass) || changeGrade != oldGrade || !changeName.equals(oldName)
                        || (index_group+1)!=oldGroup || !changePhone.equals(oldPhone)
                        || !imagePath.equals("")){
                    if (imagePath.equals("")) {
                        chackChange(oldImage);
                    } else {
                        // 先将图片上传
                        upImageToqiniu();
                    }
                } else{
                    ToastUtil.show("您没有修改任何信息");
                }
            }
        });

        // 选择组别
        bt_change_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChangeInformationActivity.this);
                builder.setTitle("选择组别");
                builder.setSingleChoiceItems(items, index_group, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index_group = which;
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bt_change_group.setText(items[index_group]);
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
        });

        // 选择头像
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseImageDialog();
            }
        });
    }

    // 弹出底部对话框
    private void showChooseImageDialog() {
        DialogPlus dialog = DialogPlus.newDialog(ChangeInformationActivity.this)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            // 拍照
                            case R.id.bt_takePhoto:
                                fromTakePhoto();
                                dialog.dismiss();
                                break;
                            // 从相册选择
                            case R.id.bt_choosePhoto:
                                fromDCIMPhoto();
                                dialog.dismiss();
                                break;
                            case R.id.bt_cancel:
                                dialog.dismiss();
                                break;
                        }
                    }
                })
                .setHeader(R.layout.dialog_header)
                .setContentHolder(new ViewHolder(R.layout.dialog_choose))
                .setExpanded(true)
                .create();
        dialog.show();
    }

    // 获取剪裁配置
    private CropOptions getCropOptions(){
        CropOptions.Builder builder=new CropOptions.Builder();
        // 设置宽高尺寸
        builder.setOutputX(800).setOutputY(800);
        // 设置是否使用自带剪裁工具
        builder.setWithOwnCrop(false);
        return builder.create();
    }

    // 从相册选择
    private void fromDCIMPhoto() {
        File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        // 进行压缩
        new Thread(){
            @Override
            public void run() {
                super.run();
                CompressConfig config=new CompressConfig.Builder()
                        .setMaxSize(102400)
                        .setMaxPixel(800)
                        .create();
                mTakePhoto.onEnableCompress(config,false);
            }
        }.start();

        TakePhotoOptions.Builder builder=new TakePhotoOptions.Builder();
        // 是否使用TakePhoto自带相册
        builder.setWithOwnGallery(false);
        // 是否纠正拍照的照片旋转角度
        builder.setCorrectImage(true);
        // 设置TakePhoto选项
        mTakePhoto.setTakePhotoOptions(builder.create());
        // 从相册中获取图片并裁剪
        mTakePhoto.onPickFromGalleryWithCrop(imageUri,getCropOptions());
    }

    // 从相机获取图片并裁剪
    private void fromTakePhoto() {
        File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        // 进行压缩
        new Thread(){
            @Override
            public void run() {
                super.run();
                CompressConfig config=new CompressConfig.Builder()
                        .setMaxSize(102400)
                        .setMaxPixel(800)
                        // ture保留原图，false删除原图，当且仅当类型为CAMERA此配置才有效
                        .enableReserveRaw(false)
                        .create();
                mTakePhoto.onEnableCompress(config,false);
            }
        }.start();

        TakePhotoOptions.Builder builder=new TakePhotoOptions.Builder();
        // 是否纠正拍照的照片旋转角度
        builder.setCorrectImage(true);
        // 设置TakePhoto选项
        mTakePhoto.setTakePhotoOptions(builder.create());
        mTakePhoto.onPickFromCaptureWithCrop(imageUri,getCropOptions());
    }

    // 选择图片成功
    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        imagePath = result.getImages().get(0).getCompressPath();
        Glide.with(this).load(imagePath).centerCrop().into(circleImageView);

        Log.d("TakePhoto", "getOriginalPath: " + result.getImages().get(0).getOriginalPath());
        Log.d("TakePhoto", "getCompressPath: " + result.getImages().get(0).getCompressPath());
    }

    // 上传图片到七牛云
    private void upImageToqiniu() {
        showProgressDialogs("修改信息中...");
        OkHttpUtils
                .get()
                .url(URLs.GET_TOKEN)
                .addParams("Bucket","hbucentre")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.e("获取Token失败");
                        ToastUtil.show("修改信息失败，请稍后重试");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                String token = jsonObject.getString("data");
                                // 获取文件名称
                                String fileName = imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length());
                                Log.d("T","文件路径："+ imagePath);
                                Log.d("T","文件名称："+fileName);
                                uploadManager.put(imagePath, fileName, token,
                                        new UpCompletionHandler() {
                                            @Override
                                            public void complete(String key, ResponseInfo info, JSONObject res) {
                                                if(info.isOK()) {
                                                    String changeImageUrl = "http://p40xgrp9e.bkt.clouddn.com/" + key;
                                                    // 修改信息
                                                    chackChange(changeImageUrl);
                                                    Logs.i("上传图片成功");
                                                } else {
                                                    Logs.e("上传图片失败！！！！");
                                                    ToastUtil.show("修改信息失败，请稍后重试");
                                                }
                                                Logs.e(key + ",\r\n " + info + ",\r\n " + res);
                                            }
                                        }, null);
                            } else {
                                Logs.e("获取Token失败");
                                ToastUtil.show("修改信息失败，请稍后重试");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Logs.e("获取Token失败");
                            ToastUtil.show("修改信息失败，请稍后重试");
                        }
                    }
                });
    }

    //检查信息更改是否成功
    private void chackChange(final String imageUrl) {
        showProgressDialogs("修改信息中...");
        OkHttpUtils
                .get()
                .url(URLs.CHANGE_INFO)
                .addParams("Account",SpUtil.getString(Constant.ACCOUNT,""))
                .addParams("Name",changeName)
                .addParams("GradeCode",changeGrade+"")
                .addParams("ClassDescription",changeClass)
                .addParams("Group",(index_group+1)+"")
                .addParams("Phone",changePhone)
                .addParams("ImageUrl",imageUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        closeProgressDialog();
                        ToastUtil.show("修改信息失败，请稍后重试");
                        Logs.e("修改信息失败："+e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                ToastUtil.show("修改信息成功");
                                SpUtil.putString(Constant.USER_NAME,changeName);
                                SpUtil.putString(Constant.USER_CLASS,changeClass);
                                SpUtil.putInt(Constant.USER_GRADE,changeGrade);
                                SpUtil.putInt(Constant.USER_GROUP,index_group+1);
                                SpUtil.putString(Constant.USER_PHONE,changePhone);
                                SpUtil.putString(Constant.USER_IMAGE,imageUrl);
                            } else {
                                ToastUtil.show("修改信息失败");
                            }
                            closeProgressDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            closeProgressDialog();
                            ToastUtil.show("修改信息失败，请稍后重试");
                            Logs.e("修改信息失败："+e.toString());
                        }
                    }
                });
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void inItView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("修改信息");
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bt_informationChange= (Button) findViewById(R.id.bt_information_change);
        bt_change_group = (Button) findViewById(R.id.bt_change_group);
        et_changeClass= (EditText) findViewById(R.id.et_change_class);
        et_changeGrade= (EditText) findViewById(R.id.et_change_grade);
        et_changeName= (EditText) findViewById(R.id.et_change_name);
        et_change_phone = (EditText) findViewById(R.id.et_change_phone);
        circleImageView = (CircleImageView) findViewById(R.id.icon);

        oldName = SpUtil.getString(Constant.USER_NAME, "");
        oldClass = SpUtil.getString(Constant.USER_CLASS,"");
        oldGrade = SpUtil.getInt(Constant.USER_GRADE,0);
        oldGroup = SpUtil.getInt(Constant.USER_GROUP,0);
        oldPhone = SpUtil.getString(Constant.USER_PHONE,"");
        oldImage = SpUtil.getString(Constant.USER_IMAGE,"");

        et_changeName.setText(oldName);
        et_changeClass.setText(oldClass);
        et_changeGrade.setText(oldGrade+"");
        et_change_phone.setText(oldPhone);
        if (oldGroup != 0) {
            bt_change_group.setText(items[oldGroup-1]);
            index_group = oldGroup-1;
        }
        Glide.with(this).load(oldImage).centerCrop()
                .error(R.drawable.icon_pic)
                .into(circleImageView);
    }

    private void showProgressDialogs(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
