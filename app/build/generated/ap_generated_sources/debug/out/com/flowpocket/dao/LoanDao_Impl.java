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
import com.flowpocket.entities.Loan;
import com.flowpocket.util.DateConverter;
import java.lang.Class;
import java.lang.Double;
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
public final class LoanDao_Impl implements LoanDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Loan> __insertionAdapterOfLoan;

  private final EntityDeletionOrUpdateAdapter<Loan> __deletionAdapterOfLoan;

  private final EntityDeletionOrUpdateAdapter<Loan> __updateAdapterOfLoan;

  public LoanDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLoan = new EntityInsertionAdapter<Loan>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `loans` (`id`,`type`,`person_name`,`amount`,`transaction_date`,`due_date`,`is_settled`,`settled_date`,`notes`,`created_at`,`updated_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Loan value) {
        stmt.bindLong(1, value.getId());
        if (value.getType() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getType());
        }
        if (value.getPersonName() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getPersonName());
        }
        stmt.bindDouble(4, value.getAmount());
        final Long _tmp = DateConverter.fromDate(value.getTransactionDate());
        if (_tmp == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindLong(5, _tmp);
        }
        final Long _tmp_1 = DateConverter.fromDate(value.getDueDate());
        if (_tmp_1 == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindLong(6, _tmp_1);
        }
        final int _tmp_2 = value.isSettled() ? 1 : 0;
        stmt.bindLong(7, _tmp_2);
        final Long _tmp_3 = DateConverter.fromDate(value.getSettledDate());
        if (_tmp_3 == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindLong(8, _tmp_3);
        }
        if (value.getNotes() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getNotes());
        }
        final Long _tmp_4 = DateConverter.fromDate(value.getCreatedAt());
        if (_tmp_4 == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindLong(10, _tmp_4);
        }
        final Long _tmp_5 = DateConverter.fromDate(value.getUpdatedAt());
        if (_tmp_5 == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindLong(11, _tmp_5);
        }
      }
    };
    this.__deletionAdapterOfLoan = new EntityDeletionOrUpdateAdapter<Loan>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `loans` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Loan value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__updateAdapterOfLoan = new EntityDeletionOrUpdateAdapter<Loan>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `loans` SET `id` = ?,`type` = ?,`person_name` = ?,`amount` = ?,`transaction_date` = ?,`due_date` = ?,`is_settled` = ?,`settled_date` = ?,`notes` = ?,`created_at` = ?,`updated_at` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Loan value) {
        stmt.bindLong(1, value.getId());
        if (value.getType() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getType());
        }
        if (value.getPersonName() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getPersonName());
        }
        stmt.bindDouble(4, value.getAmount());
        final Long _tmp = DateConverter.fromDate(value.getTransactionDate());
        if (_tmp == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindLong(5, _tmp);
        }
        final Long _tmp_1 = DateConverter.fromDate(value.getDueDate());
        if (_tmp_1 == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindLong(6, _tmp_1);
        }
        final int _tmp_2 = value.isSettled() ? 1 : 0;
        stmt.bindLong(7, _tmp_2);
        final Long _tmp_3 = DateConverter.fromDate(value.getSettledDate());
        if (_tmp_3 == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindLong(8, _tmp_3);
        }
        if (value.getNotes() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getNotes());
        }
        final Long _tmp_4 = DateConverter.fromDate(value.getCreatedAt());
        if (_tmp_4 == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindLong(10, _tmp_4);
        }
        final Long _tmp_5 = DateConverter.fromDate(value.getUpdatedAt());
        if (_tmp_5 == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindLong(11, _tmp_5);
        }
        stmt.bindLong(12, value.getId());
      }
    };
  }

  @Override
  public long insert(final Loan loan) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfLoan.insertAndReturnId(loan);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public long insertLoanSync(final Loan loan) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfLoan.insertAndReturnId(loan);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final Loan loan) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfLoan.handle(loan);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final Loan loan) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfLoan.handle(loan);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<Loan>> getAllLoans() {
    final String _sql = "SELECT * FROM loans ORDER BY is_settled ASC, transaction_date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"loans"}, false, new Callable<List<Loan>>() {
      @Override
      public List<Loan> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfPersonName = CursorUtil.getColumnIndexOrThrow(_cursor, "person_name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTransactionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "transaction_date");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfIsSettled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_settled");
          final int _cursorIndexOfSettledDate = CursorUtil.getColumnIndexOrThrow(_cursor, "settled_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<Loan> _result = new ArrayList<Loan>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Loan _item;
            final String _tmpType;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _cursor.getString(_cursorIndexOfType);
            }
            final String _tmpPersonName;
            if (_cursor.isNull(_cursorIndexOfPersonName)) {
              _tmpPersonName = null;
            } else {
              _tmpPersonName = _cursor.getString(_cursorIndexOfPersonName);
            }
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final Date _tmpTransactionDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfTransactionDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfTransactionDate);
            }
            _tmpTransactionDate = DateConverter.toDate(_tmp);
            _item = new Loan(_tmpType,_tmpPersonName,_tmpAmount,_tmpTransactionDate);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final Date _tmpDueDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfDueDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfDueDate);
            }
            _tmpDueDate = DateConverter.toDate(_tmp_1);
            _item.setDueDate(_tmpDueDate);
            final boolean _tmpIsSettled;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsSettled);
            _tmpIsSettled = _tmp_2 != 0;
            _item.setSettled(_tmpIsSettled);
            final Date _tmpSettledDate;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfSettledDate)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfSettledDate);
            }
            _tmpSettledDate = DateConverter.toDate(_tmp_3);
            _item.setSettledDate(_tmpSettledDate);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item.setNotes(_tmpNotes);
            final Date _tmpCreatedAt;
            final Long _tmp_4;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateConverter.toDate(_tmp_4);
            _item.setCreatedAt(_tmpCreatedAt);
            final Date _tmpUpdatedAt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = DateConverter.toDate(_tmp_5);
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
  public LiveData<List<Loan>> getLoansByType(final String type) {
    final String _sql = "SELECT * FROM loans WHERE type = ? ORDER BY is_settled ASC, transaction_date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (type == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, type);
    }
    return __db.getInvalidationTracker().createLiveData(new String[]{"loans"}, false, new Callable<List<Loan>>() {
      @Override
      public List<Loan> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfPersonName = CursorUtil.getColumnIndexOrThrow(_cursor, "person_name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTransactionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "transaction_date");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfIsSettled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_settled");
          final int _cursorIndexOfSettledDate = CursorUtil.getColumnIndexOrThrow(_cursor, "settled_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<Loan> _result = new ArrayList<Loan>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Loan _item;
            final String _tmpType;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _cursor.getString(_cursorIndexOfType);
            }
            final String _tmpPersonName;
            if (_cursor.isNull(_cursorIndexOfPersonName)) {
              _tmpPersonName = null;
            } else {
              _tmpPersonName = _cursor.getString(_cursorIndexOfPersonName);
            }
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final Date _tmpTransactionDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfTransactionDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfTransactionDate);
            }
            _tmpTransactionDate = DateConverter.toDate(_tmp);
            _item = new Loan(_tmpType,_tmpPersonName,_tmpAmount,_tmpTransactionDate);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final Date _tmpDueDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfDueDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfDueDate);
            }
            _tmpDueDate = DateConverter.toDate(_tmp_1);
            _item.setDueDate(_tmpDueDate);
            final boolean _tmpIsSettled;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsSettled);
            _tmpIsSettled = _tmp_2 != 0;
            _item.setSettled(_tmpIsSettled);
            final Date _tmpSettledDate;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfSettledDate)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfSettledDate);
            }
            _tmpSettledDate = DateConverter.toDate(_tmp_3);
            _item.setSettledDate(_tmpSettledDate);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item.setNotes(_tmpNotes);
            final Date _tmpCreatedAt;
            final Long _tmp_4;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateConverter.toDate(_tmp_4);
            _item.setCreatedAt(_tmpCreatedAt);
            final Date _tmpUpdatedAt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = DateConverter.toDate(_tmp_5);
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
  public LiveData<List<Loan>> getBorrowedLoans() {
    final String _sql = "SELECT * FROM loans WHERE type = 'borrowed' ORDER BY is_settled ASC, transaction_date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"loans"}, false, new Callable<List<Loan>>() {
      @Override
      public List<Loan> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfPersonName = CursorUtil.getColumnIndexOrThrow(_cursor, "person_name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTransactionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "transaction_date");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfIsSettled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_settled");
          final int _cursorIndexOfSettledDate = CursorUtil.getColumnIndexOrThrow(_cursor, "settled_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<Loan> _result = new ArrayList<Loan>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Loan _item;
            final String _tmpType;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _cursor.getString(_cursorIndexOfType);
            }
            final String _tmpPersonName;
            if (_cursor.isNull(_cursorIndexOfPersonName)) {
              _tmpPersonName = null;
            } else {
              _tmpPersonName = _cursor.getString(_cursorIndexOfPersonName);
            }
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final Date _tmpTransactionDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfTransactionDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfTransactionDate);
            }
            _tmpTransactionDate = DateConverter.toDate(_tmp);
            _item = new Loan(_tmpType,_tmpPersonName,_tmpAmount,_tmpTransactionDate);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final Date _tmpDueDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfDueDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfDueDate);
            }
            _tmpDueDate = DateConverter.toDate(_tmp_1);
            _item.setDueDate(_tmpDueDate);
            final boolean _tmpIsSettled;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsSettled);
            _tmpIsSettled = _tmp_2 != 0;
            _item.setSettled(_tmpIsSettled);
            final Date _tmpSettledDate;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfSettledDate)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfSettledDate);
            }
            _tmpSettledDate = DateConverter.toDate(_tmp_3);
            _item.setSettledDate(_tmpSettledDate);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item.setNotes(_tmpNotes);
            final Date _tmpCreatedAt;
            final Long _tmp_4;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateConverter.toDate(_tmp_4);
            _item.setCreatedAt(_tmpCreatedAt);
            final Date _tmpUpdatedAt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = DateConverter.toDate(_tmp_5);
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
  public LiveData<List<Loan>> getLentLoans() {
    final String _sql = "SELECT * FROM loans WHERE type = 'lent' ORDER BY is_settled ASC, transaction_date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"loans"}, false, new Callable<List<Loan>>() {
      @Override
      public List<Loan> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfPersonName = CursorUtil.getColumnIndexOrThrow(_cursor, "person_name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTransactionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "transaction_date");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfIsSettled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_settled");
          final int _cursorIndexOfSettledDate = CursorUtil.getColumnIndexOrThrow(_cursor, "settled_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<Loan> _result = new ArrayList<Loan>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Loan _item;
            final String _tmpType;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _cursor.getString(_cursorIndexOfType);
            }
            final String _tmpPersonName;
            if (_cursor.isNull(_cursorIndexOfPersonName)) {
              _tmpPersonName = null;
            } else {
              _tmpPersonName = _cursor.getString(_cursorIndexOfPersonName);
            }
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final Date _tmpTransactionDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfTransactionDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfTransactionDate);
            }
            _tmpTransactionDate = DateConverter.toDate(_tmp);
            _item = new Loan(_tmpType,_tmpPersonName,_tmpAmount,_tmpTransactionDate);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final Date _tmpDueDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfDueDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfDueDate);
            }
            _tmpDueDate = DateConverter.toDate(_tmp_1);
            _item.setDueDate(_tmpDueDate);
            final boolean _tmpIsSettled;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsSettled);
            _tmpIsSettled = _tmp_2 != 0;
            _item.setSettled(_tmpIsSettled);
            final Date _tmpSettledDate;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfSettledDate)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfSettledDate);
            }
            _tmpSettledDate = DateConverter.toDate(_tmp_3);
            _item.setSettledDate(_tmpSettledDate);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item.setNotes(_tmpNotes);
            final Date _tmpCreatedAt;
            final Long _tmp_4;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateConverter.toDate(_tmp_4);
            _item.setCreatedAt(_tmpCreatedAt);
            final Date _tmpUpdatedAt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = DateConverter.toDate(_tmp_5);
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
  public LiveData<List<Loan>> getPendingLoans() {
    final String _sql = "SELECT * FROM loans WHERE is_settled = 0 ORDER BY transaction_date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"loans"}, false, new Callable<List<Loan>>() {
      @Override
      public List<Loan> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfPersonName = CursorUtil.getColumnIndexOrThrow(_cursor, "person_name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTransactionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "transaction_date");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfIsSettled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_settled");
          final int _cursorIndexOfSettledDate = CursorUtil.getColumnIndexOrThrow(_cursor, "settled_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<Loan> _result = new ArrayList<Loan>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Loan _item;
            final String _tmpType;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _cursor.getString(_cursorIndexOfType);
            }
            final String _tmpPersonName;
            if (_cursor.isNull(_cursorIndexOfPersonName)) {
              _tmpPersonName = null;
            } else {
              _tmpPersonName = _cursor.getString(_cursorIndexOfPersonName);
            }
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final Date _tmpTransactionDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfTransactionDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfTransactionDate);
            }
            _tmpTransactionDate = DateConverter.toDate(_tmp);
            _item = new Loan(_tmpType,_tmpPersonName,_tmpAmount,_tmpTransactionDate);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final Date _tmpDueDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfDueDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfDueDate);
            }
            _tmpDueDate = DateConverter.toDate(_tmp_1);
            _item.setDueDate(_tmpDueDate);
            final boolean _tmpIsSettled;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsSettled);
            _tmpIsSettled = _tmp_2 != 0;
            _item.setSettled(_tmpIsSettled);
            final Date _tmpSettledDate;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfSettledDate)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfSettledDate);
            }
            _tmpSettledDate = DateConverter.toDate(_tmp_3);
            _item.setSettledDate(_tmpSettledDate);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item.setNotes(_tmpNotes);
            final Date _tmpCreatedAt;
            final Long _tmp_4;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateConverter.toDate(_tmp_4);
            _item.setCreatedAt(_tmpCreatedAt);
            final Date _tmpUpdatedAt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = DateConverter.toDate(_tmp_5);
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
  public LiveData<List<Loan>> getSettledLoans() {
    final String _sql = "SELECT * FROM loans WHERE is_settled = 1 ORDER BY settled_date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"loans"}, false, new Callable<List<Loan>>() {
      @Override
      public List<Loan> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfPersonName = CursorUtil.getColumnIndexOrThrow(_cursor, "person_name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfTransactionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "transaction_date");
          final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
          final int _cursorIndexOfIsSettled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_settled");
          final int _cursorIndexOfSettledDate = CursorUtil.getColumnIndexOrThrow(_cursor, "settled_date");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<Loan> _result = new ArrayList<Loan>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Loan _item;
            final String _tmpType;
            if (_cursor.isNull(_cursorIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _cursor.getString(_cursorIndexOfType);
            }
            final String _tmpPersonName;
            if (_cursor.isNull(_cursorIndexOfPersonName)) {
              _tmpPersonName = null;
            } else {
              _tmpPersonName = _cursor.getString(_cursorIndexOfPersonName);
            }
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final Date _tmpTransactionDate;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfTransactionDate)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfTransactionDate);
            }
            _tmpTransactionDate = DateConverter.toDate(_tmp);
            _item = new Loan(_tmpType,_tmpPersonName,_tmpAmount,_tmpTransactionDate);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final Date _tmpDueDate;
            final Long _tmp_1;
            if (_cursor.isNull(_cursorIndexOfDueDate)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getLong(_cursorIndexOfDueDate);
            }
            _tmpDueDate = DateConverter.toDate(_tmp_1);
            _item.setDueDate(_tmpDueDate);
            final boolean _tmpIsSettled;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsSettled);
            _tmpIsSettled = _tmp_2 != 0;
            _item.setSettled(_tmpIsSettled);
            final Date _tmpSettledDate;
            final Long _tmp_3;
            if (_cursor.isNull(_cursorIndexOfSettledDate)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getLong(_cursorIndexOfSettledDate);
            }
            _tmpSettledDate = DateConverter.toDate(_tmp_3);
            _item.setSettledDate(_tmpSettledDate);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item.setNotes(_tmpNotes);
            final Date _tmpCreatedAt;
            final Long _tmp_4;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getLong(_cursorIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateConverter.toDate(_tmp_4);
            _item.setCreatedAt(_tmpCreatedAt);
            final Date _tmpUpdatedAt;
            final Long _tmp_5;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getLong(_cursorIndexOfUpdatedAt);
            }
            _tmpUpdatedAt = DateConverter.toDate(_tmp_5);
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
  public LiveData<Double> getTotalBorrowedPending() {
    final String _sql = "SELECT SUM(amount) FROM loans WHERE type = 'borrowed' AND is_settled = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"loans"}, false, new Callable<Double>() {
      @Override
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if(_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
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
  public LiveData<Double> getTotalLentPending() {
    final String _sql = "SELECT SUM(amount) FROM loans WHERE type = 'lent' AND is_settled = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[]{"loans"}, false, new Callable<Double>() {
      @Override
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if(_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
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
  public List<Loan> getAllLoansSync() {
    final String _sql = "SELECT * FROM loans ORDER BY created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
      final int _cursorIndexOfPersonName = CursorUtil.getColumnIndexOrThrow(_cursor, "person_name");
      final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
      final int _cursorIndexOfTransactionDate = CursorUtil.getColumnIndexOrThrow(_cursor, "transaction_date");
      final int _cursorIndexOfDueDate = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date");
      final int _cursorIndexOfIsSettled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_settled");
      final int _cursorIndexOfSettledDate = CursorUtil.getColumnIndexOrThrow(_cursor, "settled_date");
      final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
      final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
      final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
      final List<Loan> _result = new ArrayList<Loan>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Loan _item;
        final String _tmpType;
        if (_cursor.isNull(_cursorIndexOfType)) {
          _tmpType = null;
        } else {
          _tmpType = _cursor.getString(_cursorIndexOfType);
        }
        final String _tmpPersonName;
        if (_cursor.isNull(_cursorIndexOfPersonName)) {
          _tmpPersonName = null;
        } else {
          _tmpPersonName = _cursor.getString(_cursorIndexOfPersonName);
        }
        final double _tmpAmount;
        _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
        final Date _tmpTransactionDate;
        final Long _tmp;
        if (_cursor.isNull(_cursorIndexOfTransactionDate)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getLong(_cursorIndexOfTransactionDate);
        }
        _tmpTransactionDate = DateConverter.toDate(_tmp);
        _item = new Loan(_tmpType,_tmpPersonName,_tmpAmount,_tmpTransactionDate);
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final Date _tmpDueDate;
        final Long _tmp_1;
        if (_cursor.isNull(_cursorIndexOfDueDate)) {
          _tmp_1 = null;
        } else {
          _tmp_1 = _cursor.getLong(_cursorIndexOfDueDate);
        }
        _tmpDueDate = DateConverter.toDate(_tmp_1);
        _item.setDueDate(_tmpDueDate);
        final boolean _tmpIsSettled;
        final int _tmp_2;
        _tmp_2 = _cursor.getInt(_cursorIndexOfIsSettled);
        _tmpIsSettled = _tmp_2 != 0;
        _item.setSettled(_tmpIsSettled);
        final Date _tmpSettledDate;
        final Long _tmp_3;
        if (_cursor.isNull(_cursorIndexOfSettledDate)) {
          _tmp_3 = null;
        } else {
          _tmp_3 = _cursor.getLong(_cursorIndexOfSettledDate);
        }
        _tmpSettledDate = DateConverter.toDate(_tmp_3);
        _item.setSettledDate(_tmpSettledDate);
        final String _tmpNotes;
        if (_cursor.isNull(_cursorIndexOfNotes)) {
          _tmpNotes = null;
        } else {
          _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
        }
        _item.setNotes(_tmpNotes);
        final Date _tmpCreatedAt;
        final Long _tmp_4;
        if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
          _tmp_4 = null;
        } else {
          _tmp_4 = _cursor.getLong(_cursorIndexOfCreatedAt);
        }
        _tmpCreatedAt = DateConverter.toDate(_tmp_4);
        _item.setCreatedAt(_tmpCreatedAt);
        final Date _tmpUpdatedAt;
        final Long _tmp_5;
        if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
          _tmp_5 = null;
        } else {
          _tmp_5 = _cursor.getLong(_cursorIndexOfUpdatedAt);
        }
        _tmpUpdatedAt = DateConverter.toDate(_tmp_5);
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
