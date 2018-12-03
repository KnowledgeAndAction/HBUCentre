package cn.ian2018.hbu.centre.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;

import java.util.List;
import java.util.Map;

/**
 * Created by 陈帅 on 2017/12/21/021.
 */

public class RankRecyclerAdapter extends RecyclerView.Adapter<RankRecyclerAdapter.ViewHolder> {
    private List<Map<String,String>> list;

    public RankRecyclerAdapter(List<Map<String, String>> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Map<String, String> map = list.get(position);
        String rank = map.get("rank");
        String name = map.get("name");
        holder.tv_rank.setText("第" + rank + "名");
        holder.tv_name.setText(name);

        Integer rankOfInt = Integer.valueOf(rank);
        if (rankOfInt < 4) {
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
        TextView tv_rank;
        TextView tv_name;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_rank = (TextView) itemView.findViewById(R.id.tv_rank);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
}
