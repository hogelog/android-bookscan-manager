package org.hogel.android.bookscan_manager.app.bookscan.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(suppressConstructorProperties=true)
public class Book {
    private String hash;
    private String digest;
    private String filename;
}
