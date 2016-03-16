package com.example.lazyclock.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lazyclock.Config;
import com.example.lazyclock.R;
import com.example.lazyclock.bean.AlarmBean;
import com.example.lazyclock.utils.TimeUtil;

import java.util.List;

/**
 * Created by Administrator on 2015/12/8.
 */
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.MyViewHolder> {
    private List<AlarmBean> list;
    private Context mContenxt;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onClick(View v, int position);
    }


    public AlarmAdapter(List<AlarmBean> datas, Context context) {
        list = datas;
        mContenxt = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.activity_main_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(v, mListener);


        return holder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AlarmBean alarm = list.get(position);
        if (!alarm.isWork()) {
            holder.mBGView.setBackgroundResource(R.drawable.main_item_bg_off);
            holder.mClockView.setBackgroundResource(R.drawable.main_item_click_off);
            holder.mIdView.setTextColor(mContenxt.getResources().getColor(R.color.main_item_id_unwork));
        } else {
            holder.mBGView.setBackgroundResource(R.drawable.main_item_bg_on);
            holder.mClockView.setBackgroundResource(R.drawable.main_item_click_on);
            holder.mIdView.setTextColor(mContenxt.getResources().getColor(R.color.main_item_id_work));
        }
        int id = position + 1;
        holder.mIdView.setText("0" + String.valueOf(id));
        //闹铃时间
        holder.mTimeView.setText(alarm.getTimeStr());
        //距离下一个闹铃时间
        holder.mLastTimeView.setText("离目前" + alarm.getRemainingTime());

        TimeUtil util = TimeUtil.getInstance();
        holder.mWeekView.setText(util.todayOrToMorrow(alarm.getTimeInMills()));

        switch (alarm.getStopSleepType()) {
            case Config.STOPSELECT_CLICK:
                holder.mImageView.setBackgroundResource(R.drawable.main_item_stop_touch);
                break;
            case Config.STOPSELECT_SHANK:
                holder.mImageView.setBackgroundResource(R.drawable.main_item_stop_shake);
                break;
            case Config.STOPSELECT_CODE:
                holder.mImageView.setBackgroundResource(R.drawable.main_item_stop_code);
                break;
            case Config.STOPSELECT_PIC:
                holder.mImageView.setBackgroundResource(R.drawable.main_item_stop_pic);
                break;
            case Config.STOPSELECT_MATH:
                holder.mImageView.setBackgroundResource(R.drawable.main_item_stop_math);
                break;
        }
        holder.setPosition(position);

    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mIdView, mWeekView, mTimeView, mLastTimeView;
        private RelativeLayout mBGView;
        private ImageView mImageView, mClockView;
        private OnItemClickListener listener;
        private int position;
        private View rootView;

        public MyViewHolder(View itemView, OnItemClickListener l) {
            super(itemView);
            mIdView = (TextView) itemView.findViewById(R.id.list_item_id_id);
            mWeekView = (TextView) itemView.findViewById(R.id.list_item_id_day);
            mTimeView = (TextView) itemView.findViewById(R.id.list_item_id_time);
            mLastTimeView = (TextView) itemView.findViewById(R.id.list_item_lasttime);
            mImageView = (ImageView) itemView.findViewById(R.id.list_item_id_image);
            mBGView = (RelativeLayout) itemView.findViewById(R.id.list_item_id_bg);
            mClockView = (ImageView) itemView.findViewById(R.id.list_item_id_clock);
            listener = l;
            rootView = itemView;
            rootView.setOnClickListener(this);
        }

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            listener.onClick(rootView, position);
        }
    }
}
