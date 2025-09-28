package com.flowpocket.ui;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.flowpocket.R;
import com.flowpocket.dao.MonthlyBudgetDao;
import java.util.ArrayList;
import java.util.List;

public class BudgetCategoryAdapter extends RecyclerView.Adapter<BudgetCategoryAdapter.BudgetViewHolder> {
    private List<MonthlyBudgetDao.BudgetSummary> budgets = new ArrayList<>();
    private OnEditClickListener editClickListener;

    public interface OnEditClickListener {
        void onEditClick(MonthlyBudgetDao.BudgetSummary budget);
    }

    public void setBudgets(List<MonthlyBudgetDao.BudgetSummary> budgets) {
        this.budgets = budgets != null ? budgets : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editClickListener = listener;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_budget_category, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        MonthlyBudgetDao.BudgetSummary budget = budgets.get(position);
        holder.bind(budget);
    }

    @Override
    public int getItemCount() {
        return budgets.size();
    }

    class BudgetViewHolder extends RecyclerView.ViewHolder {
        private final View colorIndicator;
        private final TextView categoryName;
        private final TextView budgetAmount;
        private final TextView spentAmount;
        private final TextView budgetStatus;
        private final ProgressBar progressBar;
        private final ImageButton editButton;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            colorIndicator = itemView.findViewById(R.id.category_color_indicator);
            categoryName = itemView.findViewById(R.id.tv_category_name);
            budgetAmount = itemView.findViewById(R.id.tv_budget_amount);
            spentAmount = itemView.findViewById(R.id.tv_spent_amount);
            budgetStatus = itemView.findViewById(R.id.tv_budget_status);
            progressBar = itemView.findViewById(R.id.budget_progress_bar);
            editButton = itemView.findViewById(R.id.btn_edit_budget);
        }

        public void bind(MonthlyBudgetDao.BudgetSummary budget) {
            categoryName.setText(budget.categoryName);
            budgetAmount.setText(String.format("₹%.0f", budget.budgetAmount));
            spentAmount.setText(String.format("₹%.0f", budget.spentAmount));

            // Set category color
            if (budget.categoryColor != null && !budget.categoryColor.isEmpty()) {
                try {
                    int color = Color.parseColor(budget.categoryColor);
                    setColorIndicator(color);
                } catch (Exception e) {
                    setColorIndicator(Color.parseColor("#607D8B")); // Default gray
                }
            } else {
                setColorIndicator(Color.parseColor("#607D8B")); // Default gray
            }

            // Calculate progress and remaining
            double remaining = budget.getRemainingBudget();
            boolean isOverBudget = budget.isOverBudget();

            // Progress calculation
            int progress;
            if (budget.budgetAmount > 0) {
                progress = (int) Math.min(100, (budget.spentAmount / budget.budgetAmount) * 100);
            } else {
                progress = 0;
            }

            progressBar.setProgress(progress);

            // Set progress bar color based on status
            if (isOverBudget) {
                progressBar.getProgressDrawable().setColorFilter(
                    Color.parseColor("#F44336"), // Red
                    android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (progress > 80) {
                progressBar.getProgressDrawable().setColorFilter(
                    Color.parseColor("#FF9800"), // Orange
                    android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                progressBar.getProgressDrawable().setColorFilter(
                    Color.parseColor("#4CAF50"), // Green
                    android.graphics.PorterDuff.Mode.SRC_IN);
            }

            // Status text and color
            if (isOverBudget) {
                budgetStatus.setText(String.format("₹%.0f over budget", Math.abs(remaining)));
                budgetStatus.setTextColor(Color.parseColor("#F44336")); // Red
            } else {
                budgetStatus.setText(String.format("₹%.0f remaining", remaining));
                if (remaining < budget.budgetAmount * 0.2) {
                    budgetStatus.setTextColor(Color.parseColor("#FF9800")); // Orange
                } else {
                    budgetStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
                }
            }

            // Edit button click
            editButton.setOnClickListener(v -> {
                if (editClickListener != null) {
                    editClickListener.onEditClick(budget);
                }
            });
        }

        private void setColorIndicator(int color) {
            GradientDrawable drawable = (GradientDrawable) colorIndicator.getBackground();
            drawable.setColor(color);
        }
    }
}