package com.flowpocket.dao;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.flowpocket.entities.ExpenseLabel;
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
public final class ExpenseLabelDao_Impl implements ExpenseLabelDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ExpenseLabel> __insertionAdapterOfExpenseLabel;

  private final EntityDeletionOrUpdateAdapter<ExpenseLabel> __deletionAdapterOfExpenseLabel;

  private final EntityDeletionOrUpdateAdapter<ExpenseLabel> __updateAdapterOfExpenseLabel;

  private final SharedSQLiteStatement __preparedStmtOfInsertDefault;

  public ExpenseLabelDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfExpenseLabel = new EntityInsertionAdapter<ExpenseLabel>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `expense_labels` (`id`,`name`,`color`,`created_at`,`updated_at`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ExpenseLabel value) {
        stmt.bindLong(1, value.getId());
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        if (value.getColor() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getColor());
        }
        final Long _tmp = DateConverter.fromDate(value.getCreatedAt());
        if (_tmp == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindLong(4, _tmp);
        }
        final Long _tmp_1 = DateConverter.fromDate(value.getUpdatedAt());
        if (_tmp_1 == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindLong(5, _tmp_1);
        }
      }
    };
    this.__deletionAdapterOfExpenseLabel = new EntityDeletionOrUpdateAdapter<ExpenseLabel>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `expense_labels` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ExpenseLabel value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__updateAdapterOfExpenseLabel = new EntityDeletionOrUpdateAdapter<ExpenseLabel>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `expense_labels` SET `id` = ?,`name` = ?,`color` = ?,`created_at` = ?,`updated_at` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ExpenseLabel value) {
        stmt.bindLong(1, value.getId());
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        if (value.getColor() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getColor());
        }
        final Long _tmp = DateConverter.fromDate(value.getCreatedAt());
        if (_tmp == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindLong(4, _tmp);
        }
        final Long _tmp_1 = DateConverter.fromDate(value.getUpdatedAt());
        if (_tmp_1 == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindLong(5, _tmp_1);
        }
        stmt.bindLong(6, value.getId());
      }
    };
    this.__preparedStmtOfInsertDefault = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "INSERT INTO expense_labels (name, color, created_at, updated_at) VALUES (?, ?, ?, ?)";
        return _query;
      }
    };
  }

  @Override
  public long insert(final ExpenseLabel label) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfExpenseLabel.insertAndReturnId(label);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public long insertLabelSync(final ExpenseLabel label) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfExpenseLabel.insertAndReturnId(label);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final ExpenseLabel label) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfExpenseLabel.handle(label);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final ExpenseLabel label) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfExpenseLabel.handle(label);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertDefault(final String name, final String color, final long createdAt,
      final long updatedAt) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfInsertDefault.acquire();
    int _argIndex = 1;
    if (name == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, name);
    }
    _argIndex = 2;
    if (color == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, color);
    }
    _argIndex = 3;
    _stmt.bindLong(_argIndex, createdAt);
    _argIndex = 4;
    _stmt.bindLong(_argIndex, updatedAt);
    __db.beginTransaction();
    try {
      _stmt.executeInsert();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfInsertDefault.release(_stmt);
    }
  }

  @Override
  public LiveData<List<ExpenseLabel>> getAllLabels() {
    final String _sql = "SELECT * FROM expense_labels";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"expense_labels"}, false, new Callable<List<ExpenseLabel>>() {
      @Override
      public List<ExpenseLabel> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<ExpenseLabel> _result = new ArrayList<ExpenseLabel>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final ExpenseLabel _item;
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpColor;
            if (_cursor.isNull(_cursorIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = _cursor.getString(_cursorIndexOfColor);
            }
            _item = new ExpenseLabel(_tmpName,_tmpColor);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final Date _tmpCreatedAt;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateConverter.toDate(_tmp);
            _item.setCreatedAt(_tmpCreatedAt);
            final Date _tmpUpdatedAt;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = DateConverter.toDate(_tmp_1);
            _item.setUpdatedAt(_tmpUpdatedAt);
            _result.add(_item);
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
  public LiveData<ExpenseLabel> getLabelById(final int id) {
    final String _sql = "SELECT * FROM expense_labels WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return __db.getInvalidationTracker().createLiveData(new String[]{"expense_labels"}, false, new Callable<ExpenseLabel>() {
      @Override
      public ExpenseLabel call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final ExpenseLabel _result;
          if(_cursor.moveToFirst()) {
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpColor;
            if (_cursor.isNull(_cursorIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = _cursor.getString(_cursorIndexOfColor);
            }
            _result = new ExpenseLabel(_tmpName,_tmpColor);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _result.setId(_tmpId);
            final Date _tmpCreatedAt;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateConverter.toDate(_tmp);
            _result.setCreatedAt(_tmpCreatedAt);
            final Date _tmpUpdatedAt;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = DateConverter.toDate(_tmp_1);
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
  public ExpenseLabel findByName(final String name) {
    final String _sql = "SELECT * FROM expense_labels WHERE name = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (name == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, name);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
      final ExpenseLabel _result;
      if(_cursor.moveToFirst()) {
        final String _tmpName;
        if (_cursor.isNull(_cursorIndexOfName)) {
          _tmpName = null;
        } else {
          _tmpName = _cursor.getString(_cursorIndexOfName);
        }
        final String _tmpColor;
        if (_cursor.isNull(_cursorIndexOfColor)) {
          _tmpColor = null;
        } else {
          _tmpColor = _cursor.getString(_cursorIndexOfColor);
        }
        _result = new ExpenseLabel(_tmpName,_tmpColor);
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _result.setId(_tmpId);
        final Date _tmpCreatedAt;
        final Long _tmp;
        if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getLong(_cursorIndexOfCreatedAt);
        }
        _tmpCreatedAt = DateConverter.toDate(_tmp);
        _result.setCreatedAt(_tmpCreatedAt);
        final Date _tmpUpdatedAt;
        final Long _tmp_1;
        if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getLong(_cursorIndexOfUpdatedAt);
        }
        _tmpUpdatedAt = DateConverter.toDate(_tmp_1);
        _result.setUpdatedAt(_tmpUpdatedAt);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int countExpensesWithLabel(final int labelId) {
    final String _sql = "SELECT COUNT(*) FROM expenses WHERE label_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, labelId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if(_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int getCount() {
    final String _sql = "SELECT COUNT(*) FROM expense_labels";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if(_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<ExpenseLabel> getAllLabelsSync() {
    final String _sql = "SELECT * FROM expense_labels";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
      final List<ExpenseLabel> _result = new ArrayList<ExpenseLabel>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ExpenseLabel _item;
        final String _tmpName;
        if (_cursor.isNull(_cursorIndexOfName)) {
          _tmpName = null;
        } else {
          _tmpName = _cursor.getString(_cursorIndexOfName);
        }
        final String _tmpColor;
        if (_cursor.isNull(_cursorIndexOfColor)) {
          _tmpColor = null;
        } else {
          _tmpColor = _cursor.getString(_cursorIndexOfColor);
        }
        _item = new ExpenseLabel(_tmpName,_tmpColor);
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final Date _tmpCreatedAt;
        final Long _tmp;
        if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getLong(_cursorIndexOfCreatedAt);
        }
        _tmpCreatedAt = DateConverter.toDate(_tmp);
        _item.setCreatedAt(_tmpCreatedAt);
        final Date _tmpUpdatedAt;
        final Long _tmp_1;
        if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getLong(_cursorIndexOfUpdatedAt);
        }
        _tmpUpdatedAt = DateConverter.toDate(_tmp_1);
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
