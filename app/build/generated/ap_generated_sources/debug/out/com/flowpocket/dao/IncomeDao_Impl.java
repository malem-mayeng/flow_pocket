package com.flowpocket.dao;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.flowpocket.entities.Income;
import com.flowpocket.util.DateConverter;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class IncomeDao_Impl implements IncomeDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Income> __insertionAdapterOfIncome;

  private final EntityDeletionOrUpdateAdapter<Income> __deletionAdapterOfIncome;

  private final EntityDeletionOrUpdateAdapter<Income> __updateAdapterOfIncome;

  public IncomeDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfIncome = new EntityInsertionAdapter<Income>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `incomes` (`id`,`amount`,`month`,`created_at`,`updated_at`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Income value) {
        stmt.bindLong(1, value.getId());
        stmt.bindDouble(2, value.getAmount());
        final Long _tmp = DateConverter.fromDate(value.getMonth());
        if (_tmp == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindLong(3, _tmp);
        }
        final Long _tmp_1 = DateConverter.fromDate(value.getCreatedAt());
        if (_tmp_1 == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindLong(4, _tmp_1);
        }
        final Long _tmp_2 = DateConverter.fromDate(value.getUpdatedAt());
        if (_tmp_2 == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindLong(5, _tmp_2);
        }
      }
    };
    this.__deletionAdapterOfIncome = new EntityDeletionOrUpdateAdapter<Income>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `incomes` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Income value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__updateAdapterOfIncome = new EntityDeletionOrUpdateAdapter<Income>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `incomes` SET `id` = ?,`amount` = ?,`month` = ?,`created_at` = ?,`updated_at` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Income value) {
        stmt.bindLong(1, value.getId());
        stmt.bindDouble(2, value.getAmount());
        final Long _tmp = DateConverter.fromDate(value.getMonth());
        if (_tmp == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindLong(3, _tmp);
        }
        final Long _tmp_1 = DateConverter.fromDate(value.getCreatedAt());
        if (_tmp_1 == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindLong(4, _tmp_1);
        }
        final Long _tmp_2 = DateConverter.fromDate(value.getUpdatedAt());
        if (_tmp_2 == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindLong(5, _tmp_2);
        }
        stmt.bindLong(6, value.getId());
      }
    };
  }

  @Override
  public long insert(final Income income) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfIncome.insertAndReturnId(income);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public long insertIncomeSync(final Income income) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfIncome.insertAndReturnId(income);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final Income income) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfIncome.handle(income);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final Income income) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfIncome.handle(income);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<Income> getLatestIncome() {
    final String _sql = "SELECT * FROM incomes ORDER BY created_at DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"incomes"}, false, new Callable<Income>() {
      @Override
      public Income call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final Income _result;
          if(_cursor.moveToFirst()) {
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final Date _tmpMonth;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfMonth)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfMonth);
            }
            _tmpMonth = DateConverter.toDate(_tmp);
            _result = new Income(_tmpAmount,_tmpMonth);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _result.setId(_tmpId);
            final Date _tmpCreatedAt;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateConverter.toDate(_tmp_1);
            _result.setCreatedAt(_tmpCreatedAt);
            final Date _tmpUpdatedAt;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = DateConverter.toDate(_tmp_2);
            _result.setUpdatedAt(_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<Income> getIncomeByMonth(final Date startDate, final Date endDate) {
    final String _sql = "SELECT * FROM incomes WHERE month BETWEEN ? AND ? ORDER BY created_at DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    final Long _tmp = DateConverter.fromDate(startDate);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, _tmp);
    }
    _argIndex = 2;
    final Long _tmp_1 = DateConverter.fromDate(endDate);
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindLong(_argIndex, _tmp_1);
    }
    return __db.getInvalidationTracker().createLiveData(new String[]{"incomes"}, false, new Callable<Income>() {
      @Override
      public Income call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final Income _result;
          if(_cursor.moveToFirst()) {
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final Date _tmpMonth;
            final Long _tmp_2;
            if (_cursor.isNull(_cursorIndexOfMonth)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getLong(_cursorIndexOfMonth);
            }
            _tmpMonth = DateConverter.toDate(_tmp_2);
            _result = new Income(_tmpAmount,_tmpMonth);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _result.setId(_tmpId);
            final Date _tmpCreatedAt;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateConverter.toDate(_tmp_3);
            _result.setCreatedAt(_tmpCreatedAt);
            final Date _tmpUpdatedAt;
            final Long _tmp_4;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = DateConverter.toDate(_tmp_4);
            _result.setUpdatedAt(_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public List<Income> getAllIncomeSync() {
    final String _sql = "SELECT * FROM incomes ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
      final int _cursorIndexOfMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "month");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
      final List<Income> _result = new ArrayList<Income>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Income _item;
        final double _tmpAmount;
        _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
        final Date _tmpMonth;
        final Long _tmp;
        if (_cursor.isNull(_cursorIndexOfMonth)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getLong(_cursorIndexOfMonth);
        }
        _tmpMonth = DateConverter.toDate(_tmp);
        _item = new Income(_tmpAmount,_tmpMonth);
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final Date _tmpCreatedAt;
        final Long _tmp_1;
        if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getLong(_cursorIndexOfCreatedAt);
        }
        _tmpCreatedAt = DateConverter.toDate(_tmp_1);
        _item.setCreatedAt(_tmpCreatedAt);
        final Date _tmpUpdatedAt;
        final Long _tmp_2;
        if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
          _tmp_2 = null;
        } else {
          _tmp_2 = _cursor.getLong(_cursorIndexOfUpdatedAt);
        }
        _tmpUpdatedAt = DateConverter.toDate(_tmp_2);
        _item.setUpdatedAt(_tmpUpdatedAt);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
