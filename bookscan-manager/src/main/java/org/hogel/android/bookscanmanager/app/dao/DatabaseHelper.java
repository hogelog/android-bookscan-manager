package org.hogel.android.bookscanmanager.app.dao;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.hogel.android.bookscanmanager.app.dao.record.BookRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.Getter;

@Singleton
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseHelper.class);

    private static final String DATABASE_NAME = "bookscan.db";
    private static final int DATABASE_VERSION = 1;

    @Getter
    private Dao<BookRecord, String> bookDao;

    @Inject
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, BookRecord.class);
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, BookRecord.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        try {
            bookDao = getDao(BookRecord.class);
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void clearBookDao() throws SQLException {
    }

    public void clearTable(Class<?> recordClass) throws SQLException {
        TableUtils.clearTable(getConnectionSource(), recordClass);
    }
}
