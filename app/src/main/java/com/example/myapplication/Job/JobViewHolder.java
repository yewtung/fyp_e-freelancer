package com.example.myapplication.Job;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;

public class JobViewHolder extends RecyclerView.ViewHolder {

    private TextView title,companyName, salary;
    private ImageView arrow_image;
    private View mView;

    public JobViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.job_lv_title);
        companyName = itemView.findViewById(R.id.job_lv_companyName);
        salary = itemView.findViewById(R.id.job_lv_salary);
        arrow_image = itemView.findViewById(R.id.job_lv_arrow);

        mView = itemView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });
    }

    private JobViewHolder.ClickListerner mClickListener;

    public interface ClickListerner{
        void onItemClick(View v, int i);
    }

    public void setOnClickListerner(ClickListerner clickListerner){
        mClickListener = clickListerner;
    }
}
