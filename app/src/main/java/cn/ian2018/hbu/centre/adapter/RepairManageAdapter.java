package cn.ian2018.hbu.centre.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;

import java.util.List;

import cn.ian2018.hbu.centre.activity.RepairInfoActivity;
import cn.ian2018.hbu.centre.model.RepairInfo;

/**
 * 管理员界面 报修情况RecyclerView适配器——陈帅
 */

public class RepairManageAdapter extends RecyclerView.Adapter<RepairManageAdapter.ViewHolder> {

    private List<RepairInfo> repairInfoList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView tv_name;
        TextView tv_time;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }

    public RepairManageAdapter(List<RepairInfo> repairInfoList) {
        this.repairInfoList = repairInfoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repair_manage_list, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final RepairInfo repairInfo = repairInfoList.get(position);
        holder.tv_name.setText(repairInfo.getName());
        holder.tv_time.setText(repairInfo.getTime().substring(0,10));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RepairInfoActivity.class);
                intent.putExtra("repair", repairInfo);
                // 跳转到报修详情页
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return repairInfoList.size();
    }
}
