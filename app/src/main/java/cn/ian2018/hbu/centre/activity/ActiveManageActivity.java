package cn.ian2018.hbu.centre.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hicc.information.sensorsignin.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.ian2018.hbu.centre.adapter.ManageActiveAdapter;
import cn.ian2018.hbu.centre.model.Active;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import cn.ian2018.hbu.centre.utils.Utils;
import okhttp3.Call;

/**
 * 活动管理
 */
public class ActiveManageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipe_refresh;
    private List<Active> mActiveList = new ArrayList<>();
    private ManageActiveAdapter myAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_manage);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        swipe_refresh.setRefreshing(true);
        // 获取活动
        getActive();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("活动管理");
        setSupportActionBar(toolbar);
        // 显示返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 设置返回按钮点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 添加菜单点击事件
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add:
                        // 跳转到添加活动界面
                        startActivity(new Intent(ActiveManageActivity.this, AddActiveActivity.class));
                        break;
                }
                return true;
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        // 初始化recyclerview
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new ManageActiveAdapter(mActiveList);
        recyclerView.setAdapter(myAdapter);

        // 设置listview点击事件
        myAdapter.setItemClickListener(new ManageActiveAdapter.OnRecyclerViewOnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 跳转到签到记录界面
                Intent intent = new Intent(ActiveManageActivity.this,SignRecordActivity.class);
                // 把活动传过去
                intent.putExtra("active",mActiveList.get(position));
                startActivity(intent);
            }
        });

        // 设置删除活动点击事件
        myAdapter.setDeleteClickListener(new ManageActiveAdapter.OnRecyclerViewDeleteClickListener() {
            @Override
            public void onDeleteClick(View view, int position) {
                // 显示确认对话框
                showConfirmDialog(position, (int) mActiveList.get(position).getActiveId());
            }
        });

        // 配置swipeRefresh
        swipe_refresh.setColorSchemeResources(R.color.colorPrimary , R.color.colorAccent, R.color.colorPrimaryDark);

        // 设置刷新事件
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActive();
            }
        });

        // 设置开始就刷新
        swipe_refresh.setRefreshing(true);

        // 获取活动
        getActive();
    }

    // 从网络获取活动信息
    private void getActive() {
        OkHttpUtils
                .get()
                .url(URLs.GET_ACTIVE_BY_ACCOUNT)
                .addParams("account", Utils.getAccount()+"")
                .addParams("rule", 1+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        ToastUtil.show("获取活动失败，请稍后重试:" + e.toString());
                        swipe_refresh.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            mActiveList.clear();
                            JSONObject jsonObject = new JSONObject(s);
                            boolean sucessed = jsonObject.getBoolean("sucessed");
                            if (sucessed) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int j = 0; j < data.length(); j++) {
                                    JSONObject activity = data.getJSONObject(j);
                                    String name = activity.getString("activityName");
                                    String des = activity.getString("activityDes");
                                    String time = activity.getString("time");
                                    String location = activity.getString("location");
                                    long activeId = activity.getLong("id");
                                    int rule = activity.getInt("rule");
                                    String endTime = activity.getString("endTime");
                                    int show = activity.getInt("display");

                                    if (show == 1) {
                                        Active active = new Active();
                                        active.setActiveId(activeId);
                                        active.setActiveName(name);
                                        active.setActiveTime(time);
                                        active.setActiveDes(des);
                                        active.setActiveLocation(location);
                                        active.setRule(rule);
                                        active.setEndTime(endTime);
                                        mActiveList.add(active);
                                    }
                                }
                            } else {
                                ToastUtil.show("暂无数据");
                            }
                            myAdapter.notifyDataSetChanged();
                            swipe_refresh.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.show("获取活动失败，请稍后重试:" + e.toString());
                            swipe_refresh.setRefreshing(false);
                        }
                    }
                });
    }

    // 删除活动
    private void deleteActive(final int position, int id) {
        // 假删除
        OkHttpUtils
                .get()
                .url(URLs.DELETE_ACTIVE)
                .addParams("Nid", id+"")
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
                                ToastUtil.show("删除活动成功");
                                mActiveList.remove(position);
                                myAdapter.notifyDataSetChanged();
                            } else {
                                ToastUtil.show("删除活动失败");
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
        builder.setTitle("删除活动");
        // 设置对话框内容
        builder.setMessage("您确认删除该活动？");
        // 设置积极的按钮
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 删除活动
                showProgressDialog();
                deleteActive(position, id);
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

    // TODO 防止用户乱删活动
    private void showSafetyDialog(final int position, final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        View view = View.inflate(this, R.layout.dialog_delete_active, null);
        dialog.setView(view, 0, 0, 0, 0);

        final EditText et_pass = (EditText) view.findViewById(R.id.et_password);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        Button bt_confirm = (Button) view.findViewById(R.id.bt_confirm);

        // 取消按钮
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // 确认按钮
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = et_pass.getText().toString().trim();
                if (pass.equals("shanchumima")) {
                    showConfirmDialog(position, id);
                    dialog.dismiss();
                } else {
                    ToastUtil.show("删除密码错误");
                }
            }
        });

        dialog.show();
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage("删除中...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    // 显示菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tool_bar, menu);
        return true;
    }
}
