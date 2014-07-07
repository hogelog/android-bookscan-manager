package org.hogel.android.bookscanmanager.app.dao;

import org.hogel.android.bookscanmanager.app.dao.record.OptimizedBookRecord;

public class OptimizedBookDaoHelper extends AbstractDaoHelper<OptimizedBookRecord, String> {
    @Override
    public Class<OptimizedBookRecord> getRecordClass() {
        return OptimizedBookRecord.class;
    }
}
