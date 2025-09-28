package com.flowpocket.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.flowpocket.R;
import com.flowpocket.entities.Income;
import com.flowpocket.viewmodel.IncomeViewModel;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

public class AddIncomeDialog extends DialogFragment {
    private IncomeViewModel viewModel;
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(IncomeViewModel.class);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View view = LayoutInflater.from(requireActivity())
            .inflate(R.layout.dialog_add_income, null);
        
        TextInputEditText amountInput = view.findViewById(R.id.income_amount_input);

        // Prepopulate with current income amount if it exists
        viewModel.getCurrentMonthIncome().observe(this, income -> {
            if (income != null) {
                amountInput.setText(String.valueOf((int) income.getAmount()));
            }
        });

        AlertDialog alertDialog = builder.setView(view)
                     .setTitle("Set Monthly Income")
                     .setPositiveButton("Save", null)
                     .setNegativeButton("Cancel", null)
                     .create();

        alertDialog.setOnShowListener(dialogInterface -> {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String amountText = amountInput.getText() != null ? amountInput.getText().toString().trim() : "";

                if (amountText.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter income amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double amount = Double.parseDouble(amountText);
                    if (amount <= 0) {
                        Toast.makeText(getContext(), "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Income income = new Income(amount, Calendar.getInstance().getTime());
                    viewModel.insertIncome(income);

                    Toast.makeText(getContext(), "Income set successfully!", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();

                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return alertDialog;
    }
}
