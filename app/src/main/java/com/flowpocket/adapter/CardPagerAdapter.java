package com.flowpocket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.flowpocket.R;
import com.flowpocket.dialog.EditLoanDialog;
import com.flowpocket.entities.Loan;
import com.flowpocket.viewmodel.LoanViewModel;
import com.google.android.material.button.MaterialButton;

public class CardPagerAdapter extends RecyclerView.Adapter<CardPagerAdapter.CardViewHolder> {
    private LifecycleOwner lifecycleOwner;
    private LoanViewModel loanViewModel;
    private OnCardActionListener actionListener;
    private RecyclerView.Adapter expensesAdapter;
    private RecyclerView.LayoutManager expensesLayoutManager;

    public interface OnCardActionListener {
        void onViewAllExpensesClick();
        void onAddBorrowedClick();
        void onAddLentClick();
        RecyclerView.Adapter getExpensesAdapter();
        RecyclerView.LayoutManager getExpensesLayoutManager();
    }

    public CardPagerAdapter(LifecycleOwner lifecycleOwner, LoanViewModel loanViewModel) {
        this.lifecycleOwner = lifecycleOwner;
        this.loanViewModel = loanViewModel;
    }

    public void setOnCardActionListener(OnCardActionListener listener) {
        this.actionListener = listener;
        if (listener != null) {
            this.expensesAdapter = listener.getExpensesAdapter();
            this.expensesLayoutManager = listener.getExpensesLayoutManager();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Expenses, Borrowed, Lent
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == 0) {
            // Recent Expenses card
            view = inflater.inflate(R.layout.card_recent_expenses, parent, false);
        } else if (viewType == 1) {
            // Borrowed Money card
            view = inflater.inflate(R.layout.card_borrowed_money, parent, false);
        } else {
            // Lent Money card
            view = inflater.inflate(R.layout.card_lent_money, parent, false);
        }

        return new CardViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        if (position == 0) {
            bindExpensesCard(holder);
        } else if (position == 1) {
            bindBorrowedCard(holder);
        } else {
            bindLentCard(holder);
        }
    }

    private void bindExpensesCard(CardViewHolder holder) {
        // Find and setup expenses RecyclerView
        RecyclerView recyclerView = holder.itemView.findViewById(R.id.expenses_recycler_view);
        if (recyclerView != null) {
            if (expensesLayoutManager != null) {
                recyclerView.setLayoutManager(expensesLayoutManager);
            }
            if (expensesAdapter != null) {
                recyclerView.setAdapter(expensesAdapter);
            }
        }

        // Wire up View All button
        MaterialButton viewAllBtn = holder.itemView.findViewById(R.id.btn_view_all_expenses);
        if (viewAllBtn != null && actionListener != null) {
            viewAllBtn.setOnClickListener(v -> actionListener.onViewAllExpensesClick());
        }
    }

    private void bindBorrowedCard(CardViewHolder holder) {
        RecyclerView recyclerView = holder.itemView.findViewById(R.id.borrowed_recycler_view);
        TextView emptyText = holder.itemView.findViewById(R.id.borrowed_empty_text);
        TextView totalAmount = holder.itemView.findViewById(R.id.borrowed_total_amount);
        ImageView addButton = holder.itemView.findViewById(R.id.btn_add_borrowed);

        // Initialize only once
        if (holder.borrowedAdapter == null) {
            holder.borrowedAdapter = new LoanAdapter();
            recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            recyclerView.setAdapter(holder.borrowedAdapter);

            // Handle checkbox toggle
            holder.borrowedAdapter.setOnLoanCheckListener((loan, isChecked) -> {
                loanViewModel.toggleSettled(loan);
            });

            // Handle item click to edit
            holder.borrowedAdapter.setOnLoanClickListener(loan -> {
                showEditLoanDialog(loan);
            });

            // Set up observers only once
            loanViewModel.getBorrowedLoans().observe(lifecycleOwner, loans -> {
                android.util.Log.d("CardPagerAdapter", "Borrowed loans updated: count=" + (loans != null ? loans.size() : 0));
                if (loans == null || loans.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);
                    holder.borrowedAdapter.setLoans(loans);
                }
            });

            loanViewModel.getTotalBorrowedPending().observe(lifecycleOwner, total -> {
                if (total != null && total > 0) {
                    totalAmount.setText(String.format("₹%,.0f", total));
                } else {
                    totalAmount.setText("₹0");
                }
            });
        }

        // Wire add button
        if (addButton != null && actionListener != null) {
            addButton.setOnClickListener(v -> actionListener.onAddBorrowedClick());
        }
    }

    private void bindLentCard(CardViewHolder holder) {
        RecyclerView recyclerView = holder.itemView.findViewById(R.id.lent_recycler_view);
        TextView emptyText = holder.itemView.findViewById(R.id.lent_empty_text);
        TextView totalAmount = holder.itemView.findViewById(R.id.lent_total_amount);
        ImageView addButton = holder.itemView.findViewById(R.id.btn_add_lent);

        // Initialize only once
        if (holder.lentAdapter == null) {
            holder.lentAdapter = new LoanAdapter();
            recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            recyclerView.setAdapter(holder.lentAdapter);

            // Handle checkbox toggle
            holder.lentAdapter.setOnLoanCheckListener((loan, isChecked) -> {
                loanViewModel.toggleSettled(loan);
            });

            // Handle item click to edit
            holder.lentAdapter.setOnLoanClickListener(loan -> {
                showEditLoanDialog(loan);
            });

            // Set up observers only once
            loanViewModel.getLentLoans().observe(lifecycleOwner, loans -> {
                android.util.Log.d("CardPagerAdapter", "Lent loans updated: count=" + (loans != null ? loans.size() : 0));
                if (loans == null || loans.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);
                    holder.lentAdapter.setLoans(loans);
                }
            });

            loanViewModel.getTotalLentPending().observe(lifecycleOwner, total -> {
                if (total != null && total > 0) {
                    totalAmount.setText(String.format("₹%,.0f", total));
                } else {
                    totalAmount.setText("₹0");
                }
            });
        }

        // Wire add button
        if (addButton != null && actionListener != null) {
            addButton.setOnClickListener(v -> actionListener.onAddLentClick());
        }
    }

    private void showEditLoanDialog(Loan loan) {
        if (lifecycleOwner instanceof FragmentActivity) {
            FragmentActivity activity = (FragmentActivity) lifecycleOwner;
            EditLoanDialog dialog = EditLoanDialog.newInstance(loan);
            dialog.setOnLoanUpdatedListener(new EditLoanDialog.OnLoanUpdatedListener() {
                @Override
                public void onLoanUpdated() {
                    // Data will auto-refresh via LiveData observers
                }

                @Override
                public void onLoanDeleted() {
                    // Data will auto-refresh via LiveData observers
                }
            });
            dialog.show(activity.getSupportFragmentManager(), "EditLoan");
        }
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        LoanAdapter borrowedAdapter;
        LoanAdapter lentAdapter;

        public CardViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
        }
    }
}
