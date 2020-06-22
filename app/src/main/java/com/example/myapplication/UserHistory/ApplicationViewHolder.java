package com.example.myapplication.UserHistory;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Invoice.InvoiceViewHolder;
import com.example.myapplication.R;

public class ApplicationViewHolder extends RecyclerView.ViewHolder {

    private TextView title,emp_name, salary, date;
    private ImageView arrow_image;
    private View mView;

    public ApplicationViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.applicationHistory_JobTitle);
        emp_name = itemView.findViewById(R.id.applicationHistory_empName);
        salary = itemView.findViewById(R.id.applicationHistory_salary);
        date = itemView.findViewById(R.id.applicationHistory_date);
        arrow_image = itemView.findViewById(R.id.invoiceBar_lv_arrow);

        mView = itemView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });
    }

    private ApplicationViewHolder.ClickListerner mClickListener;

    public interface ClickListerner{
        void onItemClick(View v, int i);
    }

    public void setOnClickListerner(ApplicationViewHolder.ClickListerner clickListerner){
        mClickListener = clickListerner;
    }

}

