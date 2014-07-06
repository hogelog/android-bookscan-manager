package org.hogel.android.bookscanmanager.app.dao.record;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hogel.bookscan.model.OptimizedBook;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties=true)
@DatabaseTable(tableName = "optimized_books")
public class OptimizedBookRecord {
    @DatabaseField(id = true)
    private String filename;

    @DatabaseField
    private String digest;

    public OptimizedBookRecord(OptimizedBook book) {
        this(book.getFilename(), book.getDigest());
    }

    public OptimizedBook toOptimizedBook() {
        return new OptimizedBook(filename, digest);
    }

    @Override
    public String toString() {
        return filename;
    }
}
