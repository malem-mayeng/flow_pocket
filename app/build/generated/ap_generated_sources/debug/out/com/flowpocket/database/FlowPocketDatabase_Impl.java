package com.flowpocket.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.RoomOpenHelper.ValidationResult;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import com.flowpocket.dao.ExpenseDao;
import com.flowpocket.dao.ExpenseDao_Impl;
import com.flowpocket.dao.ExpenseLabelDao;
import com.flowpocket.dao.ExpenseLabelDao_Impl;
import com.flowpocket.dao.IncomeDao;
import com.flowpocket.dao.IncomeDao_Impl;
import com.flowpocket.dao.LoanDao;
import com.flowpocket.dao.LoanDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class FlowPocketDatabase_Impl extends FlowPocketDatabase {
  private volatile ExpenseLabelDao _expenseLabelDao;

  private volatile ExpenseDao _expenseDao;

  private volatile IncomeDao _incomeDao;

  private volatile LoanDao _loanDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(4) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `expense_labels` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `color` TEXT, `created_at` INTEGER, `updated_at` INTEGER)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `expenses` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `amount` REAL NOT NULL, `date` INTEGER, `label_id` INTEGER NOT NULL, `created_at` INTEGER, `updated_at` INTEGER)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `incomes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `amount` REAL NOT NULL, `month` INTEGER, `created_at` INTEGER, `updated_at` INTEGER)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `loans` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `type` TEXT, `person_name` TEXT, `amount` REAL NOT NULL, `transaction_date` INTEGER, `due_date` INTEGER, `is_settled` INTEGER NOT NULL, `settled_date` INTEGER, `notes` TEXT, `created_at` INTEGER, `updated_at` INTEGER)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '04184bef6206f751f4fbb32655c30576')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `expense_labels`");
        _db.execSQL("DROP TABLE IF EXISTS `expenses`");
        _db.execSQL("DROP TABLE IF EXISTS `incomes`");
        _db.execSQL("DROP TABLE IF EXISTS `loans`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      public void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      public RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsExpenseLabels = new HashMap<String, TableInfo.Column>(5);
        _columnsExpenseLabels.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenseLabels.put("name", new TableInfo.Column("name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenseLabels.put("color", new TableInfo.Column("color", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenseLabels.put("created_at", new TableInfo.Column("created_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenseLabels.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExpenseLabels = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExpenseLabels = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoExpenseLabels = new TableInfo("expense_labels", _columnsExpenseLabels, _foreignKeysExpenseLabels, _indicesExpenseLabels);
        final TableInfo _existingExpenseLabels = TableInfo.read(_db, "expense_labels");
        if (! _infoExpenseLabels.equals(_existingExpenseLabels)) {
          return new RoomOpenHelper.ValidationResult(false, "expense_labels(com.flowpocket.entities.ExpenseLabel).\n"
                  + " Expected:\n" + _infoExpenseLabels + "\n"
                  + " Found:\n" + _existingExpenseLabels);
        }
        final HashMap<String, TableInfo.Column> _columnsExpenses = new HashMap<String, TableInfo.Column>(7);
        _columnsExpenses.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("name", new TableInfo.Column("name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("date", new TableInfo.Column("date", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("label_id", new TableInfo.Column("label_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("created_at", new TableInfo.Column("created_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExpenses.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExpenses = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExpenses = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoExpenses = new TableInfo("expenses", _columnsExpenses, _foreignKeysExpenses, _indicesExpenses);
        final TableInfo _existingExpenses = TableInfo.read(_db, "expenses");
        if (! _infoExpenses.equals(_existingExpenses)) {
          return new RoomOpenHelper.ValidationResult(false, "expenses(com.flowpocket.entities.Expense).\n"
                  + " Expected:\n" + _infoExpenses + "\n"
                  + " Found:\n" + _existingExpenses);
        }
        final HashMap<String, TableInfo.Column> _columnsIncomes = new HashMap<String, TableInfo.Column>(5);
        _columnsIncomes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("month", new TableInfo.Column("month", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("created_at", new TableInfo.Column("created_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncomes.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIncomes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesIncomes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoIncomes = new TableInfo("incomes", _columnsIncomes, _foreignKeysIncomes, _indicesIncomes);
        final TableInfo _existingIncomes = TableInfo.read(_db, "incomes");
        if (! _infoIncomes.equals(_existingIncomes)) {
          return new RoomOpenHelper.ValidationResult(false, "incomes(com.flowpocket.entities.Income).\n"
                  + " Expected:\n" + _infoIncomes + "\n"
                  + " Found:\n" + _existingIncomes);
        }
        final HashMap<String, TableInfo.Column> _columnsLoans = new HashMap<String, TableInfo.Column>(11);
        _columnsLoans.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLoans.put("type", new TableInfo.Column("type", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLoans.put("person_name", new TableInfo.Column("person_name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLoans.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLoans.put("transaction_date", new TableInfo.Column("transaction_date", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLoans.put("due_date", new TableInfo.Column("due_date", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLoans.put("is_settled", new TableInfo.Column("is_settled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLoans.put("settled_date", new TableInfo.Column("settled_date", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLoans.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLoans.put("created_at", new TableInfo.Column("created_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLoans.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLoans = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLoans = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLoans = new TableInfo("loans", _columnsLoans, _foreignKeysLoans, _indicesLoans);
        final TableInfo _existingLoans = TableInfo.read(_db, "loans");
        if (! _infoLoans.equals(_existingLoans)) {
          return new RoomOpenHelper.ValidationResult(false, "loans(com.flowpocket.entities.Loan).\n"
                  + " Expected:\n" + _infoLoans + "\n"
                  + " Found:\n" + _existingLoans);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "04184bef6206f751f4fbb32655c30576", "28de1cfed040b3f4b3b62c1a3145b643");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "expense_labels","expenses","incomes","loans");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `expense_labels`");
      _db.execSQL("DELETE FROM `expenses`");
      _db.execSQL("DELETE FROM `incomes`");
      _db.execSQL("DELETE FROM `loans`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ExpenseLabelDao.class, ExpenseLabelDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ExpenseDao.class, ExpenseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IncomeDao.class, IncomeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LoanDao.class, LoanDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  public List<Migration> getAutoMigrations(
      @NonNull Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecsMap) {
    return Arrays.asList();
  }

  @Override
  public ExpenseLabelDao expenseLabelDao() {
    if (_expenseLabelDao != null) {
      return _expenseLabelDao;
    } else {
      synchronized(this) {
        if(_expenseLabelDao == null) {
          _expenseLabelDao = new ExpenseLabelDao_Impl(this);
        }
        return _expenseLabelDao;
      }
    }
  }

  @Override
  public ExpenseDao expenseDao() {
    if (_expenseDao != null) {
      return _expenseDao;
    } else {
      synchronized(this) {
        if(_expenseDao == null) {
          _expenseDao = new ExpenseDao_Impl(this);
        }
        return _expenseDao;
      }
    }
  }

  @Override
  public IncomeDao incomeDao() {
    if (_incomeDao != null) {
      return _incomeDao;
    } else {
      synchronized(this) {
        if(_incomeDao == null) {
          _incomeDao = new IncomeDao_Impl(this);
        }
        return _incomeDao;
      }
    }
  }

  @Override
  public LoanDao loanDao() {
    if (_loanDao != null) {
      return _loanDao;
    } else {
      synchronized(this) {
        if(_loanDao == null) {
          _loanDao = new LoanDao_Impl(this);
        }
        return _loanDao;
      }
    }
  }
}
