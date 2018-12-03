package cn.ian2018.hbu.centre.activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import cn.ian2018.hbu.centre.model.PhoneInfo;
import cn.ian2018.hbu.centre.utils.Constant;
import cn.ian2018.hbu.centre.utils.PhoneInfoUtil;
import cn.ian2018.hbu.centre.utils.SpUtil;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import okhttp3.Call;


/**
 * 意见反馈
 */
public class FeedBackActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView groupText;
    private EditText feedbackEdit;
    private Button submitButton;
    private ProgressDialog progressDialog;
    private CheckBox cb_anonymous;
    private int isAnonymous = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        initView();

        setClick();
    }

    private void setClick() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        groupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 将文字复制到系统粘贴板
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = ClipData.newPlainText("text","http://url.cn/4EyBiy9");
                cm.setPrimaryClip(data);
                ToastUtil.show("已将讨论组链接拷贝到粘贴板上");
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = feedbackEdit.getText().toString().trim();
                // 判断是否匿名
                if(cb_anonymous.isChecked()){
                    isAnonymous = 1;
                } else {
                    isAnonymous = 0;
                }
                if (!TextUtils.isEmpty(message)) {
                    showProgressDialog();
                    // 上传反馈信息
                    upFeedBack(message);
                } else {
                    ToastUtil.show("反馈内容不能为空");
                }
            }
        });
    }

    // 上传反馈信息
    private void upFeedBack(String message) {
        PhoneInfo phoneInfo = PhoneInfoUtil.getPhoneInfo();
        OkHttpUtils
                .get()
                .url(URLs.FEED_BACK)
                .addParams("account", SpUtil.getString(Constant.ACCOUNT,""))
                .addParams("msg",message)
                .addParams("PhoneBrand",phoneInfo.getPhoneBrand())
                .addParams("PhoneBrandType",phoneInfo.getPhoneBrandType())
                .addParams("AndroidVersion",phoneInfo.getAndroidVersion())
                .addParams("Anonymous",isAnonymous+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        closeProgressDialog();
                        ToastUtil.show("提交反馈失败" + e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                ToastUtil.show("提交反馈成功");
                                feedbackEdit.setText("");
                            } else {
                                ToastUtil.show("提交反馈失败");
                            }
                            closeProgressDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            closeProgressDialog();
                            ToastUtil.show("提交反馈失败" + e.toString());
                        }
                    }
                });
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("意见反馈");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        groupText = (TextView) findViewById(R.id.feedback_group_text);

        feedbackEdit = (EditText) findViewById(R.id.feedback_edit);

        cb_anonymous = (CheckBox) findViewById(R.id.cb_anonymous);

        submitButton = (Button) findViewById(R.id.submit_button);
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage("发送反馈中");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }
}
