package cn.ian2018.hbu.centre.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;

/**
 * 培训情况  权限设置  各组列表
 */
public class TraningManageActivity extends AppCompatActivity {

    String[] groups = new String[]{"Android组","iOS组","Java组","PHP组","行政组","前端组","视频组",".NET组"};
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traning_manage);

        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);

        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        switch (type) {
            case 1:
                toolbar.setTitle("培训情况");
                break;
            case 2:
                toolbar.setTitle("权限设置");
                break;
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ListView lv_group = (ListView) findViewById(R.id.lv_group);
        lv_group.setAdapter(new MyAdapter());
        lv_group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int groupCode = position + 1;
                String groupName = groups[position];
                switch (type) {
                    // 跳转到培训列表
                    case 1:
                        Intent traningIntent = new Intent(TraningManageActivity.this,TrainingListActivity.class);
                        traningIntent.putExtra("groupName",groupName);
                        traningIntent.putExtra("group",groupCode);
                        startActivity(traningIntent);
                        break;
                    // 跳转到权限管理
                    case 2:
                        Intent permissionsIntent = new Intent(TraningManageActivity.this,PermissionsManageActivity.class);
                        permissionsIntent.putExtra("groupName",groupName);
                        permissionsIntent.putExtra("group",groupCode);
                        startActivity(permissionsIntent);
                        break;
                }
                
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return groups.length;
        }

        @Override
        public String getItem(int position) {
            return groups[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(TraningManageActivity.this).inflate(R.layout.item_group_list,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.tv_group_name = (TextView) convertView.findViewById(R.id.tv_group_name);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.tv_group_name.setText(groups[position]);
            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_group_name;
    }
}
