package com.flowpocket.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.flowpocket.adapter.LongPressSpinnerAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.flowpocket.R;
import com.flowpocket.entities.Expense;
import com.flowpocket.entities.ExpenseLabel;
import com.flowpocket.viewmodel.ExpenseViewModel;
import com.flowpocket.dialog.CategoryManagementDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditExpenseDialog extends DialogFragment {
    private ExpenseViewModel viewModel;
    private Expense expense;
    private OnExpenseUpdatedListener listener;
    private java.util.List<ExpenseLabel> allCategories;

    public interface OnExpenseUpdatedListener {
        void onExpenseUpdated();
        void onExpenseDeleted();
    }

    public static EditExpenseDialog newInstance(Expense expense) {
        EditExpenseDialog dialog = new EditExpenseDialog();
        Bundle args = new Bundle();
        args.putSerializable("expense", expense);
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnExpenseUpdatedListener(OnExpenseUpdatedListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(ExpenseViewModel.class);

        if (getArguments() != null) {
            expense = (Expense) getArguments().getSerializable("expense");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.CustomDialogTheme);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_expense, null);

        TextInputEditText nameInput = view.findViewById(R.id.expense_name_input);
        TextInputEditText amountInput = view.findViewById(R.id.expense_amount_input);
        Spinner categorySpinner = view.findViewById(R.id.expense_category_spinner);
        ImageView closeButton = view.findViewById(R.id.close_button);
        MaterialButton saveButton = view.findViewById(R.id.btn_save);
        MaterialButton cancelButton = view.findViewById(R.id.btn_cancel);
        MaterialButton deleteButton = view.findViewById(R.id.btn_delete);

        // Pre-fill existing data
        if (expense != null) {
            nameInput.setText(expense.getName());

            // Format amount to show whole numbers without .0
            double amount = expense.getAmount();
            if (amount == (long) amount) {
                // It's a whole number, show without decimal
                amountInput.setText(String.valueOf((long) amount));
            } else {
                // It has decimal places, show as is
                amountInput.setText(String.valueOf(amount));
            }
        }

        // Setup category spinner with database categories
        setupCategorySpinner(categorySpinner, expense);

        AlertDialog alertDialog = builder.setView(view).create();

        // Set button listeners
        saveButton.setOnClickListener(v -> {
            updateExpense(nameInput, amountInput, categorySpinner, alertDialog);
        });

        cancelButton.setOnClickListener(v -> alertDialog.dismiss());

        deleteButton.setOnClickListener(v -> {
            showDeleteConfirmation(alertDialog);
        });

        // Set close button listener
        closeButton.setOnClickListener(v -> alertDialog.dismiss());

        // Allow dismissal by clicking outside
        alertDialog.setCanceledOnTouchOutside(true);

        // Backdrop dimming is now handled by the theme

        return alertDialog;
    }

    private void updateExpense(TextInputEditText nameInput, TextInputEditText amountInput,
                              Spinner categorySpinner, AlertDialog alertDialog) {
        String name = nameInput.getText() != null ? nameInput.getText().toString().trim() : "";
        String amountText = amountInput.getText() != null ? amountInput.getText().toString().trim() : "";

        if (name.isEmpty()) {
            nameInput.setError("Please enter expense name");
            return;
        }

        if (amountText.isEmpty()) {
            amountInput.setError("Please enter amount");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                amountInput.setError("Amount must be greater than 0");
                return;
            }

            // Update expense with new values
            String selectedCategory = (String) categorySpinner.getSelectedItem();
            int labelId = getCategoryIdFromName(selectedCategory, allCategories);

            expense.setName(name);
            expense.setAmount(amount);
            expense.setLabelId(labelId);

            viewModel.updateExpense(expense);

            Toast.makeText(getContext(), "Expense updated!", Toast.LENGTH_SHORT).show();

            if (listener != null) {
                listener.onExpenseUpdated();
            }

            alertDialog.dismiss();

        } catch (NumberFormatException e) {
            amountInput.setError("Please enter a valid number");
        }
    }

    private void showDeleteConfirmation(AlertDialog parentDialog) {
        AlertDialog confirmDialog = new AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
            .setTitle("Delete Expense")
            .setMessage("Are you sure you want to delete this expense?")
            .setPositiveButton("Delete", (dialog, which) -> {
                viewModel.deleteExpense(expense);
                Toast.makeText(getContext(), "Expense deleted!", Toast.LENGTH_SHORT).show();

                if (listener != null) {
                    listener.onExpenseDeleted();
                }

                parentDialog.dismiss();
            })
            .setNegativeButton("Cancel", null)
            .create();
        confirmDialog.show();
    }

    private void setupCategorySpinner(Spinner categorySpinner, Expense expense) {
        viewModel.getAllCategories().observe(this, categories -> {
            if (categories != null) {
                allCategories = categories; // Store for later use
                java.util.List<String> categoryNames = new java.util.ArrayList<>();
                java.util.Map<String, Integer> categoryIdMap = new java.util.HashMap<>();

                // Add all categories except "Others" first
                for (ExpenseLabel category : categories) {
                    if (!"Others".equals(category.getName())) {
                        categoryNames.add(category.getName());
                        categoryIdMap.put(category.getName(), category.getId());
                    }
                }

                // Add "Others" at the end
                for (ExpenseLabel category : categories) {
                    if ("Others".equals(category.getName())) {
                        categoryNames.add(category.getName());
                        categoryIdMap.put(category.getName(), category.getId());
                        break;
                    }
                }

                LongPressSpinnerAdapter adapter = new LongPressSpinnerAdapter(requireContext(),
                    android.R.layout.simple_spinner_item, categoryNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);

                // Set up long press listener on dropdown items
                adapter.setOnItemLongClickListener((position, categoryName) -> {
                    if (!"Others".equals(categoryName)) {
                        ExpenseLabel selectedCategory = null;
                        for (ExpenseLabel category : categories) {
                            if (category.getName().equals(categoryName)) {
                                selectedCategory = category;
                                break;
                            }
                        }
                        if (selectedCategory != null) {
                            showCategoryManagementDialog(selectedCategory);
                            return true; // Only return true if we actually show the dialog
                        }
                    }
                    return false; // Always return false for Others and when no category found
                });

                // Set selected category for editing
                if (expense != null) {
                    String currentCategory = getCategoryNameFromId(expense.getLabelId(), categories);
                    int position = categoryNames.indexOf(currentCategory);
                    if (position >= 0) {
                        categorySpinner.setSelection(position);
                    }
                }
            }
        });
    }

    private String getCategoryNameFromId(int labelId, java.util.List<ExpenseLabel> categories) {
        for (ExpenseLabel category : categories) {
            if (category.getId() == labelId) {
                return category.getName();
            }
        }
        return "Others";
    }

    private int getCategoryIdFromName(String categoryName, java.util.List<ExpenseLabel> categories) {
        for (ExpenseLabel category : categories) {
            if (category.getName().equals(categoryName)) {
                return category.getId();
            }
        }
        return 7; // Default to Others
    }

    private void showCategoryManagementDialog(ExpenseLabel category) {
        CategoryManagementDialog dialog = CategoryManagementDialog.newInstance(category);
        dialog.setOnCategoryUpdatedListener(new CategoryManagementDialog.OnCategoryUpdatedListener() {
            @Override
            public void onCategoryUpdated() {
                // Refresh category spinner after update
                refreshCategorySpinner();
            }

            @Override
            public void onCategoryDeleted() {
                // Refresh category spinner after deletion
                refreshCategorySpinner();
            }
        });
        dialog.show(getParentFragmentManager(), "CategoryManagement");
    }

    private void refreshCategorySpinner() {
        // Trigger the category observer to refresh the spinner
        viewModel.getAllCategories().observe(this, categories -> {
            // This will automatically update the spinner
        });
    }
}