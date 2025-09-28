package com.flowpocket.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Date;

public class AddExpenseDialog extends DialogFragment {
    private ExpenseViewModel viewModel;
    private java.util.List<ExpenseLabel> allCategories;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(ExpenseViewModel.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.CustomDialogTheme);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_expense, null);

        TextInputEditText nameInput = view.findViewById(R.id.expense_name_input);
        TextInputEditText amountInput = view.findViewById(R.id.expense_amount_input);
        Spinner categorySpinner = view.findViewById(R.id.expense_category_spinner);
        TextInputLayout customCategoryLayout = view.findViewById(R.id.custom_category_layout);
        TextInputEditText customCategoryInput = view.findViewById(R.id.custom_category_input);

        // Load categories from database and setup spinner
        setupCategorySpinner(categorySpinner, customCategoryLayout);

        AlertDialog alertDialog = builder.setView(view)
               .setTitle("Add Expense")
               .setPositiveButton("Save", null) // We'll set this manually to control dismissal
               .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel())
               .create();

        // Set the positive button click listener after dialog creation to prevent auto-dismiss
        alertDialog.setOnShowListener(dialogInterface -> {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                saveExpense(nameInput, amountInput, categorySpinner, customCategoryInput, alertDialog);
            });
        });

        // Backdrop dimming is now handled by the theme

        return alertDialog;
    }

    private void setupCategorySpinner(Spinner categorySpinner, TextInputLayout customCategoryLayout) {
        viewModel.getAllCategories().observe(this, categories -> {
            android.util.Log.d("AddExpense", "Categories loaded: " + (categories != null ? categories.size() : "null"));
            if (categories != null) {
                allCategories = categories;
                java.util.List<String> categoryNames = new java.util.ArrayList<>();

                // Add all categories except "Others" first
                for (ExpenseLabel category : categories) {
                    if (!"Others".equals(category.getName())) {
                        categoryNames.add(category.getName());
                    }
                }

                // Add "Others" at the end
                for (ExpenseLabel category : categories) {
                    if ("Others".equals(category.getName())) {
                        categoryNames.add(category.getName());
                        break;
                    }
                }

                LongPressSpinnerAdapter adapter = new LongPressSpinnerAdapter(requireContext(),
                    android.R.layout.simple_spinner_item, categoryNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);

                // Set up listener to show custom input when "Others" is selected
                categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        android.util.Log.d("AddExpense", "onItemSelected called - position: " + position + ", id: " + id);
                        String selectedCategory = adapter.getItem(position); // Use adapter's getItem instead
                        android.util.Log.d("AddExpense", "Selected category: " + selectedCategory);
                        if ("Others".equals(selectedCategory)) {
                            customCategoryLayout.setVisibility(View.VISIBLE);
                            android.util.Log.d("AddExpense", "Showing custom category layout");
                        } else {
                            customCategoryLayout.setVisibility(View.GONE);
                            android.util.Log.d("AddExpense", "Hiding custom category layout");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        android.util.Log.d("AddExpense", "onNothingSelected called");
                        customCategoryLayout.setVisibility(View.GONE);
                    }
                });

                // Set up long press listener on dropdown items
                android.util.Log.d("AddExpense", "Setting long press listener");
                adapter.setOnItemLongClickListener((position, categoryName) -> {
                    android.util.Log.d("AddExpense", "Long press callback triggered for: " + categoryName);
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
                            return true; // Consume event only when we show dialog
                        }
                    }
                    return false; // Don't consume event for Others or when no action taken
                });
            }
        });
    }

    private void saveExpense(TextInputEditText nameInput, TextInputEditText amountInput,
                           Spinner categorySpinner, TextInputEditText customCategoryInput,
                           AlertDialog alertDialog) {
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

            // Handle category selection
            String selectedCategoryName = (String) categorySpinner.getSelectedItem();
            String customCategory = customCategoryInput.getText() != null ?
                customCategoryInput.getText().toString().trim() : "";

            int labelId;
            if ("Others".equals(selectedCategoryName) && !customCategory.isEmpty()) {
                // Create new custom category
                labelId = createOrGetCategoryId(customCategory);
            } else {
                // Use existing category
                labelId = getCategoryIdFromName(selectedCategoryName);
            }

            Expense expense = new Expense(name, amount, new Date(), labelId);
            viewModel.insertExpense(expense);

            Toast toast = Toast.makeText(getContext(), "Expense saved!", Toast.LENGTH_SHORT);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                toast.cancel();
            }, 1000);
            toast.show();
            alertDialog.dismiss();

        } catch (NumberFormatException e) {
            amountInput.setError("Please enter a valid number");
        }
    }

    private int createOrGetCategoryId(String categoryName) {
        try {
            // Run synchronously in background thread
            java.util.concurrent.FutureTask<Integer> task = new java.util.concurrent.FutureTask<>(() -> {
                ExpenseLabel existingCategory = viewModel.findCategoryByName(categoryName);
                if (existingCategory != null) {
                    return existingCategory.getId();
                }

                // Get unique color for new category
                String uniqueColor = getUniqueColorForCategory();

                ExpenseLabel newCategory = new ExpenseLabel(categoryName, uniqueColor);
                long newId = viewModel.getRepository().insertCategory(newCategory);
                return (int) newId;
            });

            new Thread(task).start();
            return task.get(); // Wait for result
        } catch (Exception e) {
            return 7; // Default to Others if error
        }
    }

    private String getUniqueColorForCategory() {
        // Extended color palette with more unique colors
        String[] availableColors = {
            "#2196F3", "#FF9800", "#4CAF50", "#9C27B0", "#F44336", "#00BCD4", "#607D8B",
            "#E91E63", "#673AB7", "#3F51B5", "#009688", "#8BC34A", "#CDDC39", "#FFC107",
            "#FF5722", "#795548", "#9E9E9E", "#FF4081", "#536DFE", "#40C4FF", "#18FFFF",
            "#69F0AE", "#B2FF59", "#EEFF41", "#FFD740", "#FFAB40", "#FF6E40"
        };

        try {
            // Get all existing categories and their colors
            java.util.List<String> usedColors = new java.util.ArrayList<>();
            if (allCategories != null) {
                for (ExpenseLabel category : allCategories) {
                    usedColors.add(category.getColor());
                }
            }

            // Find first unused color
            for (String color : availableColors) {
                if (!usedColors.contains(color)) {
                    return color;
                }
            }

            // If all colors are used, generate a random one
            java.util.Random random = new java.util.Random();
            int r = random.nextInt(256);
            int g = random.nextInt(256);
            int b = random.nextInt(256);
            return String.format("#%02X%02X%02X", r, g, b);

        } catch (Exception e) {
            // Fallback to default color
            return "#607D8B";
        }
    }

    private int getCategoryIdFromName(String categoryName) {
        if (allCategories != null) {
            for (ExpenseLabel category : allCategories) {
                if (category.getName().equals(categoryName)) {
                    return category.getId();
                }
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
