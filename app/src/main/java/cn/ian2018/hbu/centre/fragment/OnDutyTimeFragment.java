package cn.ian2018.hbu.centre.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;

import cn.ian2018.hbu.centre.activity.OnDutyTimeActivity;

/**
 * Created by 陈帅 on 2018/4/4/006.
 * 按组查看值班时长
 */

public class OnDutyTimeFragment extends BaseFragment {

    String[] groups = new String[]{"Android组","iOS组","Java组","PHP组","行政组","前端组","视频组",".NET组"};

    @Override
    public void fetchData() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onduty_time, container, false);

        initView(view);

        return view;
    }

    private void initView(View view) {
        ListView lv_group = (ListView) view.findViewById(R.id.lv_group);
        lv_group.setAdapter(new MyAdapter());
        lv_group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int groupCode = position + 1;
                // 先跳转到值班时长页面
                Intent intent = new Intent(getContext(),OnDutyTimeActivity.class);
                intent.putExtra("groupCode",groupCode);
                startActivity(intent);
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
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_group_list,parent,false);
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
