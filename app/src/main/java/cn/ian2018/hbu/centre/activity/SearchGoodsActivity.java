package cn.ian2018.hbu.centre.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.hicc.information.sensorsignin.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.ian2018.hbu.centre.adapter.ManageGoodsAdapter;
import cn.ian2018.hbu.centre.model.Goods;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import okhttp3.Call;

/**
 * 搜索物品
 */
public class SearchGoodsActivity extends AppCompatActivity {

    private EditText et_name;
    private ImageView iv_search;
    private RecyclerView recyclerView;
    private List<Goods> mGoodsList = new ArrayList<>();
    private ManageGoodsAdapter myAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_goods);

        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("物品搜索");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et_name = (EditText) findViewById(R.id.et_name);
        iv_search = (ImageView) findViewById(R.id.iv_search);

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString().trim();
                if (!name.equals("")) {
                    // 网络搜索
                    getGoods(name);
                } else {
                    ToastUtil.show("请先输入物品名称哦");
                }
            }
        });

        // 初始化recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new ManageGoodsAdapter(mGoodsList);
        recyclerView.setAdapter(myAdapter);

        // 设置listview点击事件
        myAdapter.setItemClickListener(new ManageGoodsAdapter.OnRecyclerViewOnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 跳转到物品详情页
                Intent intent = new Intent(getApplicationContext(),ManageGoodsInfoActivity.class);
                // 把活动传过去
                intent.putExtra("goods",mGoodsList.get(position));
                startActivity(intent);
            }
        });

        // 设置删除活动点击事件
        myAdapter.setDeleteClickListener(new ManageGoodsAdapter.OnRecyclerViewDeleteClickListener() {
            @Override
            public void onDeleteClick(View view, int position) {
                // 显示确认对话框
                showConfirmDialog(position, mGoodsList.get(position).getNid());
            }
        });
    }

    // 从网络获取物品信息
    private void getGoods(String name) {
        showProgressDialog("搜索中...");
        OkHttpUtils
                .get()
                .url(URLs.GET_GOODS_BY_NAME)
                .addParams("Name", name)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        ToastUtil.show("获取物品失败，请稍后重试:" + e.toString());
                        closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            mGoodsList.clear();
                            JSONObject jsonObject = new JSONObject(s);
                            boolean sucessed = jsonObject.getBoolean("sucessed");
                            if (sucessed) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int j = 0; j < data.length(); j++) {
                                    JSONObject info = data.getJSONObject(j);
                                    double price = info.getDouble("price");
                                    String name = info.getString("name");
                                    int nid = info.getInt("nid");
                                    String description = info.getString("description");
                                    int quanutity = info.getInt("quanutity");
                                    int type = info.getInt("type");
                                    int status = info.getInt("status");
                                    String location = info.getString("location");

                                    Goods goods = new Goods(price,name,nid,description,quanutity,type,status,location);
                                    mGoodsList.add(goods);
                                }
                            } else {
                                ToastUtil.show("暂无数据");
                            }
                            myAdapter.notifyDataSetChanged();
                            closeProgressDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.show("获取物品失败，请稍后重试:" + e.toString());
                            closeProgressDialog();
                        }
                    }
                });
    }

    // 删除物品
    private void deleteGoods(final int position, int id) {
        // 假删除
        OkHttpUtils
                .get()
                .url(URLs.DELETE_GOODS_BY_ID)
                .addParams("GoodsID", id+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.show("删除失败，请稍后重试：" + e.toString());
                        closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean sucessed = jsonObject.getBoolean("sucessed");
                            if (sucessed) {
                                ToastUtil.show("删除物品成功");
                                mGoodsList.remove(position);
                                myAdapter.notifyDataSetChanged();
                            } else {
                                ToastUtil.show("删除物品失败");
                            }
                            closeProgressDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.show("删除失败，请稍后重试：" + e.toString());
                            closeProgressDialog();
                        }
                    }
                });
    }

    // 显示确认对话框
    protected void showConfirmDialog(final int position, final int id) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        // 设置对话框左上角图标
        builder.setIcon(R.mipmap.ic_launcher);
        // 设置不能取消
        builder.setCancelable(false);
        // 设置对话框标题
        builder.setTitle("删除物品");
        // 设置对话框内容
        builder.setMessage("您确认删除该物品？请谨慎思考后再做决定");
        // 设置积极的按钮
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 删除活动
                showProgressDialog("删除中...");
                deleteGoods(position, id);
                dialog.dismiss();
            }
        });
        // 设置消极的按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void showProgressDialog(String msg) {
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
