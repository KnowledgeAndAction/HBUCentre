package cn.ian2018.hbu.centre.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;

import java.util.List;

import cn.ian2018.hbu.centre.activity.GoodsInfoActivity;
import cn.ian2018.hbu.centre.model.Goods;
import cn.ian2018.hbu.centre.utils.ToastUtil;

/**
 * Created by 陈帅 on 2017/12/22/022.
 */

public class GoodsLeaseAdapter extends RecyclerView.Adapter<GoodsLeaseAdapter.ViewHolder> {
    private List<Goods> goodsList;
    private final int SEND = 1;
    private final int UN_SEND = 0;

    public GoodsLeaseAdapter(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Goods goods = goodsList.get(position);
        holder.tv_name.setText(goods.getName());
        holder.tv_num.setText(goods.getQuanutity() + "");

        // 添加条目点击事件
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 如果是未借出
                if (goods.getStatus() == UN_SEND) {
                    Intent intent = new Intent(v.getContext(), GoodsInfoActivity.class);
                    intent.putExtra("goods",goods);
                    v.getContext().startActivity(intent);
                } else {
                    ToastUtil.show("该物品已被全部借出，请过段时间再来");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView tv_name;
        TextView tv_num;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_num = (TextView) itemView.findViewById(R.id.tv_num);
        }
    }
}
