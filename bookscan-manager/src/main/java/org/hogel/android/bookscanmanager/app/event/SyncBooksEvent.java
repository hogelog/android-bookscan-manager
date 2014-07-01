package org.hogel.android.bookscanmanager.app.event;

import org.hogel.bookscan.model.Book;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

public class SyncBooksEvent {
    @AllArgsConstructor(suppressConstructorProperties = true)
    @Data
    public static class Success {
        private List<Book> books;
    }

    public static class Failure {
    }

    public static Success success(List<Book> books) {
        return new Success(books);
    }

    public static Failure failure() {
        return new Failure();
    }
}
