package org.hogel.android.bookscan_manager.app.bookscan.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor()
@AllArgsConstructor(suppressConstructorProperties=true)
@DatabaseTable(tableName = "books")
public class Book {
    @DatabaseField(id = true)
    private String filename;

    @DatabaseField
    private String hash;

    @DatabaseField
    private String digest;

    private boolean downloading = false;

    @Override
    public String toString() {
        return filename;
    }
}
