package org.hogel.android.bookscanmanager.app.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hogel.bookscan.model.OptimizingBook;

import java.util.List;

public class SyncOptimizingBooksEvent {
    @AllArgsConstructor(suppressConstructorProperties = true)
    @Data
    public static class Success {
        private List<OptimizingBook> books;
    }

    public static class Failure {
    }

    public static Success success(List<OptimizingBook> books) {
        return new Success(books);
    }

    public static Failure failure() {
        return new Failure();
    }
}
