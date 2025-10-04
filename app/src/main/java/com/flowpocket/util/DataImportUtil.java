package com.flowpocket.util;

import android.content.Context;
import android.net.Uri;
import com.flowpocket.database.FlowPocketDatabase;
import com.flowpocket.entities.Expense;
import com.flowpocket.entities.ExpenseLabel;
import com.flowpocket.entities.Income;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DataImportUtil {
    private static final String TAG = "DataImportUtil";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static CompletableFuture<String> importDataFromZip(Context context, Uri zipFileUri) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                FlowPocketDatabase db = FlowPocketDatabase.getInstance(context);

                // Clear existing data (optional - you might want to ask user first)
                // db.clearAllTables();

                int importedExpenses = 0;
                int importedIncome = 0;
                int importedLabels = 0;

                try (InputStream inputStream = context.getContentResolver().openInputStream(zipFileUri);
                     ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

                    ZipEntry entry;
                    while ((entry = zipInputStream.getNextEntry()) != null) {
                        String fileName = entry.getName();

                        if (fileName.equals("categories.json")) {
                            String content = readZipEntryContent(zipInputStream);
                            importedLabels = importLabelsFromJson(db, content);
                        } else if (fileName.equals("expenses.json")) {
                            String content = readZipEntryContent(zipInputStream);
                            importedExpenses = importExpensesFromJson(db, content);
                        } else if (fileName.equals("income.json")) {
                            String content = readZipEntryContent(zipInputStream);
                            importedIncome = importIncomeFromJson(db, content);
                        }

                        zipInputStream.closeEntry();
                    }
                }

                return String.format("Import completed successfully!\n" +
                    "• %d categories imported\n" +
                    "• %d expenses imported\n" +
                    "• %d income records imported",
                    importedLabels, importedExpenses, importedIncome);

            } catch (Exception e) {
                throw new RuntimeException("Import failed: " + e.getMessage(), e);
            }
        });
    }

    private static String readZipEntryContent(ZipInputStream zipInputStream) throws Exception {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line);
        }
        return content.toString();
    }

    private static int importLabelsFromJson(FlowPocketDatabase db, String jsonContent) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonContent);
        JSONArray labelsArray = jsonObject.getJSONArray("categories");

        int count = 0;
        for (int i = 0; i < labelsArray.length(); i++) {
            JSONObject labelObj = labelsArray.getJSONObject(i);

            ExpenseLabel label = new ExpenseLabel(
                labelObj.getString("name"),
                labelObj.getString("color")
            );

            // Set dates
            try {
                Date createdAt = DATE_FORMAT.parse(labelObj.getString("createdAt"));
                Date updatedAt = DATE_FORMAT.parse(labelObj.getString("updatedAt"));
                label.setCreatedAt(createdAt);
                label.setUpdatedAt(updatedAt);
            } catch (Exception e) {
                // Use current date if parsing fails
                Date now = new Date();
                label.setCreatedAt(now);
                label.setUpdatedAt(now);
            }

            db.expenseLabelDao().insertLabelSync(label);
            count++;
        }

        return count;
    }

    private static int importExpensesFromJson(FlowPocketDatabase db, String jsonContent) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonContent);
        JSONArray expensesArray = jsonObject.getJSONArray("expenses");

        int count = 0;
        for (int i = 0; i < expensesArray.length(); i++) {
            JSONObject expenseObj = expensesArray.getJSONObject(i);

            // Parse dates first
            Date date, createdAt, updatedAt;
            try {
                date = DATE_FORMAT.parse(expenseObj.getString("date"));
                createdAt = DATE_FORMAT.parse(expenseObj.getString("createdAt"));
                updatedAt = DATE_FORMAT.parse(expenseObj.getString("updatedAt"));
            } catch (Exception e) {
                // Use current date if parsing fails
                Date now = new Date();
                date = now;
                createdAt = now;
                updatedAt = now;
            }

            Expense expense = new Expense(
                expenseObj.getString("name"),
                expenseObj.getDouble("amount"),
                date,
                expenseObj.getInt("labelId")
            );

            expense.setCreatedAt(createdAt);
            expense.setUpdatedAt(updatedAt);

            db.expenseDao().insertExpenseSync(expense);
            count++;
        }

        return count;
    }

    private static int importIncomeFromJson(FlowPocketDatabase db, String jsonContent) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonContent);
        JSONArray incomeArray = jsonObject.getJSONArray("income");

        int count = 0;
        for (int i = 0; i < incomeArray.length(); i++) {
            JSONObject incomeObj = incomeArray.getJSONObject(i);

            // Parse dates first
            Date month, createdAt, updatedAt;
            try {
                month = DATE_FORMAT.parse(incomeObj.getString("month"));
                createdAt = DATE_FORMAT.parse(incomeObj.getString("createdAt"));
                updatedAt = DATE_FORMAT.parse(incomeObj.getString("updatedAt"));
            } catch (Exception e) {
                // Use current date if parsing fails
                Date now = new Date();
                month = now;
                createdAt = now;
                updatedAt = now;
            }

            Income income = new Income(
                incomeObj.getDouble("amount"),
                month
            );

            income.setCreatedAt(createdAt);
            income.setUpdatedAt(updatedAt);

            db.incomeDao().insertIncomeSync(income);
            count++;
        }

        return count;
    }
}