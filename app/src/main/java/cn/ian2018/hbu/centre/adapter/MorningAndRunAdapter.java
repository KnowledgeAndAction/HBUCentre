package cn.ian2018.hbu.centre.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;

import java.util.List;

import cn.ian2018.hbu.centre.model.SignInfo;
import cn.ian2018.hbu.centre.utils.Utils;

/**
 * 管理员界面 值班签到情况RecyclerView适配器——陈帅
 */

public class MorningAndRunAdapter extends RecyclerView.Adapter<MorningAndRunAdapter.ViewHolder> {

    private List<SignInfo> signInfoList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView tv_name;
        TextView tv_group;
        TextView tv_inTime;
        TextView tv_outTime;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_group = (TextView) itemView.findViewById(R.id.tv_group);
            tv_inTime = (TextView) itemView.findViewById(R.id.tv_inTime);
            tv_outTime = (TextView) itemView.findViewById(R.id.tv_outTime);
        }
    }

    public MorningAndRunAdapter(List<SignInfo> signInfoList) {
        this.signInfoList = signInfoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onduty_sign_list, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SignInfo signInfo = signInfoList.get(position);
        holder.tv_name.setText(signInfo.getName());
        holder.tv_group.setText(Utils.getGroup(signInfo.getGroupCode()));
        holder.tv_inTime.setText("签到："+signInfo.getInTime().substring(11,19));
        if (signInfo.getInTime().equals(signInfo.getOutTime())) {
            holder.tv_outTime.setText("未签离");
        } else {
            holder.tv_outTime.setText("签离："+signInfo.getOutTime().substring(11,19));
        }
    }

    @Override
    public int getItemCount() {
        return signInfoList.size();
    }
}
