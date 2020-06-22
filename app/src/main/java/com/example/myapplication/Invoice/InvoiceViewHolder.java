package com.example.myapplication.Invoice;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Job.JobViewHolder;
import com.example.myapplication.R;

public class InvoiceViewHolder extends RecyclerView.ViewHolder {

    private TextView title,id, salary;
    private ImageView arrow_image;
    private View mView;

    public InvoiceViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.invoiceBar_JobTitle);
        id = itemView.findViewById(R.id.invoiceBar_ID);
        salary = itemView.findViewById(R.id.invoiceBar_salary);
        arrow_image = itemView.findViewById(R.id.invoiceBar_lv_arrow);

        mView = itemView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });
    }

    private InvoiceViewHolder.ClickListerner mClickListener;

    public interface ClickListerner{
        void onItemClick(View v, int i);
    }

    public void setOnClickListerner(InvoiceViewHolder.ClickListerner clickListerner){
        mClickListener = clickListerner;
    }

}
