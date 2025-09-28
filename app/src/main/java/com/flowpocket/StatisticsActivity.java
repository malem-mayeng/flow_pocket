package com.flowpocket;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.flowpocket.viewmodel.ExpenseViewModel;
import com.flowpocket.ui.SimpleExpenseAdapter;

public class StatisticsActivity extends AppCompatActivity {
    private ExpenseViewModel viewModel;
    private SimpleExpenseAdapter expenseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        setupToolbar();
        setupRecyclerView();
        observeData();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("All Expenses");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        viewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        expenseAdapter = new SimpleExpenseAdapter();

        RecyclerView recyclerView = findViewById(R.id.all_expenses_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(expenseAdapter);
    }

    private void observeData() {
        viewModel.getAllExpenses().observe(this, expenses -> {
            if (expenses != null) {
                expenseAdapter.setExpenses(expenses);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
