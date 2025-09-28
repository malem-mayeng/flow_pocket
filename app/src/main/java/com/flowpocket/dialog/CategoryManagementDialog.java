package com.flowpocket.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.flowpocket.R;
import com.flowpocket.entities.ExpenseLabel;
import com.flowpocket.viewmodel.ExpenseViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class CategoryManagementDialog extends DialogFragment {
    private ExpenseViewModel viewModel;
    private ExpenseLabel category;
    private OnCategoryUpdatedListener listener;

    public interface OnCategoryUpdatedListener {
        void onCategoryUpdated();
        void onCategoryDeleted();
    }

    public static CategoryManagementDialog newInstance(ExpenseLabel category) {
        CategoryManagementDialog dialog = new CategoryManagementDialog();
        Bundle args = new Bundle();
        args.putSerializable("category", category);
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnCategoryUpdatedListener(OnCategoryUpdatedListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(ExpenseViewModel.class);

        if (getArguments() != null) {
            category = (ExpenseLabel) getArguments().getSerializable("category");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.CustomDialogTheme);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_category_management, null);

        // Find and configure the text input for renaming
        TextInputEditText nameInput = view.findViewById(R.id.category_name_input);

        // Pre-fill with current category name
        if (category != null) {
            nameInput.setText(category.getName());
            nameInput.selectAll(); // Select all text for easy editing
        }

        AlertDialog alertDialog = builder.setView(view)
               .setPositiveButton("Save", null)
               .setNegativeButton("Delete", null)
               .create();

        // Set button listeners after dialog creation
        alertDialog.setOnShowListener(dialogInterface -> {
            // Save button
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                renameCategory(nameInput, alertDialog);
            });

            // Delete button
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
                showDeleteConfirmation(alertDialog);
            });
        });

        // Backdrop dimming is now handled by the theme

        return alertDialog;
    }

    private void renameCategory(TextInputEditText nameInput, AlertDialog alertDialog) {
        String newName = nameInput.getText() != null ? nameInput.getText().toString().trim() : "";

        if (newName.isEmpty()) {
            nameInput.setError("Please enter category name");
            return;
        }

        if (newName.equals(category.getName())) {
            alertDialog.dismiss(); // No change needed
            return;
        }

        // Check if name already exists
        new Thread(() -> {
            ExpenseLabel existingCategory = viewModel.findCategoryByName(newName);
            if (existingCategory != null) {
                requireActivity().runOnUiThread(() -> {
                    nameInput.setError("Category name already exists");
                });
                return;
            }

            // Update category name
            category.setName(newName);
            category.setUpdatedAt(new java.util.Date());
            viewModel.updateCategory(category);

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "Category renamed!", Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onCategoryUpdated();
                }
                alertDialog.dismiss();
            });
        }).start();
    }

    private void showDeleteConfirmation(AlertDialog parentDialog) {
        // Check if category has expenses
        new Thread(() -> {
            int expenseCount = viewModel.countExpensesWithLabel(category.getId());

            requireActivity().runOnUiThread(() -> {
                if (expenseCount > 0) {
                    // Category has expenses - show warning
                    AlertDialog warningDialog = new AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
                        .setTitle("Cannot Delete Category")
                        .setMessage("This category has " + expenseCount + " expense(s). " +
                                  "Please move or delete those expenses first before deleting this category.")
                        .setPositiveButton("OK", null)
                        .create();
                    warningDialog.show();
                } else {
                    // Safe to delete - show confirmation
                    AlertDialog confirmDialog = new AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
                        .setTitle("Delete Category")
                        .setMessage("Are you sure you want to delete the category \"" + category.getName() + "\"?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            deleteCategory(parentDialog);
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                    confirmDialog.show();
                }
            });
        }).start();
    }

    private void deleteCategory(AlertDialog parentDialog) {
        viewModel.deleteCategory(category);
        Toast.makeText(getContext(), "Category deleted!", Toast.LENGTH_SHORT).show();

        if (listener != null) {
            listener.onCategoryDeleted();
        }

        parentDialog.dismiss();
    }
}