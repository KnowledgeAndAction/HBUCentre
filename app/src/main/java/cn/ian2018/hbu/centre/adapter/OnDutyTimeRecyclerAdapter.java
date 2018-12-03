package cn.ian2018.hbu.centre.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;

import java.util.List;

import cn.ian2018.hbu.centre.model.OnDutyTime;

/**
 * Created by 陈帅 on 2018/04/04/021.
 * 值班时间列表适配器
 */

public class OnDutyTimeRecyclerAdapter extends RecyclerView.Adapter<OnDutyTimeRecyclerAdapter.ViewHolder> {
    private List<OnDutyTime> list;

    public OnDutyTimeRecyclerAdapter(List<OnDutyTime> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onduty_time,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OnDutyTime onDutyTime = list.get(position);
        holder.tv_time.setText(onDutyTime.getAllTime());
        holder.tv_name.setText(onDutyTime.getName());
        holder.tv_grade.setText(onDutyTime.getGradeCode() + "级");

        if (position < 3) {
            holder.tv_name.setTextColor(Color.parseColor("#FF8000"));
        } else {
            holder.tv_name.setTextColor(Color.parseColor("#00FF40"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_time;
        TextView tv_name;
        TextView tv_grade;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_grade = (TextView) itemView.findViewById(R.id.tv_grade);
        }
    }
}
