package cn.ian2018.hbu.centre.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import cn.ian2018.hbu.centre.model.Goods;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import okhttp3.Call;

/**
 * 修改物品
 */
public class ChangeGoodsActivity extends AppCompatActivity {

    private EditText et_goods_name;
    private EditText et_price;
    private Button bt_less;
    private TextView tv_number;
    private Button bt_add;
    private EditText et_goods_location;
    private EditText et_goods_des;
    private Button bt_submit;
    private ProgressDialog progressDialog;
    private Goods mGoods;
    private RadioGroup radioGroup;
    private int mType;
    // 单选框id
    private int[] radioButtonIds = {R.id.rb_no_rich,R.id.rb_rich};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_goods);

        Intent intent = getIntent();
        mGoods = (Goods) intent.getSerializableExtra("goods");

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
        // radiogroup的选择监听事件
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    // 普通
                    case R.id.rb_no_rich:
                        mType = 0;
                        break;
                    // 贵重
                    case R.id.rb_rich:
                        mType = 1;
                        break;
                }
            }
        });
        // 修改物品的点击事件
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_goods_name.getText().toString().trim();
                String price = et_price.getText().toString().trim();
                String location = et_goods_location.getText().toString().trim();
                String des = et_goods_des.getText().toString().trim();
                String number = tv_number.getText().toString();

                if (!name.equals(mGoods.getName()) || !price.equals(String.valueOf(mGoods.getPrice()))
                        || !location.equals(mGoods.getLocation()) || !des.equals(mGoods.getDescription())
                        || !number.equals(String.valueOf(mGoods.getQuanutity())) || mType != mGoods.getType()) {
                    changeGoods(name, price, number, location, des);
                }
            }
        });
    }

    // 修改物品
    private void changeGoods(String name, String price, String number, String location, String des) {
        showDialog();
        OkHttpUtils
                .get()
                .url(URLs.CHANGE_GOODS)
                .addParams("ID", mGoods.getNid()+"")
                .addParams("Name", name)
                .addParams("Type", mType+"")
                .addParams("Quantity", number)
                .addParams("Price", price)
                .addParams("Description", des)
                .addParams("Location", location)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        closeDialog();
                        Logs.e("修改物品失败:" + e.toString());
                        ToastUtil.show("修改物品失败，请稍后重试");
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
                ToastUtil.show("修改成功");
                finish();
            } else {
                ToastUtil.show("修改失败，请稍后重试");
            }
            closeDialog();
        } catch (JSONException e) {
            e.printStackTrace();
            closeDialog();
            Logs.e("修改物品失败:" + e.toString());
            ToastUtil.show("修改物品失败，请稍后重试");
        }
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("修改物品信息");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        et_goods_name.setText(mGoods.getName());
        et_price.setText(mGoods.getPrice() + "");
        tv_number.setText(mGoods.getQuanutity() + "");
        et_goods_location.setText(mGoods.getLocation());
        et_goods_des.setText(mGoods.getDescription());
        // 设置初始选择状态
        mType = mGoods.getType();
        radioGroup.check(radioButtonIds[mType]);
    }

    private void showDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage("修改物品中...");
        progressDialog.show();
    }

    private void closeDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
