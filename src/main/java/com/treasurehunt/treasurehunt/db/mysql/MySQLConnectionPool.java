package com.treasurehunt.treasurehunt.db.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class MySQLConnectionPool {
    private static final String CLOUD_SQL_CONNECTION_NAME = "treasure-hunt-314706:us-central1:treasure-hunt-mysql" +
            "-instance";
    private static final String DB_NAME = "treasure_hunt";
    public final DataSource pool;

    MySQLConnectionPool() throws IOException {
        Properties prop = new Properties();
        String propFileName = "config.properties";

        InputStream inputStream =
                MySQLConnectionPool.class.getClassLoader().getResourceAsStream(propFileName);
        prop.load(inputStream);

        String sql_user = prop.getProperty("sql_user");
        String sql_password = prop.getProperty("sql_password");

        // The configuration object specifies behavior for the connection pool
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql:///%s", DB_NAME));
        config.setUsername(sql_user);
        config.setPassword(sql_password);

        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
        config.addDataSourceProperty("cloudSqlInstance", CLOUD_SQL_CONNECTION_NAME);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Specify a comma delimited list of preferred IP types for connecting to instance
        config.addDataSourceProperty("ipTypes", "PUBLIC,PRIVATE");
        // maximumPoolSize limits the total number of concurrent connections this pool will keep
        config.setMaximumPoolSize(5);
        // minimumIdle is the minimum number of idle connections Hikari maintains in the pool.
        config.setMinimumIdle(5);
        // setConnectionTimeout is the max number of ms to wait for a connection checkout.
        config.setConnectionTimeout(10000); // 10 seconds
        // idleTimeout is the max amount of time a connection can sit in the pool
        config.setIdleTimeout(600000); // 10 minutes
        // maxLifeTime is the max possible lifetime of a connection in the pool.
        config.setMaxLifetime(1800000); // 30 minutes

        // Initialize the connection pool using the configuration object.
        pool = new HikariDataSource(config);
    }

}
