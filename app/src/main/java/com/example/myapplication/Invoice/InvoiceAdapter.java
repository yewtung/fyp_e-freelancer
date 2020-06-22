package com.example.myapplication.Invoice;

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

import com.example.myapplication.Invoice.Invoice;
import com.example.myapplication.R;

import java.util.ArrayList;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {

    private Context context;
    private ArrayList<Invoice> invoice;

    public InvoiceAdapter(Context context, ArrayList<Invoice> invoice) {
        this.context = context;
        this.invoice = invoice;
    }

    @NonNull
    @Override
    public InvoiceAdapter.InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new InvoiceAdapter.InvoiceViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_invoive_bar,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceAdapter.InvoiceViewHolder myViewHolder, final int i) {
        Double salary1 = invoice.get(i).getInvoice_totalSalary();

        myViewHolder.title.setText(invoice.get(i).getInvoice_jobTitle());
        myViewHolder.id.setText(invoice.get(i).getInvoiceID());
        myViewHolder.salary.setText(String.format("RM%.2f", salary1));

        myViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PaymentInvoiceDetailsActivity.class);
                intent.putExtra("intent_invoice_ID", invoice.get(i).getInvoiceID());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return invoice.size();
    }

    static class InvoiceViewHolder extends RecyclerView.ViewHolder {

        TextView title, id, salary;
        ImageView arrow_image;
        View mView;
        LinearLayout linearLayout;

        public InvoiceViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.invoiceBar_JobTitle);
            id =  itemView.findViewById(R.id.invoiceBar_ID);
            salary = itemView.findViewById(R.id.invoiceBar_salary);
            arrow_image = itemView.findViewById(R.id.invoiceBar_lv_arrow);
            linearLayout = itemView.findViewById(R.id.linearLayout_invoiceBar);

            mView = itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   mClickListerner.onItemClick(v, getAdapterPosition());

                }
            });
        }

        private com.example.myapplication.Invoice.InvoiceViewHolder.ClickListerner mClickListerner;

        public interface ClickListerner {
            void onItemClick(View v, int i);
        }

        public void setOnClickListerner(com.example.myapplication.Invoice.InvoiceViewHolder.ClickListerner clickListerner) {
            mClickListerner = clickListerner;
        }
    }
    
    
}
