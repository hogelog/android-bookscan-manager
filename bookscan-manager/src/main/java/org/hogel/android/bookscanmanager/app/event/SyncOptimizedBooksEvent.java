package org.hogel.android.bookscanmanager.app.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hogel.bookscan.model.OptimizedBook;

import java.util.List;

public class SyncOptimizedBooksEvent {
    @AllArgsConstructor(suppressConstructorProperties = true)
    @Data
    public static class Success {
        private List<OptimizedBook> books;
    }

    public static class Failure {
    }

    public static Success success(List<OptimizedBook> books) {
        return new Success(books);
    }

    public static Failure failure() {
        return new Failure();
    }
}
