package cn.ian2018.hbu.centre.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;

import java.util.List;

import cn.ian2018.hbu.centre.model.LeaseUser;
import cn.ian2018.hbu.centre.utils.Utils;

/**
 * Created by 陈帅 on 2018/3/8/008.
 */

public class GoodsLeasePeopleAdapter extends RecyclerView.Adapter<GoodsLeasePeopleAdapter.ViewHolder> {

    private List<LeaseUser> leaseUserList;

    public GoodsLeasePeopleAdapter(List<LeaseUser> leaseUserList) {
        this.leaseUserList = leaseUserList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lease_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final LeaseUser leaseUser = leaseUserList.get(position);

        holder.tv_name.setText(leaseUser.getName());
        holder.tv_group.setText(leaseUser.getGrade() + " " + Utils.getGroup(leaseUser.getGroupCode()));
        holder.tv_phone.setText(leaseUser.getPhone());

        holder.iv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到拨号界面
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + leaseUser.getPhone()));
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return leaseUserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_group;
        TextView tv_phone;
        ImageView iv_call;
        public ViewHolder(View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_group = (TextView) itemView.findViewById(R.id.tv_group);
            tv_phone = (TextView) itemView.findViewById(R.id.tv_phone);
            iv_call = (ImageView) itemView.findViewById(R.id.iv_call);
        }
    }
}
