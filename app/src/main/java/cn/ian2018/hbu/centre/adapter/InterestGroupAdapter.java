package cn.ian2018.hbu.centre.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;

import java.util.List;

import cn.ian2018.hbu.centre.activity.ChangeInterestGroupInfoActivity;
import cn.ian2018.hbu.centre.model.InterestGroup;

/**
 * Created by 陈帅 on 2018/04/09/022.
 */

public class InterestGroupAdapter extends RecyclerView.Adapter<InterestGroupAdapter.ViewHolder> {
    private List<InterestGroup> interestGroupList;

    public InterestGroupAdapter(List<InterestGroup> interestGroupList) {
        this.interestGroupList = interestGroupList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final InterestGroup interestGroup = interestGroupList.get(position);
        holder.tv_group_name.setText(interestGroup.getInterestGroup());

        // 添加条目点击事件
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChangeInterestGroupInfoActivity.class);
                intent.putExtra("interestGroup", interestGroup);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return interestGroupList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView tv_group_name;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_group_name = (TextView) itemView.findViewById(R.id.tv_group_name);
        }
    }
}
