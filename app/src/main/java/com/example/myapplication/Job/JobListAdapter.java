package com.example.myapplication.Job;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.ArrayList;

public class JobListAdapter extends RecyclerView.Adapter<JobListAdapter.JobViewHolder> {

    private Context context;
    private ArrayList<Job> job;

    public JobListAdapter(Context context, ArrayList<Job> job) {
        this.context = context;
        this.job = job;
    }

    @NonNull
    @Override
    public JobListAdapter.JobViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new JobViewHolder(LayoutInflater.from(context).inflate(R.layout.job_list_view,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder myViewHolder, final int i) {

        //String salary = job.get(i).getJob_salary();
        Double salary = Double.parseDouble(job.get(i).getJob_salary());

        myViewHolder.title.setText(job.get(i).getJob_title());
        myViewHolder.companyName.setText(job.get(i).getCompany_name());
        myViewHolder.salary.setText(String.format("RM" + "%.2f",salary));

        myViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,JobDetailsActivity.class);
                intent.putExtra("jobDetails_ID", job.get(i).getJob_ID());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return job.size();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {

        TextView title, companyName, salary;
        ImageView arrow_image;
        View mView;
        LinearLayout linearLayout;

        public JobViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.job_lv_title);
            companyName =  itemView.findViewById(R.id.job_lv_companyName);
            salary = itemView.findViewById(R.id.job_lv_salary);
            arrow_image = itemView.findViewById(R.id.job_lv_arrow);
            linearLayout = itemView.findViewById(R.id.linearLayout_jobListView);

            mView = itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListerner.onItemClick(v, getAdapterPosition());
                }
            });
        }

        private com.example.myapplication.Job.JobViewHolder.ClickListerner mClickListerner;

        public interface ClickListerner {
            void onItemClick(View v, int i);
        }

        public void setOnClickListerner(com.example.myapplication.Job.JobViewHolder.ClickListerner clickListerner) {
            mClickListerner = clickListerner;
        }
    }
}
