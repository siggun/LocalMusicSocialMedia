package com.bandswipe.shared.exception;

public class DuplicateSwipeException extends RuntimeException {

    public DuplicateSwipeException() {
        super("You have already swiped on this user.");
    }

    public DuplicateSwipeException(String message) {
        super(message);
    }
}
