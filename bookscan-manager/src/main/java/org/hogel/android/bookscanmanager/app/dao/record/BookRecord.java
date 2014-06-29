package org.hogel.android.bookscanmanager.app.dao.record;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.hogel.bookscan.model.Book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties=true)
@DatabaseTable(tableName = "books")
public class BookRecord {
    @DatabaseField(id = true)
    private String filename;

    @DatabaseField
    private String hash;

    @DatabaseField
    private String digest;

    @DatabaseField
    private String imageUrl;

    public BookRecord(Book book) {
        this(book.getFilename(), book.getHash(), book.getDigest(), book.getImageUrl());
    }

    public Book toBook() {
        return new Book(filename, hash, digest, imageUrl);
    }

    @Override
    public String toString() {
        return filename;
    }
}
