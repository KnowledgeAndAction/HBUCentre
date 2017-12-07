package cn.ian2018.hbu.centre.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;

import java.util.List;

import cn.ian2018.hbu.centre.model.Active;

/**
 * 管理员界面 活动签到RecyclerView适配器——陈帅
 */

public class SignRecordRecyclerAdapter extends RecyclerView.Adapter<SignRecordRecyclerAdapter.ViewHolder> {

    private List<Active> mActiveList;
    private OnRecyclerViewOnClickListener mListener;

    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView tv_name;
        TextView tv_location;
        TextView tv_time;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_location = (TextView) itemView.findViewById(R.id.tv_location);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }

    /**
     * 定义一个item点击事件接口
     */
    public interface OnRecyclerViewOnClickListener {
        void onItemClick(View view, int position);
    }

    public void setItemClickListener(OnRecyclerViewOnClickListener listener){
        this.mListener = listener;
    }


    public SignRecordRecyclerAdapter(List<Active> activeList) {
        mActiveList = activeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_list, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        // 设置点击事件
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                // 设置接口回调
                mListener.onItemClick(view, position);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Active active = mActiveList.get(position);
        holder.tv_name.setText(active.getActiveName());
        holder.tv_location.setText(active.getActiveLocation());
        holder.tv_time.setText(active.getActiveTime().replace("T", " ").substring(0, 16));
    }

    @Override
    public int getItemCount() {
        return mActiveList.size();
    }
}
