package cn.ian2018.hbu.centre.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;

import java.util.List;

import cn.ian2018.hbu.centre.model.FeedBack;
import cn.ian2018.hbu.centre.utils.Utils;

/**
 * 管理员界面 意见反馈RecyclerView适配器——陈帅
 */

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {

    private List<FeedBack> feedbackInfoList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView tv_name;
        TextView tv_group;
        TextView tv_anonymous;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_group = (TextView) itemView.findViewById(R.id.tv_group);
            tv_anonymous = (TextView) itemView.findViewById(R.id.tv_anonymous);
        }
    }

    public FeedbackAdapter(List<FeedBack> feedbackInfoList,Context context) {
        this.feedbackInfoList = feedbackInfoList;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback_list, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final FeedBack feedBack = feedbackInfoList.get(position);
        holder.tv_name.setText(feedBack.getName());
        holder.tv_group.setText(Utils.getGroup(feedBack.getGroupCode()));
        switch (feedBack.getAnonymous()) {
            // 公开
            case 0:
                holder.tv_anonymous.setText("公开");
                break;
            // 匿名
            case 1:
                holder.tv_anonymous.setText("匿名");
                break;
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedbackInfoDialog(feedBack);
            }
        });

    }

    // 弹出对话框
    private void showFeedbackInfoDialog(FeedBack feedBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog dialog = builder.create();
        View view = View.inflate(mContext, R.layout.dialog_feedback_info, null);
        dialog.setView(view, 0, 0, 0, 0);

        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        TextView tv_anonymous = (TextView) view.findViewById(R.id.tv_anonymous);
        TextView tv_group = (TextView) view.findViewById(R.id.tv_group);
        TextView tv_msg = (TextView) view.findViewById(R.id.tv_msg);
        TextView tv_phone_type = (TextView) view.findViewById(R.id.tv_phone_type);
        TextView tv_time = (TextView) view.findViewById(R.id.tv_time);

        tv_name.setText(feedBack.getName());
        switch (feedBack.getAnonymous()) {
            // 公开
            case 0:
                tv_anonymous.setText("公开");
                break;
            // 匿名
            case 1:
                tv_anonymous.setText("匿名");
                break;
        }
        tv_group.setText(feedBack.getGrade() + " " + Utils.getGroup(feedBack.getGroupCode()));
        tv_msg.setText("    " + feedBack.getMsg());
        tv_phone_type.setText(feedBack.getPhoneBrandType() + " " + feedBack.getAndroidVersion());
        tv_time.setText(feedBack.getTime().substring(0,16));

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return feedbackInfoList.size();
    }
}
