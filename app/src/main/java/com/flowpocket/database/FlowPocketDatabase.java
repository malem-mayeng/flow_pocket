package com.flowpocket.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.flowpocket.entities.*;
import com.flowpocket.dao.*;
import com.flowpocket.util.DateConverter;
import java.util.concurrent.Executors;

@Database(
    entities = {ExpenseLabel.class, Expense.class, Income.class},
    version = 2,
    exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class FlowPocketDatabase extends RoomDatabase {
    private static FlowPocketDatabase instance;

    public abstract ExpenseLabelDao expenseLabelDao();
    public abstract ExpenseDao expenseDao();
    public abstract IncomeDao incomeDao();
    // public abstract MonthlyBudgetDao monthlyBudgetDao();

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Insert default expense labels directly via SQL
            long currentTime = System.currentTimeMillis();

            db.execSQL("INSERT INTO expense_labels (name, color, created_at, updated_at) VALUES (?, ?, ?, ?)",
                new Object[]{"Bills", "#2196F3", currentTime, currentTime});
            db.execSQL("INSERT INTO expense_labels (name, color, created_at, updated_at) VALUES (?, ?, ?, ?)",
                new Object[]{"Food", "#FF9800", currentTime, currentTime});
            db.execSQL("INSERT INTO expense_labels (name, color, created_at, updated_at) VALUES (?, ?, ?, ?)",
                new Object[]{"Transport", "#4CAF50", currentTime, currentTime});
            db.execSQL("INSERT INTO expense_labels (name, color, created_at, updated_at) VALUES (?, ?, ?, ?)",
                new Object[]{"Entertainment", "#9C27B0", currentTime, currentTime});
            db.execSQL("INSERT INTO expense_labels (name, color, created_at, updated_at) VALUES (?, ?, ?, ?)",
                new Object[]{"Shopping", "#F44336", currentTime, currentTime});
            db.execSQL("INSERT INTO expense_labels (name, color, created_at, updated_at) VALUES (?, ?, ?, ?)",
                new Object[]{"Healthcare", "#00BCD4", currentTime, currentTime});
            db.execSQL("INSERT INTO expense_labels (name, color, created_at, updated_at) VALUES (?, ?, ?, ?)",
                new Object[]{"Others", "#607D8B", currentTime, currentTime});
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create monthly_budgets table
            database.execSQL("CREATE TABLE IF NOT EXISTS monthly_budgets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "category_id INTEGER NOT NULL, " +
                "month INTEGER NOT NULL, " +
                "year INTEGER NOT NULL, " +
                "budget_amount REAL NOT NULL, " +
                "spent_amount REAL NOT NULL DEFAULT 0, " +
                "is_active INTEGER NOT NULL DEFAULT 1, " +
                "created_at INTEGER NOT NULL, " +
                "updated_at INTEGER NOT NULL, " +
                "FOREIGN KEY(category_id) REFERENCES expense_labels(id) ON DELETE CASCADE)");

            // Create index for better performance on foreign key
            database.execSQL("CREATE INDEX IF NOT EXISTS index_monthly_budgets_category_id ON monthly_budgets(category_id)");

            // Create composite index for month/year queries
            database.execSQL("CREATE INDEX IF NOT EXISTS index_monthly_budgets_month_year ON monthly_budgets(month, year)");
        }
    };

    public static synchronized FlowPocketDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                FlowPocketDatabase.class,
                "flow_pocket_db"
            ).fallbackToDestructiveMigration()
            .allowMainThreadQueries() // Allow main thread for debugging
            .addCallback(roomCallback)
            .build();

            // Ensure default categories exist
            ensureDefaultCategories();
        }
        return instance;
    }

    private static void ensureDefaultCategories() {
        if (instance != null) {
            // Check if categories exist, if not, insert them
            new Thread(() -> {
                int count = instance.expenseLabelDao().getCount();
                if (count == 0) {
                    long currentTime = System.currentTimeMillis();
                    instance.expenseLabelDao().insertDefault("Bills", "#2196F3", currentTime, currentTime);
                    instance.expenseLabelDao().insertDefault("Food", "#FF9800", currentTime, currentTime);
                    instance.expenseLabelDao().insertDefault("Transport", "#4CAF50", currentTime, currentTime);
                    instance.expenseLabelDao().insertDefault("Entertainment", "#9C27B0", currentTime, currentTime);
                    instance.expenseLabelDao().insertDefault("Shopping", "#F44336", currentTime, currentTime);
                    instance.expenseLabelDao().insertDefault("Healthcare", "#00BCD4", currentTime, currentTime);
                    instance.expenseLabelDao().insertDefault("Others", "#607D8B", currentTime, currentTime);
                    android.util.Log.d("FlowPocketDB", "Default categories inserted");
                }
            }).start();
        }
    }
}