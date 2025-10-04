package com.flowpocket;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.flowpocket.viewmodel.ExpenseViewModel;
import com.flowpocket.viewmodel.IncomeViewModel;
import com.flowpocket.viewmodel.LoanViewModel;
import com.flowpocket.ui.SimpleExpenseAdapter;
import com.flowpocket.adapter.CardPagerAdapter;
import com.flowpocket.dialog.AddExpenseDialog;
import com.flowpocket.dialog.AddIncomeDialog;
import com.flowpocket.dialog.AddLoanDialog;
import com.flowpocket.dialog.EditExpenseDialog;
import com.flowpocket.entities.Expense;
import com.flowpocket.util.MiniChartManager;
import com.flowpocket.util.DataExportUtil;
import com.flowpocket.util.DataImportUtil;
import com.flowpocket.utils.MonthYearPicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.github.mikephil.charting.charts.PieChart;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ExpenseViewModel viewModel;
    private IncomeViewModel incomeViewModel;
    private LoanViewModel loanViewModel;
    private SimpleExpenseAdapter expenseAdapter;
    private RecyclerView expenseList;
    private TextView balanceText;
    private TextView incomeText;
    private MiniChartManager miniChartManager;

    // Navigation Drawer
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // ViewPager2 for swipeable cards
    private ViewPager2 cardsViewPager;
    private CardPagerAdapter cardPagerAdapter;
    private LinearLayout pageIndicator;
    private ImageView[] dots;

    // Month/Year picker variables
    private TextView selectedMonthYearText;
    private int currentSelectedMonth;
    private int currentSelectedYear;

    // Import functionality
    private static final int IMPORT_FILE_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display
        getWindow().setDecorFitsSystemWindows(false);

        setContentView(R.layout.activity_main);

        // Hide default ActionBar since we have custom title
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupNavigationDrawer();
        setupRecyclerView();
        setupCardsViewPager();
        setupFab();
        setupButtons();
        setupMiniChart();
        observeData();
    }

    private TextView expensesText;
    private TextView expensesSectionTitle;
    private boolean showingAllExpenses = false;
    private MaterialButton viewAllExpensesBtn;
    private com.google.android.material.appbar.AppBarLayout appBarLayout;
    private LinearLayout mainContent;

    private void initViews() {
        viewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        incomeViewModel = new ViewModelProvider(this).get(IncomeViewModel.class);
        loanViewModel = new ViewModelProvider(this).get(LoanViewModel.class);
        expenseAdapter = new SimpleExpenseAdapter();
        // Note: expenseList will be created by ViewPager adapter
        balanceText = findViewById(R.id.remaining_amount);
        incomeText = findViewById(R.id.income_amount);
        expensesText = findViewById(R.id.expenses_amount);
        appBarLayout = findViewById(R.id.app_bar_layout);
        mainContent = findViewById(R.id.main_content);
        selectedMonthYearText = findViewById(R.id.tv_selected_month_year);

        // Initialize with current month/year
        currentSelectedMonth = MonthYearPicker.getCurrentMonth();
        currentSelectedYear = MonthYearPicker.getCurrentYear();
        updateMonthYearDisplay();
    }

    private void setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Setup menu button to open drawer
        ImageView menuButton = findViewById(R.id.btn_menu);
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Set Dashboard as checked by default
        navigationView.setCheckedItem(R.id.nav_dashboard);
    }

    private void setupRecyclerView() {
        // Set viewModel for dynamic category names
        expenseAdapter.setViewModel(viewModel);

        // Set click listener for expense items
        expenseAdapter.setOnExpenseClickListener(expense -> showEditExpenseDialog(expense));
    }

    private void setupCardsViewPager() {
        cardsViewPager = findViewById(R.id.cards_view_pager);
        pageIndicator = findViewById(R.id.page_indicator);

        // Create adapter
        cardPagerAdapter = new CardPagerAdapter(this, loanViewModel);

        // Set action listeners
        cardPagerAdapter.setOnCardActionListener(new CardPagerAdapter.OnCardActionListener() {
            @Override
            public void onViewAllExpensesClick() {
                showAllExpenses();
            }

            @Override
            public void onAddBorrowedClick() {
                showAddBorrowedDialog();
            }

            @Override
            public void onAddLentClick() {
                showAddLentDialog();
            }

            @Override
            public RecyclerView.Adapter getExpensesAdapter() {
                return expenseAdapter;
            }

            @Override
            public RecyclerView.LayoutManager getExpensesLayoutManager() {
                return new LinearLayoutManager(MainActivity.this);
            }
        });

        cardsViewPager.setAdapter(cardPagerAdapter);

        setupPageIndicators();

        cardsViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updatePageIndicators(position);
            }
        });
    }

    private void setupPageIndicators() {
        dots = new ImageView[3];
        pageIndicator.removeAllViews();

        for (int i = 0; i < 3; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageResource(R.drawable.dot_inactive);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);

            pageIndicator.addView(dots[i], params);
        }

        // Set first dot as active
        if (dots.length > 0) {
            dots[0].setImageResource(R.drawable.dot_active);
        }
    }

    private void updatePageIndicators(int position) {
        for (int i = 0; i < dots.length; i++) {
            if (i == position) {
                dots[i].setImageResource(R.drawable.dot_active);
            } else {
                dots[i].setImageResource(R.drawable.dot_inactive);
            }
        }
    }


    private void setupFab() {
        FloatingActionButton mainFab = findViewById(R.id.fab_add_expense);
        mainFab.setOnClickListener(v -> showAddMenuBottomSheet());
    }

    private void showAddMenuBottomSheet() {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheet =
            new com.google.android.material.bottomsheet.BottomSheetDialog(this);

        android.view.View view = getLayoutInflater().inflate(R.layout.bottom_sheet_add_menu, null);

        // Set click listeners
        view.findViewById(R.id.option_add_expense).setOnClickListener(v -> {
            bottomSheet.dismiss();
            showAddExpenseDialog();
        });

        view.findViewById(R.id.option_add_borrowed).setOnClickListener(v -> {
            bottomSheet.dismiss();
            showAddBorrowedDialog();
        });

        view.findViewById(R.id.option_lend_money).setOnClickListener(v -> {
            bottomSheet.dismiss();
            showAddLentDialog();
        });

        bottomSheet.setContentView(view);
        bottomSheet.show();

        // Force the bottom sheet to respect the custom width and add transparency
        android.view.Window window = bottomSheet.getWindow();
        if (window != null) {
            window.setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.6), // 60% of screen width
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
            window.setGravity(android.view.Gravity.BOTTOM | android.view.Gravity.END);

            // Add 40% transparency to background (dim amount: 0.4 = 40% dark overlay)
            window.setDimAmount(0.3f);
        }
    }

    private void setupButtons() {
        // Edit income icon click
        ImageView editIncomeIcon = findViewById(R.id.edit_income_icon);
        editIncomeIcon.setOnClickListener(v -> showAddIncomeDialog());

        // Month/Year picker click listener
        selectedMonthYearText.setOnClickListener(v -> {
            MonthYearPicker.showMonthYearPicker(this, currentSelectedMonth, currentSelectedYear,
                (month, year) -> {
                    currentSelectedMonth = month;
                    currentSelectedYear = year;
                    updateMonthYearDisplay();
                    refreshDataForSelectedMonth();
                });
        });
    }

    private void showAddBorrowedDialog() {
        AddLoanDialog dialog = AddLoanDialog.newInstance("borrowed");
        dialog.show(getSupportFragmentManager(), "AddBorrowed");
    }

    private void showAddLentDialog() {
        AddLoanDialog dialog = AddLoanDialog.newInstance("lent");
        dialog.show(getSupportFragmentManager(), "AddLent");
    }

    private void setupMiniChart() {
        try {
            PieChart miniChart = findViewById(R.id.mini_pie_chart);
            miniChartManager = new MiniChartManager(miniChart);

            // Make chart clickable to open detailed chart
            miniChart.setOnClickListener(v -> {
                Intent intent = new Intent(this, CategoryChartActivity.class);
                intent.putExtra("selected_month", currentSelectedMonth);
                intent.putExtra("selected_year", currentSelectedYear);
                startActivity(intent);
            });
        } catch (Exception e) {
            // Handle chart setup errors gracefully
        }
    }

    private void showAddIncomeDialog() {
        AddIncomeDialog dialog = new AddIncomeDialog();
        dialog.show(getSupportFragmentManager(), "AddIncome");
    }

    private void observeData() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, currentSelectedYear);
        cal.set(Calendar.MONTH, currentSelectedMonth - 1); // Calendar months are 0-based
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = cal.getTime();

        viewModel.getMonthlyExpenses(startDate, endDate).observe(this, expenses -> {
            if (expenses != null) {
                updateExpenseList(expenses);
            }
        });

        viewModel.getMonthlyTotal(startDate, endDate).observe(this, totalExpenses -> {
            double expenseAmount = totalExpenses != null ? totalExpenses : 0.0;
            expensesText.setText(String.format("₹%.0f", expenseAmount));
            updateBalanceDisplay(expenseAmount, startDate, endDate);
        });

        incomeViewModel.getIncomeByMonth(startDate, endDate).observe(this, income -> {
            if (income != null) {
                incomeText.setText(String.format("₹%.0f", income.getAmount()));
            } else {
                incomeText.setText("₹0");
            }
        });
    }

    private void updateBalanceDisplay(double totalExpenses, Date startDate, Date endDate) {
        // Calculate remaining balance
        incomeViewModel.getIncomeByMonth(startDate, endDate).observe(this, income -> {
            double incomeAmount = (income != null) ? income.getAmount() : 0.0;
            double remainingBalance = incomeAmount - totalExpenses;
            balanceText.setText(String.format("₹%.0f", remainingBalance));

            // Update mini chart
            if (miniChartManager != null) {
                miniChartManager.updateChart(incomeAmount, totalExpenses);
            }
        });
    }


    private void showAddExpenseDialog() {
        AddExpenseDialog dialog = new AddExpenseDialog();
        dialog.show(getSupportFragmentManager(), "AddExpense");
    }

    private void showEditExpenseDialog(Expense expense) {
        EditExpenseDialog dialog = EditExpenseDialog.newInstance(expense);
        dialog.setOnExpenseUpdatedListener(new EditExpenseDialog.OnExpenseUpdatedListener() {
            @Override
            public void onExpenseUpdated() {
                // Refresh data after update
                refreshExpenseData();
            }

            @Override
            public void onExpenseDeleted() {
                // Refresh data after deletion
                refreshExpenseData();
            }
        });
        dialog.show(getSupportFragmentManager(), "EditExpense");
    }

    private void refreshExpenseData() {
        // Trigger observers to refresh data for selected month/year
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, currentSelectedYear);
        cal.set(Calendar.MONTH, currentSelectedMonth - 1); // Calendar months are 0-based
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = cal.getTime();

        if (showingAllExpenses) {
            // Load all expenses for selected month (not all-time)
            viewModel.getMonthlyExpenses(startDate, endDate).observe(this, expenses -> {
                if (expenses != null) {
                    expenseAdapter.setExpenses(expenses); // Show all expenses for selected month
                }
            });
        } else {
            viewModel.getMonthlyExpenses(startDate, endDate).observe(this, expenses -> {
                if (expenses != null) {
                    updateExpenseList(expenses);
                }
            });
        }
    }

    private void showAllExpenses() {
        showingAllExpenses = !showingAllExpenses;

        if (showingAllExpenses) {
            // FULLSCREEN MODE - Hide header, page indicators, and expand content
            appBarLayout.setVisibility(android.view.View.GONE);
            pageIndicator.setVisibility(android.view.View.GONE);

            // Remove scrolling behavior so content uses full screen
            androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams params =
                (androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams) mainContent.getLayoutParams();
            params.setBehavior(null);
            mainContent.setLayoutParams(params);

            // Load all expenses for selected month (not all-time)
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, currentSelectedYear);
            cal.set(Calendar.MONTH, currentSelectedMonth - 1); // Calendar months are 0-based
            cal.set(Calendar.DAY_OF_MONTH, 1);
            Date startDate = cal.getTime();
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date endDate = cal.getTime();

            viewModel.getMonthlyExpenses(startDate, endDate).observe(this, expenses -> {
                if (expenses != null) {
                    expenseAdapter.setExpenses(expenses); // Show all expenses for selected month
                }
            });
        } else {
            // NORMAL MODE - Show header, page indicators, and restore scrolling behavior
            appBarLayout.setVisibility(android.view.View.VISIBLE);
            pageIndicator.setVisibility(android.view.View.VISIBLE);

            // Restore scrolling behavior
            androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams params =
                (androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams) mainContent.getLayoutParams();
            params.setBehavior(new com.google.android.material.appbar.AppBarLayout.ScrollingViewBehavior());
            mainContent.setLayoutParams(params);

            // Reload recent expenses for selected month/year (trigger existing observer)
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, currentSelectedYear);
            cal.set(Calendar.MONTH, currentSelectedMonth - 1); // Calendar months are 0-based
            cal.set(Calendar.DAY_OF_MONTH, 1);
            Date startDate = cal.getTime();
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date endDate = cal.getTime();

            viewModel.getMonthlyExpenses(startDate, endDate).observe(this, expenses -> {
                if (expenses != null) {
                    updateExpenseList(expenses);
                }
            });
        }
    }

    private void updateExpenseList(List<Expense> expenses) {
        if (showingAllExpenses) {
            // Show all expenses
            expenseAdapter.setExpenses(expenses);
        } else {
            // Show only the last 5 expenses on home screen
            List<Expense> recentExpenses = expenses.size() > 5 ?
                expenses.subList(0, 5) : expenses;
            expenseAdapter.setExpenses(recentExpenses);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_income) {
            showAddIncomeDialog();
            return true;
        } else if (id == R.id.action_statistics) {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_budget_management) {
            Intent intent = new Intent(this, BudgetManagementActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_export_data) {
            exportData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (showingAllExpenses) {
            // If in "View All" mode, go back to recent view
            showAllExpenses(); // This will toggle back to recent mode
        } else {
            // Normal back behavior
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Already on dashboard, just close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_export) {
            exportData();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_import) {
            openFilePicker();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_budget) {
            Intent intent = new Intent(this, BudgetManagementActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_statistics) {
            Toast.makeText(this, "Statistics - Coming Soon", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_category_chart) {
            Intent intent = new Intent(this, CategoryChartActivity.class);
            intent.putExtra("selected_month", currentSelectedMonth);
            intent.putExtra("selected_year", currentSelectedYear);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_about) {
            showAboutDialog();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        return false;
    }

    private void showAboutDialog() {
        android.text.SpannableString message = new android.text.SpannableString(
            "Flow Pocket\nVersion 1.0\n\nPersonal Finance Tracker\n\nDeveloped by malem_mayeng\n\n© 2025 Flow Pocket"
        );

        // Apply italic style to "malem_mayeng"
        int start = message.toString().indexOf("malem_mayeng");
        int end = start + "malem_mayeng".length();
        message.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.ITALIC),
                       start, end, android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("About Flow Pocket")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show();
    }

    private void updateMonthYearDisplay() {
        String displayText = MonthYearPicker.getMonthYearDisplay(currentSelectedMonth, currentSelectedYear);
        selectedMonthYearText.setText(displayText);
    }

    private void refreshDataForSelectedMonth() {
        // Force refresh all data observers with selected month/year
        observeData();
    }

    private void exportData() {
        Toast.makeText(this, "Exporting data...", Toast.LENGTH_SHORT).show();

        DataExportUtil.exportAllDataAsZip(this)
            .thenAccept(zipFilePath -> {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Data exported successfully to: " + zipFilePath, Toast.LENGTH_LONG).show();
                });
            })
            .exceptionally(throwable -> {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Export failed: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                });
                return null;
            });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, IMPORT_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMPORT_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                importData(selectedFileUri);
            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void importData(Uri fileUri) {
        Toast.makeText(this, "Importing data...", Toast.LENGTH_SHORT).show();

        DataImportUtil.importDataFromZip(this, fileUri)
            .thenAccept(result -> {
                runOnUiThread(() -> {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                    // Refresh all data after import
                    refreshDataForSelectedMonth();
                });
            })
            .exceptionally(throwable -> {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Import failed: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                });
                return null;
            });
    }
}