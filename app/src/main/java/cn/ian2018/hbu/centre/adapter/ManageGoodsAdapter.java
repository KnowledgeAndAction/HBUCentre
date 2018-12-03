package cn.ian2018.hbu.centre.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;

import java.util.List;

import cn.ian2018.hbu.centre.model.Goods;

/**
 * 管理员界面 管理物品RecyclerView适配器——陈帅
 */

public class ManageGoodsAdapter extends RecyclerView.Adapter<ManageGoodsAdapter.ViewHolder> {

    private List<Goods> mGoodsList;
    private OnRecyclerViewOnClickListener mListener;
    private OnRecyclerViewDeleteClickListener mDeleteListener;

    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView tv_name;
        ImageView iv_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            iv_delete = (ImageView) itemView.findViewById(R.id.iv_delete);
        }
    }

    /**
     * 定义一个item点击事件接口
     */
    public interface OnRecyclerViewOnClickListener {
        void onItemClick(View view, int position);
    }

    // 删除图标点击事件
    public interface OnRecyclerViewDeleteClickListener {
        void onDeleteClick(View view, int position);
    }

    public void setItemClickListener(OnRecyclerViewOnClickListener listener){
        this.mListener = listener;
    }

    public void setDeleteClickListener(OnRecyclerViewDeleteClickListener listener){
        this.mDeleteListener = listener;
    }

    public ManageGoodsAdapter(List<Goods> mGoodsList) {
        this.mGoodsList = mGoodsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_manage_list, parent, false);
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

        // 设置删除按钮点击事件
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                // 设置接口回调
                mDeleteListener.onDeleteClick(v, position);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Goods goods = mGoodsList.get(position);
        holder.tv_name.setText(goods.getName());
    }

    @Override
    public int getItemCount() {
        return mGoodsList.size();
    }
}
