package com.treasurehunt.treasurehunt.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

public class MySQLTableCreator {
    public static void main(String[] args) {
        // Step 1 Connect to MySQL.
        try {
            System.out.println("Connecting to Cloud SQL");
            MySQLConnectionPool mySQLConnectionPool = new MySQLConnectionPool();
            DataSource pool = mySQLConnectionPool.pool;

            // conn opened here so that it auto closes at the end of try block
            try (Connection conn = pool.getConnection()) {
                if (conn == null) {
                    return;
                }

                // Step 2 Drop tables in case they exist
                Statement statement = conn.createStatement();
                String sql = "DROP TABLE IF EXISTS saved_records";
                statement.executeUpdate(sql);

                sql = "DROP TABLE IF EXISTS listings";
                statement.executeUpdate(sql);

                sql = "DROP TABLE IF EXISTS users";
                statement.executeUpdate(sql);

                // Step 3 Create new tables
                sql = "CREATE TABLE users ( "
                        + "user_id VARCHAR(255) NOT NULL, "
                        + "password VARCHAR(255) NOT NULL, "
                        + "first_name VARCHAR(255) NOT NULL, "
                        + "last_name VARCHAR(255) NOT NULL, "
                        + "email VARCHAR(255) NOT NULL, "
                        + "address VARCHAR(255), "
                        + "PRIMARY KEY (user_id) "
                        + ")";
                statement.executeUpdate(sql);

                sql = "CREATE TABLE listings ( "
                        + "listing_id VARCHAR(255) NOT NULL, "
                        + "title VARCHAR(255) NOT NULL, "
                        + "price DOUBLE NOT NULL, "
                        + "category VARCHAR(255) NOT NULL, "
                        + "seller_id VARCHAR(255) NOT NULL, "
                        + "seller_name VARCHAR(255) NOT NULL, "
                        + "description VARCHAR(255) NOT NULL, "
                        + "item_condition VARCHAR(255) NOT NULL, "
                        + "address VARCHAR(255) NOT NULL, "
                        + "picture_urls JSON, "
                        + "PRIMARY KEY (listing_id), "
                        + "FOREIGN KEY (seller_id) REFERENCES users(user_id)"
                        + ")";
                statement.executeUpdate(sql);

                sql = "CREATE TABLE saved_records ("
                        + "user_id VARCHAR(255) NOT NULL,"
                        + "listing_id VARCHAR(255) NOT NULL,"
                        + "PRIMARY KEY (user_id, listing_id),"
                        + "FOREIGN KEY (user_id) REFERENCES users(user_id),"
                        + "FOREIGN KEY (listing_id) REFERENCES listings(listing_id)"
                        + ")";
                statement.executeUpdate(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
