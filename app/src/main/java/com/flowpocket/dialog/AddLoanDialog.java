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

public class AddLoanDialog extends DialogFragment {
    private static final String ARG_LOAN_TYPE = "loan_type";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    private String loanType; // "borrowed" or "lent"
    private Date selectedDate;
    private Date selectedDueDate;

    private TextInputEditText personNameInput;
    private TextInputEditText amountInput;
    private TextInputEditText dateInput;
    private TextInputEditText dueDateInput;
    private TextInputEditText notesInput;

    private LoanViewModel loanViewModel;

    public static AddLoanDialog newInstance(String loanType) {
        AddLoanDialog dialog = new AddLoanDialog();
        Bundle args = new Bundle();
        args.putString(ARG_LOAN_TYPE, loanType);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loanType = getArguments().getString(ARG_LOAN_TYPE, "borrowed");
        }
        loanViewModel = new ViewModelProvider(requireActivity()).get(LoanViewModel.class);
        selectedDate = new Date(); // Default to today
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_loan, null);

        initViews(view);
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

        // Set title based on loan type
        if (loanType.equals("borrowed")) {
            titleText.setText("Add Borrowed Money");
        } else {
            titleText.setText("Add Lent Money");
        }

        // Set default date
        dateInput.setText(dateFormat.format(selectedDate));

        // Button listeners
        saveButton.setOnClickListener(v -> saveLoan());
        cancelButton.setOnClickListener(v -> dismiss());
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

    private void saveLoan() {
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

        // Create loan
        Loan loan = new Loan(loanType, personName, amount, selectedDate);

        if (selectedDueDate != null) {
            loan.setDueDate(selectedDueDate);
        }

        if (!notes.isEmpty()) {
            loan.setNotes(notes);
        }

        // Save to database
        android.util.Log.d("AddLoanDialog", "Saving loan: type=" + loanType + ", person=" + personName + ", amount=" + amount);
        loanViewModel.insertLoan(loan);

        String message = loanType.equals("borrowed") ?
            "Borrowed money recorded" : "Lent money recorded";
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

        dismiss();
    }
}
