package com.flowpocket.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.flowpocket.database.FlowPocketDatabase;
import com.flowpocket.dao.IncomeDao;
import com.flowpocket.entities.Income;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IncomeRepository {
    private final IncomeDao incomeDao;
    private final ExecutorService executorService;

    public IncomeRepository(Application application) {
        FlowPocketDatabase database = FlowPocketDatabase.getInstance(application);
        incomeDao = database.incomeDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Income income) {
        executorService.execute(() -> incomeDao.insert(income));
    }

    public void update(Income income) {
        executorService.execute(() -> incomeDao.update(income));
    }

    public void delete(Income income) {
        executorService.execute(() -> incomeDao.delete(income));
    }

    public LiveData<Income> getLatestIncome() {
        return incomeDao.getLatestIncome();
    }

    public LiveData<Income> getIncomeByMonth(Date startDate, Date endDate) {
        return incomeDao.getIncomeByMonth(startDate, endDate);
    }
}