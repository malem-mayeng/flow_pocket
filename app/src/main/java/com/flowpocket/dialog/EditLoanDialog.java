package com.flowpocket.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.flowpocket.R;
import com.flowpocket.entities.Loan;
import com.flowpocket.viewmodel.LoanViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditLoanDialog extends DialogFragment {
    private static final String ARG_LOAN = "loan";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    private Loan loan;
    private Date selectedDate;
    private Date selectedDueDate;

    private TextInputEditText personNameInput;
    private TextInputEditText amountInput;
    private TextInputEditText dateInput;
    private TextInputEditText dueDateInput;
    private TextInputEditText notesInput;

    private LoanViewModel loanViewModel;
    private OnLoanUpdatedListener listener;

    public interface OnLoanUpdatedListener {
        void onLoanUpdated();
        void onLoanDeleted();
    }

    public static EditLoanDialog newInstance(Loan loan) {
        EditLoanDialog dialog = new EditLoanDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LOAN, loan);
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnLoanUpdatedListener(OnLoanUpdatedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loan = (Loan) getArguments().getSerializable(ARG_LOAN);
        }
        loanViewModel = new ViewModelProvider(requireActivity()).get(LoanViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_loan, null);

        initViews(view);
        populateFields();
        setupDatePickers();

        builder.setView(view);
        return builder.create();
    }

    private void initViews(View view) {
        TextView titleText = view.findViewById(R.id.dialog_title);
        personNameInput = view.findViewById(R.id.input_person_name);
        amountInput = view.findViewById(R.id.input_amount);
        dateInput = view.findViewById(R.id.input_date);
        dueDateInput = view.findViewById(R.id.input_due_date);
        notesInput = view.findViewById(R.id.input_notes);
        MaterialButton saveButton = view.findViewById(R.id.btn_save);
        MaterialButton cancelButton = view.findViewById(R.id.btn_cancel);
        MaterialButton deleteButton = view.findViewById(R.id.btn_delete);

        // Set title based on loan type
        if (loan.getType().equals("borrowed")) {
            titleText.setText("Edit Borrowed Money");
        } else {
            titleText.setText("Edit Lent Money");
        }

        // Button listeners
        saveButton.setOnClickListener(v -> updateLoan());
        cancelButton.setOnClickListener(v -> dismiss());
        deleteButton.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void populateFields() {
        // Set person name
        personNameInput.setText(loan.getPersonName());

        // Set amount (format as whole number if no decimals)
        double amount = loan.getAmount();
        if (amount == (long) amount) {
            amountInput.setText(String.valueOf((long) amount));
        } else {
            amountInput.setText(String.valueOf(amount));
        }

        // Set dates
        selectedDate = loan.getTransactionDate();
        dateInput.setText(dateFormat.format(selectedDate));

        if (loan.getDueDate() != null) {
            selectedDueDate = loan.getDueDate();
            dueDateInput.setText(dateFormat.format(selectedDueDate));
        }

        // Set notes
        if (loan.getNotes() != null) {
            notesInput.setText(loan.getNotes());
        }
    }

    private void setupDatePickers() {
        // Transaction Date Picker
        dateInput.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            if (selectedDate != null) {
                cal.setTime(selectedDate);
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    selectedDate = selected.getTime();
                    dateInput.setText(dateFormat.format(selectedDate));
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Due Date Picker
        dueDateInput.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            if (selectedDueDate != null) {
                cal.setTime(selectedDueDate);
            } else if (selectedDate != null) {
                cal.setTime(selectedDate);
                cal.add(Calendar.DAY_OF_MONTH, 7); // Default 7 days from transaction date
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    selectedDueDate = selected.getTime();
                    dueDateInput.setText(dateFormat.format(selectedDueDate));
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void updateLoan() {
        String personName = personNameInput.getText().toString().trim();
        String amountStr = amountInput.getText().toString().trim();
        String notes = notesInput.getText().toString().trim();

        // Validation
        if (personName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter person name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(getContext(), "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update loan fields
        loan.setPersonName(personName);
        loan.setAmount(amount);
        loan.setTransactionDate(selectedDate);

        if (selectedDueDate != null) {
            loan.setDueDate(selectedDueDate);
        } else {
            loan.setDueDate(null);
        }

        if (!notes.isEmpty()) {
            loan.setNotes(notes);
        } else {
            loan.setNotes(null);
        }

        // Update in database
        loanViewModel.updateLoan(loan);

        String message = loan.getType().equals("borrowed") ?
            "Borrowed money updated" : "Lent money updated";
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

        if (listener != null) {
            listener.onLoanUpdated();
        }

        dismiss();
    }

    private void showDeleteConfirmation() {
        String loanType = loan.getType().equals("borrowed") ? "borrowed money" : "lent money";

        new AlertDialog.Builder(requireContext())
            .setTitle("Delete Loan")
            .setMessage("Are you sure you want to delete this " + loanType + "?")
            .setPositiveButton("Delete", (dialog, which) -> {
                loanViewModel.deleteLoan(loan);

                String message = loan.getType().equals("borrowed") ?
                    "Borrowed money deleted" : "Lent money deleted";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                if (listener != null) {
                    listener.onLoanDeleted();
                }

                dismiss();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
