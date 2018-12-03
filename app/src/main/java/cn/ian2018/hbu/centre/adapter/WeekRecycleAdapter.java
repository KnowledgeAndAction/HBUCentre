package cn.ian2018.hbu.centre.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;

import java.util.List;

import cn.ian2018.hbu.centre.model.Week;

/**
 * Created by 陈帅 on 2017/12/21/021.
 */

public class WeekRecycleAdapter extends RecyclerView.Adapter<WeekRecycleAdapter.ViewHolder> {

    private List<Week> weekList;
    private OnItemClickListener onItemClickListener;

    public WeekRecycleAdapter(List<Week> weekList) {
        this.weekList = weekList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_week,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Week week = weekList.get(position);
        holder.tv_week.setText("第" + week.getWeekCode() + "周");
        holder.tv_time.setText(week.getStartDate().substring(0,10).replace("-",".") + " - " + week.getEndDate().substring(0,10).replace("-","."));

        // 条目点击事件
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 通过接口回调
                int position = holder.getAdapterPosition();
                onItemClickListener.onItemClick(v,position);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view,int position);
    }

    @Override
    public int getItemCount() {
        return weekList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView tv_week;
        TextView tv_time;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_week = (TextView) itemView.findViewById(R.id.tv_week);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }

}
