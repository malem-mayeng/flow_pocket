package com.flowpocket.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.flowpocket.repository.IncomeRepository;
import com.flowpocket.entities.Income;
import java.util.Date;

public class IncomeViewModel extends AndroidViewModel {
    private final IncomeRepository repository;

    public IncomeViewModel(Application application) {
        super(application);
        repository = new IncomeRepository(application);
    }

    public void insertIncome(Income income) {
        repository.insert(income);
    }

    public void updateIncome(Income income) {
        repository.update(income);
    }

    public LiveData<Income> getCurrentMonthIncome() {
        return repository.getLatestIncome();
    }

    public LiveData<Income> getIncomeByMonth(Date startDate, Date endDate) {
        return repository.getIncomeByMonth(startDate, endDate);
    }
}