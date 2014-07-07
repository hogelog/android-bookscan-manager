package org.hogel.android.bookscanmanager.app.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.j256.ormlite.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

@Singleton
public abstract class AbstractDaoHelper<T, ID> {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractDaoHelper.class);

    @Inject
    private DatabaseHelper databaseHelper;

    public Dao<T, ID> dao() {
        try {
            return databaseHelper.getDao(getRecordClass());
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        try {
            databaseHelper.clearTable(getRecordClass());
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public abstract Class<T> getRecordClass();
}
