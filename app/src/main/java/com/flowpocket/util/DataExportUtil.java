package com.flowpocket.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;
import com.flowpocket.database.FlowPocketDatabase;
import com.flowpocket.entities.Expense;
import com.flowpocket.entities.ExpenseLabel;
import com.flowpocket.entities.Income;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DataExportUtil {
    private static final String TAG = "DataExportUtil";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

    public static CompletableFuture<String> exportAllDataAsZip(Context context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                FlowPocketDatabase db = FlowPocketDatabase.getInstance(context);

                // Create temp directory for files
                File tempDir = new File(context.getCacheDir(), "export_temp");
                if (!tempDir.exists()) {
                    tempDir.mkdirs();
                }

                // Export data to separate files
                String expensesJson = exportExpensesToJson(db);
                String incomeJson = exportIncomeToJson(db);
                String labelsJson = exportLabelsToJson(db);
                String expensesCsv = exportExpensesToCsv(db);
                String incomeCsv = exportIncomeToCsv(db);
                String labelsCsv = exportLabelsToCsv(db);

                // Write files to temp directory
                writeToFile(new File(tempDir, "expenses.json"), expensesJson);
                writeToFile(new File(tempDir, "income.json"), incomeJson);
                writeToFile(new File(tempDir, "categories.json"), labelsJson);
                writeToFile(new File(tempDir, "expenses.csv"), expensesCsv);
                writeToFile(new File(tempDir, "income.csv"), incomeCsv);
                writeToFile(new File(tempDir, "categories.csv"), labelsCsv);

                // Create summary file
                String summary = createSummary(db);
                writeToFile(new File(tempDir, "export_summary.txt"), summary);

                // Create zip file in Downloads directory using MediaStore (works on Android 10+)
                String timestamp = FILE_DATE_FORMAT.format(new Date());
                String fileName = "FlowPocket_Backup_" + timestamp + ".zip";

                File zipFile;
                String zipFilePath;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Use MediaStore for Android 10+ (API 29+)
                    ContentResolver resolver = context.getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/zip");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                    Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                    if (uri != null) {
                        // Write ZIP directly to MediaStore
                        try (java.io.OutputStream outputStream = resolver.openOutputStream(uri);
                             ZipOutputStream zos = new ZipOutputStream(outputStream)) {

                            File[] files = tempDir.listFiles();
                            if (files != null) {
                                for (File file : files) {
                                    if (file.isFile()) {
                                        addFileToZip(zos, file, file.getName());
                                    }
                                }
                            }
                        }
                        zipFilePath = "Downloads/" + fileName;
                    } else {
                        throw new IOException("Failed to create file in Downloads directory");
                    }
                } else {
                    // Fallback for older Android versions
                    File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    zipFile = new File(downloadsDir, fileName);
                    createZipFile(tempDir, zipFile);
                    zipFilePath = zipFile.getAbsolutePath();
                }

                // Clean up temp files
                deleteDirectory(tempDir);

                return zipFilePath;

            } catch (Exception e) {
                throw new RuntimeException("Export failed: " + e.getMessage(), e);
            }
        });
    }

    private static String exportExpensesToJson(FlowPocketDatabase db) throws JSONException {
        List<Expense> expenses = db.expenseDao().getAllExpensesSync();
        JSONArray jsonArray = new JSONArray();

        for (Expense expense : expenses) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", expense.getId());
            jsonObject.put("name", expense.getName());
            jsonObject.put("amount", expense.getAmount());
            jsonObject.put("date", DATE_FORMAT.format(expense.getDate()));
            jsonObject.put("labelId", expense.getLabelId());
            jsonObject.put("createdAt", DATE_FORMAT.format(expense.getCreatedAt()));
            jsonObject.put("updatedAt", DATE_FORMAT.format(expense.getUpdatedAt()));
            jsonArray.put(jsonObject);
        }

        JSONObject result = new JSONObject();
        result.put("expenses", jsonArray);
        result.put("exportDate", DATE_FORMAT.format(new Date()));
        result.put("totalRecords", expenses.size());

        return result.toString(2);
    }

    private static String exportIncomeToJson(FlowPocketDatabase db) throws JSONException {
        List<Income> incomes = db.incomeDao().getAllIncomeSync();
        JSONArray jsonArray = new JSONArray();

        for (Income income : incomes) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", income.getId());
            jsonObject.put("amount", income.getAmount());
            jsonObject.put("month", DATE_FORMAT.format(income.getMonth()));
            jsonObject.put("createdAt", DATE_FORMAT.format(income.getCreatedAt()));
            jsonObject.put("updatedAt", DATE_FORMAT.format(income.getUpdatedAt()));
            jsonArray.put(jsonObject);
        }

        JSONObject result = new JSONObject();
        result.put("income", jsonArray);
        result.put("exportDate", DATE_FORMAT.format(new Date()));
        result.put("totalRecords", incomes.size());

        return result.toString(2);
    }

    private static String exportLabelsToJson(FlowPocketDatabase db) throws JSONException {
        List<ExpenseLabel> labels = db.expenseLabelDao().getAllLabelsSync();
        JSONArray jsonArray = new JSONArray();

        for (ExpenseLabel label : labels) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", label.getId());
            jsonObject.put("name", label.getName());
            jsonObject.put("color", label.getColor());
            jsonObject.put("createdAt", DATE_FORMAT.format(label.getCreatedAt()));
            jsonObject.put("updatedAt", DATE_FORMAT.format(label.getUpdatedAt()));
            jsonArray.put(jsonObject);
        }

        JSONObject result = new JSONObject();
        result.put("categories", jsonArray);
        result.put("exportDate", DATE_FORMAT.format(new Date()));
        result.put("totalRecords", labels.size());

        return result.toString(2);
    }

    private static String exportExpensesToCsv(FlowPocketDatabase db) {
        List<Expense> expenses = db.expenseDao().getAllExpensesSync();
        StringBuilder csv = new StringBuilder();

        // Header
        csv.append("ID,Name,Amount,Date,Label ID,Created At,Updated At\n");

        // Data
        for (Expense expense : expenses) {
            csv.append(expense.getId()).append(",");
            csv.append("\"").append(expense.getName().replace("\"", "\"\"")).append("\",");
            csv.append(expense.getAmount()).append(",");
            csv.append(DATE_FORMAT.format(expense.getDate())).append(",");
            csv.append(expense.getLabelId()).append(",");
            csv.append(DATE_FORMAT.format(expense.getCreatedAt())).append(",");
            csv.append(DATE_FORMAT.format(expense.getUpdatedAt())).append("\n");
        }

        return csv.toString();
    }

    private static String exportIncomeToCsv(FlowPocketDatabase db) {
        List<Income> incomes = db.incomeDao().getAllIncomeSync();
        StringBuilder csv = new StringBuilder();

        // Header
        csv.append("ID,Amount,Month,Created At,Updated At\n");

        // Data
        for (Income income : incomes) {
            csv.append(income.getId()).append(",");
            csv.append(income.getAmount()).append(",");
            csv.append(DATE_FORMAT.format(income.getMonth())).append(",");
            csv.append(DATE_FORMAT.format(income.getCreatedAt())).append(",");
            csv.append(DATE_FORMAT.format(income.getUpdatedAt())).append("\n");
        }

        return csv.toString();
    }

    private static String exportLabelsToCsv(FlowPocketDatabase db) {
        List<ExpenseLabel> labels = db.expenseLabelDao().getAllLabelsSync();
        StringBuilder csv = new StringBuilder();

        // Header
        csv.append("ID,Name,Color,Created At,Updated At\n");

        // Data
        for (ExpenseLabel label : labels) {
            csv.append(label.getId()).append(",");
            csv.append("\"").append(label.getName().replace("\"", "\"\"")).append("\",");
            csv.append(label.getColor()).append(",");
            csv.append(DATE_FORMAT.format(label.getCreatedAt())).append(",");
            csv.append(DATE_FORMAT.format(label.getUpdatedAt())).append("\n");
        }

        return csv.toString();
    }

    private static String createSummary(FlowPocketDatabase db) {
        List<Expense> expenses = db.expenseDao().getAllExpensesSync();
        List<Income> incomes = db.incomeDao().getAllIncomeSync();
        List<ExpenseLabel> labels = db.expenseLabelDao().getAllLabelsSync();

        StringBuilder summary = new StringBuilder();
        summary.append("Flow Pocket Data Export Summary\n");
        summary.append("Generated on: ").append(DATE_FORMAT.format(new Date())).append("\n\n");

        summary.append("Data Statistics:\n");
        summary.append("- Total Expenses: ").append(expenses.size()).append("\n");
        summary.append("- Total Income Records: ").append(incomes.size()).append("\n");
        summary.append("- Total Categories: ").append(labels.size()).append("\n\n");

        // Calculate totals
        double totalExpenses = expenses.stream().mapToDouble(Expense::getAmount).sum();
        double totalIncome = incomes.stream().mapToDouble(Income::getAmount).sum();

        summary.append("Financial Summary:\n");
        summary.append("- Total Expenses: ₹").append(String.format("%.2f", totalExpenses)).append("\n");
        summary.append("- Total Income: ₹").append(String.format("%.2f", totalIncome)).append("\n");
        summary.append("- Net Balance: ₹").append(String.format("%.2f", totalIncome - totalExpenses)).append("\n\n");

        summary.append("Files Included:\n");
        summary.append("- expenses.json (JSON format)\n");
        summary.append("- income.json (JSON format)\n");
        summary.append("- categories.json (JSON format)\n");
        summary.append("- expenses.csv (CSV format)\n");
        summary.append("- income.csv (CSV format)\n");
        summary.append("- categories.csv (CSV format)\n");
        summary.append("- export_summary.txt (this file)\n");

        return summary.toString();
    }

    private static void writeToFile(File file, String content) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }

    private static void createZipFile(File sourceDir, File zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            File[] files = sourceDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        addFileToZip(zos, file, file.getName());
                    }
                }
            }
        }
    }

    private static void addFileToZip(ZipOutputStream zos, File file, String fileName) throws IOException {
        zos.putNextEntry(new ZipEntry(fileName));

        byte[] buffer = new byte[1024];
        try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
        }
        zos.closeEntry();
    }

    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}