package org.hogel.android.bookscanmanager.app.event;

import lombok.AllArgsConstructor;
import lombok.Data;

public class LoginEvent {
    @AllArgsConstructor(suppressConstructorProperties = true)
    @Data
    public static class Success {
        private String loginMail;
        private String loginPass;
    }

    public static class Failure {
    }

    public static Success success(String loginMail, String loginPass) {
        return new Success(loginMail, loginPass);
    }

    public static Failure failure() {
        return new Failure();
    }
}
