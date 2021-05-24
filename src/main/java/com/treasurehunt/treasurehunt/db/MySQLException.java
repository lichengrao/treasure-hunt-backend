package com.treasurehunt.treasurehunt.db;

public class MySQLException extends RuntimeException{
    public MySQLException(String errorMessage) {
        super(errorMessage);
    }
}
