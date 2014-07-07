package org.hogel.android.bookscanmanager.app.dao;

import org.hogel.android.bookscanmanager.app.dao.record.BookRecord;

public class BookDaoHelper extends AbstractDaoHelper<BookRecord, String> {
    @Override
    public Class<BookRecord> getRecordClass() {
        return BookRecord.class;
    }
}
