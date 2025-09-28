package com.flowpocket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.flowpocket.R;
import com.flowpocket.entities.Expense;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<Expense> expenses = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.nameTextView.setText(expense.getName());
        holder.amountTextView.setText(String.format("$%.2f", expense.getAmount()));
        holder.dateTextView.setText(dateFormat.format(expense.getDate()));
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
        notifyDataSetChanged();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView amountTextView;
        TextView dateTextView;

        ExpenseViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.expense_name);
            amountTextView = view.findViewById(R.id.expense_amount);
            dateTextView = view.findViewById(R.id.expense_date);
        }
    }
}
