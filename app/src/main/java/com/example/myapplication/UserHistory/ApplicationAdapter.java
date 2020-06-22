package com.example.myapplication.UserHistory;

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

import com.example.myapplication.Job.JobDetailsActivity;
import com.example.myapplication.Job.Job_user;
import com.example.myapplication.R;

import java.util.ArrayList;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    private Context context;
    private ArrayList<Job_user> job_user;

    public ApplicationAdapter(Context context, ArrayList<Job_user> job_user) {
        this.context = context;
        this.job_user = job_user;
    }

    @NonNull
    @Override
    public ApplicationAdapter.ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ApplicationAdapter.ApplicationViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_application_history,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationAdapter.ApplicationViewHolder myViewHolder, final int i) {
        Double salary = Double.valueOf(job_user.get(i).getJob_user_salary());

        myViewHolder.title.setText(job_user.get(i).getJob_title());
        myViewHolder.emp_name.setText(job_user.get(i).getEmp_name());
        myViewHolder.salary.setText(String.format("RM%.2f", salary));
        myViewHolder.date.setText(job_user.get(i).getJob_user_date());

        myViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DisplayApplicationHistoryDetailsActivity.class);
                intent.putExtra("application_job_user_ID", job_user.get(i).getJob_user_ID());
                intent.putExtra("application_job_user_status", job_user.get(i).getJob_user_status());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return job_user.size();
    }

    static class ApplicationViewHolder extends RecyclerView.ViewHolder {

        TextView title,emp_name, salary, date;
        ImageView arrow_image;
        View mView;
        LinearLayout linearLayout;

        public ApplicationViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.applicationHistory_JobTitle);
            emp_name =  itemView.findViewById(R.id.applicationHistory_empName);
            salary = itemView.findViewById(R.id.applicationHistory_salary);
            date = itemView.findViewById(R.id.applicationHistory_date);
            arrow_image = itemView.findViewById(R.id.invoiceBar_lv_arrow);
            linearLayout = itemView.findViewById(R.id.linearLayout_application);

            mView = itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListerner.onItemClick(v, getAdapterPosition());

                }
            });
        }

        private com.example.myapplication.UserHistory.ApplicationViewHolder.ClickListerner mClickListerner;

        public interface ClickListerner {
            void onItemClick(View v, int i);
        }

        public void setOnClickListerner(com.example.myapplication.UserHistory.ApplicationViewHolder.ClickListerner clickListerner) {
            mClickListerner = clickListerner;
        }
    }


}

