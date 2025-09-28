package com.flowpocket.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.flowpocket.R;
import com.flowpocket.entities.Expense;
import com.flowpocket.entities.ExpenseLabel;
import com.flowpocket.viewmodel.ExpenseViewModel;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SimpleExpenseAdapter extends RecyclerView.Adapter<SimpleExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenses = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private OnExpenseClickListener clickListener;
    private Map<Integer, String> categoryCache = new HashMap<>();
    private ExpenseViewModel viewModel;

    public interface OnExpenseClickListener {
        void onExpenseClick(Expense expense);
    }

    public void setOnExpenseClickListener(OnExpenseClickListener listener) {
        this.clickListener = listener;
    }

    public void setViewModel(ExpenseViewModel viewModel) {
        this.viewModel = viewModel;
        loadCategoryNames();
    }

    private void loadCategoryNames() {
        if (viewModel != null) {
            viewModel.getAllCategories().observeForever(categories -> {
                if (categories != null) {
                    categoryCache.clear();
                    for (ExpenseLabel category : categories) {
                        categoryCache.put(category.getId(), category.getName());
                    }
                    notifyDataSetChanged(); // Refresh to show updated category names
                }
            });
        }
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.bind(expense, dateFormat);
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onExpenseClick(expense);
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses != null ? expenses : new ArrayList<>();
        notifyDataSetChanged();
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private TextView expenseName;
        private TextView expenseAmount;
        private TextView expenseDate;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            expenseName = itemView.findViewById(R.id.expense_name);
            expenseAmount = itemView.findViewById(R.id.expense_amount);
            expenseDate = itemView.findViewById(R.id.expense_date);
        }

        public void bind(Expense expense, SimpleDateFormat dateFormat) {
            // Get category name and create formatted text
            String categoryName = getCategoryName(expense.getLabelId());
            String nameWithCategory = expense.getName() + "  " + categoryName;

            // Create spannable string to style the category part
            android.text.SpannableString spannableString = new android.text.SpannableString(nameWithCategory);

            // Find the start of category text
            int categoryStart = expense.getName().length() + 2; // +2 for the two spaces

            // Apply styling to category part: smaller, unique font (monospace), gray
            spannableString.setSpan(new android.text.style.RelativeSizeSpan(0.6f),
                categoryStart, nameWithCategory.length(),
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new android.text.style.TypefaceSpan("monospace"),
                categoryStart, nameWithCategory.length(),
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new android.text.style.ForegroundColorSpan(
                android.graphics.Color.parseColor("#666666")),
                categoryStart, nameWithCategory.length(),
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            expenseName.setText(spannableString);
            expenseAmount.setText(String.format("₹%.0f", expense.getAmount()));
            expenseDate.setText(dateFormat.format(expense.getDate()));
        }

    }

    private String getCategoryName(int labelId) {
        // Use cached category names from database
        return categoryCache.getOrDefault(labelId, "Others");
    }
}