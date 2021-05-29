package com.treasurehunt.treasurehunt.db.gcs;

public class GCSException extends RuntimeException {
    public GCSException(String errorMessage) {
        super(errorMessage);
    }
}
