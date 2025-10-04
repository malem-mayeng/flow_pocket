package com.flowpocket.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.flowpocket.R;
import com.flowpocket.entities.Loan;
import com.google.android.material.checkbox.MaterialCheckBox;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.LoanViewHolder> {
    private List<Loan> loans = new ArrayList<>();
    private OnLoanClickListener clickListener;
    private OnLoanCheckListener checkListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public interface OnLoanClickListener {
        void onLoanClick(Loan loan);
    }

    public interface OnLoanCheckListener {
        void onLoanChecked(Loan loan, boolean isChecked);
    }

    public void setOnLoanClickListener(OnLoanClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnLoanCheckListener(OnLoanCheckListener listener) {
        this.checkListener = listener;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans != null ? loans : new ArrayList<>();
        android.util.Log.d("LoanAdapter", "setLoans called with " + this.loans.size() + " loans");
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LoanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_loan, parent, false);
        return new LoanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoanViewHolder holder, int position) {
        Loan loan = loans.get(position);
        holder.bind(loan);
    }

    @Override
    public int getItemCount() {
        return loans.size();
    }

    class LoanViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCheckBox checkbox;
        private final TextView amountText;
        private final TextView personText;
        private final TextView dateText;
        private final TextView dueDateText;
        private final TextView notesText;
        private final TextView settledInfoText;

        public LoanViewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.loan_checkbox);
            amountText = itemView.findViewById(R.id.loan_amount);
            personText = itemView.findViewById(R.id.loan_person);
            dateText = itemView.findViewById(R.id.loan_date);
            dueDateText = itemView.findViewById(R.id.loan_due_date);
            notesText = itemView.findViewById(R.id.loan_notes);
            settledInfoText = itemView.findViewById(R.id.loan_settled_info);
        }

        public void bind(Loan loan) {
            // Amount
            amountText.setText(String.format("₹%,.0f", loan.getAmount()));

            // Person name
            String personLabel = loan.getType().equals("borrowed") ? "From: " : "To: ";
            personText.setText(personLabel + loan.getPersonName());

            // Date
            dateText.setText("Date: " + dateFormat.format(loan.getTransactionDate()));

            // Due date (optional)
            if (loan.getDueDate() != null) {
                dueDateText.setVisibility(View.VISIBLE);
                dueDateText.setText("Due: " + dateFormat.format(loan.getDueDate()));

                // Check if overdue
                if (!loan.isSettled() && loan.getDueDate().before(new Date())) {
                    dueDateText.setTextColor(0xFFD32F2F); // Dark red for overdue
                } else {
                    dueDateText.setTextColor(0xFFFF9800); // Orange for upcoming
                }
            } else {
                dueDateText.setVisibility(View.GONE);
            }

            // Notes (optional)
            if (loan.getNotes() != null && !loan.getNotes().trim().isEmpty()) {
                notesText.setVisibility(View.VISIBLE);
                notesText.setText("Notes: " + loan.getNotes());
            } else {
                notesText.setVisibility(View.GONE);
            }

            // Checkbox state
            checkbox.setChecked(loan.isSettled());
            checkbox.setEnabled(!loan.isSettled()); // Disable checkbox when settled

            // Settled info
            if (loan.isSettled()) {
                settledInfoText.setVisibility(View.VISIBLE);
                String settledLabel = loan.getType().equals("borrowed") ? "Paid: " : "Returned: ";
                if (loan.getSettledDate() != null) {
                    settledInfoText.setText(settledLabel + dateFormat.format(loan.getSettledDate()));
                } else {
                    settledInfoText.setText(settledLabel + "Yes");
                }
            } else {
                settledInfoText.setVisibility(View.GONE);
            }

            // Apply strikethrough if settled
            applyStrikethrough(loan.isSettled());

            // Checkbox listener
            checkbox.setOnCheckedChangeListener(null); // Clear previous listener
            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (checkListener != null) {
                    checkListener.onLoanChecked(loan, isChecked);
                }
            });

            // Item click listener
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onLoanClick(loan);
                }
            });
        }

        private void applyStrikethrough(boolean settled) {
            if (settled) {
                // Apply strikethrough
                amountText.setPaintFlags(amountText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                personText.setPaintFlags(personText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                dateText.setPaintFlags(dateText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                // Gray out text
                amountText.setTextColor(0xFF9E9E9E);
                personText.setTextColor(0xFFBDBDBD);
                dateText.setTextColor(0xFFBDBDBD);
            } else {
                // Remove strikethrough
                amountText.setPaintFlags(amountText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                personText.setPaintFlags(personText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                dateText.setPaintFlags(dateText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);

                // Normal colors
                amountText.setTextColor(0xFF212121);
                personText.setTextColor(0xFF757575);
                dateText.setTextColor(0xFF9E9E9E);
            }
        }
    }
}
