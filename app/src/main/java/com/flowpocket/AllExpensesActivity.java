package com.flowpocket;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.flowpocket.ui.SimpleExpenseAdapter;
import com.flowpocket.entities.Expense;
import com.flowpocket.database.FlowPocketDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AllExpensesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SimpleExpenseAdapter adapter;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_expenses);

        setupViews();
        setupToolbar();
        loadExpenses();
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.expenses_recycler_view);
        emptyView = findViewById(R.id.empty_view);

        adapter = new SimpleExpenseAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("All Expenses");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadExpenses() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                FlowPocketDatabase db = FlowPocketDatabase.getInstance(this);
                List<Expense> expenses = db.expenseDao().getAllExpensesSync();

                runOnUiThread(() -> {
                    if (expenses != null && !expenses.isEmpty()) {
                        adapter.setExpenses(expenses);
                        recyclerView.setVisibility(android.view.View.VISIBLE);
                        emptyView.setVisibility(android.view.View.GONE);
                    } else {
                        recyclerView.setVisibility(android.view.View.GONE);
                        emptyView.setVisibility(android.view.View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    recyclerView.setVisibility(android.view.View.GONE);
                    emptyView.setVisibility(android.view.View.VISIBLE);
                    emptyView.setText("Error loading expenses");
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}