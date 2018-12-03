package cn.ian2018.hbu.centre.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;

import java.util.List;

import cn.ian2018.hbu.centre.activity.OnDutySignInfoActivity;
import cn.ian2018.hbu.centre.model.Active;

/**
 * 管理员界面 值班情况RecyclerView适配器——陈帅
 */

public class OnDutyManageAdapter extends RecyclerView.Adapter<OnDutyManageAdapter.ViewHolder> {

    private List<Active> activeList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView tv_title;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }

    public OnDutyManageAdapter(List<Active> activeList) {
        this.activeList = activeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onduty_manage_list, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        // 设置点击事件
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(v.getContext(), OnDutySignInfoActivity.class);
                intent.putExtra("title",activeList.get(position).getActiveName());
                intent.putExtra("activeId",activeList.get(position).getActiveId());
                // 跳转到签到详情页
                v.getContext().startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Active active = activeList.get(position);
        holder.tv_title.setText(active.getActiveName());
    }

    @Override
    public int getItemCount() {
        return activeList.size();
    }
}
