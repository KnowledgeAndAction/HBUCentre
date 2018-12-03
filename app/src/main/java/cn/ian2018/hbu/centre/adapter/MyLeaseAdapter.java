package cn.ian2018.hbu.centre.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hicc.information.sensorsignin.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.ian2018.hbu.centre.model.LeaseInfo;
import cn.ian2018.hbu.centre.utils.Logs;
import cn.ian2018.hbu.centre.utils.ToastUtil;
import cn.ian2018.hbu.centre.utils.URLs;
import cn.ian2018.hbu.centre.utils.Utils;
import okhttp3.Call;

/**
 * Created by 陈帅 on 2017/12/23/023.
 */

public class MyLeaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TITLE = 0;    // 标题
    private final int ITEM = 1;     // 普通条目
    private List<LeaseInfo> unBackList;
    private List<LeaseInfo> backList;

    private Context context;
    private ProgressDialog progressDialog;

    public MyLeaseAdapter(List<LeaseInfo> unBackList, List<LeaseInfo> backList, Context context) {
        this.unBackList = unBackList;
        this.backList = backList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TITLE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_title,parent,false);
            return new TitleViewHolder(view);
        }
        if (viewType == ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mgoods_list,parent,false);
            return new ViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // 如果是标题
        if (holder instanceof TitleViewHolder) {
            if (position == 0) {
                ((TitleViewHolder) holder).tv_title.setText("未归还物品");
            } else {
                ((TitleViewHolder) holder).tv_title.setText("已归还物品");
            }
        }

        if (holder instanceof ViewHolder) {
            // 未归还
            if (position < unBackList.size() +1) {
                Logs.e("下表："+position);
                LeaseInfo unLeaseInfo = unBackList.get(position-1);
                ((ViewHolder) holder).tv_name.setText(unLeaseInfo.getName());
                ((ViewHolder) holder).tv_date.setText("应于 " + unLeaseInfo.getBacktime().substring(0,16) + " 归还");


            // 已归还
            } else {
                ((ViewHolder) holder).tv_name.setText(backList.get(position-unBackList.size()-2).getName());
                ((ViewHolder) holder).tv_date.setText("已于 " + backList.get(position-unBackList.size()-2).getActualBackTime().substring(0,16) + " 归还");
            }

            // 设置点击事件
            ((ViewHolder) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 未归还
                    if (position < unBackList.size() +1) {
                        showBackDialog(unBackList.get(position-1));
                    }
                }
            });
        }
    }

    // 弹出归还对话框
    private void showBackDialog(final LeaseInfo unLeaseInfo) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        //设置对话框标题
        builder.setTitle(unLeaseInfo.getName());
        //设置对话框内容
        builder.setMessage("租借日期：" + unLeaseInfo.getTime() + "\n应归还日期：" + unLeaseInfo.getBacktime());
        //设置积极的按钮
        builder.setPositiveButton("归还", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 归还请求
                backGoods(unLeaseInfo);
                dialog.dismiss();
            }
        });
        //设置消极的按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void backGoods(final LeaseInfo unLeaseInfo) {
        showProgressDialogs("归还中...");
        OkHttpUtils
                .get()
                .url(URLs.BACK_GOODS)
                .addParams("LoanId",unLeaseInfo.getNid()+"")
                .addParams("ArticleId",unLeaseInfo.getArticleID()+"")
                .addParams("ActualBackTime", Utils.getTime())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.e("归还失败："+ e.toString());
                        ToastUtil.show("归还失败，请稍后重试");
                        closeProgressDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("sucessed")) {
                                ToastUtil.show("归还成功");
                                // 归还成功后更新数据
                                unBackList.remove(unLeaseInfo);
                                unLeaseInfo.setActualBackTime(Utils.getTime());
                                backList.add(unLeaseInfo);
                                notifyDataSetChanged();
                            } else {
                                ToastUtil.show("归还失败");
                                Logs.e("归还失败：服务器错误");
                            }
                            closeProgressDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Logs.e("归还失败："+ e.toString());
                            ToastUtil.show("归还失败，请稍后重试");
                            closeProgressDialog();
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return unBackList.size()+backList.size()+2;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 || position == unBackList.size()+1){
            return TITLE;
        }else{
            return ITEM;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView tv_name;
        TextView tv_date;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }

    static class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        public TitleViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }

    private void showProgressDialogs(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
        }
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
