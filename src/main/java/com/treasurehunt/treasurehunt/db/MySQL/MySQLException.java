package com.treasurehunt.treasurehunt.db.MySQL;

public class MySQLException extends RuntimeException{
    public MySQLException(String errorMessage) {
        super(errorMessage);
    }
}
