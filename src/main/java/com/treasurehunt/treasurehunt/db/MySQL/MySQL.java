package com.treasurehunt.treasurehunt.db.mysql;

import com.treasurehunt.treasurehunt.entity.User;

import javax.sql.DataSource;
import java.sql.Connection;

public class MySQL {

    private static final String USERS_DB = "users";
    private static final String LISTINGS_DB = "listings";
    private static final String SAVED_RECORDS_DB = "saved_records";

    // Create user in users db
    public static String createUser(DataSource pool, User user) throws MySQLException {
        try (Connection conn = pool.getConnection()) {
            // TODO
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            throw new MySQLException("Failed to create User");
        }
    }
}
