package com.treasurehunt.treasurehunt.db.elasticsearch;

public class ElasticsearchException extends RuntimeException {
    public ElasticsearchException(String errorMessage) {
        super(errorMessage);
    }
}
