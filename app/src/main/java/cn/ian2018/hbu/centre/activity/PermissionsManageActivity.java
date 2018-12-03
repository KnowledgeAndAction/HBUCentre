package cn.ian2018.hbu.centre.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.ian2018.hbu.centre.model.PermissionsInfo;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import okhttp3.Call;

/**
 * 权限管理
 */
public class PermissionsManageActivity extends AppCompatActivity {

    private String groupName;
    private int groupCode;
    private SwipeRefreshLayout swipe_refresh;
    private List<PermissionsInfo> permissionsInfoList = new ArrayList<>();
    private MyAdapter myAdapter;
    private int mType;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions_manage);

        Intent intent = getIntent();
        groupName = intent.getStringExtra("groupName");
        groupCode = intent.getIntExtra("group", 0);

        initView();

        initData();
    }

    private void initData() {
        swipe_refresh.setRefreshing(true);
        OkHttpUtils
                .get()
                .url(URLs.GET_USER_BY_GROUP)
                .addParams("Group",groupCode+"")
                .addParams("Grade","0")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.show("获取组内成员失败，请稍后重试");
                        Logs.e("获取组内成员失败:" + e.toString());
                        swipe_refresh.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                permissionsInfoList.clear();
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject info = data.getJSONObject(i);
                                    String name = info.getString("name");
                                    int grade = info.getInt("grade");
                                    String account = info.getString("account");
                                    int type = info.getInt("type");

                                    permissionsInfoList.add(new PermissionsInfo(name,account,grade,type));
                                }
                                myAdapter.notifyDataSetChanged();
                            } else {
                                ToastUtil.show("暂无数据");
                                Logs.e("组内成员无数据");
                            }
                            swipe_refresh.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.show("获取组内成员失败，请稍后重试");
                            Logs.e("获取组内成员失败:" + e.toString());
                            swipe_refresh.setRefreshing(false);
                        }
                    }
                });
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(groupName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        // 设置下拉刷新
        swipe_refresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });

        ListView lv_training_list = (ListView) findViewById(R.id.lv_training_list);
        myAdapter = new MyAdapter();
        lv_training_list.setAdapter(myAdapter);
        lv_training_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PermissionsInfo permissionsInfo = permissionsInfoList.get(position);
                // 弹出更改权限对话框
                showChangePermissionDialog(permissionsInfo);
            }
        });
    }

    // 弹出更改权限对话框
    private void showChangePermissionDialog(final PermissionsInfo permissionsInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        View view = View.inflate(this, R.layout.dialog_change_permission, null);
        dialog.setView(view, 0, 0, 0, 0);

        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        RadioGroup radio_group = (RadioGroup) view.findViewById(R.id.radio_group);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        Button bt_confirm = (Button) view.findViewById(R.id.bt_confirm);

        tv_title.setText("更改" + permissionsInfo.getName() + "权限");

        // 未来可通过数组改进  int[] typeIds = {R.id.chairman};   radio.check(typeIds[type]);
        mType = permissionsInfo.getType();
        switch (mType) {
            // 普通用户
            case 0:
                radio_group.check(R.id.rb_ordinary);
                break;
            // 正副主席
            case 2:
                radio_group.check(R.id.rb_chairman);
                break;
            // 组长
            case 3:
                radio_group.check(R.id.rb_leader);
                break;
            // 人力
            case 4:
                radio_group.check(R.id.rb_hr);
                break;
            // 维修
            case 5:
                radio_group.check(R.id.rb_repair);
                break;
        }

        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    // 主席
                    case R.id.rb_chairman:
                        mType = 2;
                        break;
                    // 组长
                    case R.id.rb_leader:
                        mType = 3;
                        break;
                    // 人力
                    case R.id.rb_hr:
                        mType = 4;
                        break;
                    // 维修
                    case R.id.rb_repair:
                        mType = 5;
                        break;
                    // 普通用户
                    case R.id.rb_ordinary:
                        mType = 0;
                        break;
                }
            }
        });

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
                // 修改权限
                if (permissionsInfo.getType() != mType) {
                    changePermission(permissionsInfo.getAccount(),mType);
                    dialog.dismiss();
                } else {
                    ToastUtil.show("您没有做修改");
                }
            }
        });

        dialog.show();
    }

    // 修改权限
    private void changePermission(String account, int type) {
        showDialog();
        OkHttpUtils
                .get()
                .url(URLs.CHANGE_USER_TYPE)
                .addParams("Account",account)
                .addParams("Type",type+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        closeDialog();
                        ToastUtil.show("修改权限失败，请稍后重试");
                        Logs.e("修改权限失败：" + e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                initData();
                                ToastUtil.show("修改权限成功");
                            } else {
                                ToastUtil.show("修改权限失败，请稍后重试");
                            }
                            closeDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            closeDialog();
                            ToastUtil.show("修改权限失败");
                            Logs.e("修改权限解析异常："+e.toString());
                        }
                    }
                });
    }

    // listview适配器
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return permissionsInfoList.size();
        }

        @Override
        public PermissionsInfo getItem(int position) {
            return permissionsInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_permission_list, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_grade = (TextView) convertView.findViewById(R.id.tv_grade);
                viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tv_name.setText(getItem(position).getName());
            viewHolder.tv_grade.setText(getItem(position).getGrade() + "级");
            switch (getItem(position).getType()) {
                // 普通用户
                case 0:
                    viewHolder.tv_type.setText("组员");
                    break;
                // 超级管理员
                case 1:
                    viewHolder.tv_type.setText("管理员");
                    break;
                // 正副主席
                case 2:
                    viewHolder.tv_type.setText("主席");
                    break;
                // 组长
                case 3:
                    viewHolder.tv_type.setText("组长");
                    break;
                // 人力
                case 4:
                    viewHolder.tv_type.setText("人力");
                    break;
                // 维修
                case 5:
                    viewHolder.tv_type.setText("维修员");
                    break;
            }

            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_name;
        TextView tv_grade;
        TextView tv_type;
    }

    private void showDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("修改权限中...");
        progressDialog.show();
    }

    private void closeDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
