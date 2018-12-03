package cn.ian2018.hbu.centre.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
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

import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import okhttp3.Call;

/**
 * 添加物品
 */
public class AddGoodsActivity extends TakePhotoActivity{

    private EditText et_goods_name;
    private EditText et_price;
    private Button bt_less;
    private TextView tv_number;
    private Button bt_add;
    private EditText et_goods_location;
    private EditText et_goods_des;
    private Button bt_submit;
    private ProgressDialog progressDialog;
    private RadioGroup radioGroup;
    private int type = 0;
    private String imagePath = "";
    private UploadManager uploadManager;
    private TakePhoto mTakePhoto;
    private ImageView iv_image;
    private String name;
    private String price;
    private String location;
    private String des;
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods);

        // 初始化七牛云配置
        Configuration config = new Configuration.Builder().build();
        uploadManager = new UploadManager(config);

        // 获取图片选择对象
        mTakePhoto = getTakePhoto();

        initView();

        initData();
    }

    private void initData() {
        // 减号的点击事件
        bt_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = tv_number.getText().toString();
                int number = Integer.parseInt(num);
                if (number > 1) {
                    number--;
                    tv_number.setText(number + "");
                } else {
                    ToastUtil.show("不能少于1");
                }
            }
        });
        // 加号的点击事件
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = tv_number.getText().toString();
                int number = Integer.parseInt(num);
                number++;
                tv_number.setText(number + "");
            }
        });
        // radio单选监听事件
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    // 普通
                    case R.id.rb_no_rich:
                        type = 0;
                        break;
                    // 贵重
                    case R.id.rb_rich:
                        type = 1;
                        break;
                }
            }
        });
        // 添加物品的点击事件
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = et_goods_name.getText().toString().trim();
                price = et_price.getText().toString().trim();
                location = et_goods_location.getText().toString().trim();
                des = et_goods_des.getText().toString().trim();
                number = tv_number.getText().toString();

                if (!name.equals("") && !price.equals("") && !location.equals("") && !des.equals("") && !imagePath.equals("")) {
                    // 上传图片到七牛云
                    upImageToqiniu();
                } else {
                    ToastUtil.show("请将信息填写完整");
                }
            }
        });
        // 添加图片
        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出底部对话框
                showChooseImageDialog();
            }
        });
    }

    // 弹出底部对话框
    private void showChooseImageDialog() {
        DialogPlus dialog = DialogPlus.newDialog(AddGoodsActivity.this)
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
        Glide.with(this).load(imagePath).into(iv_image);

        Log.d("TakePhoto", "getOriginalPath: " + result.getImages().get(0).getOriginalPath());
        Log.d("TakePhoto", "getCompressPath: " + result.getImages().get(0).getCompressPath());
    }

    // 添加物品
    private void addGoods(String imageUrl) {
        OkHttpUtils
                .get()
                .url(URLs.ADD_GOODS)
                .addParams("Name", name)
                .addParams("Type", type+"")
                .addParams("Quantity", number)
                .addParams("Price", price)
                .addParams("Description", des)
                .addParams("Location", location)
                .addParams("ImageUrl",imageUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        closeDialog();
                        Logs.e("添加物品失败:" + e.toString());
                        ToastUtil.show("添加物品失败，请稍后重试");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        parseJsom(response);
                    }
                });
    }

    // 解析json数据
    private void parseJsom(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean sucessed = jsonObject.getBoolean("sucessed");
            if (sucessed) {
                ToastUtil.show("添加成功");
                et_goods_name.setText("");
                et_price.setText("");
                et_goods_location.setText("");
                et_goods_des.setText("");
                tv_number.setText("1");
                iv_image.setImageResource(R.mipmap.icon_add_image);
            } else {
                ToastUtil.show("添加失败，请稍后重试");
            }
            closeDialog();
        } catch (JSONException e) {
            e.printStackTrace();
            closeDialog();
            Logs.e("添加物品失败:" + e.toString());
            ToastUtil.show("添加物品失败，请稍后重试");
        }
    }

    // 上传图片到七牛云
    private void upImageToqiniu() {
        showDialog();
        OkHttpUtils
                .get()
                .url(URLs.GET_TOKEN)
                .addParams("Bucket","hbucentre")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.e("获取Token失败");
                        ToastUtil.show("添加物品失败，请稍后重试");
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
                                                    String imageUrl = "http://p40xgrp9e.bkt.clouddn.com/" + key;
                                                    addGoods(imageUrl);
                                                    Logs.i("上传图片成功");
                                                } else {
                                                    Logs.e("上传图片失败！！！！");
                                                    ToastUtil.show("添加物品失败，请稍后重试");
                                                }
                                                Logs.e(key + ",\r\n " + info + ",\r\n " + res);
                                            }
                                        }, null);
                            } else {
                                Logs.e("获取Token失败");
                                ToastUtil.show("添加物品失败，请稍后重试");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Logs.e("获取Token失败");
                            ToastUtil.show("添加物品失败，请稍后重试");
                        }
                    }
                });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("添加物品");
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et_goods_name = (EditText) findViewById(R.id.et_goods_name);
        et_price = (EditText) findViewById(R.id.et_price);
        bt_less = (Button) findViewById(R.id.bt_less);
        tv_number = (TextView) findViewById(R.id.tv_number);
        bt_add = (Button) findViewById(R.id.bt_add);
        et_goods_location = (EditText) findViewById(R.id.et_goods_location);
        et_goods_des = (EditText) findViewById(R.id.et_goods_des);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        iv_image = (ImageView) findViewById(R.id.iv_image);
    }

    private void showDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage("添加物品中...");
        progressDialog.show();
    }

    private void closeDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
