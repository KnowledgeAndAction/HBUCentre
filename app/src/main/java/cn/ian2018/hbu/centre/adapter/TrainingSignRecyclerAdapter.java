package cn.ian2018.hbu.centre.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;

import java.util.List;
import java.util.Map;

/**
 * Created by 陈帅 on 2018/3/6/006.
 */

public class TrainingSignRecyclerAdapter extends RecyclerView.Adapter<TrainingSignRecyclerAdapter.ViewHolder> {

    private List<Map<String,String>> mapList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        TextView tv_time;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }

    public TrainingSignRecyclerAdapter(List<Map<String, String>> mapList) {
        this.mapList = mapList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_training_sign,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Map<String, String> map = mapList.get(position);
        holder.tv_name.setText(map.get("name"));
        holder.tv_time.setText(map.get("time").substring(0,16));
    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }


}
